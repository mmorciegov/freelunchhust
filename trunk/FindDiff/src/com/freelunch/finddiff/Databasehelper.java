package com.freelunch.finddiff;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Rect;
import android.util.Log;

public class Databasehelper extends SQLiteOpenHelper {
	private static Databasehelper mInstance = null;	
	private static SQLiteDatabase db = null;
	
	public static final String DATABASE_NAME = "photomanager.db3";	
	private static final int DATABASE_VERSION = 1;
	
	public Databasehelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	static private void copyBigDataBase(Context context, String databaseFileNameString ) throws IOException {  
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
	
	static private void InitDabaseFile(Context context)
    {
    	String databasePathString = context.getApplicationInfo().dataDir + "/databases";
    	File databaseFolderFile = new File(databasePathString);
    	if( !databaseFolderFile.exists() )
    	{
    		databaseFolderFile.mkdir();
    	}
    	
    	String databaseFileName = context.getDatabasePath(DATABASE_NAME).getAbsolutePath().toString();  
    	
        File targetFile = new File(databaseFileName);
    	if (!targetFile.exists())        
    	{
			try {
				targetFile.createNewFile();
				copyBigDataBase(context,databaseFileName);
				Log.v("Debug", "copyBigDataBase");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    } 
	
	static synchronized Databasehelper getInstance(Context context){
		if(mInstance == null )
		{
			mInstance = new Databasehelper(context);
	        //Init database   
	        InitDabaseFile(context);  			
	        db = SQLiteDatabase.openDatabase(context.getDatabasePath(DATABASE_NAME).getAbsolutePath().toString(), null, SQLiteDatabase.OPEN_READWRITE);
	        
	        if( db == null )
	        {
	            return null;
	        }
		}
		return mInstance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean GetPhotoInfo(int classinfo, int pos, PhotoInfo info)
	{
    	Cursor cursor = db.query("photoinfo", new String[]{"DISTINCT *"}, "class=? and position=?",
    			new String[]{String.valueOf(classinfo), String.valueOf(pos)}, null, null, null, null);
    	if( cursor != null && cursor.getCount() > 0 )
    	{
    		cursor.moveToFirst();        	
        	do{       
        		info.srcName = cursor.getString(0);
        		info.dstName = cursor.getString(1);
        		String diffinfo = cursor.getString(2);
        		String infos[] = diffinfo.split(",|;");
        		
        		info.diffList = new ArrayList<Rect>();
        		for (int i=0; i<infos.length; )
        		{
        			Rect rect = new Rect();
        			rect.left = Integer.parseInt(infos[i]);
        			rect.top = Integer.parseInt(infos[i+1]);
        			rect.right = Integer.parseInt(infos[i+2]);
        			rect.bottom = Integer.parseInt(infos[i+3]);
        			
        			info.diffList.add(rect);
        			i += 4;
        		}
        		return true;
        	} while( cursor.moveToNext() );
    	}    
		
    	return false;
	}
}

