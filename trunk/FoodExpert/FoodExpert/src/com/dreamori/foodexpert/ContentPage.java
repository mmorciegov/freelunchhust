package com.dreamori.foodexpert;
import android.widget.TextView;

public class ContentPage extends TitleActivity {
	public String m_name1 = null;
	public String m_name2 = null;
	public int m_flag = 0;
	
	public TextView m_textViewHint;	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		m_textViewHint = null;		
	}

}
