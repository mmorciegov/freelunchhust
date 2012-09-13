package com.dreamori.foodexpert;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.NavUtils;

public class DiseaseResultPage extends DiseasePage {
	
	private String m_diseasePageSearchName;
	private int  m_searchType;

	public void Search(String value, int searchType, List<RelativeData> dataList)
	{
		m_dbHelper.findDiseaseRelatedInfo(value, searchType, dataList);
	}
	
	private void InitGrid(List<RelativeData> dataList)
	{
		UpdateGrid(dataList);
        
        m_gridview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				GridViewHolder holder = (GridViewHolder) arg1.getTag();
				
				Bundle bundle = new Bundle();
				if( m_searchType == FoodConst.DISEASE_RESULT_SEARCH_DISEASE )
				{
					bundle.putSerializable("Name1", m_diseasePageSearchName);
					bundle.putSerializable("Name2", holder.name.getText().toString());
				}
				else
				{
					bundle.putSerializable("Name1", holder.name.getText().toString());
					bundle.putSerializable("Name2", m_diseasePageSearchName);		
				}

				bundle.putSerializable("Relative", FoodConst.ACTIVITY_TYPE_DISEASE);
			
				Intent intent = new Intent(DiseaseResultPage.this, DetailInfoPage.class);
				intent.putExtras(bundle);
				
				startActivity(intent);
			}
        });        
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_disease_result);

        
        m_context = this;
        m_level = 3;
        
    	//Get Database
        m_dbHelper = DatabaseHelper.getInstance(getApplicationContext()); 
        m_gridview = (GridView)findViewById(R.id.ui_disease_result_grid);   
        
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        
        m_diseasePageSearchName = bundle.getString(FoodConst.INTENT_DISEASE);
        
        m_searchType = bundle.getInt(FoodConst.INTENT_DISEASE_SEARCH_TYPE);
        
        setTitle(m_diseasePageSearchName + getString(R.string.title_food_related));
        
        List<RelativeData> dataList = new ArrayList<RelativeData>();
                
        Search(m_diseasePageSearchName, m_searchType, dataList);
        
        InitGrid(dataList);
        
        m_gridview.setFocusable(true);
        m_gridview.setFocusableInTouchMode(true);
        m_gridview.requestFocus();   
        
    }


}
