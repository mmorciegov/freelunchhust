package com.dreamori.foodexpert;

import java.util.ArrayList;
import java.util.List;

import com.dreamori.foodexpert.ComboBox.GirdViewItemClickListener;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class InquireSearchPage extends GridViewBasePage {
	private AutoCompleteTextView m_textview;
	private ComboBox m_foodClassSpin;
	
	private String m_curFoodClass;
	private List<String> m_curFoodList;
	private List<String> m_FoodClassList;
	private List<String> m_allFoodList;
	
	private List<String> m_displayFoodList;
	
	private List<RelativeData> m_dataList = null;

		
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		m_textview = null;
		m_foodClassSpin = null;
	}

	class SearchFoodFilter extends Filter
	{
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			// TODO Auto-generated method stub
			FilterResults results = new FilterResults(); 
			if (m_allFoodList == null) 
			{  
				return null;
			} 

			if (constraint == null || constraint.length() == 0) 
			{  
				results.values = m_allFoodList;  
				results.count = m_allFoodList.size();  
			} 
			else { 
				List<String> curList = new ArrayList<String>();
	
				for (int i = 0; i < m_allFoodList.size(); i++) { 
					if (m_allFoodList.get(i).contains(constraint.toString().toLowerCase()))
					{
						curList.add(m_allFoodList.get(i));
					}
				}
				results.values = curList;  
				results.count = curList.size();  
			}  
			return results;  			
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			// TODO Auto-generated method stub
			m_displayFoodList = (List<String>) results.values;
		}
	}
	
	class SearchFoodAdapter extends BaseAdapter implements Filterable
	{
		Filter m_filter = null;
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return m_displayFoodList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return m_displayFoodList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView text = new TextView(InquireSearchPage.this);
			text.setText(m_displayFoodList.get(position));
			text.setTextSize(30);
			text.setPadding(0, 3, 0, 0);
			return text;
		}

		@Override
		public Filter getFilter() {
			// TODO Auto-generated method stub
			if (m_filter == null)
			{
				m_filter = new SearchFoodFilter();
			}
			return m_filter;
		}
		
	}
	
	private boolean IsFoodValid(String foodName)
	{
		if(m_allFoodList.contains(foodName))
		{
			return true;
		}

		return false;
	}
	
	private List<String> GetFoodClassList()
	{	
 		List<String>  foodClassList = new ArrayList<String>();	
 		//foodClassList.add(getString(R.string.food_class_usual));	
 		foodClassList.add(getString(R.string.food_class_all));    
 		foodClassList.addAll(getDatabaseHelper().GetFoodClassList());
    	
    	return foodClassList;	
	}
		
	private List<String> GetPreferFoodList( String foodClassName )
	{
     	if (foodClassName.equalsIgnoreCase(getString(R.string.food_class_all))) 
    	{
    		return getDatabaseHelper().getAllFoodList();
    	}
//    	else if (foodClassName.equalsIgnoreCase(getString( R.string.food_class_usual)))
//    	{
//    		List<String> dataList = new ArrayList<String>();
//   			List<String> foodList = getDatabaseHelper().getAllFoodList();
//    		int count = foodList.size() < FoodConst.COMMON_FOOD_INIT_COUNT ? foodList.size() : FoodConst.COMMON_FOOD_INIT_COUNT;
//    		for (int i=0; i<count; i++)
//    		{
//    			dataList.add(foodList.get(i));
//    		}
//    		return dataList;
//    	}
    	else
    	{		    		
    		return getDatabaseHelper().getPreferFoodList(foodClassName);
    	}	    
	}	

	private void InitFoodClassSpin(String foodClass)
	{
		m_FoodClassList = GetFoodClassList();
		m_foodClassSpin.setData(m_FoodClassList);
		m_curFoodClass = m_FoodClassList.get(0);
		m_curFoodList = GetPreferFoodList(m_curFoodClass);
		
		if( m_allFoodList == null )
		{
			m_allFoodList = getDatabaseHelper().getAllFoodListWithAliasName();
		}
		
	}
	
	private void UpdateInputTextView()
	{
		SearchFoodAdapter textViewAdapter = new SearchFoodAdapter();
	    m_textview.setAdapter(textViewAdapter);  	    
	    m_textview.setThreshold(1); 		
	}

	private void InitInputTextView()
	{
		UpdateInputTextView();
	    m_textview.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String foodName = m_textview.getText().toString();
				if (IsFoodValid(foodName))
				{			
					m_textview.setText(null);
			        m_gridview.setFocusable(true);
			        m_gridview.setFocusableInTouchMode(true);
			        m_gridview.requestFocus();
			        
					GotoResultPage(foodName);
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
	
	private void GotoResultPage( String curFood )
	{
		Bundle bind = new Bundle();
		bind.putSerializable("FoodClass", m_curFoodClass);
		bind.putSerializable("FoodName", curFood);
		    	
		Intent intent = new Intent(InquireSearchPage.this, InquireResultPage.class);
		intent.putExtras(bind);
		
		startActivity(intent);
	}
		
	private void UpdateGrid()
	{
		if( m_dataList != null )
		{
			m_dataList.clear();
		}
		else
		{
			m_dataList = new ArrayList<RelativeData>();
		}
		
		for(int i = 0; i < m_curFoodList.size(); i++ )
		{
			m_dataList.add(new RelativeData(m_curFoodList.get(i)));
		}
		
		UpdateGrid( m_dataList, FoodConst.GRID_DEFAULT );			
	}
	
	private void InitGrid()
	{		
		UpdateGrid( );
        
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
        
        m_level = 2;
        
    	//Get Database
        m_textview = (AutoCompleteTextView) findViewById(R.id.ui_inquire_search_food_input);
        m_foodClassSpin = (ComboBox) findViewById(R.id.ui_inquire_search_food_class_selector);
        m_gridview = (GridView)findViewById(R.id.ui_inquire_search_grid);
        
        InitFoodClassSpin(null);
        m_foodClassSpin.setGridViewOnClickListener(new GirdViewItemClickListener(){
			@Override
			public void onItemClick(int position) {
				// TODO Auto-generated method stub
				if (!m_FoodClassList.get(position).equalsIgnoreCase(m_curFoodClass))
				{
					m_curFoodClass = m_FoodClassList.get(position);
					m_curFoodList = GetPreferFoodList(m_curFoodClass);		
					
					UpdateInputTextView();
					UpdateGrid();
				}
			}
        });

        InitInputTextView();
        InitGrid();
        
        m_gridview.setFocusable(true);
        m_gridview.setFocusableInTouchMode(true);
        m_gridview.requestFocus();
    }
}
