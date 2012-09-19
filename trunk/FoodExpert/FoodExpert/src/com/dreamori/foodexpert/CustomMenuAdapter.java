package com.dreamori.foodexpert;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomMenuAdapter extends BaseAdapter {
	Context m_context;
	
	public CustomMenuAdapter(Context context) {
		m_context = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 3;
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
			convertView = LayoutInflater.from(m_context).inflate(R.layout.ui_menu_item, null);
			
			holder.icon = (ImageView) convertView.findViewById(R.id.ui_menu_item_img);
			holder.name = (TextView) convertView.findViewById(R.id.ui_menu_item_txt);
			
			convertView.setTag(holder);
		} 
		else 
		{
			holder = (GridViewHolder) convertView.getTag();
		}
		
		switch(position)
		{
		case 0:
			holder.icon.setImageResource(R.drawable.tip);
			TitleActivity act = (TitleActivity) m_context;
			if(ConfigData.GetSystemTip(act.GetConfigFileName()) == 0)
			{
				holder.name.setText(m_context.getString(R.string.menu_show_tip));
			}
			else
			{
				holder.name.setText(m_context.getString(R.string.menu_hide_tip));
			}
			break;
		case 1:
			holder.icon.setImageResource(R.drawable.email);
			holder.name.setText(m_context.getString(R.string.menu_contact));			
			break;
		case 2:
			holder.icon.setImageResource(R.drawable.exit);
			holder.name.setText(m_context.getString(R.string.menu_exit));			
			break;
		default:
			break;
		}
		
		return convertView;
	}

}
