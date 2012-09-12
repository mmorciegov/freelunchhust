package com.dreamori.foodexpert;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;

public class TitleActivity extends Activity {
	public static final int SETTING_DIALOG = 0x113;
	public static final String DATABASE_NAME = "food.db";
	public static final String CONFIG_FILENAME = "/config.xml";
	
	public Context m_context;
	public static final String BROADCAST_EXIT = "FoodExit";
	public int m_level;
	
	public BroadcastReceiver m_recv = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();

			if(action.equalsIgnoreCase(BROADCAST_EXIT))
			{
				Bundle bind = intent.getExtras();
				int level = (Integer)bind.getSerializable("Level");
				if (m_level >= level)
				{
					finish();
				}
			}
		}
	};
	
	public void RegisterBroadcastReceiver()
	{
		IntentFilter filter = new IntentFilter();
		filter.addAction(BROADCAST_EXIT);
		
		registerReceiver(m_recv, filter);
	}
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RegisterBroadcastReceiver();
    }
		
	@Override
	protected void onDestroy() {
		super.onDestroy();		
		unregisterReceiver(m_recv);		
	}

	public String GetConfigFileName()
	{
		return getApplicationContext().getFilesDir().getAbsoluteFile().toString() + CONFIG_FILENAME;
	}
	
	public void KillActivity( int level )
	{
		Intent broadIntent = new Intent(BROADCAST_EXIT);
		Bundle broadbind = new Bundle();
		broadbind.putSerializable("Level", level);
		broadIntent.putExtras(broadbind);
		sendBroadcast(broadIntent);
	}
	
	
	@Override
	public Dialog onCreateDialog(int id, Bundle state)
	{
		// Find out dialog type.
		switch (id)
		{
			case SETTING_DIALOG:
				Builder b = new AlertDialog.Builder(this);
				View view = LayoutInflater.from(m_context).inflate(R.layout.ui_setting, null);
				b.setView(view);
				
				final CheckBox tipCheck = (CheckBox) view.findViewById(R.id.ui_setting_tip);
	        	if(ConfigData.GetSystemTip(GetConfigFileName()) == 0)
	        	{
	        		tipCheck.setChecked(false);
	        	}
	        	else
	        	{
	        		tipCheck.setChecked(true);
	        	}
	        	
	        	tipCheck.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
			        	String filename = GetConfigFileName();
			        	ConfigData configData = new ConfigData();
			        	
			        	if (tipCheck.isChecked())
			        	{
			        		configData.tip = 1;
			        	}
			        	else
			        	{
			        		configData.tip = 0;
			        	}
			        	XmlOperation.WriteXml(filename, configData);
					}
	        	});
	        	
	        	final Button mailBtn = (Button) view.findViewById(R.id.ui_setting_email);
	        	mailBtn.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String addr = "mailto:freelunch@gmail.com";
						Intent intent = new Intent(Intent.ACTION_SENDTO);
						intent.setData(Uri.parse(addr));
						intent.putExtra(Intent.EXTRA_SUBJECT, "");
						intent.putExtra(Intent.EXTRA_TEXT, "");
						startActivity(intent);
					}
	        	});
				
//				// Set Dialog icon
//				b.setIcon(R.drawable.ic_launcher);
//				// Set Dialog title
//				b.setTitle(this.getString(R.string.menu_settings));
//				final boolean[] checkStatus = new boolean[2];
//	        	
//	        	if(ConfigData.GetSystemTip(GetConfigFileName()) == 0)
//	        	{
//	        		checkStatus[0] = false;
//	        	}
//	        	else
//	        	{
//	        		checkStatus[0] = true;
//	        	}
//	        	
//	        	if(ConfigData.GetSystemDisplay(GetConfigFileName()) == 0)
//	        	{
//	        		checkStatus[1] = false;
//	        	}
//	        	else
//	        	{
//	        		checkStatus[1] = true;
//	        	}
//	        	
//				// Set multi table for dialog. 
//				b.setMultiChoiceItems(new String[] { this.getString(R.string.init_introduction), this.getString(R.string.display_list_view) }
//				// Set item checked default status
//					, checkStatus
//					// Set Listener for items
//					, new OnMultiChoiceClickListener()
//					{
//						@Override
//						public void onClick(DialogInterface dialog, int which,
//							boolean isChecked)
//						{
//				        	String filename = GetConfigFileName();
//				        	ConfigData configData = new ConfigData();
//				        	
//				        	configData.tip = checkStatus[0] ? 1 : 0;
//				        	configData.display = checkStatus[1] ? 1 : 0;
//				        	XmlOperation.WriteXml(filename, configData);
//						}
//					});
				
				// Add button
				b.setPositiveButton(this.getString(R.string.menu_exit), null);
				// Create Dialog
				return b.create();
		}
		return null;
	}    
	
    @Override
    public boolean onOptionsItemSelected(MenuItem mi)
    {
    	switch(mi.getItemId())
    	{
		default:
			showDialog(SETTING_DIALOG);
			break;
    	}
    	return true;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
}
