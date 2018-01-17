package com.fxf.extract.mr;

import org.apache.hadoop.util.ProgramDriver;

/**
 * 程序
 */
public class JobDriver {

	public static void main(String[] args) throws Throwable {
		ProgramDriver programDriver = new ProgramDriver();
		programDriver.addClass("extractxpath", ExtractContentXpathJob.class, "提取网页内容的xpath到mysql中");
		programDriver.addClass("extractcontent", ExtractContentJob.class, "提取网页内容到hbase中");
		int run = programDriver.run(args);
		System.exit(run);
	}

}
