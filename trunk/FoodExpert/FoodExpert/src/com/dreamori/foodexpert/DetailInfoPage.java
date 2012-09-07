package com.dreamori.foodexpert;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailInfoPage extends ContentPage {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_detail_info);
        
        m_dbHelper = DatabaseHelper.getInstance(this);
        
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
        
        m_imageView1 = (ImageView)findViewById(R.id.ui_detail_icon_1);
        m_imageView2 = (ImageView)findViewById(R.id.ui_detail_icon_2);
        m_textView1 = (TextView)findViewById(R.id.ui_detail_name_1);
        m_textView2 = (TextView)findViewById(R.id.ui_detail_name_2);
        
        m_imageViewDegree = (ImageView)findViewById(R.id.ui_detail_degree);
        m_textViewHint = (TextView)findViewById(R.id.ui_detail_hint);
        
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
        
    }
}
