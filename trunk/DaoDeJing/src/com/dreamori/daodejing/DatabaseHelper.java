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
import com.dreamori.daodejing.ParameterObject.TitleContent;



import SQLite3.Blob;
import SQLite3.Constants;
import SQLite3.Database;
import SQLite3.Exception;
import SQLite3.TableResult;

import android.R.string;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Rect;

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
			
			DreamoriLog.LogDaoDeJing("Close Data Base success.");
			
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
				DreamoriLog.LogDaoDeJing("copyBigDataBase");
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
    public final static String CONTENT_IMAGE_WIDTH = "tpkd";
    public final static String CONTENT_IMAGE_HEIGHT = "tpgd";
    public final static String CONTENT_COMMENT = "bz";

    public void GeImageWidthAndHeight( int index, ParameterObject.Size size  )
    {
    	size.width = 0;
    	size.height = 0;
 	   	TableResult tableResult = null; 	   	

 	   	try {
			//String sql = "select HEX("+ CONTENT + ") from wztp where " +  CONTENT_ID + " = " + index;
			String sql = "select "+ CONTENT_IMAGE_WIDTH + "," + CONTENT_IMAGE_HEIGHT + " from "+TABLE_PIC_CONTENT  +" where " +  CONTENT_ID + " = " + index;
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
    
    public String GetImageContentName( int index )
    {
 	   	TableResult tableResult = null;

    	try {
			//String sql = "select HEX("+ CONTENT + ") from wztp where " +  CONTENT_ID + " = " + index;
			String sql = "select "+ CONTENT_NAME + " from "+ TABLE_PIC_CONTENT +" where " +  CONTENT_ID + " = " + index;
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
    public final static String HISTORY_LAST_IMAGE_INDEX = "sctp";
    public final static String HISTORY_CONFIG_SHOW_TOUCH_TIP = "xsbj";
    
    public boolean NeedShowTouchTips( )
    {
    	boolean showTouchTips = true;
 	   	TableResult tableResult = null;

    	try {
			//String sql = "select HEX("+ CONTENT + ") from wztp where " +  CONTENT_ID + " = " + index;
			String sql = "select "+ HISTORY_CONFIG_SHOW_TOUCH_TIP + " from "+TABLE_HISTORY;
			tableResult = db.get_table(sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		if( tableResult == null || tableResult.rows.isEmpty() )
			return showTouchTips;
		
		String result = tableResult.rows.get(0)[0];
		if (result.compareTo("1") == 0)
		{
			showTouchTips = true;
		}
		else {
			showTouchTips = false;
		}

		if( tableResult != null )
    	{
    		tableResult.clear();
    		tableResult = null;
    	}   		
		return showTouchTips;
    }
    

    public int GetLastImageIndex( )
    {
    	int lastImageIndex = Const.m_minImageIndex;
 	   	TableResult tableResult = null;

    	try {
			//String sql = "select HEX("+ CONTENT + ") from wztp where " +  CONTENT_ID + " = " + index;
			String sql = "select "+ HISTORY_LAST_IMAGE_INDEX + " from "+TABLE_HISTORY;
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
			db.exec("update "+TABLE_HISTORY+" set "+HISTORY_LAST_IMAGE_INDEX+"="+index, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
    }        
    
    public final static String TABLE_HOTSPOT = "cmqy";
    public final static String HOTSPOT_IMAGE_INDEX = "tpbh";
    public final static String HOTSPOT_HOTSPOT_INDEX = "qybh";
    public final static String HOTSPOT_HOTSPOT_LEFT_TOP_X = "zsx";
    public final static String HOTSPOT_HOTSPOT_LEFT_TOP_Y = "zsy";
    public final static String HOTSPOT_HOTSPOT_RIGHT_BOTTOM_X = "yxx";
    public final static String HOTSPOT_HOTSPOT_RIGHT_BOTTOM_Y = "yxy";
        
    public void GetImageHotSpot( int imageIndex, ArrayList<Hotspot> hotspots)
    {
    	//TODO:Add width/height for every picture.
    	ParameterObject.Size size = new ParameterObject.Size();
    	GeImageWidthAndHeight( imageIndex, size );
    	
    	Hotspot.SetOrignalImageSize( new Rect(0, 0, size.width, size.height) );
	
    	
 	   	TableResult tableResult = null;

    	try {
			String sql = "select "+ HOTSPOT_HOTSPOT_INDEX + " , " + HOTSPOT_HOTSPOT_LEFT_TOP_X +
					" , " + HOTSPOT_HOTSPOT_LEFT_TOP_Y +
					" , " + HOTSPOT_HOTSPOT_RIGHT_BOTTOM_X +
					" , " + HOTSPOT_HOTSPOT_RIGHT_BOTTOM_Y +	
					" from "+ TABLE_HOTSPOT + " where " +  HOTSPOT_IMAGE_INDEX + " = " + imageIndex;
			tableResult = db.get_table(sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		if( tableResult == null || tableResult.rows.isEmpty() )
			return ;
		
		Hotspot.imgIndex = imageIndex;
		TitleContent titleContent = new TitleContent();
		
		for (String[] tableRow : tableResult.rows) {
			
			Hotspot hotspot = new Hotspot();							
			hotspot.hotspotIndex = Integer.parseInt(tableRow[0]);
			hotspot.posRect.left = Integer.parseInt(tableRow[1]);
			hotspot.posRect.top = Integer.parseInt(tableRow[2]); 
			hotspot.posRect.right = Integer.parseInt(tableRow[3]);
			hotspot.posRect.bottom = Integer.parseInt(tableRow[4]);
			if( GetHotspotExplanation(imageIndex, hotspot.hotspotIndex, titleContent) )
			{
				hotspot.contentString = titleContent.content;
				hotspot.titleString = titleContent.title;
			}
			
			
			hotspots.add(hotspot);			
		}
		
   		if( tableResult != null )
    	{
    		tableResult.clear();
    		tableResult = null;
    	}   		
    	
		return;
    }    

    public final static String TABLE_HOTSPOT_CONTENT = "cmnr";
    public final static String HOTSPOT_CONTENT_IMAGE_INDEX = "tpbh";
    public final static String HOTSPOT_CONTENT_HOTSPOT_INDEX = "qybh";
    public final static String HOTSPOT_CONTENT_TITLE = "jsbt";
    public final static String HOTSPOT_CONTENT_EXPLANAITION= "jsnr";
    
    public boolean GetHotspotExplanation( int imageIndex, int hotspotIndex, TitleContent titleContent)
    {   	
 	   	TableResult tableResult = null;

    	try {
			String sql = "select "+ HOTSPOT_CONTENT_TITLE + " , " +HOTSPOT_CONTENT_EXPLANAITION + 
					" from "+ TABLE_HOTSPOT_CONTENT + " where " +  HOTSPOT_CONTENT_IMAGE_INDEX + " = " + imageIndex 
					+ " and " + HOTSPOT_CONTENT_HOTSPOT_INDEX + " = " + hotspotIndex ;
			tableResult = db.get_table(sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		if( tableResult == null || tableResult.rows.isEmpty() )
			return false;
		
		titleContent.title = tableResult.rows.get(0)[0];
		titleContent.content = tableResult.rows.get(0)[1];
		
   		if( tableResult != null )
    	{
    		tableResult.clear();
    		tableResult = null;
    	}   	
   		
   		return true;
    	
    }
    
    public final static String TABLE_BACKGROUND_CONTENT = "bjtp";
    public final static String BACKGROUND_IMAGE_INDEX = "tpbh";
    public final static String BACKGROUND_IMAGE_NAME = "tpmc";

    public String GetBackgroundImageContentName( int index )
    {
 	   	TableResult tableResult = null;

    	try {
			//String sql = "select HEX("+ CONTENT + ") from wztp where " +  CONTENT_ID + " = " + index;
			String sql = "select "+ BACKGROUND_IMAGE_NAME + " from "+ TABLE_BACKGROUND_CONTENT +" where " +  BACKGROUND_IMAGE_INDEX + " = " + index;
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
    

    public int GetBackgroundImageCount( )
    {
    	int backgroundImageCount = 0;
 	   	TableResult tableResult = null;

    	try {
			//String sql = "select HEX("+ CONTENT + ") from wztp where " +  CONTENT_ID + " = " + index;
			String sql = "select count(*) from "+ TABLE_BACKGROUND_CONTENT;
			tableResult = db.get_table(sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		if( tableResult == null || tableResult.rows.isEmpty() )
			return 0;
		
		backgroundImageCount = Integer.parseInt(tableResult.rows.get(0)[0]);
		//byte[] data =  getBytes( charData );;
		
   		if( tableResult != null )
    	{
    		tableResult.clear();
    		tableResult = null;
    	}   		
    	
		return backgroundImageCount;
    }
    
    public final static String TABLE_EXPLANATION = "qwsy";
    public final static String EXPLANATION_IMAGE_INDEX = "tpbh";
    public final static String EXPLANATION_TITLE = "bt";
    public final static String EXPLANATION_CONTENT = "qwsy";

    public boolean GetExplanationContent( int index, TitleContent titleContent )
    {
 	   	TableResult tableResult = null;

    	try {
			//String sql = "select HEX("+ CONTENT + ") from wztp where " +  CONTENT_ID + " = " + index;
			String sql = "select "+ EXPLANATION_TITLE + " , " + EXPLANATION_CONTENT + " from "+ TABLE_EXPLANATION +" where " +  EXPLANATION_IMAGE_INDEX + " = " + index;
			tableResult = db.get_table(sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		if( tableResult == null || tableResult.rows.isEmpty() )
			return false;
		
		titleContent.title = tableResult.rows.get(0)[0];
		titleContent.content = tableResult.rows.get(0)[1];
		//byte[] data =  getBytes( charData );;
		
   		if( tableResult != null )
    	{
    		tableResult.clear();
    		tableResult = null;
    	}   		
    	
		return true;
    }
    
    
    public final static String TABLE_MUSIC = "pyxx";
    public final static String MUSIC_IMAGE_INDEX = "tpbh";
    public final static String MUSIC_NAME = "pymc";

    public String GetMusicName( int index )
    {
 	   	TableResult tableResult = null;

    	try {
			//String sql = "select HEX("+ CONTENT + ") from wztp where " +  CONTENT_ID + " = " + index;
			String sql = "select "+ MUSIC_NAME + " from "+ TABLE_MUSIC +" where " +  MUSIC_IMAGE_INDEX + " = " + index;
			tableResult = db.get_table(sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		if( tableResult == null || tableResult.rows.isEmpty() )
			return null;
		
		String result = tableResult.rows.get(0)[0];
		
   		if( tableResult != null )
    	{
    		tableResult.clear();
    		tableResult = null;
    	}   		
    	
		return result;
    }
    
    
    
}
