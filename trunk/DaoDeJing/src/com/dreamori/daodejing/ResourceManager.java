package com.dreamori.daodejing;


import android.content.Context;

public class ResourceManager {
	
	public static int GetBackgroundIcon(Context context, String name)
	{
		int id = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
		if( id == 0 )
		{
			id = R.drawable.background;
		}		
		
		return id;
	}
	
	public static int GetMusicId(Context context, String name)
	{
		int id = context.getResources().getIdentifier(name, "raw", context.getPackageName());
		if( id == 0 )
		{
			id = R.raw.m01;
		}		
		
		return id;
	}
}
