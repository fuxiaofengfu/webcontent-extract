package com.fxf.extract.util;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {

	public static String streamToStr(InputStream inputStream) throws IOException {
		if (null == inputStream){
			return null;
		}
		return IOUtils.toString(inputStream);
	}
}
