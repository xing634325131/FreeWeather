package com.kingtime.freeweather.utils;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	private final static Pattern emailer = Pattern
			.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

	private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	public static String toMD5(String plainText) {
		String re_md5 = new String();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}

			re_md5 = buf.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return re_md5;
	}

	public static boolean isCorrectEmail(String email) {
		if (email == null || email.trim().length() == 0)
			return false;
		return emailer.matcher(email).matches();
	}

	public static int toInt(String str, int defValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
		}
		return defValue;
	}

	public static int toInt(Object obj) {
		if (obj == null)
			return 0;
		return toInt(obj.toString(), 0);
	}

	public static float toFloat(String str, float defValue) {
		try {
			return Long.parseLong(str);
		} catch (Exception e) {
		}
		return defValue;
	}

	public static float toFloat(Object obj) {
		if (obj == null)
			return 0;
		return toFloat(obj.toString(), 0);
	}

	public static boolean toBoolean(String key) {
		try {
			return Boolean.parseBoolean(key);
		} catch (Exception e) {
		}
		return false;
	}

	public static String getFormatNowDate() {
		Date now = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.ENGLISH);
		return df.format(now).toString();
	}

	public static Date toDate(String sdate) {
		try {
			return dateFormater.get().parse(sdate);
		} catch (ParseException e) {
			return null;
		}
	}

	public static String parse(InputStream inputStream) {
		byte[] data = null;
		try {
			data = StreamTool.read(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(data);
	}

	public static int parseTempToNum(String tempString) {
		// System.out.println("To parse string:"+tempString);
		return Integer
				.valueOf(tempString.substring(0, tempString.indexOf("℃")));
	}

	public static int parsePercentToNum(String percentString) {
		return Integer.valueOf(percentString.substring(0,
				percentString.indexOf("%")));
	}

	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}
}
