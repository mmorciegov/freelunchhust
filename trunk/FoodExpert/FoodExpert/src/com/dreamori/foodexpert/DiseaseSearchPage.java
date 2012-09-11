package com.dreamori.foodexpert;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class DiseaseSearchPage extends DiseasePage {
	
	private void InitGrid(List<RelativeData> dataList)
	{
		UpdateGrid(dataList);
        
        m_gridview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				GridViewHolder holder = (GridViewHolder) arg1.getTag();
				
				Bundle bind = new Bundle();
				bind.putSerializable("Name1", m_curDisease);
				bind.putSerializable("Name2", holder.name.getText().toString());
				bind.putSerializable("Relative", 1);
				
				Intent intent = new Intent(DiseaseSearchPage.this, DetailInfoPage.class);
				intent.putExtras(bind);
				
				startActivity(intent);
			}
        });        
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_disease_search);
        
        m_context = this;
        m_level = 2;
        
    	//Get Database
        m_dbHelper = DatabaseHelper.getInstance(m_context); 
        
        m_diseaseSpin = (Spinner) findViewById(R.id.ui_disease_search_disease_selector);
        m_relativeSpin = (Spinner) findViewById(R.id.ui_disease_search_food_relative);
        m_gridview = (GridView)findViewById(R.id.ui_disease_search_grid);   

        InitDiseaseSpin();
        m_diseaseSpin.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				TextView tv = (TextView)arg1;
				
				if (!m_curDisease.equalsIgnoreCase(tv.getText().toString()))
				{
					m_curDisease = tv.getText().toString();
					int relativeFlag = m_relativeSpin.getSelectedItemPosition();
					
			        List<RelativeData> dataList = new ArrayList<RelativeData>();
			        Search(m_curDisease, dataList);	
			        
			        UpdateGrid(dataList);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});		
        
        InitRelativeSpin();
        
        List<RelativeData> dataList = new ArrayList<RelativeData>();
        Search(m_curDisease, dataList);
        
        InitGrid(dataList);
        
        m_gridview.setFocusable(true);
        m_gridview.setFocusableInTouchMode(true);
        m_gridview.requestFocus();        
    }
}
