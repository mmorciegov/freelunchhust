package com.dreamori.foodexpert;

import java.util.ArrayList;
import java.util.List;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public class InquirePage extends TitleActivity {
	public AutoCompleteTextView m_textview;
	public Spinner m_foodClassSpin;
	public GridView m_gridview;
	
	public DatabaseHelper m_dbHelper = null;	
	public String m_curFoodClass;
	public List<String> m_curFoodList;
	
	public boolean IsFoodValid(String foodName)
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
	
	public void InitFoodClassSpin(String foodClass)
	{
		List<String> dataList = m_dbHelper.GetFoodClassList();
		SpinAdapter adapter = new SpinAdapter(m_context, dataList);
		m_foodClassSpin.setAdapter(adapter);
		
		if (foodClass == null)
		{
			m_curFoodClass = dataList.get(0);
		}
		else
		{
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
		}
		m_curFoodList = m_dbHelper.getPreferFoodList(m_curFoodClass);
	}
	
	public void UpdateInputTextView()
	{
	    ArrayAdapter<String> textViewAdapter = new ArrayAdapter<String>(this,   
	            android.R.layout.simple_dropdown_item_1line, m_curFoodList);   
	    m_textview.setAdapter(textViewAdapter);  	    
	    m_textview.setThreshold(1); 		
	}
	
	public void UpdateGrid(List<RelativeData> dataList)
	{
        List<GridViewHolderData> gridDataList = new ArrayList<GridViewHolderData>();
		for (int i=0; i<dataList.size(); i++)
		{
	        GridViewHolderData data = new GridViewHolderData();
	        data.name = dataList.get(i).name;
	        data.icon = ResourceManager.GetIcon(this,  m_dbHelper.getIconName(data.name));
	        data.degree = ResourceManager.GetDegreeId(dataList.get(i).degree);

	        gridDataList.add(data);		
		}
        
        ListAdapter adapter = new GridViewAdapter(m_context, gridDataList);
        m_gridview.setAdapter(adapter);		
	}
}
