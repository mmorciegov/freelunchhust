package com.freelunch.food;

import java.util.ArrayList;
import java.util.List;

import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public class DiseasePage extends TitleActivity {
	public Spinner m_diseaseSpin;
	public Spinner m_relativeSpin;
	public GridView m_gridview;
	public ListView m_listview;
	
	public DatabaseHelper m_dbHelper = null;	
	public String m_curDisease;
	
	public void InitDiseaseSpin()
	{
		List<String> dataList = m_dbHelper.GetDiseaseList();
		SpinAdapter adapter = new SpinAdapter(m_context, dataList);
		m_diseaseSpin.setAdapter(adapter);
		
		m_curDisease = dataList.get(0);		
	}
	
	public void InitRelativeSpin()
	{
		List<String> dataList = new ArrayList<String>();
		dataList.add("全部");
		dataList.add("相生");
		dataList.add("相克");
		
		SpinAdapter adapter = new SpinAdapter(m_context, dataList);
		m_relativeSpin.setAdapter(adapter);
	}
	
	public void Search(String curDisease, int relativeFlag, List<RelativeData> dataList)
	{
		String goodBad = null;
		
		switch(relativeFlag)
		{
		case 0:
			goodBad = DatabaseHelper.ALL_COMBINATION;
			break;
		case 1:
			goodBad = DatabaseHelper.GOOD_COMBINATION;
			break;
		default:
			goodBad = DatabaseHelper.BAD_COMBINATION;
			break;
		}

		m_dbHelper.findRelatedFoodByDisease(curDisease, dataList, goodBad);
	}
	
	public void UpdateGrid(List<RelativeData> dataList)
	{
        List<GridViewHolderData> gridDataList = new ArrayList<GridViewHolderData>();
		for (int i=0; i<dataList.size(); i++)
		{
	        GridViewHolderData data = new GridViewHolderData();
	        data.name = dataList.get(i).name;
	        data.icon = ResourceManager.GetIcon(this, data.name );
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
	        data.icon = ResourceManager.GetIcon(this, data.name);
	        data.degree = ResourceManager.GetDegreeId(dataList.get(i).degree);
	        listDataList.add(data);		
		}
        
        ListAdapter adapter = new ListViewAdapter(m_context, listDataList);
        m_listview.setAdapter(adapter);		
	}
}
