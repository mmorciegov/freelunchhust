package com.freelunch.food;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListAdapter;

public class Food extends TitleActivity {
	private GridView m_gridview;
	
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
				Intent intent = null;
				switch (arg2)
				{
				case 0:
					intent = new Intent(Food.this, InquireSearchPage.class);
					startActivity(intent);
					break;
					
				case 1:				
					if (ConfigData.GetSystemDisplay(GetConfigFileName()) == 0)
					{
						intent = new Intent(Food.this, DiseaseSearchPage.class);
					}
					else
					{
						intent = new Intent(Food.this, DiseaseSearchListPage.class);
					}
					startActivity(intent);
					break;
				
				default:
					break;
				}
			}
        });   
    }
}
