package com.dreamori.foodexpert;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TipPage extends ContentPage {
	private CheckBox m_tip;
	private Intent m_intent;
	private Timer m_timer;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_tip);
        
        m_dbHelper = DatabaseHelper.getInstance(this);
        m_intent = new Intent(TipPage.this, Food.class);
        	
    	List<String> randList = new ArrayList<String>();
    	Random rand = new Random();
    	m_flag = rand.nextInt() % 2;
    	if (m_flag == 0)
    	{
    		m_dbHelper.getRandomDataInFood(randList);
    	}
    	else
    	{
    		m_dbHelper.getRandomDataInDisease(randList);
    	}
    	
    	if (randList.size() >= 2)
    	{
        	m_name1 = randList.get(0);
        	m_name2 = randList.get(1);	
    	}
        
        m_imageView1 = (ImageView)findViewById(R.id.ui_tip_icon_1);
        m_imageView2 = (ImageView)findViewById(R.id.ui_tip_icon_2);
        m_textView1 = (TextView)findViewById(R.id.ui_tip_name_1);
        m_textView2 = (TextView)findViewById(R.id.ui_tip_name_2);
        
        m_imageViewDegree = (ImageView)findViewById(R.id.ui_tip_degree);
        m_textViewHint = (TextView)findViewById(R.id.ui_tip_hint);
        m_tip = (CheckBox)findViewById(R.id.ui_tip_check);
        
        m_tip.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
	        	String filename = GetConfigFileName();
	        	ConfigData configData = new ConfigData();
	        	
	        	XmlOperation.ReadXML(filename, configData);	
	        	configData.tip = isChecked ? 1: 0;
	        	XmlOperation.WriteXml(filename, configData);
			}
        });
        
        InitFood(m_imageView1, m_textView1, m_name1);
        InitFood(m_imageView2, m_textView2, m_name2);
        
        // Get hint and degree from database
        // m_flag : 0 Bad Relationship
        // m_flag : 1 Good Relationship
        List<RelativeData> dataList = new ArrayList<RelativeData>();
        if (m_flag == 0)
        {
        	m_dbHelper.findDetailInfoInFood(m_name1, m_name2, dataList);
        }
        else
        {
        	m_dbHelper.findDetailInfoInDisease(m_name1, m_name2, dataList);
        }
        
        if (dataList.size() > 0)
        {
	        m_imageViewDegree.setBackgroundResource(ResourceManager.GetDegreeId(dataList.get(0).degree));
	        m_textViewHint.setText(dataList.get(0).hint);
        }
        
        m_timer = null;
    	if (ConfigData.GetSystemTip(GetConfigFileName()) == 1)
    	{
    		m_timer = new Timer();
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					startActivity(m_intent);
					finish();
				}
			};
			m_timer.schedule(task, 1000 * 5);
			m_tip.setChecked(true); 		
    	}
    	else
    	{
    		startActivity(m_intent);
    		finish();
    	}
    }
    
    public boolean onTouchEvent(MotionEvent event)
    {
    	if (m_timer != null)
    	{
    		m_timer.cancel();
    	}
		startActivity(m_intent);
		finish();    	
    	return true;
    }
}
