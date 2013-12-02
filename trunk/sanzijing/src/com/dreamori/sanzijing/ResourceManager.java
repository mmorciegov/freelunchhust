package com.dreamori.sanzijing;


import android.content.Context;

public class ResourceManager {
	
	public static int GetIcon(Context context, String name)
	{
		int id = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
		if( id == 0 )
		{
			//TODO
			//id = R.drawable.p100001;
		}		
		
		return id;
	}
	
	public static int GetTextViewId(Context context, String name)
	{
		int id = context.getResources().getIdentifier(name, "id", context.getPackageName());
		if( id == 0 )
		{
			id = R.id.text11;
		}	
		
		return id;
	}
	
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
			//TODO
			//id = R.raw.m01;
		}		
		
		return id;
	}
}
