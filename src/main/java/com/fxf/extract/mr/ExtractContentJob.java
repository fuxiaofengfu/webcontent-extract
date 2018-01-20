package com.fxf.extract.mr;

import com.fxf.extract.algorithm.HtmlContentExtractor;
import com.fxf.extract.hbase.HBaseUtil;
import com.fxf.extract.util.DateUtil;
import com.fxf.extract.util.Md5Util;
import com.fxf.extract.util.UrlUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.*;

/**
 * 提取爬虫网页内容到hbase中
 */
public class ExtractContentJob extends AbstractJob {

	private static final String CONTENT_XPATH = "content_xpath";
	private static final String INPUT_FILE_CONTENT_SP = "\001";

	private static final TableName tableName = TableName.valueOf("web_crawler_content");

	public Job getJob(String[] args) throws Exception {
		int zero = 0;
		Configuration conf = getConf();
		Job job = Job.getInstance(conf, getJobName());
		job.setJarByClass(ExtractContentJob.class);
		job.setMapperClass(ExtractContentMapper.class);
		job.setNumReduceTasks(zero);
		job.setMapOutputKeyClass(NullWritable.class);
		job.setMapOutputValueClass(Text.class);

		FileInputFormat.setInputPaths(job, conf.get(INPUT_PATH));
		FileSystem fileSystem = FileSystem.get(conf);
		Path out = new Path(conf.get(OUTPUT_PATH));
		if (fileSystem.exists(out)) {
			fileSystem.delete(out, true);
		}
		FileOutputFormat.setOutputPath(job, out);
		return job;
	}

	public String getJobName() {
		return "extractcontent";
	}

	private static class ExtractContentMapper extends Mapper<LongWritable, Text, NullWritable, Text> {

		@Override
		protected void setup(Context context) throws IOException, InterruptedException {
//			Configuration configuration = context.getConfiguration();
//			String s = configuration.get(CONTENT_XPATH);
//			if (StringUtils.isEmpty(s)) {
//				configuration.set(CONTENT_XPATH, "com.fxf.extract.mr.MrCache");
//			}
			MrCache.getCacheData();
		}

		/**
		 * @param key
		 * @param value
		 * @param context
		 * @throws IOException
		 * @throws InterruptedException
		 */
		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

			if (null == value) {
				return;
			}
			String s = value.toString();
			if (StringUtils.isEmpty(s)) return;

			String[] split = s.split(INPUT_FILE_CONTENT_SP);
			String host = UrlUtil.getHost(split[0]);
			List<String> contentXpathByHost = MrCache.getContentXpathByHost(host);
			if (CollectionUtils.isEmpty(contentXpathByHost)) return;

			String url = split[0];
			String html = split[1];

			String dateStr = DateUtil.formatDate(new Date(), "yyyyMMddHHmmss");
			try {
				String md5url = Md5Util.md5(url);
				String row = dateStr + "_" + host + "_" + md5url;

				List<Map<String, String>> dataList = new ArrayList<>();
//				-- f1:webhosts,url,title,xpath
//				-- f2:content
//				-- f3:html
				for (String xpath : contentXpathByHost) {
					String content = HtmlContentExtractor.getContentByXpath1(html, xpath);
					if (StringUtils.isEmpty(content)) continue;

					Map<String, String> dataMap = new HashMap<>();
					dataMap.put("row", row);
					dataMap.put("family", "f1");
					dataMap.put("qualifier", "webhosts");
					dataMap.put("value", host);

					Map<String, String> dataMap1 = new HashMap<>();
					dataMap1.put("row", row);
					dataMap1.put("family", "f1");
					dataMap1.put("qualifier", "url");
					dataMap1.put("value", url);

					Map<String, String> dataMap2 = new HashMap<>();
					dataMap2.put("row", row);
					dataMap2.put("family", "f1");
					dataMap2.put("qualifier", "title");
					dataMap2.put("value", HtmlContentExtractor.getTitleByHtml(html));

					Map<String, String> dataMap3 = new HashMap<>();
					dataMap3.put("row", row);
					dataMap3.put("family", "f1");
					dataMap3.put("qualifier", "xpath");
					dataMap3.put("value", xpath);

					Map<String, String> dataMap4 = new HashMap<>();
					dataMap4.put("row", row);
					dataMap4.put("family", "f2");
					dataMap4.put("qualifier", "content");
					dataMap4.put("value", content);

					Map<String, String> dataMap5 = new HashMap<>();
					dataMap5.put("row", row);
					dataMap5.put("family", "f3");
					dataMap5.put("qualifier", "html");
					dataMap5.put("value", html);

					dataList.add(dataMap);
					dataList.add(dataMap1);
					dataList.add(dataMap2);
					dataList.add(dataMap3);
					dataList.add(dataMap4);
					dataList.add(dataMap5);
				}
				HBaseUtil.saveBatchData(tableName, dataList,"_" + md5url);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		try {
			System.exit(ToolRunner.run(new ExtractContentJob(), args));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
