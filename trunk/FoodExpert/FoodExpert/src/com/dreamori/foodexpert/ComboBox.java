package com.dreamori.foodexpert;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ComboBox extends LinearLayout {
	private Context	m_context;
	private List<String> m_data;
	
	private Button m_Button;
	private TextView m_EditText;
	
	private View m_dropView;
	
	private GirdViewItemClickListener	m_listener;
	private PopupWindow 	m_popupwindow;
	
	public ComboBox(Context context) {
		super(context);
		m_context = context;		
		init();
	}

	public ComboBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		m_context = context;
		init();
	}
	
	private void init() {
		View mainView = LayoutInflater.from(m_context).inflate(R.layout.combobox_main, this, true);
		m_Button = (Button) mainView.findViewById(R.id.combobox_main_button);		
		m_EditText = (TextView) mainView.findViewById(R.id.combobox_main_text);
		
//		ComboBoxGridViewAdapter gridAdapter = new ComboBoxGridViewAdapter(m_context);
//		m_dropView = LayoutInflater.from(m_context).inflate(R.layout.combobox_grid, null);
//		GridView grid = (GridView)m_dropView.findViewById(R.id.combobox_grid_gridView);
//		grid.setAdapter(gridAdapter);
//		grid.setClickable(true);
//		grid.setOnItemClickListener(new OnItemClickListener(){
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//				// TODO Auto-generated method stub
//				m_popupwindow.dismiss();
//				m_EditText.setText(m_data.get(arg2));
//				
//				if (m_listener != null){
//					m_listener.onItemClick(arg2);
//				}
//			}
//		});
//		
		setListeners();
	}
	
	private void setListeners() {
		m_Button.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		
		m_EditText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				m_Button.performClick();
			}
		});
		
		m_Button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 if(m_popupwindow == null){
					 
					 ComboBoxGridViewAdapter gridAdapter = new ComboBoxGridViewAdapter(m_context);
						m_dropView = LayoutInflater.from(m_context).inflate(R.layout.combobox_grid, null);
						GridView grid = (GridView)m_dropView.findViewById(R.id.combobox_grid_gridView);
						grid.setAdapter(gridAdapter);
						grid.setClickable(true);
						grid.setOnItemClickListener(new OnItemClickListener(){
							@Override
							public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
								// TODO Auto-generated method stub
								m_popupwindow.dismiss();
								m_EditText.setText(m_data.get(arg2));
								
								if (m_listener != null){
									m_listener.onItemClick(arg2);
								}
							}
						});
											 
					 m_popupwindow = new PopupWindow(m_dropView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	                 m_popupwindow.setBackgroundDrawable(new BitmapDrawable());
					 
	                 //pop.setFocusable(true)
					 m_popupwindow.setFocusable(true);  
					 m_popupwindow.setOutsideTouchable(true);
					 m_popupwindow.showAsDropDown(ComboBox.this, 0, 0);

					 }else if(m_popupwindow.isShowing()){
	                	m_popupwindow.dismiss();
					 }else{
	                	m_popupwindow.showAsDropDown(ComboBox.this);
				}
			}
			
		});
	}	

	
	public void setData(List<String> data){
		if (null == data || data.size() <= 0){
			return ;
		}
		
		m_data = data;
		m_EditText.setText(data.get(0));
	}
	
	public void setGridViewOnClickListener(GirdViewItemClickListener listener){
		m_listener = listener;
	}
	
	
	class ComboBoxGridViewAdapter extends BaseAdapter {
		private LayoutInflater m_inflater;
		
		public ComboBoxGridViewAdapter(Context context) {
			m_inflater = LayoutInflater.from(context);
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
			TextView holder = null;
			if (convertView == null) 
			{
				convertView = m_inflater.inflate(R.layout.combobox_item, null);
				holder = (TextView)convertView.findViewById(R.id.combobox_item_text);
				convertView.setTag(holder);
			} 
			else 
			{
				holder = (TextView) convertView.getTag();
			}
			
			holder.setText(m_data.get(position));
			return convertView;
		}
	}
	
	public String getText(){
		return m_EditText.getText().toString();
	}
	 
	public interface GirdViewItemClickListener{
		void onItemClick(int position);
	}
}
