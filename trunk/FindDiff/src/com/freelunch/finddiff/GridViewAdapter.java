package com.freelunch.finddiff;

import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GridViewAdapter extends BaseAdapter {
	Context m_context;
	boolean m_bCanNext;
	
	public GridViewAdapter(Context context, boolean canNext) {
		m_context = context;
		m_bCanNext = canNext;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ImageView img = null;
		if (convertView == null) 
		{
			convertView = LayoutInflater.from(m_context).inflate(R.layout.gridview_item, null);
			img = (ImageView)convertView.findViewById(R.id.gridview_item_icon);
			convertView.setTag(img);
		} 
		else 
		{
			img = (ImageView) convertView.getTag();
		}
		
		if (position == 0)
		{
			img.setBackgroundResource(R.drawable.redo);
		}
		else 
		{
			img.setBackgroundResource(R.drawable.next);
			if (!m_bCanNext)
			{
				img.setAlpha(0.5f);
			}
		}
		
		return convertView;
	}

}
