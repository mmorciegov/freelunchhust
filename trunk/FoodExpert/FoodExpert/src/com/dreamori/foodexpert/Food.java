package com.dreamori.foodexpert;

import com.tapjoy.TapjoyConnect;
import com.tapjoy.TapjoyEarnedPointsNotifier;
import com.tapjoy.TapjoyNotifier;

import cn.domob.android.ads.DomobUpdater;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Food extends TitleActivity {

	static boolean isStartVersionUpdateFlag = false;
	public static final int MAX_POINT = 20;
	private static int m_curPoint = 0;
	private long m_exitTime = 0;

	
	TextView m_pointTV1;
	final Handler m_Handler = new Handler();

	@TargetApi(8)
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		DreamoriLog.LogFoodExpert("Food Activity onDestroy");

		DatabaseHelper dbHelper = getDatabaseHelper();
		if (dbHelper != null) {
			dbHelper.closeDatabase();
		}

		ActivityManager activityManager = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		activityManager.killBackgroundProcesses(getPackageName());
		System.exit(0);
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_main);

		m_pointTV1 = (TextView)findViewById(R.id.ui_main_points2);
		TextView tv = (TextView)findViewById(R.id.ui_main_points);
		tv.setText(getString(R.string.point_prompt2)+MAX_POINT+getString(R.string.point_prompt3));
		
		m_level = 1;

		if (!isStartVersionUpdateFlag) {
			isStartVersionUpdateFlag = true;

			DomobUpdater.checkUpdate(this, "56OJznHIuMm0L4Zdwd");
		}
		
		TapjoyConnect.requestTapjoyConnect(getApplicationContext(), "7473ae7f-6f58-4d5d-9471-12ce5ff32623", "gmMSXipEaXWvNZeVWCcL");
		TapjoyConnect.getTapjoyConnectInstance().setEarnedPointsNotifier(new TapjoyEarnedPointsNotifier()
		{
			@Override
			public void earnedTapPoints(int amount)
			{
				m_curPoint += amount;
				final String pointText = getString(R.string.point_prompt)+m_curPoint;
				m_Handler.post(new Runnable(){
					
					@Override
					public void run() {
						if (m_pointTV1 != null) 
						{
							m_pointTV1.setText(pointText);
						}
					}}
				);
			}
		});
		TapjoyConnect.getTapjoyConnectInstance().getTapPoints(new TapjoyNotifier()
		{
			@Override
			public void getUpdatePointsFailed(String error)
			{
				DreamoriLog.LogFoodExpert("getTapPoints error: " + error);
			}
			
			@Override
			public void getUpdatePoints(String currencyName, int pointTotal)
			{
				m_curPoint = pointTotal;
				if(m_curPoint >= MAX_POINT)
				{
					getDatabaseHelper().setConfig("showAds","0");
				}
				final String pointText = getString(R.string.point_prompt)+m_curPoint;
				m_Handler.post(new Runnable(){
					@Override
					public void run() {
						if (m_pointTV1 != null) 
						{
							m_pointTV1.setText(pointText);
						}
					}}
				);
			}
		});
	}
	

	@Override
	protected void onResume()
	{
		super.onResume();

		TapjoyConnect.getTapjoyConnectInstance().enableDisplayAdAutoRefresh(true);
		TapjoyConnect.getTapjoyConnectInstance().appResume();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		
		TapjoyConnect.getTapjoyConnectInstance().enableDisplayAdAutoRefresh(false);
		TapjoyConnect.getTapjoyConnectInstance().appPause();
	}

	public void startFood(View v) {
		Intent intent = new Intent(getApplicationContext(), InquireSearchPage.class);
		startActivity(intent);
	}

	public void startDisease(View v) {
		Intent intent = new Intent(getApplicationContext(), DiseaseSearchPage.class);
		startActivity(intent);
	}
	
	public void removeAds(View v){
		TapjoyConnect.getTapjoyConnectInstance().showOffers(); 
	}

	@Override
	public void onBackPressed() {
		if ((System.currentTimeMillis() - m_exitTime) > 3500)
		{
			Toast.makeText(getApplicationContext(), getString(R.string.exit),Toast.LENGTH_LONG).show();
			m_exitTime = System.currentTimeMillis();
		} else {
			KillActivity(0);
			finish();
		}

	}

}
