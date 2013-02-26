package com.example.screenshot;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class ScreenshotService extends Service {

	public native boolean startScreenShot(String ip);
	public native boolean stopScreenShot();
	
	public static final String BIND = "com.example.screenshot.ScreenshotService.BIND";  
	
	static{
		System.loadLibrary("screenshot");
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		
		Bundle bundle = intent.getExtras();
		final String ip = bundle.getString(MainActivity.EXTRA_MESSAGE);
		
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				startScreenShot(ip);	
			}			
		}).start();				
	}
	
	@Override
	public void onDestroy()
	{
		stopScreenShot();
	}
}