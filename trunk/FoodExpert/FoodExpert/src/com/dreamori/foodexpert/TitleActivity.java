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
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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
		
	public String GetConfigFileName()
	{
		return getApplicationContext().getFilesDir().getAbsoluteFile().toString() + CONFIG_FILENAME;
	}
	
	@Override
	public Dialog onCreateDialog(int id, Bundle state)
	{
		// Find out dialog type.
		switch (id)
		{
			case SETTING_DIALOG:
				Builder b = new AlertDialog.Builder(this);
				// Set Dialog icon
				b.setIcon(R.drawable.ic_launcher);
				// Set Dialog title
				b.setTitle(this.getString(R.string.menu_settings));
				final boolean[] checkStatus = new boolean[2];
	        	
	        	if(ConfigData.GetSystemTip(GetConfigFileName()) == 0)
	        	{
	        		checkStatus[0] = false;
	        	}
	        	else
	        	{
	        		checkStatus[0] = true;
	        	}
	        	
	        	if(ConfigData.GetSystemDisplay(GetConfigFileName()) == 0)
	        	{
	        		checkStatus[1] = false;
	        	}
	        	else
	        	{
	        		checkStatus[1] = true;
	        	}
	        	
				// Set multi table for dialog. 
				b.setMultiChoiceItems(new String[] { this.getString(R.string.init_introduction), this.getString(R.string.display_list_view) }
				// Set item checked default status
					, checkStatus
					// Set Listener for items
					, new OnMultiChoiceClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which,
							boolean isChecked)
						{
				        	String filename = GetConfigFileName();
				        	ConfigData configData = new ConfigData();
				        	
				        	configData.tip = checkStatus[0] ? 1 : 0;
				        	configData.display = checkStatus[1] ? 1 : 0;
				        	XmlOperation.WriteXml(filename, configData);
						}
					});
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
