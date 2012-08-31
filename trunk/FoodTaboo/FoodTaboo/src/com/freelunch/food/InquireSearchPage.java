package com.freelunch.food;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Spinner;
import android.widget.TextView;

public class InquireSearchPage extends Activity {
	private Context m_context;
	
	private AutoCompleteTextView m_textview;
	private Spinner m_foodClassSpin;
	private Spinner m_relativeSpin;
	private GridView m_gridview;
	
	private Databasehelper m_dbHelper = null;	
	private String m_curFoodClass;
	private List<String> m_curFoodList;
	
	private void InitFoodClassSpin()
	{
		List<String> dataList = m_dbHelper.GetFoodClassList();
		SpinAdapter adapter = new SpinAdapter(m_context, dataList);
		m_foodClassSpin.setAdapter(adapter);
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
		
		m_curFoodClass = dataList.get(0);
		m_curFoodList = m_dbHelper.getPreferFoodList(m_curFoodClass);
	}
	
	private void InitRelativeSpin()
	{
		List<String> dataList = new ArrayList<String>();
		dataList.add("全部");
		dataList.add("相生");
		dataList.add("相克");
		
		SpinAdapter adapter = new SpinAdapter(m_context, dataList);
		m_relativeSpin.setAdapter(adapter);
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
		int relativeFlag = m_relativeSpin.getSelectedItemPosition();
		
		Bundle bind = new Bundle();
		bind.putSerializable("FoodClass", m_curFoodClass);
		bind.putSerializable("FoodName", curFood);
		bind.putSerializable("Relative", relativeFlag);
		
    	String filename = getApplicationContext().getFilesDir().getAbsoluteFile().toString() + "/config.xml";
    	ConfigData configData = new ConfigData();
    	XmlOperation.ReadXML(filename, configData);	
    	
		Intent intent = null;
		
		if (configData.display == 0)
		{
			intent = new Intent(InquireSearchPage.this, InquireResultPage.class);
		}
		else
		{
			intent = new Intent(InquireSearchPage.this, InquireResultListPage.class);
		}
		intent.putExtras(bind);
		
		startActivity(intent);
	}
	
	private void UpdateGrid()
	{
        List<GridViewHolderData> gridDataList = new ArrayList<GridViewHolderData>();
		for (int i=0; i<m_curFoodList.size(); i++)
		{
	        GridViewHolderData data = new GridViewHolderData();
	        data.icon = R.drawable.food;
	        data.name = m_curFoodList.get(i);
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
        m_dbHelper = Databasehelper.getInstance(m_context);        
        
        m_textview = (AutoCompleteTextView) findViewById(R.id.ui_inquire_search_food_input);
        m_foodClassSpin = (Spinner) findViewById(R.id.ui_inquire_search_food_class_selector);
        m_relativeSpin = (Spinner) findViewById(R.id.ui_inquire_search_food_relative);
        m_gridview = (GridView)findViewById(R.id.ui_inquire_search_grid);
        
        InitFoodClassSpin();
        InitRelativeSpin();
        InitInputTextView();
        InitGrid();
        
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
