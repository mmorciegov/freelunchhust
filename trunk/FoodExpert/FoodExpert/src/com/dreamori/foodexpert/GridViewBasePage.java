package com.dreamori.foodexpert;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.ImageView;
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
	
	
	private void ReleaseGridBitmap()
	{
		if (m_gridview.getChildCount() <= 0)
		{
			return;
		}
		
		for (int i=0; i<m_gridview.getChildCount(); i++)
		{
			View convertView = m_gridview.getChildAt(i);
			GridViewHolder holder = (GridViewHolder) convertView.getTag();
			ImageView imgView = holder.icon;
			
			GridViewAdapter.ReleaseImageView(imgView);
		}
	}

	public void UpdateGrid(List<RelativeData> dataList, int showDegree )
	{
//		ReleaseIconRes();
		ReleaseGridBitmap();
		Boolean isScaleDegree = false;
        m_gridDataList = new ArrayList<GridViewHolderData>();
		for (int i=0; i<dataList.size(); i++)
		{
	        GridViewHolderData data = new GridViewHolderData();
	        data.name = dataList.get(i).name;
	        data.icon = ResourceManager.GetIcon(this,  getDatabaseHelper().getIconName(data.name) );
	        switch(showDegree)
	        {
	        case FoodConst.DEGREE_BOOL:
	        	isScaleDegree = true;
	        case FoodConst.DEGREE_SHOW:
	        	data.degree = ResourceManager.GetDegreeId(dataList.get(i).degree);
	        	break;
	        case FoodConst.DEGREE_HIDE:
	        default:
	        	data.degree = 0;
	        	break;
	        }
	        
	        m_gridDataList.add(data);		
		}
        
		m_adapter = new GridViewAdapter(this, m_gridDataList, isScaleDegree);
        m_gridview.setAdapter(m_adapter);		
	}
}
