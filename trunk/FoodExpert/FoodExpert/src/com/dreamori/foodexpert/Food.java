package com.dreamori.foodexpert;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListAdapter;

public class Food extends TitleActivity {
	
	private List<GridViewHolderData> m_dataList = null;
	private ListAdapter m_adapter = null;
	
	public Food() {
		// TODO Auto-generated constructor stub
		if( m_dataList == null )
		{
			DreamoriLog.LogFoodExpert("Init Food Class and Pitures here. It should be touched once.");
			m_dataList = new ArrayList<GridViewHolderData>();
	        GridViewHolderData inq_data = new GridViewHolderData();
	        inq_data.icon = R.drawable.foodrelationship;
	        inq_data.name = null;
	        inq_data.degree = 0;
	        m_dataList.add(inq_data);
	        
	        GridViewHolderData disease_data = new GridViewHolderData();
	        disease_data.icon = R.drawable.foodanddisease;
	        disease_data.name = null;
	        disease_data.degree = 0;       
	        m_dataList.add(disease_data);
		}	
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		DreamoriLog.LogFoodExpert("Food Activity onDestroy");
		
		DatabaseHelper dbHelper = getDatabaseHelper();
		if( dbHelper != null )
		{
			dbHelper.closeDatabase();
		}
		
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.killBackgroundProcesses(getPackageName());
        System.exit(0);
        
		super.onDestroy();
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_main);
        
        m_level = 1;
        
        
        DreamoriLog.LogFoodExpert("Food Activyt, onCreate");    
		
        m_gridview = (GridView)findViewById(R.id.ui_main_grid);
                
		if( m_adapter == null )
		{
			m_adapter = new GridViewAdapter(this, m_dataList);
		}
        m_gridview.setAdapter(m_adapter);	
        m_gridview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = null;
				switch (arg2)
				{
				case 0:
					intent = new Intent(Food.this, InquireSearchPage.class);
					startActivity(intent);
					break;
					
				case 1:				
					intent = new Intent(Food.this, DiseaseSearchPage.class);
					startActivity(intent);
					break;
				
				default:
					break;
				}
			}
        });   
    }
}
