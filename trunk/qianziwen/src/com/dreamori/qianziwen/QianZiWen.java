package com.dreamori.qianziwen;

import cn.domob.android.ads.DomobUpdater;

import com.dreamori.qianziwen.PlayerService;
import com.dreamori.qianziwen.DatabaseHelper;
import com.dreamori.qianziwen.ParameterObject.TitleContent;
import com.kyview.AdViewInterface;
import com.kyview.AdViewLayout;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;

public class QianZiWen extends Activity implements  AdViewInterface   {

	public final static String ACTION_UPDATE_PAGE = "com.dreamori.qianziwen.update_page";
	public final static String ACTION_UPDATE_PLAY_STATE = "com.dreamori.qianziwen.update_play_state";
	public final static String IS_PLAYING = "is_playing";

    public static int m_currentImageIndex = Const.m_minImageIndex;

	static boolean isStartVersionUpdateFlag = false;
	
	private DatabaseHelper m_dbHelper = null;	
	private int backgroundImageCount = 0;	
	private Random rand = new Random(5);

	private long m_exitTime = 0;
	private LinearLayout m_adLayout = null;
	private boolean m_adLoaded = false;
	
    
    private String[] _textContent = new String[QianZiWenView.TEXTVIEW_COUNT];
	
    
	private BroadcastReceiver m_recv = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if(action.equalsIgnoreCase(ACTION_UPDATE_PAGE))
			{
				UpdateTextContent();
			}
			else if(action.equalsIgnoreCase(ACTION_UPDATE_PLAY_STATE))
			{
				updatePlayButton();
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
    
    private void ChangeBackgroud()
    {
    	getWindow().getDecorView().setBackgroundResource(ResourceManager.GetBackgroundIcon(this, m_dbHelper.GetBackgroundImageContentName(rand.nextInt(backgroundImageCount))));  	
    }


	public void ShowPreviosImage(View v)
	{	
		m_currentImageIndex--;
		if( m_currentImageIndex < Const.m_minImageIndex )
		{
			m_currentImageIndex = Const.m_maxImageIndex;
		}
		
		UpdateTextContentAndMusic();
	}
		
	public void ShowNextImage(View v)
	{
		
		m_currentImageIndex++;
		if( m_currentImageIndex > Const.m_maxImageIndex )
		{
			m_currentImageIndex = Const.m_minImageIndex;
		}
				
		UpdateTextContentAndMusic();
	}
	
	private void UpdateTextContentAndMusic()
	{
		UpdateTextContent();		
		
		if( PlayerService.isPlaying )
		{
			Intent intent = new Intent();  
	        intent.setAction(PlayerService.ACTION_UPDATE_PLAYING);
	        this.sendBroadcast(intent);
		}
	}
	
	private void UpdateTextContent()
	{
		ChangeBackgroud();
		int outputSpellCount = m_dbHelper.GetTextContent(m_currentImageIndex, _textContent);
		String contentIndexString = m_dbHelper.GetContentIndexString(m_currentImageIndex);
		((QianZiWenView)findViewById(R.id.text_content)).updateContent(_textContent, contentIndexString, outputSpellCount);
	}
	

	public void ShowWholeExplanation()
	{
		TitleContent titleContent = new TitleContent();
		if( m_dbHelper.GetExplanationContent(QianZiWen.m_currentImageIndex, titleContent))
		{
			ShowExplanationDialog(titleContent.title, titleContent.content );		
		}
	}

	public void ShowExplanationDialog( String title, String contentString )
	{
		AlertDialog.Builder builder = new Builder(this);
		
		LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		View rootView = inflater.inflate(R.layout.explain_dialog, null);
		rootView.setBackgroundResource(ResourceManager.GetBackgroundIcon(this, m_dbHelper.GetBackgroundImageContentName(rand.nextInt(backgroundImageCount))));

		TextView expTV = (TextView)rootView.findViewById(R.id.explain_text);
		expTV.setText(contentString);
		
	    builder.setView(rootView);
	    builder.setPositiveButton(this.getString(R.string.OK), new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}});

		builder.create().show();
	}
	
	public void ShowWholeExplanation(View v)
	{
		ShowWholeExplanation();
	}
	
	public void PlayMp3(View v)
	{
		if( PlayerService.isPlaying  )
		{
			((Button)v).setBackgroundResource(R.drawable.music);
			stopService(new Intent(this, PlayerService.class));
			return;
		}
		else
		{
			((Button)v).setBackgroundResource(R.drawable.music2);
			Intent intent = new Intent(this, PlayerService.class);
			startService(intent);
		}
	}
	
	public void Setting(View v)
	{
		startActivity(new Intent(getApplicationContext(), SettingActivity.class));	
	}

	private GestureDetector mGestureDetector;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
		//m_currentImageIndex = getDatabaseHelper().GetLastImageIndex();
		backgroundImageCount = getDatabaseHelper().GetBackgroundImageCount();
		
		PreferenceManager.setDefaultValues(this, R.xml.preference, false);

        IntentFilter filter = new IntentFilter();  
        filter.addAction(ACTION_UPDATE_PAGE);
        filter.addAction(ACTION_UPDATE_PLAY_STATE);
        registerReceiver(m_recv, filter);  
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		m_currentImageIndex = sharedPref.getInt(getString(R.string.pref_key_last_img_index), 1);
		PlayerService.CurrentPlayMode = sharedPref.getString(getString(R.string.pref_key_mode_list), getString(R.string.mode_single));
		
		
		
		UpdateTextContentAndMusic();
		
		mGestureDetector = new GestureDetector(this, new MyGestureListener());
		
		if (!isStartVersionUpdateFlag) {
			isStartVersionUpdateFlag = true;

			DomobUpdater.checkUpdate(this, "56OJznHIuNPK/FPLav");
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		//contentView.InitRect();
		updatePlayButton();
		
		m_adLayout = (LinearLayout) findViewById(R.id.adLayout);
		if (m_adLayout == null)
		{
			return;
		}

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean showAdsChecked = sharedPref.getBoolean(getString(R.string.pref_key_show_ads), true);
		
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
		
		if ( showAdsChecked || showAds != 0 ) 
		{
			if (!m_adLoaded) 
			{
				AdViewLayout adViewLayout = new AdViewLayout(this, "SDK20131104111249sfvvqv3dqj42xc9");
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
		super.onDestroy();

		unregisterReceiver(m_recv);

		if(!PlayerService.isPlaying)
		{
			ActivityManager activityManager = (ActivityManager) this
					.getSystemService(Context.ACTIVITY_SERVICE);
			activityManager.killBackgroundProcesses(getPackageName());
			System.exit(0);
		}
	}
    
    @Override
	protected void onResume() {
    	super.onResume();
    	
    	if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_key_keep_screen), false))
    	{
    		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	}
    	else
    	{
    		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	}
    }
	
	@Override
	public void onClickAd() {
	}

	@Override
	public void onDisplayAd() {
	}
	
	@Override
	public void onBackPressed() {
		if ((System.currentTimeMillis() - m_exitTime) > 3500 && !PlayerService.isPlaying)
		{
			Toast.makeText(getApplicationContext(), getString(R.string.exit),Toast.LENGTH_LONG).show();
			m_exitTime = System.currentTimeMillis();
		} else {

			//getDatabaseHelper().SetLastImageIndex(m_currentImageIndex);
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
			editor.putInt(getString(R.string.pref_key_last_img_index), m_currentImageIndex);
			editor.commit();
			
			finish();
		}
	}

	
	public void updatePlayButton()
	{
		Button musicBtn = (Button)findViewById(R.id.btn_music);
		if(musicBtn == null) return;
		
		if(PlayerService.isPlaying)
		{
			musicBtn.setBackgroundResource(R.drawable.music2);
		}
		else
		{
			musicBtn.setBackgroundResource(R.drawable.music);
		}
	}
	
	class MyGestureListener extends GestureDetector.SimpleOnGestureListener{

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if( e1.getX() - e2.getX() > 120 )
			{
				DreamoriLog.LogQianZiWen("fling right");
				ShowNextImage(null);
			}
			else if( e1.getX() - e2.getX() < -120 ) 
			{
				DreamoriLog.LogQianZiWen("fling left");
				ShowPreviosImage(null);
			}
			else
			{
				return false;
			}
			
			return true;
		}		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}
	
}
