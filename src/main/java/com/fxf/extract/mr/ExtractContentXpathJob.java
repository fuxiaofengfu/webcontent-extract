package com.fxf.extract.mr;

import com.fxf.extract.algorithm.HtmlContentExtractor;
import com.fxf.extract.jdbc.util.PropertyUtil;
import com.fxf.extract.util.DateUtil;
import com.fxf.extract.util.Md5Util;
import com.fxf.extract.util.StreamUtil;
import com.fxf.extract.util.UrlUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

/**
 * 提取网页内容的xpath到mysql中
 */
public class ExtractContentXpathJob extends AbstractJob {

	private static final String SP = "\001";
	private static final String MYSQL_SP = "\t";

	private Configuration configuration;

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public Job getJob(String[] args) throws Exception {

		Configuration conf = getConf();
		setConfiguration(conf);
		Job job = Job.getInstance(conf, getJobName());

		job.setJarByClass(ExtractContentXpathJob.class);
		job.setMapperClass(ExtractContentXpathMapper.class);
		job.setMapOutputKeyClass(NullWritable.class);
		job.setMapOutputValueClass(Text.class);
		job.setNumReduceTasks(0);

		FileInputFormat.setInputPaths(job, conf.get(INPUT_PATH));
		String outputPath = conf.get(OUTPUT_PATH);
		Path outPath = new Path(outputPath);
		FileSystem fileSystem = FileSystem.get(conf);
		if (fileSystem.exists(outPath)) {
			fileSystem.delete(outPath, true);
		}
		FileOutputFormat.setOutputPath(job, outPath);
		return job;
	}

	public String getJobName() {
		return "extractcontentxpath";
	}

	private static class ExtractContentXpathMapper extends Mapper<LongWritable, Text, NullWritable, Text> {

		Text va = new Text();

		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			context.getCounter("fuxiaofeng", "total").increment(1);
			if (null == value) return;
			String s = value.toString();
			if (StringUtils.isEmpty(s)) return;
			String[] split = s.split(SP);
			Map<String, String> xpathMap = HtmlContentExtractor.generateXpath(split[1].replace("\002", ""));
			if (null == xpathMap || xpathMap.isEmpty()) {
				return;
			}
			// webhosts ,content_xpath ,md5_xpath ,url,md5_url, create_time
			String xpath = xpathMap.get(HtmlContentExtractor.CONTENT_KEY);
			String host = UrlUtil.getHost(split[0]);
			StringBuilder builder = new StringBuilder();
			builder.append(MYSQL_SP);
			builder.append(host);
			builder.append(MYSQL_SP);
			builder.append(xpath);
			try {
				builder.append(MYSQL_SP);
				builder.append(Md5Util.md5(xpath));
				builder.append(MYSQL_SP);
				builder.append(split[0]);
				builder.append(MYSQL_SP);
				builder.append(Md5Util.md5(split[0]));
				builder.append(MYSQL_SP);
				builder.append(DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			va.set(builder.toString());
			context.write(NullWritable.get(), va);
		}
	}

	public static void main(String[] args) throws Exception {
		ExtractContentXpathJob extractContentXpathJob = new ExtractContentXpathJob();
		int run = ToolRunner.run(extractContentXpathJob, args);
		int loadDataInMysql_fail = 111;
		if (0 == run) {
			String shell_path = PropertyUtil.getProperty("shell_path");
			if (StringUtils.isEmpty(shell_path)) {
				run = loadDataInMysql_fail;
			}
			Runtime runtime = Runtime.getRuntime();
			String command = MessageFormat.format("sh -c {0}", shell_path);
			Process exec = runtime.exec(command);
			exec.waitFor();
			String s = StreamUtil.streamToStr(exec.getErrorStream());
			System.out.println(s);
		}
		System.exit(run);
	}

}
