package com.felipecsl.activitys;

import android.util.Log;

public class FyLog {

	private String TAG = FyLog.class.getSimpleName();
	private static final boolean LOG = true;
	private static final boolean D = false;
	private static final boolean V = false;
	private static final boolean E = false;
	private static final boolean W = true;
	private static final boolean I = false;
	
	public static void v(String tag, String msg){
		if(LOG && V){
			Log.v(tag, msg);
		}
	}
	public static void v(String tag, String msg, Throwable tr){
		if(LOG && V){
			Log.v(tag, msg, tr);
		}
	}
	public static void d(String tag, String msg){
		if(LOG && D){
			Log.d(tag, msg);
		}
	}
	public static void d(String tag, String msg, Throwable tr){
		if(LOG && D){
			Log.d(tag, msg, tr);
		}
	}
	public static void i(String tag, String msg){
		if(LOG && I){
			Log.i(tag, msg);
		}
	}
	public static void i(String tag, String msg, Throwable tr){
		if(LOG && I){
			Log.i(tag, msg, tr);
		}
	}
	public static void e(String tag, String msg){
		if(LOG && E){
			Log.e(tag, msg);
		}
	}
	public static void e(String tag, String msg, Throwable tr){
		if(LOG && E){
			Log.e(tag, msg, tr);
		}
	}
	public static void w(String tag, String msg){
		if(LOG && W){
			Log.w(tag, msg);
		}
	}
	public static void w(String tag, String msg, Throwable tr){
		if(LOG && W){
			Log.w(tag, msg, tr);
		}
	}
}
