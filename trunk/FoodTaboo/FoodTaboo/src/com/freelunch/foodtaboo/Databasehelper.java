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
	
    public final static String TABLE_CONFLICT = "xsxk";
    public final static String ID = "_id";
    public final static String FOOD_FIRST = "sw1";
    public final static String FOOD_SECOND = "sw2";
    public final static String LEVEL = "jb";
    public final static String REASON = "yy";
    // 0 -> Bad;  1 -> Good
    public final static String GoodBad = "xsxk";

    public final static String TABLE_FOOD_INFO = "swxx";
    public final static String FOOD_NAME = "swmz";
    public final static String FOOD_TYPE = "swfl";
    public final static String FOOD_PHOTO = "swtp";
    public final static String FOOD_SEARCH_NUMBER = "cxcs";
    
    private static List<PriData> foodConflictData = null;
    private static ArrayList<String> foodList = null;	
    
    public final static String BAD_COMBINATION = "0";
    public final static String GOOD_COMBINATION = "1";
    public final static String ALL_COMBINATION = "2";
    
	
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
	            return null;
	        }
		}
		return mInstance;
	}
	
    
    public List<PriData> GetPrivateData( )
    {
    	if( foodConflictData == null )
    	{
    		foodConflictData = new ArrayList<PriData>();
    		
        	Cursor cursor = db.query(TABLE_CONFLICT, null, null,
        			null, null, null, null, null);
        	if( cursor != null && cursor.getCount() > 0 )
        	{
        		cursor.moveToFirst();   
        		
            	do{       
            		
                	PriData pridata = new PriData();
                	pridata.srcName = cursor.getString(1);
                	pridata.dstName = cursor.getString(2);
                	pridata.degree = cursor.getInt(3);
                	pridata.hint = cursor.getString(4);
                	pridata.goodBad = cursor.getString(5);
                	
                	foodConflictData.add(pridata);
                	
                	} while( cursor.moveToNext() );
        	}       	

    	}
    	
    	return foodConflictData;
    }
    
    public List<String> getFoodList()
    {
     	if( foodList == null )
    	{
     		foodList = new ArrayList<String>();
    		
        	Cursor cursor = db.query(TABLE_FOOD_INFO, new String[]{FOOD_NAME}, null,
        			null, null, null, FOOD_SEARCH_NUMBER+" DESC", null);
        	if( cursor != null && cursor.getCount() > 0 )
        	{
        		cursor.moveToFirst();        	
            	do{       
                	
            		foodList.add(cursor.getString(0));
            	
            	} while( cursor.moveToNext() );
        	}       	
    	}
    	
    	return foodList;
    }
    
    public boolean findConflictedFood(String foodName, List<PriData> dataList)
    {
    	if( findRelatedFood(foodName, dataList, BAD_COMBINATION))
    	{
    		return true;    	
    	}    		
    	return false;
    }
    
    public boolean findFittingFood(String foodName, List<PriData> dataList)
    {
    	if( findRelatedFood(foodName, dataList, GOOD_COMBINATION))
    	{
    		return true;    	
    	}    		
    	return false;
    }
    
    /***
     * 
     * @param foodName
     * @param dataList
     * @param goodBad
     * 0 -> Bad  BAD_COMBINATION
     * 1 -> Good GOOD_COMBINATION 
     * 2 -> All  ALL_COMBINATION
     * @return
     */

    public boolean findRelatedFood(String foodName, List<PriData> dataList, String goodBad )
    {
    	Cursor cursor;
    	if( goodBad.equals(ALL_COMBINATION) )
    	{
    		cursor = db.query(TABLE_CONFLICT, new String[]{"DISTINCT *"}, FOOD_FIRST+"=?"+" or "+ FOOD_SECOND + "=?",
    				new String[]{foodName, foodName}, null, null, LEVEL+" DESC", null);   	
    	}
    	else
    	{
    		cursor = db.query(TABLE_CONFLICT, new String[]{"DISTINCT *"}, "(" + FOOD_FIRST+"=?"+" or "+ FOOD_SECOND + "=?" + ")" + " and " + GoodBad +"=?" ,
    				new String[]{foodName, foodName, goodBad}, null, null, LEVEL+" DESC", null);
    	}
    	
    	if (cursor == null || cursor.getCount() == 0)
    	{
    		return false;
    	}
    	
   		cursor.moveToFirst();      		
    	
    	do{    		
    	PriData pridata = new PriData();
    	pridata.srcName = cursor.getString(1);
    	pridata.dstName = cursor.getString(2);
    	pridata.degree = cursor.getInt(3);
    	pridata.hint = cursor.getString(4);
    	pridata.goodBad = cursor.getString(5);
    	
    	dataList.add(pridata);
    	
    	} while( cursor.moveToNext() );  
    	
    	return true;
    }
       
    
	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	public boolean deleteDatabase(Context context) {
		return context.deleteDatabase(DATABASE_NAME);
	}	
}
