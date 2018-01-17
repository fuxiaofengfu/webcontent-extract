package com.fxf.extract.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public synchronized static String formatDate(Date date, String format) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		String format1 = simpleDateFormat.format(date);
		return format1;
	}
}
