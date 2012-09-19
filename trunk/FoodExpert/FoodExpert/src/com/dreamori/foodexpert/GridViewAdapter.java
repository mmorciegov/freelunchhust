package com.dreamori.foodexpert;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridViewAdapter extends BaseAdapter {
	private List<GridViewHolderData> m_data;
	private LayoutInflater m_inflater;
	private Context m_context;
	
	public GridViewAdapter(Context context, List<GridViewHolderData> data) {
		m_context = context;
		m_inflater = LayoutInflater.from(context);
		m_data = data;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return m_data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return m_data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	private void SetImageView(ImageView imageView, int resid)
	{
		Bitmap bm = BitmapFactory.decodeResource(m_context.getResources(), resid);
		BitmapDrawable bd = new BitmapDrawable(m_context.getResources(), bm);
		imageView.setBackgroundDrawable(bd);
		
		DreamoriLog.LogFoodExpert("new BitmapDrawable from SetImageView");
		
//		imageView.setBackgroundResource(resid);
	}
	
	public static void ReleaseImageView(ImageView imageView)
	{
		BitmapDrawable bd = (BitmapDrawable) imageView.getBackground();
		imageView.setBackgroundResource(0);
		bd.setCallback(null);
		bd.getBitmap().recycle();
		
		
		DreamoriLog.LogFoodExpert("new BitmapDrawable from SetImageView");
		
//		imageView.setBackgroundResource(0);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		GridViewHolder holder = null;
		if (convertView == null) 
		{
			holder = new GridViewHolder();
			convertView = m_inflater.inflate(R.layout.gridview_item, null);
			
			holder.icon = (ImageView) convertView.findViewById(R.id.gridview_item_icon);
			holder.name = (TextView) convertView.findViewById(R.id.gridview_item_name);
			holder.degree = (ImageView) convertView.findViewById(R.id.gridview_item_degree);
			
			convertView.setTag(holder);
			

			if (m_data.get(position).icon != 0)
			{
				SetImageView(holder.icon, m_data.get(position).icon);
			}
			else
			{
				holder.icon.setVisibility(View.GONE);
			}
			
			if (m_data.get(position).name != null && m_data.get(position).name.length() > 0)
			{
				holder.name.setText(m_data.get(position).name);
			}
			else
			{
				holder.name.setVisibility(View.GONE);
			}
			
			if (m_data.get(position).degree != 0)
			{
				SetImageView(holder.degree, m_data.get(position).degree);
			}
			else
			{
				holder.degree.setVisibility(View.GONE);
			}
		} 
		else 
		{
			holder = (GridViewHolder) convertView.getTag();
		}
		
		
		return convertView;
	}

}
