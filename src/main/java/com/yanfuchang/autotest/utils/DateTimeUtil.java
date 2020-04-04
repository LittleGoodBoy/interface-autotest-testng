package com.yanfuchang.autotest.utils;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateTimeUtil {
	private static String pat = "yyyy:MM:dd HH:mm:ss:SSS";
	private static DateFormat df = null;
	private static SimpleDateFormat sdf = null;
	
	/**
	 * 获取时间戳
	 */
	public static Long getTimeStamp(){
		return new Date().getTime();
	}
	
	/**
	 * 获取当前时间和日期
	 */
	public static String getDateTime(){
		df = DateFormat.getDateTimeInstance();
		return df.format(new Date());
	}
	
	/**
	 * 获取当前时间和日期及毫秒
	 */
	public static String getDateComplete(){
		sdf = new SimpleDateFormat(pat);
		return sdf.format(new Date());
	}
	
	/**
	 * 获取当前日期是Date类型
	 */
	public static Date getDateType(String strDate) throws ParseException{
		sdf = new SimpleDateFormat(pat);
		return sdf.parse(strDate);
	}
}
