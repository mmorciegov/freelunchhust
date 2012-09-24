package com.dreamori.foodexpert;

import java.util.ArrayList;
import java.util.List;

import com.dreamori.foodexpert.ComboBox.GirdViewItemClickListener;

import android.os.Bundle;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DiseaseResultPage extends GridViewBasePage {
	
	private String m_diseasePageSearchName;
	private int  m_searchType;
	private List<RelativeData> m_dataList = null;
	
	private ComboBox m_foodClassSpin;
	private AutoCompleteTextView m_textView;
	
	
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if( m_dataList != null )
		{
			m_dataList.clear();
			m_dataList = null;
		}
	}

	public void Search(String value, int searchType, String foodClass, List<RelativeData> dataList)
	{
		if( dataList != null )
		{
			dataList.clear();
		}
		getDatabaseHelper().findDiseaseRelatedInfo(value, searchType, foodClass, dataList);
	}
	
	private void GotoResultPage( String curChoice )
	{
		Bundle bundle = new Bundle();
		if( m_searchType == FoodConst.DISEASE_RESULT_SEARCH_DISEASE )
		{
			bundle.putSerializable("Name1", m_diseasePageSearchName);
			bundle.putSerializable("Name2", curChoice);
		}
		else
		{
			bundle.putSerializable("Name1", curChoice);
			bundle.putSerializable("Name2", m_diseasePageSearchName);		
		}

		bundle.putSerializable("Relative", FoodConst.ACTIVITY_TYPE_DISEASE);
	
		Intent intent = new Intent(DiseaseResultPage.this, DetailInfoPage.class);
		intent.putExtras(bundle);
		
		startActivity(intent);
	}
	
	private void UpdateGrid(List<RelativeData> dataList)
	{
		UpdateGrid(dataList, FoodConst.GRID_DISEASE_RESULT);
        
        m_gridview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				GridViewHolder holder = (GridViewHolder) arg1.getTag();		
				if(m_textView.getVisibility() == View.GONE)
				{
					GotoResultPage(holder.name.getText().toString());
				}
				else {
					m_textView.setText(holder.name.getText());	
				}
				

			}
        });        
	}
	
	private String m_curFoodClass;
	private List<String> m_FoodClassList;
	
	public List<String> GetFoodClassListForDiseasePage()
	{	
 		List<String>  foodClassList = new ArrayList<String>();    	
 		foodClassList.add(getString(R.string.food_class_all));
 		foodClassList.addAll(getDatabaseHelper().GetFoodClassList()); 		
    	
    	return foodClassList;	
	}
	
	private void HideFoodClassAndSearchData()
	{
		m_textView.setVisibility(View.GONE);
		m_foodClassSpin.setVisibility(View.GONE);		
	}

	private void InitFoodClassAndSearchData() {
		m_FoodClassList = GetFoodClassListForDiseasePage();
		m_foodClassSpin.setData(m_FoodClassList);
		m_curFoodClass = m_FoodClassList.get(0);
		if (0 == m_curFoodClass
				.compareToIgnoreCase(getString(R.string.food_class_all))) {
			m_curFoodClass = "";
		}

		m_foodClassSpin
		.setGridViewOnClickListener(new GirdViewItemClickListener() {
			@Override
			public void onItemClick(int position) {
				// TODO Auto-generated method stub
				if (!m_FoodClassList.get(position).equalsIgnoreCase(
						m_curFoodClass)) {
					m_curFoodClass = m_FoodClassList.get(position);
					if (0 == m_curFoodClass
							.compareToIgnoreCase(getString(R.string.food_class_all))) {
						m_curFoodClass = "";
					}

					Search(m_diseasePageSearchName, m_searchType,
							m_curFoodClass, m_dataList);

					UpdateGrid(m_dataList);
				}
			}
		});
		
		
		InitInputTextView();
	}

	private List<String> m_relatedDeseaseFoodList;

	private List<String> m_displayFoodList;
	
	class SearchFoodFilter extends Filter
	{
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			// TODO Auto-generated method stub
			FilterResults results = new FilterResults(); 
			if (m_relatedDeseaseFoodList == null) 
			{  
				return null;
			} 

			if (constraint == null || constraint.length() == 0) 
			{  
				results.values = m_relatedDeseaseFoodList;  
				results.count = m_relatedDeseaseFoodList.size();  
			} 
			else { 
				List<String> curList = new ArrayList<String>();
	
				for (int i = 0; i < m_relatedDeseaseFoodList.size(); i++) { 
					if (m_relatedDeseaseFoodList.get(i).contains(constraint.toString().toLowerCase()))
					{
						curList.add(m_relatedDeseaseFoodList.get(i));
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
			TextView text = new TextView(DiseaseResultPage.this);
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
	
	private void UpdateInputTextView()
	{
		SearchFoodAdapter textViewAdapter = new SearchFoodAdapter();
	    m_textView.setAdapter(textViewAdapter);  	    
	    m_textView.setThreshold(1); 		
	}
	
	private boolean IsFoodValid(String foodName)
	{
		if( m_relatedDeseaseFoodList.contains(foodName) )
		{
			return true;
		}		
		
		return false;
	}
	
	private void InitInputTextView()
	{
		m_relatedDeseaseFoodList = getDatabaseHelper().getFoodListFromDisease(m_diseasePageSearchName);
		UpdateInputTextView();
	    m_textView.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String foodName = m_textView.getText().toString();
				if (IsFoodValid(foodName))
				{			
					m_textView.setText(null);
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
	

		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_disease_result);
        m_level = 3;
        m_gridview = (GridView)findViewById(R.id.ui_disease_result_grid);   
		m_textView = (AutoCompleteTextView) findViewById(R.id.ui_disease_result_search_food_input);
		m_foodClassSpin = (ComboBox) findViewById(R.id.ui_disease_result_food_class_selector);
        
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        
        m_diseasePageSearchName = bundle.getString(FoodConst.INTENT_DISEASE);
        
        m_searchType = bundle.getInt(FoodConst.INTENT_DISEASE_SEARCH_TYPE);
        
        if( m_searchType == FoodConst.DISEASE_RESULT_SEARCH_DISEASE )
        {        	
        	setTitle(getString(R.string.title_choose) + m_diseasePageSearchName + getString(R.string.title_food_related));
            InitFoodClassAndSearchData();
            ((TextView)findViewById(R.id.ui_disease_result_cur_text)).setText(
            		getString(R.string.with) +  m_diseasePageSearchName + getString(R.string.result_food));
        }
        else
        {
        	setTitle(getString(R.string.title_choose) + m_diseasePageSearchName + getString(R.string.title_disease_related));
        	HideFoodClassAndSearchData();
            ((TextView)findViewById(R.id.ui_disease_result_cur_text)).setText(
            		getString(R.string.with) +  m_diseasePageSearchName + getString(R.string.result_disease));        	 
		}               
    
        ImageView imageViewSearchIcon = (ImageView)findViewById(R.id.ui_disease_result_cur_pic);
        if( imageViewSearchIcon != null )
        {
        	InitSearchIcon(imageViewSearchIcon, m_diseasePageSearchName);
        }
                
		if( m_dataList != null )
		{
			m_dataList.clear();
		}
		else
		{
			m_dataList = new ArrayList<RelativeData>();
		}

        Search(m_diseasePageSearchName, m_searchType, m_curFoodClass, m_dataList);         
        
        UpdateGrid(m_dataList);
        
        m_gridview.setFocusable(true);
        m_gridview.setFocusableInTouchMode(true);
        m_gridview.requestFocus();   
        
    }


}
