package com.dreamori.sanzijing;

import com.tapjoy.TapjoyConnect;
import com.tapjoy.TapjoyNotifier;

import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.view.MenuItem;

public class SettingActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	
	public static final int MAX_POINT = 20;
	
	private static int m_curPoint = 0;
	private DatabaseHelper m_dbHelper = null;
	private ListPreference m_playModePref;
	private Preference m_getPointPref;
	private CheckBoxPreference m_showAdsCheckboxPref;
	private final Handler m_Handler = new Handler();
	
    public DatabaseHelper getDatabaseHelper()
	{
		if( m_dbHelper == null )
		{
			m_dbHelper = DatabaseHelper.getInstance(getApplicationContext());
		}
		
		return m_dbHelper;
	}
    
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
		{
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		
		addPreferencesFromResource(R.xml.preference);
		m_playModePref = (ListPreference)getPreferenceScreen().findPreference(getString(R.string.pref_key_mode_list));
		
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
		
		m_showAdsCheckboxPref = (CheckBoxPreference)getPreferenceScreen().findPreference(getString(R.string.pref_key_show_ads));
		if(showAds == 0)
		{
			m_showAdsCheckboxPref.setEnabled(true);
		}
		else
		{
			m_showAdsCheckboxPref.setEnabled(false);
			m_showAdsCheckboxPref.setChecked(true);
		}
		
		m_getPointPref = getPreferenceScreen().findPreference(getString(R.string.pref_key_get_point));
		if(m_getPointPref != null)
		{
			m_getPointPref.setSummary(getString(R.string.max_point1) + MAX_POINT + getString(R.string.max_point2));
			m_getPointPref.setOnPreferenceClickListener(new OnPreferenceClickListener(){
				@Override
				public boolean onPreferenceClick(Preference preference) {
					TapjoyConnect.getTapjoyConnectInstance().showOffers(); 
					return true;
				}
			});
		}
		
		TapjoyConnect.requestTapjoyConnect(getApplicationContext(), "33a68bb1-d46a-4722-85a8-8711fecc56fa", "ymNgchYBn3QWeG3Qev7V");
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        finish();
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		
		TapjoyConnect.getTapjoyConnectInstance().getTapPoints(new TapjoyNotifier(){

			@Override
			public void getUpdatePoints(String currencyName, int pointTotal)
			{
				m_curPoint = pointTotal;
				if(m_curPoint >= MAX_POINT)
				{
					getDatabaseHelper().pointReched();
				}
				final String curPointText = getString(R.string.current_point) + m_curPoint;
				m_Handler.post(new Runnable(){
					@Override
					public void run() {
						if (m_getPointPref != null) 
						{
							m_getPointPref.setSummary(getString(R.string.max_point1) + MAX_POINT + getString(R.string.max_point2) + curPointText);
						}
						
						if(m_curPoint >= MAX_POINT && m_showAdsCheckboxPref != null)
						{
							m_showAdsCheckboxPref.setEnabled(true);
						}
					}
				});
			}

			@Override
			public void getUpdatePointsFailed(String error) 
			{
				DreamoriLog.LogSanZiJing("getTapPoints error: " + error);
			}
		});
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        
        if(m_playModePref.getEntry() == null)
        {
        	m_playModePref.setValueIndex(0);
        }
        
        m_playModePref.setSummary(m_playModePref.getEntry().toString());        
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

		TapjoyConnect.getTapjoyConnectInstance().enableDisplayAdAutoRefresh(true);
		TapjoyConnect.getTapjoyConnectInstance().appResume();
    }
	
	@Override
    protected void onPause() {
        super.onPause();

        // Unregister the listener whenever a key changes            
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);    

		TapjoyConnect.getTapjoyConnectInstance().enableDisplayAdAutoRefresh(false);
		TapjoyConnect.getTapjoyConnectInstance().appPause();
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		
		if (key.equals(getString(R.string.pref_key_mode_list))) {
			m_playModePref.setSummary(m_playModePref.getEntry().toString());
			
			PlayerService.CurrentPlayMode = m_playModePref.getEntry().toString();
        }
	}

}
