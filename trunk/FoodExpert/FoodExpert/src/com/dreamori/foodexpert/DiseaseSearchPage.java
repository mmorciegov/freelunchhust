package com.dreamori.foodexpert;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class DiseaseSearchPage extends GridViewBasePage {
	
	private List<RelativeData> m_dataList;
		
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if( m_dataList != null )
		{
			m_dataList.clear();
			m_dataList = null;
		}
	}



	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_disease_search);

        m_level = 2;
        
    	//Get Database  
        m_gridview = (GridView)findViewById(R.id.ui_disease_search_grid);   
        
        List<String> diseaseList = getDatabaseHelper().GetDiseaseList();
        
        if( m_dataList == null )
        {
        	m_dataList = new ArrayList<RelativeData>();
    		for (int i=0; i<diseaseList.size(); i++)
    		{
    			m_dataList.add(new RelativeData(diseaseList.get(i)));
    		}       
        }
        
        UpdateGrid(m_dataList, FoodConst.GRID_DEFAULT);
			
        m_gridview.setFocusable(true);
        m_gridview.setFocusableInTouchMode(true);
        m_gridview.requestFocus();     
        
        
        m_gridview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				GridViewHolder holder = (GridViewHolder) arg1.getTag();
				
				Bundle bundle = new Bundle();
				bundle.putString(FoodConst.INTENT_DISEASE, holder.name.getText().toString());
				bundle.putInt(FoodConst.INTENT_DISEASE_SEARCH_TYPE, FoodConst.DISEASE_RESULT_SEARCH_DISEASE);
				
				DreamoriLog.LogFoodExpert("Disease Name: "+ holder.name.getText().toString() );
				
				Intent intent = new Intent(DiseaseSearchPage.this, DiseaseResultPage.class);
				intent.putExtras(bundle);
				
				startActivity(intent);
			}
        });  
    }
}
