package com.freelunch.foodtaboo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ComboBox extends LinearLayout {
	private final static 	String TAG = "ComboBox";
    
	private ListViewItemClickListener	m_listener;
	
    private View	 		m_view;
    private ListView 		m_listView;
    private PopupWindow 	m_popupwindow;
    private ListViewAdapter m_adapter_listview;
	private List<String>	m_data;
	private List<String>	m_srcdata; 
	private Context			m_context;
	private Button          m_Button;
	private EditText        m_EditText;
    
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
	
	private void init(){
		View newView = LayoutInflater.from(m_context).inflate(R.layout.combobox_entire, this, true);
		m_Button = (Button) newView.findViewById(R.id.comboButton);		
		m_EditText = (EditText) newView.findViewById(R.id.comboEditText);
		
		m_EditText.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String str = m_EditText.getText().toString();
				Log.v("afterTextChanged str", str);
				if (str.length() == 0)
				{
					m_data = m_srcdata;
					Log.v("afterTextChanged str", "str == null");
				}
				else
				{
					m_data = new ArrayList<String>();
					int index = 0;
					for (int i=0; i<m_srcdata.size(); i++)
					{
						if (m_srcdata.get(i).length() >= str.length())
						{
							if (m_srcdata.get(i).substring(0, str.length()).equals(str))
							{
								//m_data[index++] = m_srcdata[i];
								index++;
								m_data.add(m_srcdata.get(i));
								Log.v("afterTextChanged find", m_srcdata.get(i));
							}
						}
					}
					Log.v("afterTextChanged index", String.valueOf(index));
					
					if (index > 0)
					{
						if (m_data.size() == 1 && m_data.get(0).equalsIgnoreCase(str))
						{
					
						}
						else
						{
							m_Button.performClick();
						}
					}
				}
				
				if (m_listener != null){
					m_listener.onItemClick(0);
				}		
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}
		}
		);
		
		
		m_adapter_listview = new ListViewAdapter(m_context);
    	m_view = LayoutInflater.from(m_context).inflate(R.layout.combobox_listview, null);

    	m_listView =  (ListView)m_view.findViewById(R.id.id_listview);
    	m_listView.setAdapter(m_adapter_listview);
    	m_listView.setClickable(true);
    	m_listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				m_popupwindow.dismiss();
				m_EditText.setText(m_data.get(position));
				
				if (m_listener != null){
					m_listener.onItemClick(position);
				}
			}
		});
    	
    	setListeners();
	}
	
	public void setData(List<String> data){
		if (null == data || data.size() <= 0){
			return ;
		}
		
		m_data = data;
		m_srcdata = m_data;
	}

	public void setListViewOnClickListener(ListViewItemClickListener listener){
		m_listener = listener;
	}
	
	private void setListeners() {
		m_Button.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});
		
		m_Button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
								
				Log.d(TAG, "Click......");
				 if(m_popupwindow == null){
					 if (m_data.size() > 4)
					 {
						 m_popupwindow = new PopupWindow(m_view, ComboBox.this.getWidth(), 300);//LayoutParams.WRAP_CONTENT);
					 }   
					 else if (m_data.size() == 0)
					 {
						 m_popupwindow = new PopupWindow(m_view, ComboBox.this.getWidth(), 50);
					 }
					 else
					 {
						 m_popupwindow = new PopupWindow(m_view, ComboBox.this.getWidth(), LayoutParams.WRAP_CONTENT);
					 }
					 m_popupwindow.setBackgroundDrawable(new BitmapDrawable());
					 
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
	
	 class ListViewAdapter extends BaseAdapter {
        private LayoutInflater 	m_inflate;
        
        public ListViewAdapter(Context context) {        	
            // TODO Auto-generated constructor stub
        	m_inflate = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return m_data == null ? 0 : m_data.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
            TextView textview = null;
            
            if(convertView==null){
                convertView	= m_inflate.inflate(R.layout.combobox_item, null);
                textview = (TextView)convertView.findViewById(R.id.id_txt);
                
                convertView.setTag(textview);
            }else{
            	textview = (TextView) convertView.getTag();
            }

            textview.setText(m_data.get(position));
             
            return convertView;
		}
    }
	
	public String getText(){
		return m_EditText.getText().toString();
	}
	 
	public interface ListViewItemClickListener{
		void onItemClick(int position);
	}	
}
