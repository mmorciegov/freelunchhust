package com.dreamori.foodexpert;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.widget.ListAdapter;

public class GridViewBasePage extends TitleActivity {		
	private List<GridViewHolderData> m_gridDataList = null;
	private ListAdapter m_adapter = null;
	
	private BitmapDrawable bDrawable = null;
	
	public void InitSearchIcon(ImageView image,  String name)
	{
		bDrawable = ResourceManager.GetBitmapDrawable(this,  getDatabaseHelper().getIconName(name) );
		image.setImageDrawable(bDrawable);
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

		ReleaseIconRes();
		m_adapter = null;
		
		if( bDrawable != null )
		{
			bDrawable.setCallback(null);
			bDrawable.getBitmap().recycle();
			bDrawable = null;
		}
	}
	
	private void ReleaseIconRes()
	{
		if( m_gridDataList != null )
		{
			for (int i = 0; i < m_gridDataList.size(); i++) 
			{
				DreamoriLog.LogFoodExpert("Release Icon Res in GridViewBasePage " + i );
				if( m_gridDataList.get(i).icon != null )
				{
					m_gridDataList.get(i).icon.setCallback(null);
					m_gridDataList.get(i).icon.getBitmap().recycle();	
					m_gridDataList.get(i).icon = null;
				}
				
				if( m_gridDataList.get(i).degree != null )
				{
					m_gridDataList.get(i).degree.setCallback(null);
					m_gridDataList.get(i).degree.getBitmap().recycle();	
					m_gridDataList.get(i).degree = null;
				}		
			}			
			
			m_gridDataList.clear();
			m_gridDataList = null;
		}	
	}

	public void UpdateGrid(List<RelativeData> dataList, int gridType )
	{
		ReleaseIconRes();

        m_gridDataList = new ArrayList<GridViewHolderData>();
		for (int i=0; i<dataList.size(); i++)
		{
	        GridViewHolderData data = new GridViewHolderData();
	        data.name = dataList.get(i).name;
	        data.icon = ResourceManager.GetBitmapDrawable(this,  getDatabaseHelper().getIconName(data.name) );
	        switch(gridType)
	        {
	        case FoodConst.GRID_DISEASE_RESULT:
	        case FoodConst.GRID_FOOD_RESULT:
	        	data.degree = ResourceManager.GetBitmapDrawable(this, ResourceManager.GetDegreeIconName(dataList.get(i).degree));
	        	break;
	        default:
	        	data.degree = null;
	        	break;
	        }
	        
	        m_gridDataList.add(data);		
		}
        
		m_adapter = new GridViewAdapter(this, m_gridDataList, gridType);
        m_gridview.setAdapter(m_adapter);		
	}
}
