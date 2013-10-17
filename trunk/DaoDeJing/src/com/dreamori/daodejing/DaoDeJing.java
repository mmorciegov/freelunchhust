package com.dreamori.daodejing;

import cn.domob.android.ads.DomobUpdater;

import com.dreamori.daodejing.DatabaseHelper;
import com.kyview.AdViewInterface;
import com.kyview.AdViewLayout;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class DaoDeJing extends Activity implements AdViewInterface {

	public final static String ACTION_UPDATE_PAGE = "com.dreamori.daodejing.update_page";
	public final static String ACTION_UPDATE_PLAY_STATE = "com.dreamori.daodejing.update_play_state";
	public final static String IS_PLAYING = "is_playing";

    public static int m_currentImageIndex = Const.m_minImageIndex;

	static boolean isStartVersionUpdateFlag = false;
	
	private DatabaseHelper m_dbHelper = null;	
	
	private ContentView contentView = null;
	private long m_exitTime = 0;
	private LinearLayout m_adLayout = null;
	private boolean m_adLoaded = false;
	
    
	private BroadcastReceiver m_recv = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if(action.equalsIgnoreCase(ACTION_UPDATE_PAGE))
			{
				contentView.UpdateImageAndHotspot();
			}
			else if(action.equalsIgnoreCase(ACTION_UPDATE_PLAY_STATE))
			{
				updatePlayButton();
			}
		}
	};
	
    public DatabaseHelper getDatabaseHelper()
	{
		if( m_dbHelper == null )
		{
			m_dbHelper = DatabaseHelper.getInstance(getApplicationContext());
		}
		
		return m_dbHelper;
	}
	
	public void ShowNextImage(View v)
	{
		contentView.ShowNextImage();
	}
	
	public void ShowPreviosImage(View v)
	{	
		contentView.ShowPreviosImage();
	}
	
	public void ShowWholeExplanation(View v)
	{
		contentView.ShowWholeExplanation();
	}
	
	public void PlayMp3(View v)
	{
		if( PlayerService.isPlaying  )
		{
			((Button)v).setBackgroundResource(R.drawable.music);
			stopService(new Intent(this, PlayerService.class));
			return;
		}
		else
		{
			((Button)v).setBackgroundResource(R.drawable.music2);
			Intent intent = new Intent(this, PlayerService.class);
			startService(intent);
		}
	}
	
	public void Setting(View v)
	{
		startActivity(new Intent(getApplicationContext(), SettingActivity.class));	
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
        contentView =(ContentView)findViewById(R.id.img_content);
		m_currentImageIndex = getDatabaseHelper().GetLastImageIndex();
		
		PreferenceManager.setDefaultValues(this, R.xml.preference, false);

        IntentFilter filter = new IntentFilter();  
        filter.addAction(ACTION_UPDATE_PAGE);
        filter.addAction(ACTION_UPDATE_PLAY_STATE);
        registerReceiver(m_recv, filter);  
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		PlayerService.CurrentPlayMode = sharedPref.getString(getString(R.string.pref_key_mode_list), getString(R.string.mode_single));
		

		if (!isStartVersionUpdateFlag) {
			isStartVersionUpdateFlag = true;

			DomobUpdater.checkUpdate(this, "56OJznHIuNMr+C+6F6");
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		contentView.InitRect();
		updatePlayButton();

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean showAdsChecked = sharedPref.getBoolean(getString(R.string.pref_key_show_ads), true);
		
		m_adLayout = (LinearLayout) findViewById(R.id.adLayout);
		if (m_adLayout == null)
		{
			return;
		}

		int showAds = 1;
		String value = getDatabaseHelper().getConfig("showAds");
		try 
		{
			showAds = Integer.parseInt(value);
		} 
		catch (Exception e) 
		{
			e.toString();
			showAds = 1;
		}
		
		if ( showAdsChecked || showAds != 0 ) 
		{
			if (!m_adLoaded) 
			{
				AdViewLayout adViewLayout = new AdViewLayout(this, "SDK20131505030937atzn8f4t77r42lk");
				adViewLayout.setAdViewInterface(this);
				m_adLayout.addView(adViewLayout);
				m_adLayout.invalidate();
				m_adLoaded = true;
			}
		} 
		else 
		{
			m_adLayout.removeAllViews();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		unregisterReceiver(m_recv);

		if(!PlayerService.isPlaying)
		{
			if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO)
			{
			ActivityManager activityManager = (ActivityManager) this
					.getSystemService(Context.ACTIVITY_SERVICE);
			activityManager.killBackgroundProcesses(getPackageName());
			}
			System.exit(0);
		}
	}
    
    @Override
	protected void onResume() {
    	super.onResume();
    	
    	if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_key_keep_screen), false))
    	{
    		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	}
    	else
    	{
    		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	}
    }
	
	@Override
	public void onClickAd() {
	}

	@Override
	public void onDisplayAd() {
		
		contentView.postDelayed(new Runnable(){
			@Override
			public void run() {
				contentView.InitRect();
			}}, 500);
	}
	
	@Override
	public void onBackPressed() {
		if ((System.currentTimeMillis() - m_exitTime) > 3500 && !PlayerService.isPlaying)
		{
			Toast.makeText(getApplicationContext(), getString(R.string.exit),Toast.LENGTH_LONG).show();
			m_exitTime = System.currentTimeMillis();
		} else {
			finish();
		}
	}
	

	
	public void updatePlayButton()
	{
		Button musicBtn = (Button)findViewById(R.id.btn_music);
		if(musicBtn == null) return;
		
		if(PlayerService.isPlaying)
		{
			musicBtn.setBackgroundResource(R.drawable.music2);
		}
		else
		{
			musicBtn.setBackgroundResource(R.drawable.music);
		}
	}
	
}
