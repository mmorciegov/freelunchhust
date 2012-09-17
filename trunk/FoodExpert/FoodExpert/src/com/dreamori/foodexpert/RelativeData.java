package com.dreamori.foodexpert;

public class RelativeData {
	public RelativeData(){}
	
	public RelativeData(String name )
	{
		this.name = name;
		degree = 0;
		hint = "";		
	}
	
	public String name;
	// degree > 0 Good
	// degree < 0 Bad
	public int degree;
	public String hint;
}
