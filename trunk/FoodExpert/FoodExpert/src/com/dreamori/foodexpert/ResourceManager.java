package com.dreamori.foodexpert;

import android.content.Context;

public class ResourceManager {
	public static int GetDegreeId(int degree)
	{
		int id = 0;
		switch(degree)
		{
		case 1:
			id = R.drawable.good_1;
			break;
		case 2:
			id = R.drawable.good_2;
			break;
		case 3:
			id = R.drawable.good_3;
			break;
		case -1:
			id = R.drawable.bad_1;
			break;
		case -2:
			id = R.drawable.bad_2;
			break;			
		case -3:
			id = R.drawable.bad_3;
			break;
		default:
			break;
		}
		
		return id;
	}
	
	public static int GetIcon(Context context, String name)
	{
		int id = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
		if( id == 0 )
		{
			id = R.drawable.food;
		}		
		
		return id;
	}
}
