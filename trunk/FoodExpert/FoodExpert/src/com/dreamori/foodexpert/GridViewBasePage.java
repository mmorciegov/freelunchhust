package com.dreamori.foodexpert;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ListAdapter;

public class GridViewBasePage extends TitleActivity {		
	private List<GridViewHolderData> m_gridDataList = null;
	private ListAdapter m_adapter = null;
		
	@Override
	protected void onDestroy() {
		super.onDestroy();

//		ReleaseIconRes();
		m_adapter = null;
	}
//	
//	private void ReleaseIconRes()
//	{
//		if( m_gridDataList != null )
//		{
//			DreamoriLog.LogFoodExpert("Release Icon Res in GridViewBasePage");
//			int iconId = 0;
//			for (int i = 0; i < m_gridDataList.size(); i++) 
//			{
//				iconId = m_gridDataList.get(i).icon;
//				Bitmap icon = BitmapFactory.decodeResource(getResources(), iconId);
//				if( icon != null && !icon.isRecycled())
//				{
//					icon.recycle();
//					icon = null;
//				}
//			}
//			
//			
//			m_gridDataList.clear();
//			m_gridDataList = null;
//		}	
//	}

	public void UpdateGrid(List<RelativeData> dataList, Boolean showIcon )
	{
//		ReleaseIconRes();
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
