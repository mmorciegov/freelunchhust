package com.dreamori.foodexpert;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class InquireResultPage extends GridViewBasePage {
	private String m_foodname;
	private List<RelativeData> m_dataList;
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if( m_dataList != null )
		{
			m_dataList.clear();
			m_dataList = null;
		}
	}

	private void Search(String curFood, List<RelativeData> dataList)
	{
		if( dataList != null )
		{
			dataList.clear();
		}
		
		getDatabaseHelper().findRelatedFood(curFood, dataList);
	}
	
	private void InitGrid(List<RelativeData> dataList)
	{
		UpdateGrid(dataList, FoodConst.GRID_FOOD_RESULT);
        
        m_gridview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				GridViewHolder holder = (GridViewHolder) arg1.getTag();
				
				Bundle bind = new Bundle();
				bind.putSerializable("Name1", m_foodname);
				bind.putSerializable("Name2", holder.name.getText().toString());
				bind.putSerializable("Relative", FoodConst.ACTIVITY_TYPE_FOOD);
				
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

        m_level = 3;
        
    	//Get Database
        m_gridview = (GridView)findViewById(R.id.ui_inquire_result_grid);   
        
        Intent intent = getIntent();
        Bundle bind = intent.getExtras();
        
//        String foodClass;
        String foodName;

//        foodClass = (String)bind.getSerializable("FoodClass");
        foodName = (String)bind.getSerializable("FoodName");
        
        m_foodname = foodName;
        setTitle(getString(R.string.title_choose) +  m_foodname + getString(R.string.title_food_related));
        ((TextView)findViewById(R.id.ui_inquire_result_cur_text)).setText(
        		getString( R.string.with) +  m_foodname + getString(R.string.result_food));
        
        ImageView imageViewSearchIcon = (ImageView)findViewById(R.id.ui_disease_result_cur_pic);
        if( imageViewSearchIcon != null )
        {
        	InitSearchIcon(imageViewSearchIcon, m_foodname);
        }
        
        m_dataList = new ArrayList<RelativeData>();
        Search(foodName, m_dataList);
        
        InitGrid(m_dataList);
        
        m_gridview.setFocusable(true);
        m_gridview.setFocusableInTouchMode(true);
        m_gridview.requestFocus();        
    }
}
