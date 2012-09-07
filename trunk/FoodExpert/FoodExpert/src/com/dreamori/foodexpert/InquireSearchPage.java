package com.dreamori.foodexpert;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Spinner;
import android.widget.TextView;

public class InquireSearchPage extends InquirePage {
	
	private void InitInputTextView()
	{
		UpdateInputTextView();
	    
	    m_textview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				GotoResultPage();
			}
	    });
	    
	    m_textview.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (IsFoodValid(m_textview.getText().toString()))
				{
					GotoResultPage();
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
	
	private void GotoResultPage()
	{
		String curFood = m_textview.getText().toString();
		int relativeFlag = 0;
		
		Bundle bind = new Bundle();
		bind.putSerializable("FoodClass", m_curFoodClass);
		bind.putSerializable("FoodName", curFood);
		bind.putSerializable("Relative", relativeFlag);
    	
		Intent intent = new Intent(InquireSearchPage.this, InquireResultPage.class);
		intent.putExtras(bind);
		
		startActivity(intent);
	}
	
	private void UpdateGrid()
	{
        List<GridViewHolderData> gridDataList = new ArrayList<GridViewHolderData>();
		for (int i=0; i<m_curFoodList.size(); i++)
		{
	        GridViewHolderData data = new GridViewHolderData();
	        data.name = m_curFoodList.get(i);
	        data.icon = ResourceManager.GetIcon(this,  m_dbHelper.getIconName(data.name));
	        data.degree = 0;
	        gridDataList.add(data);		
		}
        
        ListAdapter adapter = new GridViewAdapter(m_context, gridDataList);
        m_gridview.setAdapter(adapter);		
	}
	
	private void InitGrid()
	{
		UpdateGrid();
        
        m_gridview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				GridViewHolder holder = (GridViewHolder) arg1.getTag();
				//m_textview.setThreshold(100);
				m_textview.setText(holder.name.getText());	
			}
        });        
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_inquire_search);
        
        m_context = this;
        
    	//Get Database
        m_dbHelper = DatabaseHelper.getInstance(m_context);        
        
        m_textview = (AutoCompleteTextView) findViewById(R.id.ui_inquire_search_food_input);
        m_foodClassSpin = (Spinner) findViewById(R.id.ui_inquire_search_food_class_selector);
        m_gridview = (GridView)findViewById(R.id.ui_inquire_search_grid);
        
        InitFoodClassSpin(null);
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
					UpdateGrid();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});

        InitInputTextView();
        InitGrid();
        
        m_gridview.setFocusable(true);
        m_gridview.setFocusableInTouchMode(true);
        m_gridview.requestFocus();
    }
}
