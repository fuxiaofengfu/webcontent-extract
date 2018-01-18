package com.fxf.extract.mr;

import com.fxf.extract.CountXpath;
import org.apache.hadoop.util.ProgramDriver;

/**
 * 程序
 */
public class JobDriver {

	public static void main(String[] args) throws Throwable {
		ProgramDriver programDriver = new ProgramDriver();
		programDriver.addClass("extractxpath", ExtractContentXpathJob.class, "提取网页内容的xpath到mysql中");
		programDriver.addClass("extractcontent", ExtractContentJob.class, "提取网页内容到hbase中");
		programDriver.addClass("countxpath", CountXpath.class, "统计可用的content_xpath到mysql中");
		int run = programDriver.run(args);
		System.exit(run);
	}

}
