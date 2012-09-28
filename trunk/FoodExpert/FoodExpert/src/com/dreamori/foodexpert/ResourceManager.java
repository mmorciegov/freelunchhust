package com.dreamori.foodexpert;


import android.content.Context;

public class ResourceManager {
	
	public static String GetDegreeIconName( int degree )
	{
		String degreeIconName = "g1";
		switch ( degree ) {
		case 1:
			degreeIconName = "g1";			
			break;

		case 2:
			degreeIconName = "g2";			
			break;
			
		case 3:
			degreeIconName = "g3";			
			break;
			
			
		case -1:
			degreeIconName = "b1";			
			break;

		case -2:
			degreeIconName = "b2";			
			break;
			
		case -3:
			degreeIconName = "b3";		
			
		default:
			break;
		}		
		
		return degreeIconName;
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
