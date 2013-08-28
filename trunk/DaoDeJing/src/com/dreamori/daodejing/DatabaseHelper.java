package com.dreamori.daodejing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.sql.Ref;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.xml.sax.Parser;


import SQLite3.Blob;
import SQLite3.Constants;
import SQLite3.Database;
import SQLite3.Exception;
import SQLite3.TableResult;

import android.R.string;
import android.content.Context;
import android.database.Cursor;

public class DatabaseHelper {

	private static DatabaseHelper mInstance = null;
	static Database db = null;

	public static final String DATABASE_NAME = "data.db3";
    
	private DatabaseHelper(Context context) {
	}
	
	
	static synchronized DatabaseHelper getInstance(Context context){
		if(mInstance == null )
		{
			mInstance = new DatabaseHelper(context);
					
	        //Init database   
	        InitDabaseFile(context);
	        try {
	        	db = new Database();
	        	db.open(context.getDatabasePath(DATABASE_NAME).getAbsolutePath(), Constants.SQLITE_OPEN_READWRITE);
	        	
	        	//String uri = (String) db.getdb()+"SQLITE_OPEN_READWRITE";
		        //db.key(uri);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        if( db == null )
	        {
	            return null;
	        }
		}
		return mInstance;
	}
	

    
	public boolean deleteDatabase(Context context) {
		return context.deleteDatabase(DATABASE_NAME);
	}
	
	public void closeDatabase()
	{
		try {
			db.close();
			db = null;
			mInstance = null;
			
			DreamoriLog.LogFoodExpert("Close Data Base success.");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	static private void InitDabaseFile(Context context)
    {    	
        File dbFile = context.getDatabasePath(DATABASE_NAME);
		if (!dbFile.getParentFile().exists()) {
			dbFile.getParentFile().mkdirs();
		}
		
    	
    	String databaseFileName = dbFile.getAbsolutePath().toString();  
    	
        File targetFile = new File(databaseFileName);
        if (!targetFile.exists())        
    	{
			try {
				targetFile.createNewFile();
				copyBigDataBase(context,databaseFileName);
				DreamoriLog.LogFoodExpert("copyBigDataBase");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	static private void copyBigDataBase(Context context,
			String databaseFileNameString) throws IOException {
		InputStream myInput;
		OutputStream myOutput = new FileOutputStream(databaseFileNameString);

		myInput = context.getAssets().open(DATABASE_NAME);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		myOutput.flush();
		myOutput.close();
        myInput.close();  
    }  
	
	
	//Functions for tables	
    public final static String TABLE_PIC_CONTENT = "wztp";
    public final static String CONTENT_ID = "bh";
    public final static String CONTENT = "tp";
    public final static String CONTENT_NAME = "tpmc";
    public final static String CONTENT_COMMENT = "bz";

    public String GetImageContentName( int index )
    {
 	   	TableResult tableResult = null;

    	try {
			//String sql = "select HEX("+ CONTENT + ") from wztp where " +  CONTENT_ID + " = " + index;
			String sql = "select "+ CONTENT_NAME + " from wztp where " +  CONTENT_ID + " = " + index;
			tableResult = db.get_table(sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		if( tableResult == null || tableResult.rows.isEmpty() )
			return null;
		
		String result = tableResult.rows.get(0)[0];
		//byte[] data =  getBytes( charData );;
		
   		if( tableResult != null )
    	{
    		tableResult.clear();
    		tableResult = null;
    	}   		
    	
		return result;
    }
    
    public final static String TABLE_HISTORY = "config";
    public final static String CONTENT_LAST_IMAGE_INDEX = "sctp";
    public final static String CONTENT_CONFIG_IMAGE_WIDTH = "tpkd";
    public final static String CONTENT_CONFIG_IMAGE_HEIGHT = "tpcd";

    public int GetLastImageIndex( )
    {
    	int lastImageIndex = Const.m_minImageIndex;
 	   	TableResult tableResult = null;

    	try {
			//String sql = "select HEX("+ CONTENT + ") from wztp where " +  CONTENT_ID + " = " + index;
			String sql = "select "+ CONTENT_LAST_IMAGE_INDEX + " from "+TABLE_HISTORY;
			tableResult = db.get_table(sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		if( tableResult == null || tableResult.rows.isEmpty() )
			return lastImageIndex;
		
		String result = tableResult.rows.get(0)[0];
   		lastImageIndex = Integer.parseInt(result);
		
		if( tableResult != null )
    	{
    		tableResult.clear();
    		tableResult = null;
    	}   		
		return lastImageIndex;
    }
    
    
    public void SetLastImageIndex( int index )
    {
		//Update food frequency
		try {
			db.exec("update "+TABLE_HISTORY+" set "+CONTENT_LAST_IMAGE_INDEX+"="+index, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
    } 
       

    public void GeImageWidthAndHeight( ObjectParameter.Size size  )
    {
    	size.width = 0;
    	size.height = 0;
 	   	TableResult tableResult = null;

    	try {
			//String sql = "select HEX("+ CONTENT + ") from wztp where " +  CONTENT_ID + " = " + index;
			String sql = "select "+ CONTENT_CONFIG_IMAGE_WIDTH + "," + CONTENT_CONFIG_IMAGE_HEIGHT + " from "+TABLE_HISTORY;
			tableResult = db.get_table(sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		if( tableResult == null || tableResult.rows.isEmpty() )
			return ;
		
		size.width = Integer.parseInt(tableResult.rows.get(0)[0]);
		size.height = Integer.parseInt(tableResult.rows.get(0)[1]);
		
		if( tableResult != null )
    	{
    		tableResult.clear();
    		tableResult = null;
    	}   		
    	   		
		return;
    }
    
    public final static String TABLE_HOTSPOT = "cmqy";
    public final static String CONTENT_IMAGE_INDEX = "tpbh";
    public final static String CONTENT_HOTSPOT_INDEX = "qybh";
    public final static String CONTENT_HOTSPOT_LEFT_TOP_X = "zsx";
    public final static String CONTENT_HOTSPOT_LEFT_TOP_Y = "zsy";
    public final static String CONTENT_HOTSPOT_RIGHT_BOTTOM_X = "yxx";
    public final static String CONTENT_HOTSPOT_RIGHT_BOTTOM_Y = "yxy";
    public final static String CONTENT_HOTSPOT_CONTENT = "jsnr";
    
    private boolean initHotspot = false;
    
    public void GetImageHotSpot( int imageIndex, ArrayList<Hotspot> hotspots)
    {
    	if( !initHotspot )
    	{
    		initHotspot = true;
    		
    		ObjectParameter.Size size = new ObjectParameter.Size();
    		GeImageWidthAndHeight( size );
    		
    		Hotspot.InitOrignalImageSize( size.width, size.height );
    	}   	
    	
 	   	TableResult tableResult = null;

    	try {
			String sql = "select "+ CONTENT_HOTSPOT_INDEX + " , " + CONTENT_HOTSPOT_LEFT_TOP_X +
					" , " + CONTENT_HOTSPOT_LEFT_TOP_Y +
					" , " + CONTENT_HOTSPOT_RIGHT_BOTTOM_X +
					" , " + CONTENT_HOTSPOT_RIGHT_BOTTOM_Y +	
					" , " + CONTENT_HOTSPOT_CONTENT +	
					" from "+ TABLE_HOTSPOT + " where " +  CONTENT_IMAGE_INDEX + " = " + imageIndex;
			tableResult = db.get_table(sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		if( tableResult == null || tableResult.rows.isEmpty() )
			return ;
		
		Hotspot.imgIndex = imageIndex;
		
		for (String[] tableRow : tableResult.rows) {
			
			Hotspot hotspot = new Hotspot();							
			hotspot.hotspotIndex = Integer.parseInt(tableRow[0]);
			hotspot.posRect.left = Integer.parseInt(tableRow[1]);
			hotspot.posRect.top = Integer.parseInt(tableRow[2]);
			hotspot.posRect.right = Integer.parseInt(tableRow[3]);
			hotspot.posRect.bottom = Integer.parseInt(tableRow[4]);
			hotspot.contentString = tableRow[5];
			
			hotspots.add(hotspot);			
		}

		
   		if( tableResult != null )
    	{
    		tableResult.clear();
    		tableResult = null;
    	}   		
    	
		return;
    }
}
