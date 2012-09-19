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
        
        setTitle(getString(R.string.title_tip));
        
        if( getDatabaseHelper() == null )
        {
        	DreamoriLog.LogFoodExpert("Get db error");
        	return;
        }
        
        m_intent = new Intent(TipPage.this, Food.class);
        	
    	List<String> randList = new ArrayList<String>();
    	Random rand = new Random();
    	m_flag = Math.abs(rand.nextInt()) % 2;
    	if (m_flag == FoodConst.ACTIVITY_TYPE_FOOD)
    	{
    		DreamoriLog.LogFoodExpert("Before getRandomDataInFood");
    		getDatabaseHelper().getRandomDataInFood(randList);
    		DreamoriLog.LogFoodExpert("After getRandomDataInFood");
    	}
    	else
    	{
    		getDatabaseHelper().getRandomDataInDisease(randList);
    	}
    	
    	if (randList.size() >= 2)
    	{
        	m_name1 = randList.get(0);
        	m_name2 = randList.get(1);	
    	}
              
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
        

        ImageView imageView1 = (ImageView)findViewById(R.id.ui_tip_icon_1);
        ImageView imageView2 = (ImageView)findViewById(R.id.ui_tip_icon_2);
    	TextView textView1 = (TextView)findViewById(R.id.ui_tip_name_1);
        TextView textView2 = (TextView)findViewById(R.id.ui_tip_name_2);
        InitFood(imageView1, textView1, m_name1);
        InitFood(imageView2, textView2, m_name2);
        
        // Get hint and degree from database
        // m_flag : 0 Food Relationship
        // m_flag : 1 Disease Relationship
        List<RelativeData> dataList = new ArrayList<RelativeData>();
        if (m_flag == FoodConst.ACTIVITY_TYPE_FOOD)
        {
        	getDatabaseHelper().findDetailInfoInFood(m_name1, m_name2, dataList);
        }
        else
        {
        	getDatabaseHelper().findDetailInfoInDisease(m_name1, m_name2, dataList);
        }
        
		if (dataList.size() > 0) {
			
			DreamoriLog.LogFoodExpert("DataList has value");

	    	ImageView imageViewDegree = (ImageView)findViewById(R.id.ui_tip_degree);
			imageViewDegree.setImageResource(ResourceManager
					.GetDegreeId(dataList.get(0).degree));
			m_textViewHint.setText(dataList.get(0).hint);
			
			if (dataList.get(0).degree > 0) {
				setTitle(m_name1 + getString(R.string.and) + m_name2
						+ getString(R.string.food_related_good));
				m_textViewHint.setTextColor( getResources().getColor(R.color.good_relationship));
				
			} else if (dataList.get(0).degree < 0) {
				setTitle(m_name1 + getString(R.string.and) + m_name2
						+ getString(R.string.food_related_bad));
				m_textViewHint.setTextColor(getResources().getColor(R.color.bad_relationship));
			}
		}
		else
		{
			DreamoriLog.LogFoodExpert("DataList is null");
		}



		m_timer = null;
    	if (ConfigData.GetSystemTip(GetConfigFileName()) == 1)
    	{
    		m_timer = new Timer();
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					DreamoriLog.LogFoodExpert("TimerTask");
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
    	if (event.getAction() == MotionEvent.ACTION_DOWN)
    	{
        	DreamoriLog.LogFoodExpert("onTouchEvgent ACTION_DOWN");
        	if (m_timer != null)
        	{
        		m_timer.cancel();
        	}
    		startActivity(m_intent);
    		finish();    	
        	return true;	
    	}
    	
    	return false;
    }

	@Override
	protected void onDestroy() {
    	if (m_timer != null)
    	{
    		m_timer.cancel();
    		m_timer = null;
    	}
		super.onDestroy();
	}
    
    
}
