package com.fxf.extract.util;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlUtil {

	public static String getHost(String url) throws MalformedURLException {
		URL url1 = new URL(url);
		return url1.getHost();
	}

}
