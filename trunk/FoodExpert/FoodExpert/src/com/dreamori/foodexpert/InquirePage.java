package com.dreamori.foodexpert;

import java.util.ArrayList;
import java.util.List;

import android.widget.GridView;
import android.widget.ListAdapter;

public class InquirePage extends TitleActivity {

	public GridView m_gridview;
	
	public void UpdateGrid(List<RelativeData> dataList)
	{
        List<GridViewHolderData> gridDataList = new ArrayList<GridViewHolderData>();
		for (int i=0; i<dataList.size(); i++)
		{
	        GridViewHolderData data = new GridViewHolderData();
	        data.name = dataList.get(i).name;
	        data.icon = ResourceManager.GetIcon(this,  getDatabaseHelper().getIconName(data.name));
	        data.degree = ResourceManager.GetDegreeId(dataList.get(i).degree);

	        gridDataList.add(data);		
		}
        
        ListAdapter adapter = new GridViewAdapter(m_context, gridDataList);
        m_gridview.setAdapter(adapter);		
	}
}
