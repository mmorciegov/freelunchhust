package com.dreamori.foodexpert;

public class ConfigData {
	public int tip;
	
	public ConfigData()
	{
		tip = 1;
	}
	
	public static int GetSystemTip(String filename)
	{
    	ConfigData configData = new ConfigData();
    	XmlOperation.ReadXML(filename, configData);	
    	
    	return configData.tip;
	}
}
