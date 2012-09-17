package com.dreamori.foodexpert;

import android.util.Log;

public final class DreamoriLog {
	
	static void LogFoodExpert(String msg)
	{
		Log.v(FoodConst.LOG_CAT, msg);		
	}

}
