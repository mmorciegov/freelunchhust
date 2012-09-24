package com.dreamori.foodexpert;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

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
	
	public static BitmapDrawable GetBitmapDrawable(Context context, String name)
	{
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(), GetIcon(context, name));
		BitmapDrawable bd = new BitmapDrawable(context.getResources(),bm);
		
		DreamoriLog.LogFoodExpert("new BitmapDrawable in Resource Management");
		
		return bd;		
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
