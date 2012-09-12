package com.dreamori.foodexpert;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class InquireResultPage extends InquirePage {
	private String m_foodname;

	private void Search(String curFood, List<RelativeData> dataList)
	{
		m_dbHelper.findRelatedFood(curFood, dataList);
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
				
				Bundle bind = new Bundle();
				bind.putSerializable("Name1", m_foodname);
				bind.putSerializable("Name2", holder.name.getText().toString());
				bind.putSerializable("Relative", 0);
				
				Intent intent = new Intent(InquireResultPage.this, DetailInfoPage.class);
				intent.putExtras(bind);
				
				startActivity(intent);
			}
        });        
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_inquire_result);
        
        m_context = this;
        m_level = 3;
        
    	//Get Database
        m_dbHelper = DatabaseHelper.getInstance(m_context); 
        m_gridview = (GridView)findViewById(R.id.ui_inquire_result_grid);   
        
        Intent intent = getIntent();
        Bundle bind = intent.getExtras();
        
//        String foodClass;
        String foodName;
        int relativeFlag;
        
//        foodClass = (String)bind.getSerializable("FoodClass");
        foodName = (String)bind.getSerializable("FoodName");
        relativeFlag = (Integer)bind.getSerializable("Relative");
        
        m_foodname = foodName;
        setTitle(m_foodname + getString(R.string.title_food_related));
        
        List<RelativeData> dataList = new ArrayList<RelativeData>();
        Search(foodName, dataList);
        
        InitGrid(dataList);
        
        m_gridview.setFocusable(true);
        m_gridview.setFocusableInTouchMode(true);
        m_gridview.requestFocus();        
    }
}
