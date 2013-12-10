package com.dreamori.sanzijing;

import java.util.Timer;
import java.util.TimerTask;

import com.dreamori.sanzijing.ParameterObject.MusicInfo;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;

public class PlayerService extends Service {
	
	public final static String ACTION_UPDATE_PLAYING = "com.dreamori.sanzijing.update_playing";
	
	public static String CurrentPlayMode = null;
    public static boolean isPlaying = false;
	
	private DatabaseHelper m_dbHelper = null;
	private static MediaPlayer mp = null; 

	private Timer timer = null;
	
	
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
	
	private MusicInfo musicInfo = new MusicInfo();
	
	void StartTimerForMeidaPlayer()
	{
		timer = new Timer(true);
		getDatabaseHelper().GetMusicInfo(SanZiJing.m_currentImageIndex , musicInfo);
		
		TimerTask timerTask = new TimerTask()
		{

			@Override
			public void run() {
				if(CurrentPlayMode.equals(getString(R.string.mode_single_cycle)))
				{
					if( mp != null  && isPlaying )
					{						
						int currPos = mp.getCurrentPosition() ;
						if( currPos >= musicInfo.stopTime  ||  currPos < musicInfo.startTime )
						{
							mp.pause();
							mp.seekTo(musicInfo.startTime);
							mp.start();
						}						
					}	
				}
				else if(CurrentPlayMode.equals(getString(R.string.mode_all)))
				{
					if( mp != null  && isPlaying )
					{						
						int currPos = mp.getCurrentPosition() ;
						if( currPos >= musicInfo.stopTime  ||  currPos < musicInfo.startTime )
						{
							if(SanZiJing.m_currentImageIndex == Const.m_maxImageIndex)
							{
								stopSelf();
								UpdatePlayButton(false);
							}
							else
							{
								SanZiJing.m_currentImageIndex++;								
								getDatabaseHelper().GetMusicInfo(SanZiJing.m_currentImageIndex , musicInfo);
								
								UpdatePage();
							}
						}						
					}	
					
				}
				else if(CurrentPlayMode.equals(getString(R.string.mode_all_cycle)))
				{
					if( mp != null  && isPlaying )
					{						
						int currPos = mp.getCurrentPosition() ;
						if( currPos >= musicInfo.stopTime  ||  currPos < musicInfo.startTime )
						{
							SanZiJing.m_currentImageIndex++;
							if( SanZiJing.m_currentImageIndex > Const.m_maxImageIndex )
							{
								SanZiJing.m_currentImageIndex = Const.m_minImageIndex;
								StartPlay();
							}
							UpdatePage();
							getDatabaseHelper().GetMusicInfo(SanZiJing.m_currentImageIndex , musicInfo);
						}						
					}										
				}
				else
				{
					if( mp != null  && isPlaying )
					{						
						int currPos = mp.getCurrentPosition() ;
						if( currPos >= musicInfo.stopTime  ||  currPos < musicInfo.startTime )
						{
							stopSelf();
							UpdatePlayButton(false);
						}						
					}	

				}
				
			}
			
		};
		
		timer.scheduleAtFixedRate(timerTask, 1000, 1000);
	}
	
	void StartPlay()
	{
		if( mp!= null )
		{
			getDatabaseHelper().GetMusicInfo(SanZiJing.m_currentImageIndex, musicInfo);
			
			mp.seekTo(musicInfo.startTime);
			mp.start();			
			isPlaying = true;
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Intent intent1 = new Intent(this, SanZiJing.class);
		intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendIntent = PendingIntent.getActivity(this, 0, intent1, 0);
		Notification notification = new Notification(R.drawable.ic_launcher, getString(R.string.player_service_msg), System.currentTimeMillis());
		notification.setLatestEventInfo(this, getString(R.string.player_service_title), getString(R.string.player_service_detail), pendIntent);
		
		startForeground(54316, notification);

		if(CurrentPlayMode == null)
			CurrentPlayMode = getString(R.string.mode_single);
		
		mp = MediaPlayer.create(this, ResourceManager.GetMusicId(this, "m0001"));
		
		if( mp!= null )
		{
			mp.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
			mp.setLooping(true);

			StartPlay();
			
			StartTimerForMeidaPlayer();
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
		DreamoriLog.LogSanZiJing("PlayerService onDestroy");	

		unregisterReceiver(m_recv);
		
		if( mp != null )
		{
			mp.stop();	
			mp.release();
			mp = null;
		}
		
		if( timer != null )
		{
			timer.cancel();
			timer = null;
		}
		
		isPlaying = false;
	}
	
	private void UpdatePage()
	{
		Intent intent = new Intent();  
        intent.setAction(SanZiJing.ACTION_UPDATE_PAGE);
        sendBroadcast(intent);
	}
	
	private void UpdatePlayButton(Boolean isPlaying)
	{
		Intent intent = new Intent();  
        intent.setAction(SanZiJing.ACTION_UPDATE_PLAY_STATE);
        sendBroadcast(intent);
	}
	

	private void UpdatePlaying() 
	{
		StartPlay();		
	}

}
