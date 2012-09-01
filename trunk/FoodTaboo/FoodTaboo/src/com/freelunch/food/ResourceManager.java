package com.freelunch.food;

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
	
	public static int GetIcon(String name)
	{
		int id = 0;
		if (name.equalsIgnoreCase("»Æ¹Ï"))
		{
			id = R.drawable.food;
		}		
		else if (name.equalsIgnoreCase("°×ÂÜ²·"))
		{
			id = R.drawable.food;
		}
		else
		{
			id = R.drawable.food;
		}
		
		return id;
	}
}
