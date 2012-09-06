package com.dreamori.foodexpert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static DatabaseHelper mInstance = null;	
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
    
    public final static String TABLE_FOOD_CLASS_INFO = "swfl";
    public final static String FOOD_CLASS_NAME = "swfl";
    
    public final static String TABLE_DISEASE_INFO = "jbxx";
    public final static String DISEASE_NAME = "jbmc";
    public final static String DISEASE_SEARCH_NUMBER = "cxcs";
    
    public final static String TABLE_DISEASE_FOOD_INFO = "swjb";
    
    
    //private static List<PriData> foodConflictData = null;
    private static ArrayList<String> foodList = null;
    private static ArrayList<String> diseaseList = null;
    private static ArrayList<String> foodClassList = null;	
    
    public final static String BAD_COMBINATION = "0";
    public final static String GOOD_COMBINATION = "1";
    public final static String ALL_COMBINATION = "2";
    
	private static Context mContext = null;
    
	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	static synchronized DatabaseHelper getInstance(Context context){
		if(mInstance == null )
		{
			mInstance = new DatabaseHelper(context);
			
			mContext = context;
					
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
	
	public List<String> GetDiseaseList()
	{
     	if( diseaseList == null )
    	{
     		diseaseList = new ArrayList<String>();

        	Cursor cursor = db.query(TABLE_DISEASE_INFO, new String[]{DISEASE_NAME}, null,
        			null, null, null, DISEASE_SEARCH_NUMBER+" DESC", null);
        	if( cursor != null && cursor.getCount() > 0 )
        	{
        		cursor.moveToFirst();        	
            	do{       
                	
            		diseaseList.add(cursor.getString(0));
            	
            	} while( cursor.moveToNext() );
        	}
    	}
    	
    	return diseaseList;	
	}
	
	public List<String> GetFoodClassList()
	{
     	if( foodClassList == null )
    	{
     		foodClassList = new ArrayList<String>();    		
     		
     		foodClassList.add(mContext.getString(R.string.food_class_usual));
     		
        	Cursor cursor = db.query(TABLE_FOOD_CLASS_INFO, new String[]{FOOD_CLASS_NAME}, null,
        			null, null, null, null, null);
        	if( cursor != null && cursor.getCount() > 0 )
        	{
        		cursor.moveToFirst();        	
            	do{       
                	
            		foodClassList.add(cursor.getString(0));
            	
            	} while( cursor.moveToNext() );
        	}        	

     		foodClassList.add(mContext.getString(R.string.food_class_all));
    	}
    	
    	return foodClassList;	
	}
	
    
    public List<String> getAllFoodList()
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
    
    
    
    public List<String> getPreferFoodList(String foodClassName)
    {
     	if (foodClassName.equalsIgnoreCase(mContext.getString(R.string.food_class_all)))
    	{
    		return getAllFoodList();
    	}
    	else if (foodClassName.equalsIgnoreCase(mContext.getString(R.string.food_class_usual)))
    	{
    		List<String> dataList = new ArrayList<String>();
    		if (foodList == null)
    		{
    			getAllFoodList();
    		}
    		int count = foodList.size() < 9 ? foodList.size() : 9;
    		for (int i=0; i<count; i++)
    		{
    			dataList.add(foodList.get(i));
    		}
    		return dataList;
    	}
    	else
    	{
    		// get data by class
     		List<String> dataList = new ArrayList<String>();
    		
        	Cursor cursor = db.query(TABLE_FOOD_INFO, new String[]{FOOD_NAME}, FOOD_CLASS_NAME+"=?",
        			new String[]{foodClassName}, null, null, FOOD_SEARCH_NUMBER+" DESC", null);
        	if( cursor != null && cursor.getCount() > 0 )
        	{
        		cursor.moveToFirst();        	
            	do{       
                	
            		dataList.add(cursor.getString(0));
            	
            	} while( cursor.moveToNext() );
        	}       	
		    		
    		return dataList;
    	}
    }
    
    public boolean findConflictedFood(String foodName, List<RelativeData> dataList)
    {
    	if( findRelatedFood(foodName, dataList, BAD_COMBINATION))
    	{
    		return true;    	
    	}    		
    	return false;
    }
    
    public boolean findFittingFood(String foodName, List<RelativeData> dataList)
    {
    	if( findRelatedFood(foodName, dataList, GOOD_COMBINATION))
    	{
    		return true;    	
    	}    		
    	return false;
    }
    
    public boolean getRandomDataInFood(List<String> dataList)
    {
    	Cursor cursor;
		cursor = db.query(TABLE_CONFLICT, new String[]{"DISTINCT *"}, null,
				null, null, null, LEVEL+" DESC", null);   	
    	
    	if (cursor == null || cursor.getCount() == 0)
    	{
    		return false;
    	}
    	
    	Random random = new Random();
    	int rand = random.nextInt() % cursor.getCount();
    	
   		cursor.moveToFirst();     
   		
   		for (int i=0; i<rand; i++)
   		{
   			cursor.moveToNext();
   		} 		
		
		dataList.add(cursor.getString(1));
		dataList.add(cursor.getString(2));
    	
    	return true;   	
    }
    
    public boolean getRandomDataInDisease(List<String> dataList)
    {
    	Cursor cursor;
		cursor = db.query(TABLE_DISEASE_FOOD_INFO, new String[]{"DISTINCT *"}, null,
				null, null, null, LEVEL+" DESC", null);   	
    	
    	if (cursor == null || cursor.getCount() == 0)
    	{
    		return false;
    	}
    	
    	Random random = new Random();
    	int rand = random.nextInt() % cursor.getCount();
    	
   		cursor.moveToFirst();     
   		
   		for (int i=0; i<rand; i++)
   		{
   			cursor.moveToNext();
   		} 		
		
		dataList.add(cursor.getString(2));
		dataList.add(cursor.getString(1));
    	
    	return true;    	
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

    public boolean findRelatedFood(String foodName, List<RelativeData> dataList, String goodBad )
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
    		RelativeData pridata = new RelativeData();
    		if (cursor.getString(1).equalsIgnoreCase(foodName))
    		{
    			pridata.name = cursor.getString(2);
    		}
    		else
    		{
    			pridata.name = cursor.getString(1);
    		}

	    	pridata.degree = cursor.getInt(3);
	    	pridata.hint = cursor.getString(4);
	    	
	    	dataList.add(pridata);
    	
    	} while( cursor.moveToNext() );  
    	
    	return true;
    }
       
    public boolean findRelatedFoodByDisease(String diseaseName, List<RelativeData> dataList, String goodBad )
    {
    	Cursor cursor;
    	if( goodBad.equals(ALL_COMBINATION) )
    	{
    		cursor = db.query(TABLE_DISEASE_FOOD_INFO, new String[]{"DISTINCT *"}, DISEASE_NAME+"=?",
    				new String[]{diseaseName}, null, null, LEVEL+" DESC", null);   	
    	}
    	else
    	{
    		cursor = db.query(TABLE_DISEASE_FOOD_INFO, new String[]{"DISTINCT *"}, DISEASE_NAME+"=?" + " and " + GoodBad +"=?" ,
    				new String[]{diseaseName, goodBad}, null, null, LEVEL+" DESC", null);
    	}
    	
    	if (cursor == null || cursor.getCount() == 0)
    	{
    		return false;
    	}
    	
   		cursor.moveToFirst();      		
    	
    	do{    		
    		RelativeData pridata = new RelativeData();
    		pridata.name = cursor.getString(1);
	    	pridata.degree = cursor.getInt(3);
	    	pridata.hint = cursor.getString(4);
	    	
	    	dataList.add(pridata);
    	
    	} while( cursor.moveToNext() );  
    	
    	return true;
    }
    
    public boolean findDetailInfoInFood(String foodName1, String foodName2, List<RelativeData> dataList)
    {
    	Cursor cursor;
		cursor = db.query(TABLE_CONFLICT, new String[]{"DISTINCT *"}, 
				"("+FOOD_FIRST+"=?"+" and "+ FOOD_SECOND + "=?" + ")" + " or "
				+"("+FOOD_FIRST+"=?"+" and "+ FOOD_SECOND + "=?" + ")",
				new String[]{foodName1, foodName2, foodName2, foodName1}, null, null, LEVEL+" DESC", null);   	
    	
    	if (cursor == null || cursor.getCount() == 0)
    	{
    		return false;
    	}
    	
   		cursor.moveToFirst();      		
    	
    	do{    		
    		RelativeData pridata = new RelativeData();
    		pridata.name = cursor.getString(1);
	    	pridata.degree = cursor.getInt(3);
	    	pridata.hint = cursor.getString(4);
	    	
	    	dataList.add(pridata);
    	
    	} while( cursor.moveToNext() );  
    	
    	return true;
    }
    
    public String getIconName (String iconName) 
    {
    	Cursor cursor;
		cursor = db.query(TABLE_FOOD_INFO, new String[]{FOOD_PHOTO}, "("+FOOD_NAME+"=?"+")", new String[]{iconName}, null, null, null, null);   	
    	
    	if (cursor == null || cursor.getCount() == 0)
    	{
    		return "food";
    	}
    	
   		cursor.moveToFirst();      		
    	
   		String iconNameString = cursor.getString(0);
   		
   		if( iconNameString == null )
   		{
   			iconNameString = "food";
   		}
   		   		
    	return iconNameString;
    }
   
    
    public boolean findDetailInfoInDisease(String diseaseName, String foodName, List<RelativeData> dataList)
    {
    	Cursor cursor;
		cursor = db.query(TABLE_DISEASE_FOOD_INFO, new String[]{"DISTINCT *"}, 
				DISEASE_NAME+"=?" + " and " + "swmc=?",
				new String[]{diseaseName, foodName}, null, null, LEVEL+" DESC", null);   	
    	
    	if (cursor == null || cursor.getCount() == 0)
    	{
    		return false;
    	}
    	
   		cursor.moveToFirst();      		
    	
    	do{    		
    		RelativeData pridata = new RelativeData();
    		pridata.name = cursor.getString(1);
	    	pridata.degree = cursor.getInt(3);
	    	pridata.hint = cursor.getString(4);
	    	
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

	
   
   
   
}
