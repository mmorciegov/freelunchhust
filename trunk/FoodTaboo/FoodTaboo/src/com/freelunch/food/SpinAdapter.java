package com.freelunch.food;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SpinAdapter extends BaseAdapter {
	private List<String> m_data;
	private Context m_context;
	
	public SpinAdapter(Context context, List<String> data) {
		m_context = context;
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
		TextView text = new TextView(m_context);
		text.setText(m_data.get(position));
		return text;
	}

}
