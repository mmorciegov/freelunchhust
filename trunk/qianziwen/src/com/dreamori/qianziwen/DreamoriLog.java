package com.dreamori.qianziwen;

import android.util.Log;

public final class DreamoriLog {
	
	static void LogQianZiWen(String msg)
	{
		Log.v(DreamoriConst.LOG_CAT, msg);		
	}
	
	static void LogQianZiWen(String[] msg)
	{
		for(String str : msg)
		{
			LogQianZiWen(str);
		}	
	}

}
