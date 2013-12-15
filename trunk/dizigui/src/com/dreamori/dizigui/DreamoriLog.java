package com.dreamori.dizigui;

import android.util.Log;

public final class DreamoriLog {
	
	static void LogDiZiGui(String msg)
	{
		Log.v(DreamoriConst.LOG_CAT, msg);		
	}
	
	static void LogDiZiGui(String[] msg)
	{
		for(String str : msg)
		{
			LogDiZiGui(str);
		}	
	}

}
