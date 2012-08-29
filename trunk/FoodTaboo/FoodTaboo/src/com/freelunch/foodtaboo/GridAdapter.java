package com.freelunch.foodtaboo;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridAdapter extends BaseAdapter {
	private List<Map<String, Object>> mData;
	private LayoutInflater mInflater;
	private DisplayMetrics localDisplayMetrics;
	
	public GridAdapter(Context context, List<Map<String, Object>> data) {
		mInflater = LayoutInflater.from(context);
		mData = data;
		localDisplayMetrics = context.getResources().getDisplayMetrics();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
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
		GridViewHolder holder = null;
		if (convertView == null) 
		{
			holder = new GridViewHolder();
	
			convertView = mInflater.inflate(R.layout.gridview_item, null);
			
			holder.photo = (ImageView) convertView.findViewById(R.id.gridview_icon);
			holder.name = (TextView) convertView.findViewById(R.id.gridview_name);
			holder.degree = (ImageView) convertView.findViewById(R.id.gridview_degree);
			
			convertView.setTag(holder);
	
		} 
		else 
		{
			holder = (GridViewHolder) convertView.getTag();
		}
		
		holder.photo.setBackgroundResource((Integer) mData.get(position).get("photo"));
		if (mData.get(position).get("hint") != null)
		{
			holder.photo.setTag(mData.get(position).get("hint"));
		}
		holder.name.setText((String) mData.get(position).get("name"));
		if (mData.get(position).get("degree") != null)
		{
			holder.degree.setBackgroundResource((Integer) mData.get(position).get("degree"));
		}
			
		convertView.setMinimumHeight((int)(96.0F * localDisplayMetrics.density));
		convertView.setMinimumWidth(((-12 + localDisplayMetrics.widthPixels) / 3));
		
		return convertView;
	}

}
