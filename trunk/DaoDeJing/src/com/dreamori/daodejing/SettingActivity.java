package com.dreamori.daodejing;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

public class SettingActivity extends Activity {

	private DatabaseHelper m_dbHelper = null;
	
	private CheckBox m_cbShowExp = null;
	
    public DatabaseHelper getDatabaseHelper()
	{
		if( m_dbHelper == null )
		{
			m_dbHelper = DatabaseHelper.getInstance(getApplicationContext());
		}
		
		return m_dbHelper;
	}
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		m_cbShowExp = (CheckBox)findViewById(R.id.checkbox_show_explain);
		
		if(m_cbShowExp != null && getDatabaseHelper().NeedShowTouchTips())
		{
			m_cbShowExp.setChecked(true);
		}
		
	}
	
	public void showExplain(View v)
	{
		if(m_cbShowExp == null) return;
		
		if(m_cbShowExp.isChecked())
		{
			getDatabaseHelper().setConfig("xsbj", "1");
		}
		else
		{
			getDatabaseHelper().setConfig("xsbj", "0");
		}
	}

}
