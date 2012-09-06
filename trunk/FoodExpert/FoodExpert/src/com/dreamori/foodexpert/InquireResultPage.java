package com.dreamori.foodexpert;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class InquireResultPage extends InquirePage {
	
	private void InitInputTextView(String foodName)
	{
		UpdateInputTextView();
		m_textview.setText(foodName);
	    
	    m_textview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String curFood = m_textview.getText().toString();
				int relativeFlag = m_relativeSpin.getSelectedItemPosition();
				
		        List<RelativeData> dataList = new ArrayList<RelativeData>();
		        Search(curFood, relativeFlag, dataList);	
		        
		        UpdateGrid(dataList);
			}
	    });
	    
	    m_textview.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (IsFoodValid(m_textview.getText().toString()))
				{
					String curFood = m_textview.getText().toString();
					int relativeFlag = m_relativeSpin.getSelectedItemPosition();
					
			        List<RelativeData> dataList = new ArrayList<RelativeData>();
			        Search(curFood, relativeFlag, dataList);	
			        
			        UpdateGrid(dataList);			
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}
	    });
	}
	
	private void Search(String curFood, int relativeFlag, List<RelativeData> dataList)
	{
		String goodBad = null;
		
		switch(relativeFlag)
		{
		case 0:
			goodBad = Databasehelper.ALL_COMBINATION;
			break;
		case 1:
			goodBad = Databasehelper.GOOD_COMBINATION;
			break;
		default:
			goodBad = Databasehelper.BAD_COMBINATION;
			break;
		}

		m_dbHelper.findRelatedFood(curFood, dataList, goodBad);
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
				bind.putSerializable("Name1", m_textview.getText().toString());
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
        
    	//Get Database
        m_dbHelper = Databasehelper.getInstance(m_context); 
        
        m_textview = (AutoCompleteTextView) findViewById(R.id.ui_inquire_result_food_input);
        m_foodClassSpin = (Spinner) findViewById(R.id.ui_inquire_result_food_class_selector);
        m_relativeSpin = (Spinner) findViewById(R.id.ui_inquire_result_food_relative);
        m_gridview = (GridView)findViewById(R.id.ui_inquire_result_grid);   
        
        Intent intent = getIntent();
        Bundle bind = intent.getExtras();
        
        String foodClass;
        String foodName;
        int relativeFlag;
        
        foodClass = (String)bind.getSerializable("FoodClass");
        foodName = (String)bind.getSerializable("FoodName");
        relativeFlag = (Integer)bind.getSerializable("Relative");
        
        InitFoodClassSpin(foodClass);
        m_foodClassSpin.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				TextView tv = (TextView)arg1;
				
				if (!m_curFoodClass.equalsIgnoreCase(tv.getText().toString()))
				{
					m_curFoodClass = tv.getText().toString();
					m_curFoodList = m_dbHelper.getPreferFoodList(m_curFoodClass);		
					
					UpdateInputTextView();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});		
        
        InitRelativeSpin(relativeFlag);
        InitInputTextView(foodName);
        
        List<RelativeData> dataList = new ArrayList<RelativeData>();
        Search(foodName, relativeFlag, dataList);
        
        InitGrid(dataList);
        
        m_gridview.setFocusable(true);
        m_gridview.setFocusableInTouchMode(true);
        m_gridview.requestFocus();        
    }
}
