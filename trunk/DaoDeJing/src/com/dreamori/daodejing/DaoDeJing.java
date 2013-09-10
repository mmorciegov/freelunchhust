package com.dreamori.daodejing;

import com.dreamori.daodejing.DatabaseHelper;
import com.kyview.AdViewInterface;
import com.kyview.AdViewLayout;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class DaoDeJing extends Activity implements AdViewInterface {
	private DatabaseHelper m_dbHelper = null;	
	
	private ContentView contentView = null;
	private long m_exitTime = 0;
	private long m_adClickTime = 0;
	private LinearLayout m_adLayout = null;
	private static boolean m_showAdThisTime = true;
	private boolean m_adLoaded = false;
	
    static int m_currentImageIndex = Const.m_minImageIndex;
	
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
		contentView.PlayMp3(v);		
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
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		contentView.InitRect();

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
		
		if (showAds != 0 && m_showAdThisTime) 
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
		getDatabaseHelper().SetLastImageIndex(m_currentImageIndex);
		contentView.StopMp3();

		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO)
		{
		ActivityManager activityManager = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		activityManager.killBackgroundProcesses(getPackageName());
		}
		System.exit(0);
	}
        
    @Override
	protected void onPause() {
    	super.onPause();
    	contentView.StopMp3();

		if(System.currentTimeMillis() - m_adClickTime < 1000)
		{
        	if(m_adLayout!=null)
			{
				m_adLayout.removeAllViews();
				m_showAdThisTime = false;
			}
		}
    }

	@Override
	public void onClickAd() {

		long thisTime = System.currentTimeMillis();
		
		if(thisTime - m_adClickTime < 5000)
		{
			m_adLayout.setVisibility(View.GONE);
			m_showAdThisTime = false;
		}
		
		m_adClickTime = thisTime;
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
		if ((System.currentTimeMillis() - m_exitTime) > 3500)
		{
			Toast.makeText(getApplicationContext(), getString(R.string.exit),Toast.LENGTH_LONG).show();
			m_exitTime = System.currentTimeMillis();
		} else {
			finish();
		}
	}
	
}
