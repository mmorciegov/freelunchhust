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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

public class TitleActivity extends Activity implements AdViewInterface {
	public static final int SETTING_DIALOG = 0x113;
	public static final String DATABASE_NAME = "food.db";
	public static final String CONFIG_FILENAME = "/config.xml";
	
	public Context m_context;
	public static final String BROADCAST_EXIT = "FoodExit";
	public int m_level;
	
	public PopupWindow m_popWnd;
	public GridView m_menuGrid;
	
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
        
        ListAdapter adapter = new CustomMenuAdapter(this);
        View view = getLayoutInflater().inflate(R.layout.ui_menu, null);
        GridView gridview = (GridView)view.findViewById(R.id.ui_menu_grid);
        gridview.setAdapter(adapter);
        gridview.setBackgroundColor(Color.GRAY);
        gridview.setClickable(true);
        gridview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				switch(arg2)
				{
				case 0:
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
		        	
		        	GridViewHolder holder = (GridViewHolder) arg1.getTag();
		        	if(ConfigData.GetSystemTip(GetConfigFileName()) == 0)
		        	{
		        		holder.name.setText(getString(R.string.menu_show_tip));
		        	}
		        	else
		        	{
		        		holder.name.setText(getString(R.string.menu_hide_tip));
		        	}					
					break;
				case 1:
					String addr = "mailto:freelunch@gmail.com";
					Intent intent = new Intent(Intent.ACTION_SENDTO);
					intent.setData(Uri.parse(addr));
					intent.putExtra(Intent.EXTRA_SUBJECT, "");
					intent.putExtra(Intent.EXTRA_TEXT, "");
					startActivity(intent);
		    		break;	
				case 2:
		    		Builder b = new AlertDialog.Builder(m_context);
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
			}
		});
        
        m_popWnd = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        m_menuGrid = gridview;
 
        m_popWnd.setTouchable(true);
        m_popWnd.setFocusable(true);        
        m_popWnd.setOutsideTouchable(true);
        m_popWnd.update();
        
        m_popWnd.setBackgroundDrawable(new ColorDrawable(0));
        
//        m_popWnd.setTouchInterceptor(new OnTouchListener(){
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// TODO Auto-generated method stub
//				if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
//				{
//					m_popWnd.dismiss();
//					return true;
//				}
//				return false;
//			}
//        });
        
        m_menuGrid.setFocusableInTouchMode(true);
        m_menuGrid.setOnKeyListener(new OnKeyListener(){
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (m_popWnd.isShowing())
				{
					if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_BACK)
					{
						m_popWnd.dismiss();
						return true;
					}
				}
				return false;
			}
        });
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
	
//    @Override
//    public boolean onOptionsItemSelected(MenuItem mi)
//    {
//    	switch(mi.getItemId())
//    	{
//    	case R.id.menu_showtip:
//        	String filename = GetConfigFileName();
//        	ConfigData configData = new ConfigData();
//        	
//        	if(ConfigData.GetSystemTip(GetConfigFileName()) == 0)
//        	{
//        		configData.tip = 1;
//        	}
//        	else
//        	{
//        		configData.tip = 0;
//        	}
//        	XmlOperation.WriteXml(filename, configData);  
//        	
//        	if(ConfigData.GetSystemTip(GetConfigFileName()) == 0)
//        	{
//        		mi.setTitle(getString(R.string.menu_show_tip));
//        	}
//        	else
//        	{
//        		mi.setTitle(getString(R.string.menu_hide_tip));
//        	}
//    		break;
//    	case R.id.menu_contact:
//			String addr = "mailto:freelunch@gmail.com";
//			Intent intent = new Intent(Intent.ACTION_SENDTO);
//			intent.setData(Uri.parse(addr));
//			intent.putExtra(Intent.EXTRA_SUBJECT, "");
//			intent.putExtra(Intent.EXTRA_TEXT, "");
//			startActivity(intent);
//    		break;
//    	case R.id.menu_exit:
//    		Builder b = new AlertDialog.Builder(this);
//    		b.setIcon(getResources().getDrawable(R.drawable.exit));
//    		b.setTitle(getString(R.string.menu_exit));
//    		b.setMessage(getString(R.string.menu_exit_sure));
//    		b.setPositiveButton(getString(R.string.menu_ok), new DialogInterface.OnClickListener(){
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					// TODO Auto-generated method stub
//					KillActivity(0);
//					finish();
//				}
//    		});
//    		
//    		b.setNegativeButton(getString(R.string.menu_cancel), null);
//    		
//    		b.create().show();
//
//    		break;
//		default:
//			break;
//    	}
//    	return true;
//    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu, menu);
    	menu.add("setting");
        return true;
    }
    
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu)
//    {
//    	super.onPrepareOptionsMenu(menu);
//    	
//    	MenuItem showtip = menu.getItem(0);
//    	if(ConfigData.GetSystemTip(GetConfigFileName()) == 0)
//    	{
//    		showtip.setTitle(getString(R.string.menu_show_tip));
//    	}
//    	else
//    	{
//    		showtip.setTitle(getString(R.string.menu_hide_tip));
//    	}
//    	
//    	return true;
//    }

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		// TODO Auto-generated method stub
		if (m_popWnd != null)
		{
			if (!m_popWnd.isShowing())
			{
				m_popWnd.showAtLocation(findViewById(R.id.relativeLayout1), Gravity.BOTTOM, 0, 0);		
			}
		}
		return false;
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
