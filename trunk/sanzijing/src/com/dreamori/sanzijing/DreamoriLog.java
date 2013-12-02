package com.dreamori.sanzijing;

import android.util.Log;

public final class DreamoriLog {
	
	static void LogSanZiJing(String msg)
	{
		Log.v(DreamoriConst.LOG_CAT, msg);		
	}
	
	static void LogSanZiJing(String[] msg)
	{
		for(String str : msg)
		{
			LogSanZiJing(str);
		}	
	}

}
