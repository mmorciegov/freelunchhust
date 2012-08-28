package com.freelunch.foodtaboo;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {
	private List<Map<String, Object>> mData;
	private LayoutInflater mInflater;

	public ListViewAdapter(Context context, List<Map<String, Object>> data) {
		this.mInflater = LayoutInflater.from(context);
		mData = data;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}
	
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	
		ListViewHolder holder = null;
		if (convertView == null) 
		{
			holder = new ListViewHolder();
	
			convertView = mInflater.inflate(R.layout.listview_item, null);
			convertView.setMinimumHeight(100);
			
			holder.title = (TextView) convertView.findViewById(R.id.listview_name);
			holder.img = (ImageView) convertView.findViewById(R.id.listview_icon);
			
			convertView.setTag(holder);
	
		} 
		else 
		{
			holder = (ListViewHolder) convertView.getTag();
		}
		
		holder.title.setText((String) mData.get(position).get("title"));
		holder.img.setBackgroundResource((Integer) mData.get(position).get("img"));
			
		return convertView;
	}

}
