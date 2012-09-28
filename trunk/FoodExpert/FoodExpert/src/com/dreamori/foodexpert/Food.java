package com.dreamori.foodexpert;

import android.os.Bundle;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;

public class Food extends TitleActivity {
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		DreamoriLog.LogFoodExpert("Food Activity onDestroy");
		
		DatabaseHelper dbHelper = getDatabaseHelper();
		if( dbHelper != null )
		{
			dbHelper.closeDatabase();
		}
		
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.killBackgroundProcesses(getPackageName());
        System.exit(0);
        
		super.onDestroy();
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_main);
        
        m_level = 1;
           
    }
	
	public void startFood(View v)
	{
		Intent intent = new Intent(Food.this, InquireSearchPage.class);
		startActivity(intent);
	}
	
	public void startDisease(View v)
	{
		Intent intent = new Intent(Food.this, DiseaseSearchPage.class);
		startActivity(intent);
	}
}
