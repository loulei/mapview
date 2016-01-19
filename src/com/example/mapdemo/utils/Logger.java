package com.example.mapdemo.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Logger {

	protected static String logFormat = ".%m:%l: %M";

	protected static String dateFormat = "yyyy.MM.dd HH:mm:ss.S";

	public static String tag;

	protected static int loglevel=Log.WARN;
	
	public static final String PREFS_NAME="LOGPREFS";

	public static final String PREFS_LEVEL="LOGLEVEL";

	public Logger() {
		tag = "Logger";

	}

	public static void setLogLevel(int loglevel) {
		if (loglevel < Log.VERBOSE && loglevel > Log.ASSERT) {
			Logger.loglevel = Log.DEBUG;
		} else {
			Logger.loglevel = loglevel;
		}

	}
	
	public static boolean saveLogLevel(Context ctx){
		SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
		return settings.edit().putInt(PREFS_LEVEL, loglevel).commit();
	}
	
	public static void setLogLevelFromPreferences(Context ctx){
		SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
		loglevel=settings.getInt(PREFS_LEVEL, Log.DEBUG);
	}
	
	
	public static int getLogLevel(){
		return loglevel;
	}

	public static boolean isTraceEnabled(){
		return loglevel <= Log.VERBOSE;
	}
	
	public static boolean isVerboseEnabled() {
		return loglevel <= Log.VERBOSE;
	}

	public static boolean isDebugEnabled() {
		return loglevel <= Log.DEBUG;
	}

	public static boolean isInfoEnabled() {
		return loglevel <= Log.INFO;
	}

	public static boolean isWarnEnabled() {
		return loglevel <= Log.WARN;
	}

	public static boolean isErrorEnabled() {
		return loglevel <= Log.ERROR;
	}

	public static boolean isAssertEnabled() {
		return true;
	}

	public Logger(String logTag) {
		tag = logTag;

	}

	public Logger(Class<?> className) {
		tag = className.getSimpleName();
	}

	public static void d(String msg) {

		logMessage(Log.DEBUG, null, msg, null);
	}

	public void debug(String msg) {
		logMessage(Log.DEBUG, tag, msg, null);
	}

	public void debug(String msg, Throwable tr) {
		logMessage(Log.DEBUG, tag, msg, tr);
	}

	public static void d(String msg, Throwable tr) {
		logMessage(Log.DEBUG, null, msg, tr);
	}

	public void verbose(String msg) {
		logMessage(Log.VERBOSE, tag, msg, null);
	}

	public static void v(String msg) {
		logMessage(Log.VERBOSE, null, msg, null);
	}

	public void verbose(String msg, Throwable tr) {
		logMessage(Log.VERBOSE, tag, msg, tr);
	}

	public static void v(String msg, Throwable tr) {
		logMessage(Log.VERBOSE, null, msg, tr);
	}
	
	public void trace(String msg) {
		logMessage(Log.VERBOSE, tag, msg, null);
	}

	public static void t(String msg) {
		logMessage(Log.VERBOSE, null, msg, null);
	}

	public void trace(String msg, Throwable tr) {
		logMessage(Log.VERBOSE, tag, msg, tr);
	}

	public static void t(String msg, Throwable tr) {
		logMessage(Log.VERBOSE, null, msg, tr);
	}
	

	public void info(String msg) {
		logMessage(Log.INFO, tag, msg, null);
	}

	public static void i(String msg) {
		logMessage(Log.INFO, null, msg, null);
	}

	public void info(String msg, Throwable tr) {
		logMessage(Log.INFO, tag, msg, tr);
	}

	public static void i(String msg, Throwable tr) {
		logMessage(Log.INFO, null, msg, tr);
	}

	public void warn(String msg) {
		logMessage(Log.WARN, tag, msg, null);
	}

	public static void w(String msg) {
		logMessage(Log.WARN, null, msg, null);
	}

	public void warn(String msg, Throwable tr) {
		logMessage(Log.WARN, tag, msg, tr);
	}

	public static void w(String msg, Throwable tr) {
		logMessage(Log.WARN, null, msg, tr);
	}

	public void error(String msg) {
		logMessage(Log.ERROR, tag, msg, null);
	}

	public static void e(String msg) {
		logMessage(Log.ERROR, null, msg, null);
	}

	public void error(String msg, Throwable tr) {
		logMessage(Log.ERROR, tag, msg, tr);
	}

	public static void e(String msg, Throwable tr) {
		logMessage(Log.ERROR, null, msg, tr);
	}

	public void wtf(String msg) {
		logMessage(Log.ASSERT, tag, msg, null);
	}

	public static void f(String msg) {
		logMessage(Log.ASSERT, null, msg, null);
	}

	public void wtf(String msg, Throwable tr) {
		logMessage(Log.ASSERT, tag, msg, tr);
	}

	public static void f(String msg, Throwable tr) {
		logMessage(Log.ASSERT, null, msg, tr);
	}

	public void azzert(boolean logIfTrue, String msg) {
		if (logIfTrue)
			logMessage(Log.ASSERT, tag, msg, null);
	}

	protected static void logMessage(int level, String tag, String msg, Throwable exception) {

		if (level >= loglevel) {

			StringBuffer log = new StringBuffer();

			StackTraceElement caller = Thread.currentThread().getStackTrace()[4];
			
			String fullClass=caller.getClassName();
			int dotPos=fullClass.lastIndexOf('.');

			if (tag == null) {
				tag = dotPos<0?fullClass:fullClass.substring(dotPos+1);
//				Log.d("LOGGER", caller.getClassName());
			}

			for (int i = 0; i < logFormat.length(); i++) {

				if (logFormat.charAt(i) == '%') {

					switch (logFormat.charAt(i + 1)) {
					case 'd':
						// date replacement
						log.append(new SimpleDateFormat(dateFormat).format(new Date()));
						break;

					case 'p':
						// package
						log.append(dotPos<0?"":fullClass.substring(0, dotPos));
						break;

					case 'C':
						// full class name
						log.append(fullClass);
						break;

					case 'c':
						// short class name
						log.append(dotPos<0?fullClass:fullClass.substring(dotPos+1));
						break;

					case 'f':
						// file name
						log.append(caller.getFileName());
						break;

					case 'm':
						// method name
						log.append(caller.getMethodName());
						break;

					case 'l':
						// line number
						log.append(caller.getLineNumber());
						break;

					case 'M':
						// log message
						log.append(msg);
						break;

					case '%':
						// % sign
						log.append("%");
						break;

					case 'n':
						// new line
						log.append("\n");
						break;

					default:
						log.append("%" + logFormat.charAt(i + 1));
						break;

					}

					// skip next character
					i++;
				} else {
					// just add the character
					log.append(logFormat.charAt(i));
				}

			}

			if (exception == null) {
				switch (level) {
				case Log.VERBOSE:
					Log.v(tag, log.toString());
					break;
				case Log.DEBUG:
					Log.d(tag, log.toString());
					break;
				case Log.INFO:
					Log.i(tag, log.toString());
					break;
				case Log.WARN:
					Log.w(tag, log.toString());
					break;
				case Log.ERROR:
					Log.e(tag, log.toString());
					break;
				case Log.ASSERT:
					Log.wtf(tag, log.toString());
					break;
				}
			} else {
				switch (level) {
				case Log.VERBOSE:
					Log.v(tag, log.toString(), exception);
					break;
				case Log.DEBUG:
					Log.d(tag, log.toString(), exception);
					break;
				case Log.INFO:
					Log.i(tag, log.toString(), exception);
					break;
				case Log.WARN:
					Log.w(tag, log.toString(), exception);
					break;
				case Log.ERROR:
					Log.e(tag, log.toString(), exception);
					break;
				case Log.ASSERT:
					Log.wtf(tag, log.toString(), exception);
					break;
				}
			}

		}
		
	}

	public static String getLogFormat() {
		return logFormat;
	}

	public static void setLogFormat(String logFormat) {
		Logger.logFormat = logFormat;
	}

	public static String getDateFormat() {
		return dateFormat;
	}

	public static void setDateFormat(String dateFormat) {
		Logger.dateFormat = dateFormat;
	}

}
