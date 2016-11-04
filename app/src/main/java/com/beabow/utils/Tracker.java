package com.beabow.utils;

import java.text.SimpleDateFormat;

import android.util.Log;
import android.os.Process;

public class Tracker {

	public static boolean DEBUG_ABLE = true;
	public static String packageName;
	private static final int a = Process.myPid();
	private static final SimpleDateFormat b = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
	private static final SimpleDateFormat c = new SimpleDateFormat("yyyyMMdd");

	public static void debug(String msg) {
		if (DEBUG_ABLE) {
			Log.d("Tracker", a(msg));
		}
	}
	
	
	public static void debug(String msg, Throwable throwable) {
		if (DEBUG_ABLE) {
			Log.d("Tracker", a(msg), throwable);
		}
	}

	private static String a(String msg) {
		return String.format("%s %s", new Object[] { a(3), msg });
	}

	private static String a(int level) {
		StackTraceElement trace = new Exception().getStackTrace()[level];
		return String.format(
				"(%s:%s) %s -> ",
				new Object[] { trace.getFileName(),
						Integer.valueOf(trace.getLineNumber()),
						trace.getMethodName() });
	}
}
