package com.dreamori.foodexpert;

import com.waps.AppConnect;
import com.waps.UpdatePointsNotifier;

import cn.domob.android.ads.DomobUpdater;
import android.os.Bundle;
import android.os.Handler;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

public class Food extends TitleActivity implements UpdatePointsNotifier {

	static boolean isStartVersionUpdateFlag = false;
	
	TextView m_pointTV1;
	String m_pointText1="";
	final Handler m_Handler = new Handler();
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			if (m_pointTV1 != null) 
			{
				m_pointTV1.setText(m_pointText1);
			}
		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		DreamoriLog.LogFoodExpert("Food Activity onDestroy");

		DatabaseHelper dbHelper = getDatabaseHelper();
		if (dbHelper != null) {
			dbHelper.closeDatabase();
		}

		ActivityManager activityManager = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		activityManager.killBackgroundProcesses(getPackageName());
		System.exit(0);

		AppConnect.getInstance(this).finalize();
		
		super.onDestroy();
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
		
		AppConnect.getInstance("4efc262d53252f8ab598f2d38a7861fe","WAPS",this);
		AppConnect.getInstance(this).setAdViewClassName("com.dreamori.foodexpert.Point");
		AppConnect.getInstance(this).setCrashReport(false);
		
		int oldPoints=0;
    	String value = getDatabaseHelper().getConfig("djcs");
    	try{
    		oldPoints = Integer.parseInt(value);
    	}
    	 catch (NumberFormatException e) {
 			e.printStackTrace();
 			oldPoints = 0;
 		}
    	AppConnect.getInstance(this).awardPoints(oldPoints, this);
    	
    	getDatabaseHelper().setConfig("djcs","0");
    	AppConnect.getInstance(this).getPoints(this);   	

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
		AppConnect.getInstance(this).showOffers(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			ShowExitMessage();
		}
		return false;
	}

	@Override
	public void getUpdatePoints(String currencyName, int pointTotal) {
		// TODO Auto-generated method stub
		if(pointTotal>=30)
		{
			getDatabaseHelper().setConfig("showAds","0");
		}
		m_pointText1 = getString(R.string.point_prompt)+pointTotal;
		m_Handler.post(mUpdateResults);
	}

	@Override
	public void getUpdatePointsFailed(String error) {
		// TODO Auto-generated method stub
	}

}
