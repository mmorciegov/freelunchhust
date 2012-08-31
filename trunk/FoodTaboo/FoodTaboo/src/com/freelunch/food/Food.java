package com.freelunch.food;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListAdapter;

public class Food extends Activity {
	private Context m_context;
	private GridView m_gridview;
	
	public static final String DATABASE_NAME = "food.db";
	
	private void InitGrid()
	{
        List<GridViewHolderData> dataList = new ArrayList<GridViewHolderData>();
        
        GridViewHolderData inq_data = new GridViewHolderData();
        inq_data.icon = R.drawable.food;
        inq_data.name = "查询";
        inq_data.degree = 0;
        dataList.add(inq_data);
        
        GridViewHolderData disease_data = new GridViewHolderData();
        disease_data.icon = R.drawable.food;
        disease_data.name = "食疗";
        disease_data.degree = 0;       
        dataList.add(disease_data);
        
        ListAdapter adapter = new GridViewAdapter(m_context, dataList);
        m_gridview.setAdapter(adapter);	
	}
	
    private void copyBigDataBase(String databaseFileNameString ) throws IOException {  
        InputStream myInput;  
        OutputStream myOutput = new FileOutputStream(databaseFileNameString);  

        myInput = getAssets().open(DATABASE_NAME);  
        byte[] buffer = new byte[1024];  
        int length;  
        while ((length = myInput.read(buffer)) > 0) {  
            myOutput.write(buffer, 0, length);  
        }  
        myOutput.flush();  
        myOutput.close();
        myInput.close();  
    }  	
	
	private void InitDabaseFile()
    {
    	String databasePathString = getApplicationContext().getApplicationInfo().dataDir + "/databases";
    	File databaseFolderFile = new File(databasePathString);
    	if( !databaseFolderFile.exists() )
    	{
    		databaseFolderFile.mkdir();
    	}
    	
    	String databaseFileName = getApplicationContext().getDatabasePath(DATABASE_NAME).getAbsolutePath().toString();  
    	
        File targetFile = new File(databaseFileName);
    	if (!targetFile.exists())        
    	{
			try {
				targetFile.createNewFile();
				copyBigDataBase(databaseFileName);
				Log.v("Debug", "copyBigDataBase");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    } 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_main);
        
        m_context = this;
        
        //Init database   
        InitDabaseFile();  
        
        m_gridview = (GridView)findViewById(R.id.ui_main_grid);
        InitGrid();
        m_gridview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				switch (arg2)
				{
				case 0:
					Intent intent = new Intent(Food.this, InquireSearchPage.class);
					startActivity(intent);
					break;
				
				default:
					break;
				}
			}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
}
