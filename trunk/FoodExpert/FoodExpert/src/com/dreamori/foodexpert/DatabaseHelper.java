package com.dreamori.foodexpert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import SQLite3.Constants;
import SQLite3.Database;
import SQLite3.Exception;
import SQLite3.TableResult;
import android.content.Context;

//Before we publish our database, we should run below sql to get redundancy data

//update swxx set swlx = 1;
//update swxx set swlx = 0
//where swmz in
//(
//select swmz from swxx
//where swmz not in
//(
//select distinct sw1 from xsxk
//) and swmz not in
//(
//select distinct sw2 from xsxk
//)
//)


public class DatabaseHelper {

	private static DatabaseHelper mInstance = null;
	static Database db = null;
	//static String srcDBName = "libFoodFuncion.so";
	static String srcDBName = "food.db";

	public static final String DATABASE_NAME = "food.db";
	
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
    //Some food doesn't have related food, we should hide them in the first page.
    public final static String FOOD_HAS_RELATED = "swlx";
    
    public final static String TABLE_FOOD_CLASS_INFO = "swfl";
    public final static String FOOD_CLASS_NAME = "swfl";
    
    public final static String TABLE_DISEASE_INFO = "jbxx";
    public final static String DISEASE_NAME = "jbmc";
    public final static String DISEASE_PHOTO = "jbtp";
    public final static String DISEASE_RELATED_FOOD_NAME = "swmc";
    public final static String DISEASE_SEARCH_NUMBER = "cxcs";
    
    public final static String TABLE_DISEASE_FOOD_INFO = "swjb";
    
    public final static String DISEASE_FOOD_EAT = "swcf";
    
    
    //private static List<PriData> foodConflictData = null;
    private static ArrayList<String> foodList = null;
    private static ArrayList<String> diseaseList = null;
    private static ArrayList<String> foodClassList = null;	
    
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
	
	public List<String> GetDiseaseList()
	{
     	if( diseaseList == null )
    	{
     		diseaseList = new ArrayList<String>();
        	
			TableResult tableResult = null;
			try {
				tableResult = db.get_table("select "+DISEASE_NAME+" from "+TABLE_DISEASE_INFO+" order by "+DISEASE_SEARCH_NUMBER+" desc");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (String[] rowData : tableResult.rows) {
				diseaseList.add(rowData[0]);
			}
			
	   		if( tableResult != null )
	    	{
	    		tableResult.clear();
	    		tableResult = null;
	    	}
    	}
    	
    	return diseaseList;	
	}
	
	public List<String> GetFoodClassList()
	{
     	if( foodClassList == null )
    	{
     		foodClassList = new ArrayList<String>();    		
     		
     		
     		TableResult tableResult = null;
			try {
				tableResult = db.get_table("select "+FOOD_CLASS_NAME+" from "+TABLE_FOOD_CLASS_INFO);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (String[] rowData : tableResult.rows) {
				foodClassList.add(rowData[0]);
			}
			
	   		if( tableResult != null )
	    	{
	    		tableResult.clear();
	    		tableResult = null;
	    	}
    	}
    	
    	return foodClassList;	
	}
	
    
    public List<String> getAllFoodList()
    {
     	if( foodList == null )
    	{
     		foodList = new ArrayList<String>();
    		
			TableResult tableResult = null;
			try {
				tableResult = db.get_table(
						"select "+FOOD_NAME+" from "+TABLE_FOOD_INFO
						+" where "+ FOOD_HAS_RELATED+"=1"
						+" order by "+FOOD_SEARCH_NUMBER+" desc"
						);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (String[] rowData : tableResult.rows) {
				foodList.add(rowData[0]);
			}
    	}
    	
    	return foodList;
    }
    
    public void AddFoodSearchFrequency( String foodName )
    {   		
   		TableResult tableResult = null;
		try {
			tableResult = db.get_table("select "+FOOD_SEARCH_NUMBER+" from "+TABLE_FOOD_INFO
					+" where "+FOOD_NAME+"='"+foodName+"'");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if( tableResult == null || tableResult.rows.isEmpty() )
			return;
		
		int foodSearchFrequency = Integer.parseInt(tableResult.rows.get(0)[0]);
   		if( tableResult != null )
    	{
    		tableResult.clear();
    		tableResult = null;
    	}

   		
   		if( foodSearchFrequency >= 0 )
   		{
   			foodSearchFrequency++;
   		}
   		   		   		
   		//Update food frequency
   		try {
			db.exec("update "+TABLE_FOOD_INFO+" set "+FOOD_SEARCH_NUMBER+"="+foodSearchFrequency
					+" where "+FOOD_NAME+"='"+foodName+"'", null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
    }
        

    public void AddDiseaseSearchFrequency( String diseaseName )
    {   		
   		TableResult tableResult = null;
		try {
			tableResult = db.get_table("select "+DISEASE_SEARCH_NUMBER+" from "+TABLE_DISEASE_INFO
					+" where "+DISEASE_NAME+"='"+diseaseName+"'");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if( tableResult == null || tableResult.rows.isEmpty() )
			return;
		
		int diseaseSearchFrequency = Integer.parseInt(tableResult.rows.get(0)[0]);
   		if( tableResult != null )
    	{
    		tableResult.clear();
    		tableResult = null;
    	}

   		
   		if( diseaseSearchFrequency >= 0 )
   		{
   			diseaseSearchFrequency++;
   		}
   		   		   		
   		//Update food frequency
   		try {
			db.exec("update "+TABLE_DISEASE_INFO+" set "+DISEASE_SEARCH_NUMBER+"="+diseaseSearchFrequency
					+" where "+DISEASE_NAME+"='"+diseaseName+"'", null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
    }
    
    public List<String> getPreferFoodList(String foodClassName)
    {
		// get data by class
 		List<String> dataList = new ArrayList<String>();

    	TableResult tableResult = null;
		try {
			tableResult = db.get_table("select "+FOOD_NAME+" from "+TABLE_FOOD_INFO
					+" where "+FOOD_CLASS_NAME+"='"+foodClassName
					+ " and "+FOOD_HAS_RELATED+"=1"
					+" order by "+FOOD_SEARCH_NUMBER+" desc");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (String[] rowData : tableResult.rows) {
			dataList.add(rowData[0]);
		}
		
   		if( tableResult != null )
    	{
    		tableResult.clear();
    		tableResult = null;
    	}
	    		
		return dataList;
     }
    
    public boolean getRandomDataInFood(List<String> dataList)
    {
    	TableResult tableResult = null;
		try {
			tableResult = db.get_table("select distinct * from "+TABLE_CONFLICT+" order by "+LEVEL+" desc");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if( tableResult == null || tableResult.rows.isEmpty() )
			return false;
		
		Random random = new Random();
    	int rand = Math.abs(random.nextInt())  % tableResult.rows.size();

    	dataList.add(tableResult.rows.get(rand)[1]);
    	dataList.add(tableResult.rows.get(rand)[2]);
    	
   		if( tableResult != null )
    	{
    		tableResult.clear();
    		tableResult = null;
    	}
    	
    	return true;   	
    }
    
    public boolean getRandomDataInDisease(List<String> dataList)
    {	
    	TableResult tableResult = null;
		try {
			tableResult = db.get_table("select distinct * from "+TABLE_DISEASE_FOOD_INFO+" order by "+LEVEL+" desc");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if( tableResult == null || tableResult.rows.isEmpty() )
			return false;
		
		Random random = new Random();
    	int rand = Math.abs(random.nextInt()) % tableResult.rows.size();

    	dataList.add(tableResult.rows.get(rand)[1]);
    	dataList.add(tableResult.rows.get(rand)[2]);
    	
   		if( tableResult != null )
    	{
    		tableResult.clear();
    		tableResult = null;
    	}
    	
    	return true;    	
    }
    
    /***
     * 
     * @param foodName
     * @param dataList
     * @return
     */

    public boolean findRelatedFood(String foodName, List<RelativeData> dataList )
    {
    	TableResult tableResult = null;
		try {
			tableResult = db.get_table("select distinct * from "+TABLE_CONFLICT
					+" where "+FOOD_FIRST+"='"+foodName+"' or "+FOOD_SECOND+"='"+foodName+"'"
					+" order by "+LEVEL+" desc");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (String[] rowData : tableResult.rows) {
			
			RelativeData pridata = new RelativeData();
    		if (rowData[1].equalsIgnoreCase(foodName))
    		{
    			pridata.name = rowData[2];
    		}
    		else
    		{
    			pridata.name = rowData[1];
    		}
    		
	    	pridata.degree = Integer.parseInt(rowData[3]);
	    	pridata.hint = rowData[4];
	    	
	    	dataList.add(pridata);
		}
		
   		if( tableResult != null )
    	{
    		tableResult.clear();
    		tableResult = null;
    	}
    	
    	return true;
    }

    
    private boolean getDiseaseListFromFood(String foodName, List<RelativeData> dataList)
    {
		TableResult tableResult = null;

		try {
			tableResult = db.get_table("select distinct * from "
					+ TABLE_DISEASE_FOOD_INFO + " where "
					+ DISEASE_RELATED_FOOD_NAME + "='" + foodName + "'"
					+ " order by " + LEVEL + " desc");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (String[] rowData : tableResult.rows) {

			RelativeData pridata = new RelativeData();
			pridata.name = rowData[2];
			pridata.degree = Integer.parseInt(rowData[3]);
			pridata.hint = rowData[4];

			dataList.add(pridata);
		}

		if (tableResult != null) {
			tableResult.clear();
    		tableResult = null;
    	}
		
    	return true;
    }
    

    public List<String> getFoodListFromDisease(String diseaseName)
    {
		TableResult tableResult = null;
		try {
				tableResult = db.get_table("select distinct "+DISEASE_RELATED_FOOD_NAME+" from "
						+ TABLE_DISEASE_FOOD_INFO + " where " + DISEASE_NAME + "='"
						+ diseaseName + "'" + " order by " + LEVEL + " desc");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<String> diseaseList = new ArrayList<String>();

		for (String[] rowData : tableResult.rows) {
			diseaseList.add( rowData[0]);
		}

		if (tableResult != null) {
    		tableResult.clear();
    		tableResult = null;
    	}
		
    	return diseaseList;
    }
    
    public String getFoodEatMethod( String diseaseName, String foodName )
    {
    	String foodEatMethod = null;
    	
    	TableResult tableResult = null;
		try {
			String sqlStr = "select distinct "+ DISEASE_FOOD_EAT +" from " + TABLE_DISEASE_FOOD_INFO
					+" where "+  "(" +  DISEASE_NAME+"='"+diseaseName+"' and "+ DISEASE_RELATED_FOOD_NAME+"='"+foodName+"'" + ")" 
					+ " or " + "(" +  DISEASE_NAME+"='"+foodName+"' and "+ DISEASE_RELATED_FOOD_NAME+"='"+diseaseName+"'" + ")" 
					+" order by "+LEVEL+" desc";
			tableResult = db.get_table(sqlStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

		for (String[] rowData : tableResult.rows) {
			foodEatMethod = rowData[0];
		}
		
		if (tableResult != null) {
    		tableResult.clear();
    		tableResult = null;
    	}
    	
    	return foodEatMethod;
    }
    
    
    private boolean getFoodListFromDisease(String diseaseName, String foodClass, List<RelativeData> dataList )
    {
		TableResult tableResult = null;
		try {
			
			if( foodClass!=null &&foodClass!="" )
			{
				tableResult = db.get_table("select distinct * from "
						+ TABLE_DISEASE_FOOD_INFO +" inner join " + TABLE_FOOD_INFO 
						+ " on " + TABLE_DISEASE_FOOD_INFO +"."+ DISEASE_RELATED_FOOD_NAME
						+ " = " + TABLE_FOOD_INFO +"."+ FOOD_NAME						
						+ " where " + DISEASE_NAME + "='" + diseaseName + "'" 
						+ " and " + FOOD_CLASS_NAME + "='" + foodClass + "'" 
						+ " order by " + LEVEL + " desc");
			}
			else 
			{
				tableResult = db.get_table("select distinct * from "
						+ TABLE_DISEASE_FOOD_INFO + " where " + DISEASE_NAME + "='"
						+ diseaseName + "'" + " order by " + LEVEL + " desc");
			}			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (String[] rowData : tableResult.rows) {

			RelativeData pridata = new RelativeData();
			pridata.name = rowData[1];
			pridata.degree = Integer.parseInt(rowData[3]);
			pridata.hint = rowData[4];

			dataList.add(pridata);
		}

		if (tableResult != null) {
    		tableResult.clear();
    		tableResult = null;
    	}
		
    	return true;
    }
    
    
    //input disease, return food list
    //input food, return disease list
    public boolean findDiseaseRelatedInfo(String diseaseName, int searchType, String foodClass, List<RelativeData> dataList )
    {    	
    	boolean bRet = false;
   	
    	if( searchType == FoodConst.DISEASE_RESULT_SEARCH_DISEASE )
    	{
    		bRet = getFoodListFromDisease(diseaseName, foodClass, dataList);        	
    	}
    	else
    	{    	
    		bRet = getDiseaseListFromFood(diseaseName, dataList);
		}
		
    	return bRet;
    }
    
    public boolean findDetailInfoInFood(String foodName1, String foodName2, List<RelativeData> dataList)
    {
    	TableResult tableResult = null;
		try {
			tableResult = db.get_table("select distinct * from "+TABLE_CONFLICT
					+" where ("+FOOD_FIRST+"='"+foodName1+"' and "+FOOD_SECOND+"='"+foodName2+"'"
					+") or ("+FOOD_FIRST+"='"+foodName2+"' and "+FOOD_SECOND+"='"+foodName1+"'"
					+") order by "+LEVEL+" desc");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (String[] rowData : tableResult.rows) {
			
			RelativeData pridata = new RelativeData();
    		pridata.name = rowData[1];
	    	pridata.degree = Integer.parseInt(rowData[3]);
	    	pridata.hint = rowData[4];
	    	
	    	dataList.add(pridata);
		}
		
		if( tableResult != null )
    	{
    		tableResult.clear();
    		tableResult = null;
    	}

    	return true;
    }
    
    public String getIconName (String iconName) 
    {    	
    	TableResult tableResult = null;
		try {
			tableResult = db.get_table("select "+FOOD_PHOTO+" from "+TABLE_FOOD_INFO
					+" where "+FOOD_NAME+"='"+iconName+"'");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if( tableResult == null || tableResult.rows.isEmpty() )
		{
			try {
				tableResult = db.get_table("select "+DISEASE_PHOTO+" from "+TABLE_DISEASE_INFO
						+" where "+DISEASE_NAME+"='"+iconName+"'");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		String iconNameString= null;
		if( tableResult != null && !tableResult.rows.isEmpty() )
			iconNameString = tableResult.rows.get(0)[0];
   		
   		if( iconNameString == null )
   		{
   			iconNameString = "food";
   		}
   		   		
   		if( tableResult != null )
    	{
    		tableResult.clear();
    		tableResult = null;
    	}

    	return iconNameString;
    }
   
    
    public boolean findDetailInfoInDisease(String diseaseName, String foodName, List<RelativeData> dataList)
    {
    	TableResult tableResult = null;
		try {
			String sqlStr = "select distinct * from "+TABLE_DISEASE_FOOD_INFO
					+" where "+  "(" +  DISEASE_NAME+"='"+diseaseName+"' and "+ DISEASE_RELATED_FOOD_NAME+"='"+foodName+"'" + ")" 
					+ " or " + "(" +  DISEASE_NAME+"='"+foodName+"' and "+ DISEASE_RELATED_FOOD_NAME+"='"+diseaseName+"'" + ")" 
					+" order by "+LEVEL+" desc";
			tableResult = db.get_table(sqlStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (String[] rowData : tableResult.rows) {
			
			RelativeData pridata = new RelativeData();
    		pridata.name = rowData[1];
	    	pridata.degree = Integer.parseInt(rowData[3]);
	    	pridata.hint = rowData[4];
	    	
	    	dataList.add(pridata);
		}
		
		if( tableResult != null )
    	{
    		tableResult.clear();
    		tableResult = null;
    	}

    	
    	return true;
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

		myInput = context.getAssets().open(srcDBName);
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
