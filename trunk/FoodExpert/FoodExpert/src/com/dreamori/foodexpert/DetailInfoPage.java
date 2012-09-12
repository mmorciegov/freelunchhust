package com.dreamori.foodexpert;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailInfoPage extends ContentPage {
	
	private int m_diseaseSerachType;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_detail_info);
        
        m_dbHelper = DatabaseHelper.getInstance(this);
        m_level = 4;
        
        Intent intent = getIntent();
        if (intent != null)
        {
            Bundle bundle = intent.getExtras();
            if (bundle != null)
            {
                m_name1 = (String)bundle.getSerializable("Name1");
                m_name2 = (String)bundle.getSerializable("Name2");
                m_flag = (Integer)bundle.getSerializable("Relative");                
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

        if (m_flag == FoodConst.ACTIVITY_TYPE_FOOD)
        {
        	m_dbHelper.findDetailInfoInFood(m_name1, m_name2, dataList);    
        	//Update food query frequency
	        m_dbHelper.AddFoodSearchFrequency(m_name1);
        }
        else
        {
        	m_dbHelper.findDetailInfoInDisease(m_name1, m_name2, dataList);
        	//Update disease query frequency        	
            m_dbHelper.AddDiseaseSearchFrequency(m_name1);
        }
        
        m_dbHelper.AddFoodSearchFrequency(m_name2);
        
        if (dataList.size() > 0)
        {
	        m_imageViewDegree.setBackgroundResource(ResourceManager.GetDegreeId(dataList.get(0).degree));
	        m_textViewHint.setText(dataList.get(0).hint);
        }
                
        if(dataList.get(0).degree > 0)
        {
        	
        	Log.v("Food Expert", "m_flag > 0");
        	
        	setTitle(m_name1 + getString(R.string.and) + m_name2 + getString(R.string.food_related_good));
        	m_textViewHint.setTextColor( getResources().getColor(R.color.good_relationship));
        }
        else if(dataList.get(0).degree < 0)
        {
        	Log.v("Food Expert", "m_flag < 0");  	
        	
        	setTitle(m_name1 + getString(R.string.and) + m_name2 + getString(R.string.food_related_bad));
        	m_textViewHint.setTextColor( getResources().getColor(R.color.bad_relationship));
		}
        
        
        m_imageView1.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				KillActivity(FoodConst.ACTIVITY_LEVEL3);				
				Bundle bundle = new Bundle();
		    	
				Intent intent;
				if (m_flag == FoodConst.ACTIVITY_TYPE_FOOD)
				{			
					bundle.putSerializable("FoodName", m_name1);
					bundle.putSerializable("Relative", m_flag);
					intent = new Intent(DetailInfoPage.this, InquireResultPage.class);
				}
				else
				{
					bundle.putSerializable(FoodConst.INTENT_DISEASE, m_name1);
					bundle.putSerializable(FoodConst.INTENT_DISEASE_SEARCH_TYPE, FoodConst.DISEASE_RESULT_SEARCH_DISEASE);
					intent = new Intent(DetailInfoPage.this, DiseaseResultPage.class);
				}
				
				intent.putExtras(bundle);
				
				startActivity(intent);
				finish();			
			}
		});

		m_imageView2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				KillActivity(FoodConst.ACTIVITY_LEVEL3);

				Bundle bundle = new Bundle();
				Intent intent;
				if (m_flag == FoodConst.ACTIVITY_TYPE_FOOD)
				{			
					bundle.putSerializable("FoodName", m_name2);
					bundle.putSerializable("Relative", m_flag);
					intent = new Intent(DetailInfoPage.this, InquireResultPage.class);
				}
				else
				{
					bundle.putSerializable(FoodConst.INTENT_DISEASE, m_name2);
					bundle.putSerializable(FoodConst.INTENT_DISEASE_SEARCH_TYPE, FoodConst.DISEASE_RESULT_SEARCH_FOOD);
					intent = new Intent(DetailInfoPage.this, DiseaseResultPage.class);
				}
				intent.putExtras(bundle);

				startActivity(intent);
				finish();
			}
		});

		Button search = (Button) findViewById(R.id.ui_detail_search);
		Button share = (Button)findViewById(R.id.ui_detail_share);
        
        search.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Close all activity first
				KillActivity(FoodConst.ACTIVITY_LEVEL2);
				
				Intent intent = null;
				if (m_flag == FoodConst.ACTIVITY_TYPE_FOOD)
				{
					intent = new Intent(DetailInfoPage.this, InquireSearchPage.class);					
				}
				else
				{
					intent = new Intent(DetailInfoPage.this, DiseaseResultPage.class);
				}
				
				startActivity(intent);
				finish();
			}
        });
        
        share.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
        });
    }
}
