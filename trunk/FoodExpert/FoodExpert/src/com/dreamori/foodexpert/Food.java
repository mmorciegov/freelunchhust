package com.dreamori.foodexpert;

import com.lenovo.lps.sus.SUS;

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

			if (!SUS.isVersionUpdateStarted()) {
				SUS.AsyncStartVersionUpdate(this);
			}
		}
	}

	public void startFood(View v) {
		Intent intent = new Intent(Food.this, InquireSearchPage.class);
		startActivity(intent);
	}

	public void startDisease(View v) {
		Intent intent = new Intent(Food.this, DiseaseSearchPage.class);
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			ShowExitMessage();
		}
		return false;
	}

}
