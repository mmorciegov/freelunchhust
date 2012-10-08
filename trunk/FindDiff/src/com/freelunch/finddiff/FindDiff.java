package com.freelunch.finddiff;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;

public class FindDiff extends Activity {
	DiffView dv1;
	DiffView dv2;
	ProgressBar proBar;
	Button tip;
	Button pause;
	
	int inittime = 20;
	int tipcount = 5;
	int leftTime = 0;
	boolean isPause = false;
	
	int m_easy = 1;
	int m_pos = 1;
	
	Databasehelper m_dbHelper;

	AlertDialog pauseDlg;
	AlertDialog passDlg;
	AlertDialog failDlg;
	
	PopupWindow m_pauseDlg;
	PopupWindow m_passDlg;
	PopupWindow m_failDlg;
	
	class DiffSync implements SyncData
	{
		public void Sync(int index)
		{
			dv1.m_data.get(index).isHit = true;
			dv2.m_data.get(index).isHit = true;
			dv1.invalidate();
			dv2.invalidate();
			
			for (int i=0; i<dv1.m_data.size(); i++)
			{
				if (!dv1.m_data.get(i).isHit)
				{
					return;
				}
			}
			
			isPause = true;
			passDlg.show();
		}
	}
	
	private void CreateDlg(final boolean isPass, PopupWindow popupWnd)
	{
		Builder dlgBuild = new AlertDialog.Builder(this);	
        ListAdapter adapter = new GridViewAdapter(this, isPass);
        
        View view = getLayoutInflater().inflate(R.layout.popup_dlg, null);
        
        if (isPass)
        {
        	view.setBackgroundResource(R.drawable.face_pass);
        }
        else
        {
        	view.setBackgroundResource(R.drawable.face_fail);
        }
        
        GridView gridview = (GridView)view.findViewById(R.id.popup_dlg_grid);
        gridview.setAdapter(adapter);	
		
        popupWnd = new PopupWindow(view, 280, 360);
		
		gridview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (arg2 == 0)
				{
					Restart();
				}
				else if (arg2 == 1)
				{
					if (isPass)
					{
						m_pos++;
						Restart();
					}
					else
					{
						return;
					}
				}
			}
		});
	}
	
	private void Restart()
	{
        PhotoInfo info = new PhotoInfo();
        m_dbHelper.GetPhotoInfo(m_easy, m_pos, info);
        
        List<MatchRecord> rects = new ArrayList<MatchRecord>();
        for (int i=0; i<info.diffList.size(); i++)
        {
        	MatchRecord rec = new MatchRecord();
        	rec.rect = info.diffList.get(i);        	
        	rec.isHit = false;

        	rects.add(rec);        	
        } 
        
        proBar.setMax(inittime);
        leftTime = proBar.getMax();
        
    	Bitmap bitmap1 = null;
    	Bitmap bitmap2 = null;
		try {
			bitmap1 = BitmapFactory.decodeStream(getAssets().open(info.srcName));
			bitmap2 = BitmapFactory.decodeStream(getAssets().open(info.dstName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		dv1.SetData(rects, bitmap1, new DiffSync());
		dv2.SetData(rects, bitmap2, new DiffSync());
		
		dv1.invalidate();
		dv2.invalidate();
		
		isPause = false;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        m_dbHelper = Databasehelper.getInstance(this);
        
        tip = (Button)findViewById(R.id.tip);
        pause = (Button)findViewById(R.id.pause);
        dv1 = (DiffView)findViewById(R.id.diffView1);
        dv2 = (DiffView)findViewById(R.id.diffView2);
        proBar = (ProgressBar)findViewById(R.id.progressbar1);
        
        tip.setText("tip x"+String.valueOf(tipcount));
        tip.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (tipcount > 0)
				{
					for (int i=0; i<dv1.m_data.size(); i++)
					{
						if (!dv1.m_data.get(i).isHit)
						{
							new DiffSync().Sync(i);
							break;
						}
					}
					tipcount --;
					tip.setText("tip x"+String.valueOf(tipcount));
				}
			}
        });
        
        pause.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isPause = !isPause;
				if (isPause)
				{
					pauseDlg.show();
				}
				else
				{
					pauseDlg.hide();
				}
			}
        });
        
        final Handler myhandler = new Handler()
        {
			@Override
			public void handleMessage(Message msg)
			{
				if (msg.what == 0x111)
				{
					proBar.setProgress(leftTime);
					if (leftTime == 0)
					{
						failDlg.show();
					}
				}
			}
        };
        
        new Timer().schedule(new TimerTask()
        {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (!isPause)
    			{
    				leftTime --;
					Message m = new Message();
					m.what = 0x111;
					myhandler.sendMessage(m);
    			}
			}
        } , 0, 1000);
        
//        new Thread()
//        {
//        	public void run()
//        	{
//        		while(leftTime > 0)
//        		{
//        			try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//        			
//        			if (!isPause)
//        			{
//        				leftTime --;
//    					Message m = new Message();
//    					m.what = 0x111;
//    					myhandler.sendMessage(m);
//        			}
//        		}
//        	}
//        }.start();
	
		Builder pauseDlgBuild = new AlertDialog.Builder(this);
		ImageView pauseView = new ImageView(this);
		pauseView.setBackgroundResource(R.drawable.pause);
		pauseDlgBuild.setView(pauseView);
		
		CreateDlg(true, m_passDlg);
		CreateDlg(false, m_failDlg);
		
		pauseDlg = pauseDlgBuild.create();
		
		Restart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}

