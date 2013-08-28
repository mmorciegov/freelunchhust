package com.dreamori.daodejing;


import android.content.Context;

public class ResourceManager {
	
	public static int GetIcon(Context context, String name)
	{
		int id = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
		if( id == 0 )
		{
			id = R.drawable.p00000;
		}		
		
		return id;
	}
}
