package com.fxf.extract.util;

import java.security.MessageDigest;

public class Md5Util {


	public static String md5(String stri) throws Exception {
		char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
		MessageDigest md5 = MessageDigest.getInstance("md5");
		md5.update(stri.getBytes("utf-8"));
		byte[] digest = md5.digest();
		int j = digest.length;
		char str[] = new char[j * 2];
		int k = 0;
		for (int i = 0; i < j; i++) {
			byte byte0 = digest[i];
			str[k++] = hexDigits[byte0 >>> 4 & 0xf];
			str[k++] = hexDigits[byte0 & 0xf];
		}
		return new String(str);
	}

	public static void main(String[] args) {
		try {
			String afdsgdhfjg = md5("哈哈哈");
			System.out.println(afdsgdhfjg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
