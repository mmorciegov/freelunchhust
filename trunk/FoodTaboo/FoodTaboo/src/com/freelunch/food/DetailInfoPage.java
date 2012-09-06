package com.freelunch.food;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailInfoPage extends TitleActivity {
	private String m_name1 = null;
	private String m_name2 = null;
	private int m_flag = 0;
	
	private ImageView m_imageView1;
	private ImageView m_imageView2;
	private TextView m_textView1;
	private TextView m_textView2;
	
	private ImageView m_imageViewDegree;
	private TextView m_textViewHint;
	
	private CheckBox m_tip;
	
	private Databasehelper m_dbHelper = null;	
	private boolean m_isTipStart = false;
	
	private void InitFood(ImageView image, TextView text, String name)
	{
		image.setBackgroundResource(ResourceManager.GetIcon(this, m_dbHelper.getIconName(name)));
		text.setText(name);
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_detail_info);
        
        m_dbHelper = Databasehelper.getInstance(this);
        
        Intent intent = getIntent();
        if (intent != null)
        {
            Bundle bind = intent.getExtras();
            if (bind != null)
            {
                m_name1 = (String)bind.getSerializable("Name1");
                m_name2 = (String)bind.getSerializable("Name2");
                m_flag = (Integer)bind.getSerializable("Relative");
            }
        }
        
        if (m_name1 == null)
        {
        	// Get random data for tip page.
        	m_isTipStart = true;
        	
        	m_name1 = "花生";
        	m_name2 = "牛奶";
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
        }
        
        m_imageView1 = (ImageView)findViewById(R.id.ui_detail_icon_1);
        m_imageView2 = (ImageView)findViewById(R.id.ui_detail_icon_2);
        m_textView1 = (TextView)findViewById(R.id.ui_detail_name_1);
        m_textView2 = (TextView)findViewById(R.id.ui_detail_name_2);
        
        m_imageViewDegree = (ImageView)findViewById(R.id.ui_detail_degree);
        m_textViewHint = (TextView)findViewById(R.id.ui_detail_hint);
        m_tip = (CheckBox)findViewById(R.id.ui_detail_tip);
        
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
        
        if (!m_isTipStart)
        {
        	m_tip.setVisibility(View.GONE);
        }
        
        InitFood(m_imageView1, m_textView1, m_name1);
        InitFood(m_imageView2, m_textView2, m_name2);
        
        // Get hint and degree from database
        // m_flag : 0 查食物食物禁忌
        // m_flag : 1 查疾病食物禁忌
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
        
        if (m_isTipStart)
        {
        	final Intent it = new Intent(DetailInfoPage.this, Food.class);
        	if (ConfigData.GetSystemTip(GetConfigFileName()) == 1)
        	{
    			Timer timer = new Timer();
    			TimerTask task = new TimerTask() {
    				@Override
    				public void run() {
    					startActivity(it);
    					finish();
    				}
    			};
    			timer.schedule(task, 1000 * 3);
    			m_tip.setChecked(true);
        	}
        	else
        	{
        		startActivity(it);
        		finish();
        	}
        }
    }
}
