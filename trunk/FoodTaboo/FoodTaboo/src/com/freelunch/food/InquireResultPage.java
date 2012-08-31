package com.freelunch.food;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class InquireResultPage extends Activity {
	private Context m_context;
	
	private AutoCompleteTextView m_textview;
	private Spinner m_foodClassSpin;
	private Spinner m_relativeSpin;
	private GridView m_gridview;
	
	private Databasehelper m_dbHelper = null;	
	private String m_curFoodClass;
	private List<String> m_curFoodList;
	
	private void InitFoodClassSpin(String foodClass)
	{
		List<String> dataList = m_dbHelper.GetFoodClassList();
		SpinAdapter adapter = new SpinAdapter(m_context, dataList);
		m_foodClassSpin.setAdapter(adapter);
		
		int index = 0;
		for (int i=0; i<dataList.size(); i++)
		{
			if (foodClass.equalsIgnoreCase(dataList.get(i)))
			{
				index = i;
				break;
			}
		}
		m_foodClassSpin.setSelection(index);
		
		m_curFoodClass = foodClass;
		m_curFoodList = m_dbHelper.getPreferFoodList(m_curFoodClass);
		
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
	}
	
	private void InitRelativeSpin(int pos)
	{
		List<String> dataList = new ArrayList<String>();
		dataList.add("全部");
		dataList.add("相生");
		dataList.add("相克");
		
		SpinAdapter adapter = new SpinAdapter(m_context, dataList);
		m_relativeSpin.setAdapter(adapter);
		m_relativeSpin.setSelection(pos);
	}
	
	private boolean IsFoodValid(String foodName)
	{
		for (int i=0; i<m_curFoodList.size(); i++)
		{
			if (foodName.equalsIgnoreCase(m_curFoodList.get(i)))
			{
				return true;
			}
		}
		return false;
	}
	
	private void UpdateInputTextView()
	{
	    ArrayAdapter<String> textViewAdapter = new ArrayAdapter<String>(this,   
	            android.R.layout.simple_dropdown_item_1line, m_curFoodList);   
	    m_textview.setAdapter(textViewAdapter);  	    
	    m_textview.setThreshold(1); 		
	}
	
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
			goodBad = m_dbHelper.ALL_COMBINATION;
			break;
		case 1:
			goodBad = m_dbHelper.GOOD_COMBINATION;
			break;
		default:
			goodBad = m_dbHelper.BAD_COMBINATION;
			break;
		}

		m_dbHelper.findRelatedFood(curFood, dataList, goodBad);
	}
	
	private int GetDegreeId(int degree)
	{
		int id = 0;
		switch(degree)
		{
		case 1:
			id = R.drawable.good_1;
			break;
		case 2:
			id = R.drawable.good_2;
			break;
		case 3:
			id = R.drawable.good_3;
			break;
		case -1:
			id = R.drawable.bad_1;
			break;
		case -2:
			id = R.drawable.bad_2;
			break;			
		case -3:
			id = R.drawable.bad_3;
			break;
		default:
			break;
		}
		
		return id;
	}
	
	private void UpdateGrid(List<RelativeData> dataList)
	{
        List<GridViewHolderData> gridDataList = new ArrayList<GridViewHolderData>();
		for (int i=0; i<dataList.size(); i++)
		{
	        GridViewHolderData data = new GridViewHolderData();
	        data.icon = R.drawable.food;
	        data.name = dataList.get(i).name;
	        data.degree = GetDegreeId(dataList.get(i).degree);
	        gridDataList.add(data);		
		}
        
        ListAdapter adapter = new GridViewAdapter(m_context, gridDataList);
        m_gridview.setAdapter(adapter);		
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
        InitRelativeSpin(relativeFlag);
        InitInputTextView(foodName);
        
        List<RelativeData> dataList = new ArrayList<RelativeData>();
        Search(foodName, relativeFlag, dataList);
        
        InitGrid(dataList);
        
        m_gridview.setFocusable(true);
        m_gridview.setFocusableInTouchMode(true);
        m_gridview.requestFocus();        
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
}
