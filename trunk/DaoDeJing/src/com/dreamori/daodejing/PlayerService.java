package com.dreamori.daodejing;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;

public class PlayerService extends Service {
	
	public final static String ACTION_UPDATE_PLAYING = "com.dreamori.daodejing.update_playing";
	
	public static String CurrentPlayMode = null;
    public static boolean isPlaying = false;
	
	private DatabaseHelper m_dbHelper = null;
	private static MediaPlayer mp = null; 
	

	private BroadcastReceiver m_recv = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if(action.equalsIgnoreCase(ACTION_UPDATE_PLAYING))
			{
				UpdatePlaying();
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
    
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Intent intent1 = new Intent(this, DaoDeJing.class);
		intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendIntent = PendingIntent.getActivity(this, 0, intent1, 0);
		Notification notification = new Notification(R.drawable.ic_launcher, getString(R.string.player_service_msg), System.currentTimeMillis());
		notification.setLatestEventInfo(this, getString(R.string.player_service_title), getString(R.string.player_service_detail), pendIntent);
		
		startForeground(54316, notification);

		if(CurrentPlayMode == null)
			CurrentPlayMode = getString(R.string.mode_single);
		
		DaoDeJing.m_currentImageIndex = getDatabaseHelper().GetLastImageIndex();
		mp = MediaPlayer.create(this, ResourceManager.GetMusicId(this, getDatabaseHelper().GetMusicName(DaoDeJing.m_currentImageIndex)));
		if( mp!= null )
		{
			mp.setOnCompletionListener(new OnCompletionListener(){

				@Override
				public void onCompletion(MediaPlayer arg0) {

					if(CurrentPlayMode.equals(getString(R.string.mode_single_cycle)))
					{
						mp.start();
					}
					else if(CurrentPlayMode.equals(getString(R.string.mode_all)))
					{
						if(DaoDeJing.m_currentImageIndex == Const.m_maxImageIndex)
						{
							stopSelf();
							UpdatePlayButton(false);
						}
						else
						{
							DaoDeJing.m_currentImageIndex++;
							UpdatePage();
							UpdatePlaying();
						}
					}
					else if(CurrentPlayMode.equals(getString(R.string.mode_all_cycle)))
					{
						DaoDeJing.m_currentImageIndex++;
						if( DaoDeJing.m_currentImageIndex > Const.m_maxImageIndex )
						{
							DaoDeJing.m_currentImageIndex = Const.m_minImageIndex;
						}
						UpdatePage();
						UpdatePlaying();
					}
					else
					{
						stopSelf();
						UpdatePlayButton(false);
					}
					getDatabaseHelper().SetLastImageIndex(DaoDeJing.m_currentImageIndex);
					
				}
			});

			mp.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
			mp.start();
			isPlaying = true;
		}
		

        IntentFilter filter = new IntentFilter();  
        filter.addAction(ACTION_UPDATE_PLAYING);
        registerReceiver(m_recv, filter);  
		
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		DreamoriLog.LogDaoDeJing("PlayerService onDestroy");	

		unregisterReceiver(m_recv);
		
		if( mp != null )
		{
			mp.stop();	
			mp.release();
			mp = null;
		}
		isPlaying = false;
	}
	
	private void UpdatePage()
	{
		Intent intent = new Intent();  
        intent.setAction(DaoDeJing.ACTION_UPDATE_PAGE);
        sendBroadcast(intent);
	}
	
	private void UpdatePlayButton(Boolean isPlaying)
	{
		Intent intent = new Intent();  
        intent.setAction(DaoDeJing.ACTION_UPDATE_PLAY_STATE);
        sendBroadcast(intent);
	}
	

	private void UpdatePlaying() 
	{
		if (mp == null)
			return;

		mp.reset();
		
		try {
			mp.setDataSource(this, Uri.parse("android.resource://com.dreamori.daodejing/" + ResourceManager.GetMusicId(this, getDatabaseHelper().GetMusicName(DaoDeJing.m_currentImageIndex))));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		mp.setOnPreparedListener(new OnPreparedListener(){

			@Override
			public void onPrepared(MediaPlayer mp) {
				mp.start();
				
			}});
		
		mp.prepareAsync();
		
	}

}
