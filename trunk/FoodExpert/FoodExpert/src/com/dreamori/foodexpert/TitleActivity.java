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
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

public class TitleActivity extends Activity implements AdViewInterface {
	public static final String CONFIG_FILENAME = "/config.xml";
	
	private DatabaseHelper m_dbHelper = null;
	public DatabaseHelper getDatabaseHelper()
	{
		if( m_dbHelper == null )
		{
			m_dbHelper = DatabaseHelper.getInstance(getApplicationContext());
		}
		
		return m_dbHelper;
	}
		
	public GridView m_gridview;

	public int m_level;
	
	public PopupWindow m_popWnd;
	public GridView m_menuGrid;
	
	public BroadcastReceiver m_recv = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();

			if(action.equalsIgnoreCase(FoodConst.BROADCAST_EXIT))
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
		filter.addAction(FoodConst.BROADCAST_EXIT);
		
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
					String addr = "mailto:dreamorisoft@gmail.com";
					Intent intent = new Intent(Intent.ACTION_SENDTO);
					intent.setData(Uri.parse(addr));
					intent.putExtra(Intent.EXTRA_SUBJECT, "");
					intent.putExtra(Intent.EXTRA_TEXT, "");
					startActivity(intent);
		    		break;
				case 2:
					Intent intentAbout = new Intent(getApplicationContext(),About.class);
					startActivity(intentAbout);
					break;
				case 3:
		    		Builder b = new AlertDialog.Builder(TitleActivity.this);
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
		
		DreamoriLog.LogFoodExpert("Destory Activity");	
		
	}

	public String GetConfigFileName()
	{
		return getApplicationContext().getFilesDir().getAbsoluteFile().toString() + CONFIG_FILENAME;
	}
	
	public void KillActivity( int level )
	{
		Intent broadIntent = new Intent(FoodConst.BROADCAST_EXIT);
		Bundle broadbind = new Bundle();
		broadbind.putSerializable("Level", level);
		broadIntent.putExtras(broadbind);
		sendBroadcast(broadIntent);
	} 

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu, menu);
    	menu.add("setting");
        return true;
    }

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
	
	public void InitImageIcon(ImageView image,  String name)
	{
		image.setImageResource(ResourceManager.GetIcon(this, getDatabaseHelper().getIconName(name)));
	}	
	
	public void InitDegreeIcon(ImageView image,  String name)
	{
		image.setImageResource(ResourceManager.GetIcon(this, name));
	}	
	
	public void InitImageAndTextIcon(ImageView image, TextView text, String name)
	{
		image.setImageResource(ResourceManager.GetIcon(this, getDatabaseHelper().getIconName(name)));
		text.setText(name);
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
