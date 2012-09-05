package com.freelunch.food;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class DiseaseSearchListPage extends DiseasePage {
	
	private void InitList(List<RelativeData> dataList)
	{
		UpdateList(dataList);
        
        m_listview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				GridViewHolder holder = (GridViewHolder) arg1.getTag();
				
				Bundle bind = new Bundle();
				bind.putSerializable("Name1", m_curDisease);
				bind.putSerializable("Name2", holder.name.getText().toString());
				bind.putSerializable("Relative", 1);
				
				Intent intent = new Intent(DiseaseSearchListPage.this, FoodMasterPage.class);
				intent.putExtras(bind);
				
				startActivity(intent);
			}
        });        
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_disease_search_list);
        
        m_context = this;
        
    	//Get Database
        m_dbHelper = DatabaseHelper.getInstance(m_context); 
        
        m_diseaseSpin = (Spinner) findViewById(R.id.ui_disease_search_list_disease_selector);
        m_relativeSpin = (Spinner) findViewById(R.id.ui_disease_search_list_food_relative);
        m_listview = (ListView)findViewById(R.id.ui_disease_search_listview);   

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
			        Search(m_curDisease, relativeFlag, dataList);	
			        
			        UpdateList(dataList);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});		
        
        InitRelativeSpin();
        
        List<RelativeData> dataList = new ArrayList<RelativeData>();
        Search(m_curDisease, 0, dataList);
        
        InitList(dataList);
        
        m_listview.setFocusable(true);
        m_listview.setFocusableInTouchMode(true);
        m_listview.requestFocus();        
    }
}
