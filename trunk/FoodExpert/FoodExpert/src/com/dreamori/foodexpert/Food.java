package com.dreamori.foodexpert;

import cn.domob.android.ads.DomobUpdater;
import android.os.Bundle;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;

public class Food extends TitleActivity {

	static boolean isStartVersionUpdateFlag = false;

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

		super.onDestroy();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_main);

		m_level = 1;

		if (!isStartVersionUpdateFlag) {
			isStartVersionUpdateFlag = true;

			DomobUpdater.checkUpdate(this, "56OJznHIuMm0L4Zdwd");
		}
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
		Intent intentAbout = new Intent(getApplicationContext(),About.class);
		startActivity(intentAbout);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			ShowExitMessage();
		}
		return false;
	}

}
