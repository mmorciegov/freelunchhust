package com.dreamori.sanzijing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import com.dreamori.sanzijing.ParameterObject.TitleContent;

import SQLite3.Constants;
import SQLite3.Database;
import SQLite3.Exception;
import SQLite3.TableResult;

import android.content.Context;
import android.graphics.Rect;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseHelper {
	
	private static DatabaseHelper mInstance = null;
	static Database db = null;

	public static final String DATABASE_NAME = "data.db3";
	private static final String DATABASE_VERSION = "1.0.0";

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
			
			DreamoriLog.LogSanZiJing("Close Data Base success.");
			
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
				DreamoriLog.LogSanZiJing("copyBigDataBase");
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
	public final static String TABLE_TEXT_CONTENT = "wzxx";
	public final static String TEXT_ID = "id";
	public final static String TEXT_CAPTER_ID = "bhdx";
	public final static String TEXT_CONTENT = "wznr";
	public final static String TEXT_SPELL = "wzpy";
	public final static String TEXT_EXPLANATION = "wzjs";
	private final int TextSpellCount = 24;
	
	
	public void GetCapterIdName( int inputId, String captureId )
	{
	   	TableResult tableResult = null; 	   	

 	   	try {
			String sql = "select "+ TEXT_CAPTER_ID  + " from " + TABLE_TEXT_CONTENT  +" where " +  TEXT_ID + " = " + inputId;
			tableResult = db.get_table(sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	if( tableResult == null || tableResult.rows.isEmpty() )
			return ;
    	
    	captureId = tableResult.rows.get(0)[0];
		
		if( tableResult != null )
    	{
    		tableResult.clear();
    		tableResult = null;
    	}   		
    	   		
		return;
	}

	private String replaceBlack(String str)
	{
		String dest = "";
		if( str != null )
		{
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		
		return dest;
	}
	
	private String replaceBlackWithSingleBlack(String str)
	{
		String dest = "";
		if( str != null )
		{
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll(" ");
			dest = dest.replace("  ", "|");
			dest = dest.replace(" ", "");	
			dest = dest.replace("|", " ");
		}		

		return dest;
	}

	public int GetTextContent( int inputId, String[] textContent )
	{
	   	TableResult tableResult = null; 	   	

 	   	try {
	
 	   		String sql = "select "+ TEXT_CONTENT + " , " + TEXT_SPELL   + " from "+TABLE_TEXT_CONTENT  +" where " +  TEXT_ID + " = " + inputId;
			
			tableResult = db.get_table(sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	if( tableResult == null || tableResult.rows.isEmpty() )
			return 0;

    	
    	String textResult = replaceBlack(tableResult.rows.get(0)[0]);
    	String spellResult = tableResult.rows.get(0)[1];
    	
    	String [] spellStrings = ( replaceBlackWithSingleBlack(spellResult) ).split(" ");
    	
    	int outputCount = spellStrings.length;

    	if( outputCount > TextSpellCount )
    	{
    		outputCount = TextSpellCount;
    	}
    	
    	if( outputCount % 6 != 0 )
    	{
    		outputCount = ( (outputCount + 1)/6 ) * 6 ;
    	}
    	
    	for( int k = 0; k < outputCount/6; k ++ )
    	{
    		for( int s = 0; s< 6; s++ )
    		{
    			textContent[ 6*2*k + s ] = (spellStrings[ 6*k  + s ]).trim();
    			textContent[ 6*(2*k+1) + s] = textResult.substring(6*k + s, 6*k+ s + 1);
    		}
    	}
    	
		if( tableResult != null )
    	{
    		tableResult.clear();
    		tableResult = null;
    	}   		
    	   		
		return outputCount;
	}
	
	public String GetContentIndexString( int inputId )
	{
		String result = "";
	   	TableResult tableResult = null;

    	try {
			String sql = "select "+ TEXT_CAPTER_ID + " from "+TABLE_TEXT_CONTENT  +" where " +  TEXT_ID + " = " + inputId;
			tableResult = db.get_table(sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		if( tableResult == null || tableResult.rows.isEmpty() )
			return result;
		
		result = tableResult.rows.get(0)[0];
		
   		if( tableResult != null )
    	{
    		tableResult.clear();
    		tableResult = null;
    	}   		
    	
		return result;	
	}

    public boolean GetExplanationContent( int inputId, TitleContent titleContent )
    {
 	   	TableResult tableResult = null;

    	try {
			String sql = "select "+ TEXT_CONTENT + " , " + TEXT_EXPLANATION + " from "+TABLE_TEXT_CONTENT  +" where " +  TEXT_ID + " = " + inputId;
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


    public final static String TABLE_CONFIG = "config";
    public final static String CONFIG_LAST_IMAGE_INDEX = "sctp";
    public final static String CONFIG_SHOW_TOUCH_TIP = "xsbj";
    public String getConfig(String key)
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
			return null;

		String ret = tableResult.rows.get(0)[0];

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
		setConfig(CONFIG_LAST_IMAGE_INDEX, ""+index);
    }        
    
    public final static String TABLE_BACKGROUND_CONTENT = "bjtp";
    public final static String BACKGROUND_IMAGE_INDEX = "tpbh";
    public final static String BACKGROUND_IMAGE_NAME = "tpmc";

    public String GetBackgroundImageContentName( int index )
    {
 	   	TableResult tableResult = null;

    	try {
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
    
    
    
}
