package com.freelunch.foodtaboo;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Databasehelper extends SQLiteOpenHelper {

	private static Databasehelper mInstance = null;
	
	static SQLiteDatabase db = null;
	
	public static final String DATABASE_NAME = "food.db";
	
	private static final int DATABASE_VERSION = 1;
	
	//Food conflict table
	private static final String TABLE_CREATE_FOOD_CONFLICT = "create table swct("
			+ "_id INTEGER PRIMARY KEY AUTOINCREMENT," + "sw1 TEXT," + "sw2 TEXT,"
			+ "ctjb INTEGER DEFAULT 3," + "ctyy TEXT);";
	
	//Food Info table
	private static final String TABLE_CREATE_FOOD_INFO = "create table swxx("
			+"_id INTEGER PRIMARY KEY AUTOINCREMENT," + "swmz TEXT," + "swjs TEXT,"
			+ "swtp TEXT," + "djcs INTEGER DEFAULT 0);";
	
	public Databasehelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	static synchronized Databasehelper getInstance(Context context){
		if(mInstance == null )
		{
			mInstance = new Databasehelper(context);
			
	        db = SQLiteDatabase.openDatabase(context.getDatabasePath(DATABASE_NAME).getAbsolutePath().toString(), null, SQLiteDatabase.OPEN_READWRITE);
	        
	        if( db == null )
	        {
	            db = mInstance.getReadableDatabase();
	            initDatabase();
	        }
		}
		return mInstance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE_FOOD_CONFLICT);
		db.execSQL(TABLE_CREATE_FOOD_INFO);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean deleteDatabase(Context context) {
		return context.deleteDatabase(DATABASE_NAME);
	}	
	
	// Database operator	   
    public final static String TABLE_CONFLICT = "swct";
    public final static String ID = "_id";
    public final static String FOOD_FIRST = "sw1";
    public final static String FOOD_SECOND = "sw2";
    public final static String LEVEL = "ctjb";
    public final static String REASON = "ctyy";

    public final static String TABLE_FOOD_INFO = "swxx";
    public final static String FOOD_NAME = "swmz";
    
    private static List<PriData> foodConflictData = null;
    private static ArrayList<String> foodList = null;
    
    public List<PriData> GetPrivateData( )
    {
    	if( foodConflictData == null )
    	{
    		foodConflictData = new ArrayList<PriData>();
    		
        	Cursor cursor = db.query(TABLE_CONFLICT, null, null,
        			null, null, null, null, null);
        	if( cursor.getCount() > 0 )
        	{
        		cursor.moveToFirst();        		
        	}
        	
        	do{       
        		
        	PriData pridata = new PriData();
        	pridata.srcName = cursor.getString(1);
        	pridata.dstName = cursor.getString(2);
        	pridata.degree = cursor.getInt(3);
        	pridata.hint = cursor.getString(4);
        	
        	foodConflictData.add(pridata);
        	
        	} while( cursor.moveToNext() );
    	}
    	
    	return foodConflictData;
    }
    
    public List<String> getFoodList()
    {
     	if( foodList == null )
    	{
     		foodList = new ArrayList<String>();
    		
        	Cursor cursor = db.query(TABLE_FOOD_INFO, new String[]{FOOD_NAME}, null,
        			null, null, null, null, null);
        	if( cursor.getCount() > 0 )
        	{
        		cursor.moveToFirst();        		
        	}
        	
        	do{       
        	
        		foodList.add(cursor.getString(0));
        	
        	} while( cursor.moveToNext() );
    	}
    	
    	return foodList;
    }
    
    public boolean findConflictedFood(String foodName, List<PriData> dataList)
    {
    	Cursor cursor = db.query(TABLE_CONFLICT, new String[]{"DISTINCT *"}, FOOD_FIRST+"=?"+" or "+ FOOD_SECOND + "=?",
    			new String[]{foodName, foodName}, null, null, LEVEL+" DESC", null);
    	
    	if (cursor == null || cursor.getCount() == 0)
    	{
    		return false;
    	}
    	
    	if( cursor.getCount() > 0 )
    	{
    		cursor.moveToFirst();        		
    	}
    	
    	do{       
    		
    	PriData pridata = new PriData();
    	pridata.srcName = cursor.getString(1);
    	pridata.dstName = cursor.getString(2);
    	pridata.degree = cursor.getInt(3);
    	pridata.hint = cursor.getString(4);
    	
    	dataList.add(pridata);
    	
    	} while( cursor.moveToNext() );  
    	
    	return true;
    }
    
    static private int getFoodCount()
    {
    	Cursor cursor = db.query(TABLE_CONFLICT, null, null,
    			null, null, null, null, null);
    	if( cursor == null )
    	{
    		return 0;
    	}
    	int count = cursor.getCount();
    	return count;    	
    }
    
    static private void initDatabase()
    {
    	if( db == null )
    	{
    		return ;
    	}

    	//Already existed.
    	if( getFoodCount() > 0 )
    	{
    		return ;    	
    	}
    	
    	//TODO: Insert Data Here
    	return ;    	
    }
	
}
