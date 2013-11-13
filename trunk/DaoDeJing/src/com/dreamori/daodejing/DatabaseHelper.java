package com.dreamori.daodejing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import com.dreamori.daodejing.ParameterObject.TitleContent;

import SQLite3.Constants;
import SQLite3.Database;
import SQLite3.Exception;
import SQLite3.TableResult;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class DatabaseHelper {

	private static DatabaseHelper mInstance = null;
	static Database db = null;

	private static final String DATABASE_NAME = "data.db3";
	private static final String DATABASE_VERSION = "1.0.2";
	
	private static Context mContext = null; 
    
	private DatabaseHelper(Context context) {
	}
	
	
	static synchronized DatabaseHelper getInstance(Context context){
		if(mInstance == null )
		{
			mContext = context;
			mInstance = new DatabaseHelper(context);
					
	        //Init database   
	        InitDabaseFile(context, false);
	        openDatabase();
	        mInstance.checkDbVersion();
	        
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
	
	private void checkDbVersion()
	{
		try {
			if(!DATABASE_VERSION.equalsIgnoreCase(getConfig("version")))
			{
				db.close();
				InitDabaseFile(mContext, true);
				openDatabase();
			}
	
			} catch (Exception e) {
				e.printStackTrace();
		}
	}
	
	public static void closeDatabase()
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
	
	static void openDatabase()
	{
    	try 
    	{
    		db = new Database();
			db.open(mContext.getDatabasePath(DATABASE_NAME).getAbsolutePath(), Constants.SQLITE_OPEN_READWRITE);
	    	//String uri = (String) db.getdb()+"SQLITE_OPEN_READWRITE";
	        //db.key(uri);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
	}

	static private void InitDabaseFile(Context context, boolean forceReplace)
    {    	
        File dbFile = context.getDatabasePath(DATABASE_NAME);
		if (!dbFile.getParentFile().exists()) {
			dbFile.getParentFile().mkdirs();
		}
		
    	
    	String databaseFileName = dbFile.getAbsolutePath().toString();  
    	
        File targetFile = new File(databaseFileName);
        if (!targetFile.exists() || forceReplace)        
    	{
			try {
				targetFile.delete();
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
    

    public final static String TABLE_CONFIG = "config";
    public final static String CONFIG_LAST_IMAGE_INDEX = "sctp";
    public final static String CONFIG_SHOW_TOUCH_TIP = "xsbj";
    public String getConfig(String key)
    {
    	String ret = null;
    	TableResult tableResult = null;
		try {
			tableResult = db.get_table("select value from "+TABLE_CONFIG +" where key='"+key+"'");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		if( tableResult == null || tableResult.rows.isEmpty() )
			return null;

		ret = tableResult.rows.get(0)[0];

   		if( tableResult != null )
    	{
    		tableResult.clear();
    		tableResult = null;
    	}
   		
   		return ret;
    }
    
    public void setConfig(String key, String value)
    {
    	TableResult tableResult = null;
		try {
			tableResult = db.get_table("select value from "+TABLE_CONFIG
					+" where key='"+key+"'");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if( tableResult == null || tableResult.rows.isEmpty() )
		{
			try {
				tableResult = db.get_table("insert into "+TABLE_CONFIG
						+" values ('"+key+"','"+value+"')");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{		
			try {
				db.exec("update "+TABLE_CONFIG+" set value='"+value
						+"' where key='"+key+"'", null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
    
	public boolean NeedShowTouchTips() 
	{
		int res = 1;
		try 
		{
			res = Integer.parseInt(getConfig(CONFIG_SHOW_TOUCH_TIP));
		} 
		catch (java.lang.Exception e) 
		{
			e.toString();
		}
		if (res == 0) {
    		return false;
    	}
    	
		return true;
    }
    

    public int GetLastImageIndex( )
    {
    	int lastImageIndex = Const.m_minImageIndex;
    	try 
    	{
    		lastImageIndex = Integer.parseInt(getConfig(CONFIG_LAST_IMAGE_INDEX));
		} 
    	catch (java.lang.Exception e) 
    	{
			e.toString();
		}

		return lastImageIndex;
    }
    
    
    public void SetLastImageIndex( int index )
    {
		//Update food frequency
		setConfig(CONFIG_LAST_IMAGE_INDEX, ""+index);
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
			String sql = "select count(*) from "+ TABLE_BACKGROUND_CONTENT;
			tableResult = db.get_table(sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		if( tableResult == null || tableResult.rows.isEmpty() )
			return 0;
		
		backgroundImageCount = Integer.parseInt(tableResult.rows.get(0)[0]);
		
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
			String sql = "select "+ EXPLANATION_TITLE + " , " + EXPLANATION_CONTENT + " from "+ TABLE_EXPLANATION +" where " +  EXPLANATION_IMAGE_INDEX + " = " + index;
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
    
    
    public final static String TABLE_MUSIC = "pyxx";
    public final static String MUSIC_IMAGE_INDEX = "tpbh";
    public final static String MUSIC_NAME = "pymc";

    public String GetMusicName( int index )
    {
 	   	TableResult tableResult = null;

    	try {
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
    
    public Bitmap getBitmap(int index)
    {
    	Bitmap bmp = null;
    	TableResult tableResult = null;
    	
    	try {
			
            String sql = "select " + CONTENT + " from " + TABLE_PIC_CONTENT  + " where " +  CONTENT_ID + " = " + index;
			tableResult = db.get_table(sql);
			
			String str = tableResult.rows.get(0)[0];
			String png = str.substring(2, str.length()-1);
			
			char[] hexChars = png.toCharArray();
			int buflen = png.length()/2;
			byte[] buff = new byte[buflen];
			for (int i = 0; i < buflen; i++) {   
		        int pos = i * 2;   
		        buff[i] = (byte) ((byte) "0123456789ABCDEF".indexOf(hexChars[pos]) << 4 | (byte) "0123456789ABCDEF".indexOf(hexChars[pos + 1]));   
		    }		
			bmp = BitmapFactory.decodeByteArray(buff, 0, buflen);

	   		if( tableResult != null )
	    	{
	    		tableResult.clear();
	    		tableResult = null;
	    	}  

		} catch (Exception e) {
			e.printStackTrace();
			return bmp;
		}
    	
    	return bmp;
    }
    
}
