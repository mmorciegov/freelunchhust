package com.freelunch.food;

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
	public Spinner m_relativeSpin;
	
	public GridView m_gridview;
	public ListView m_listview;
	
	public Databasehelper m_dbHelper = null;	
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
	
	public void InitRelativeSpin(int pos)
	{
		List<String> dataList = new ArrayList<String>();
		dataList.add("全部");
		dataList.add("相生");
		dataList.add("相克");
		
		SpinAdapter adapter = new SpinAdapter(m_context, dataList);
		m_relativeSpin.setAdapter(adapter);
		m_relativeSpin.setSelection(pos);
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
	        data.icon = ResourceManager.GetIcon(data.name);
	        data.degree = ResourceManager.GetDegreeId(dataList.get(i).degree);
	        gridDataList.add(data);		
		}
        
        ListAdapter adapter = new GridViewAdapter(m_context, gridDataList);
        m_gridview.setAdapter(adapter);		
	}
	
	public void UpdateList(List<RelativeData> dataList)
	{
        List<ListViewHolderData> listDataList = new ArrayList<ListViewHolderData>();
		for (int i=0; i<dataList.size(); i++)
		{
			ListViewHolderData data = new ListViewHolderData();
	        data.name = dataList.get(i).name;
	        data.icon = ResourceManager.GetIcon(data.name);
	        data.degree = ResourceManager.GetDegreeId(dataList.get(i).degree);
	        listDataList.add(data);		
		}
        
        ListAdapter adapter = new ListViewAdapter(m_context, listDataList);
        m_listview.setAdapter(adapter);		
	}
}
