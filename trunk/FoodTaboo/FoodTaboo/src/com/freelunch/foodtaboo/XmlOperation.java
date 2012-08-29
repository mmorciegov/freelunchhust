package com.freelunch.foodtaboo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

public class XmlOperation {
	public static void ReadXML(String filename, List<PriData> dataList)
	{
    	File targetFile = new File(filename);
    	
    	if (targetFile == null || !targetFile.exists())
    	{
//    		Log.v("debug", "xml is not exist.");
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
    		
//    		Log.v("debug", String.valueOf(topNodeList.getLength()));
    		
    		for(int i=0; i<nodeList.getLength(); i++)
    		{
				Node node = nodeList.item(i);
				Element elem = (Element)nodeList.item(i);
				
				PriData data = new PriData();
				data.srcName = elem.getAttribute("src");
				data.dstName = elem.getAttribute("dst");
				String degree = elem.getAttribute("degree");
				data.degree = Integer.parseInt(degree);
				data.hint = elem.getAttribute("hint");
				
				dataList.add(data);
    		}
    	}catch (IOException e) {
    	} catch (SAXException e) {
    	} catch (ParserConfigurationException e) {
    	} finally {
    		doc = null;
    		docBuilder = null;
    		docBuilderFactory = null;
    	}
	}
	
    private static String Data2XmlString(List<PriData> dataList)
    {
    	XmlSerializer serializer = Xml.newSerializer();
    	StringWriter writer = new StringWriter();
    	try{
    		serializer.setOutput(writer);
    		serializer.startDocument("UTF-8",true);
    		
    		serializer.startTag("","Food");
    		
    		for (int i=0; i<dataList.size(); i++)
    		{
        		serializer.startTag("","Data");
        		serializer.attribute("","src", dataList.get(i).srcName);
        		serializer.attribute("","dst", dataList.get(i).dstName);
        		serializer.attribute("","degree", String.valueOf(dataList.get(i).degree));
        		serializer.attribute("","hint", dataList.get(i).hint);
        		serializer.endTag("","Data");
    		}

    		serializer.endTag("","Food");	
    		serializer.endDocument();
    		
    		return writer.toString();
    	}
    	catch(Exception e)
    	{
    		throw new RuntimeException(e);
    	}
    }    
    
    public static boolean WriteXml(String filename, List<PriData> dataList)
    {
    	File file = new File(filename);
    	String txt = Data2XmlString(dataList);
    	
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
