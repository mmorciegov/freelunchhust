package com.dreamori.foodexpert;

import java.util.ArrayList;
import java.util.List;

import android.widget.GridView;
import android.widget.ListAdapter;

public class GridViewBasePage extends TitleActivity {		
	private List<GridViewHolderData> m_gridDataList = null;
	private ListAdapter m_adapter = null;
		
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if( m_gridDataList != null )
		{
			m_gridDataList.clear();
			m_gridDataList = null;
			
			m_adapter = null;
		}
	}

	public void UpdateGrid(List<RelativeData> dataList, Boolean showIcon )
	{
		if( m_gridDataList != null )
		{
			m_gridDataList.clear();
		}
        m_gridDataList = new ArrayList<GridViewHolderData>();
		for (int i=0; i<dataList.size(); i++)
		{
	        GridViewHolderData data = new GridViewHolderData();
	        data.name = dataList.get(i).name;
	        data.icon = ResourceManager.GetIcon(this,  getDatabaseHelper().getIconName(data.name) );
	        if( showIcon )
	        {
	        	data.degree = ResourceManager.GetDegreeId(dataList.get(i).degree);
	        }
	        else
	        {
	        	data.degree = 0;
	        }
	        
	        m_gridDataList.add(data);		
		}
        
		m_adapter = new GridViewAdapter(this, m_gridDataList);
        m_gridview.setAdapter(m_adapter);		
	}
}
