package com.dreamori.foodexpert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

public class XmlOperation {
	public static void ReadXML(String filename, ConfigData data)
	{
    	File targetFile = new File(filename);
    	
    	if (targetFile == null || !targetFile.exists())
    	{
    		return;
    	}
    	
    	DocumentBuilderFactory docBuilderFactory = null;
    	DocumentBuilder docBuilder = null;
    	Document doc = null;
    	
    	try {
    		docBuilderFactory = DocumentBuilderFactory.newInstance();
    		docBuilder = docBuilderFactory.newDocumentBuilder();

    		doc = docBuilder.parse(targetFile);
    		Element root = doc.getDocumentElement();
    		NodeList nodeList = root.getChildNodes();
    		
			Element elem = (Element)nodeList.item(0);
			
			data.tip = Integer.parseInt(elem.getAttribute("tip"));
			data.display = Integer.parseInt(elem.getAttribute("display"));
				
    	}catch (IOException e) {
    	} catch (SAXException e) {
    	} catch (ParserConfigurationException e) {
    	} finally {
    		doc = null;
    		docBuilder = null;
    		docBuilderFactory = null;
    	}
	}
	
    private static String Data2XmlString(ConfigData data)
    {
    	XmlSerializer serializer = Xml.newSerializer();
    	StringWriter writer = new StringWriter();
    	try{
    		serializer.setOutput(writer);
    		serializer.startDocument("UTF-8",true);
    		
    		serializer.startTag("","Food");
    		
    		serializer.startTag("","Data");
    		serializer.attribute("","tip", String.valueOf(data.tip));
    		serializer.attribute("","display", String.valueOf(data.display));
    		serializer.endTag("","Data");

    		serializer.endTag("","Food");	
    		serializer.endDocument();
    		
    		return writer.toString();
    	}
    	catch(Exception e)
    	{
    		throw new RuntimeException(e);
    	}
    } 
    
    public static boolean WriteXml(String filename, ConfigData data)
    {
    	File file = new File(filename);
    	String txt = Data2XmlString(data);
    	
    	try
    	{
    		file.createNewFile();
    		FileOutputStream os = new FileOutputStream(filename);
    		os.write(txt.getBytes());
    		os.close();	
    	}
    	catch(FileNotFoundException e)
    	{
    		return false;
    	}
    	catch(IOException e)
    	{
    		return false;
    	}
    	
    	return true;
    }         
}
