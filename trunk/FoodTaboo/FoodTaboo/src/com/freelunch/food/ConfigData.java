package com.freelunch.food;

public class ConfigData {
	public int tip;
	// 0: gridview
	// 1: listview
	public int display;
	
	public ConfigData()
	{
		tip = 1;
		display = 0;
	}
	
	public static int GetSystemTip(String filename)
	{
    	ConfigData configData = new ConfigData();
    	XmlOperation.ReadXML(filename, configData);	
    	
    	return configData.tip;
	}
	
	public static int GetSystemDisplay(String filename)
	{
    	ConfigData configData = new ConfigData();
    	XmlOperation.ReadXML(filename, configData);	
    	
    	return configData.display;
	}
}
