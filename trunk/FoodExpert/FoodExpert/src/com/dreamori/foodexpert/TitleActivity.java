package com.dreamori.foodexpert;

import com.adview.AdViewInterface;
import com.adview.AdViewLayout;
import com.adview.AdViewTargeting;
import com.adview.AdViewTargeting.RunMode;
import com.adview.AdViewTargeting.UpdateMode;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

public class TitleActivity extends Activity implements AdViewInterface {
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
    protected void onStart()
    {
    	super.onStart();
    	LinearLayout layout = (LinearLayout)findViewById(R.id.adLayout);
        if (layout == null) 
            return;

        AdViewTargeting.setUpdateMode(UpdateMode.EVERYTIME);  //Must delete before release!!!!!
        AdViewTargeting.setRunMode(RunMode.TEST);        //Must delete before release!!!!!

        AdViewLayout adViewLayout = new AdViewLayout(this, "SDK20120912090935ahwlxnjz3q9r67q");
        adViewLayout.setAdViewInterface(this);
        layout.addView(adViewLayout);
        layout.invalidate(); 
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
    public boolean onOptionsItemSelected(MenuItem mi)
    {
    	switch(mi.getItemId())
    	{
    	case R.id.menu_showtip:
        	String filename = GetConfigFileName();
        	ConfigData configData = new ConfigData();
        	
        	if(ConfigData.GetSystemTip(GetConfigFileName()) == 0)
        	{
        		configData.tip = 1;
        	}
        	else
        	{
        		configData.tip = 0;
        	}
        	XmlOperation.WriteXml(filename, configData);  
        	
        	if(ConfigData.GetSystemTip(GetConfigFileName()) == 0)
        	{
        		mi.setTitle(getString(R.string.menu_show_tip));
        	}
        	else
        	{
        		mi.setTitle(getString(R.string.menu_hide_tip));
        	}
    		break;
    	case R.id.menu_contact:
			String addr = "mailto:freelunch@gmail.com";
			Intent intent = new Intent(Intent.ACTION_SENDTO);
			intent.setData(Uri.parse(addr));
			intent.putExtra(Intent.EXTRA_SUBJECT, "");
			intent.putExtra(Intent.EXTRA_TEXT, "");
			startActivity(intent);
    		break;
    	case R.id.menu_exit:
    		Builder b = new AlertDialog.Builder(this);
    		b.setIcon(getResources().getDrawable(R.drawable.exit));
    		b.setTitle(getString(R.string.menu_exit));
    		b.setMessage(getString(R.string.menu_exit_sure));
    		b.setPositiveButton(getString(R.string.menu_ok), new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					KillActivity(0);
					finish();
				}
    		});
    		
    		b.setNegativeButton(getString(R.string.menu_cancel), null);
    		
    		b.create().show();

    		break;
		default:
			break;
    	}
    	return true;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
    	super.onPrepareOptionsMenu(menu);
    	
    	MenuItem showtip = menu.getItem(0);
    	if(ConfigData.GetSystemTip(GetConfigFileName()) == 0)
    	{
    		showtip.setTitle(getString(R.string.menu_show_tip));
    	}
    	else
    	{
    		showtip.setTitle(getString(R.string.menu_hide_tip));
    	}
    	
    	return true;
    }

	@Override
	public void onClickAd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisplayAd() {
		// TODO Auto-generated method stub
		
	}
}
