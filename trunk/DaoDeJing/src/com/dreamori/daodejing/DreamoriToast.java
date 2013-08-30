package com.dreamori.daodejing;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

public class DreamoriToast {
	
	static private Toast m_toast = null;
	static private boolean m_continue = true;
	static private Timer timer = new Timer();
	
	static private void execToast(final int cnt) {		
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				initToast( cnt + 1);
			}

		}, 1900);
	}

	static private void initToast(int cnt) {

		if( !m_continue )
		{
			return;
		}
		
		m_toast.show();
		execToast(cnt);
	}

	static public void invokeLongTimeToast(Context context, String info) {
		if( m_toast != null )
		{
			killToast();
		}
		m_continue = true;
		m_toast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
		if (timer == null) {
			timer = new Timer();
		}
	
		initToast(0);

	}
	
	static public void killToast()
	{
		
		if( m_toast != null )
		{
			m_continue = false;
			timer.cancel();			
			timer = null;
			m_toast.cancel();	
			m_toast = null;
		}			
	}
}
