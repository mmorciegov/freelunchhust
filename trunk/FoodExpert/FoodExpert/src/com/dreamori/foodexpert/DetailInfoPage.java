package com.dreamori.foodexpert;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailInfoPage extends ContentPage {		
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		m_imageView1 = null;
		m_imageView2 = null;
		m_textView1 = null;
		m_textView2 = null;

		m_imageViewDegree = null;
		m_textViewHint = null;
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_detail_info);

//        View titleView = getWindow().getDecorView();
//        final TextView titleViewTxt = (TextView) titleView.findViewById(R.id.ui_title_text);
//        titleViewTxt.setTextColor(Color.WHITE);
//        ImageView shareView = (ImageView) titleView.findViewById(R.id.ui_title_share);
//        shareView.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent(Intent.ACTION_SEND);
//				intent.setType("text/plain");
//				intent.putExtra(Intent.EXTRA_SUBJECT, titleViewTxt.getText());
//				intent.putExtra(Intent.EXTRA_TEXT, m_textViewHint.getText());
//				startActivity(Intent.createChooser(intent, "Share"));
//			}
//        });
        
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
        	getDatabaseHelper().findDetailInfoInFood(m_name1, m_name2, dataList);    
        	//Update food query frequency
        	getDatabaseHelper().AddFoodSearchFrequency(m_name1);
        }
        else
        {
        	getDatabaseHelper().findDetailInfoInDisease(m_name1, m_name2, dataList);
        	//Update disease query frequency        	
        	getDatabaseHelper().AddDiseaseSearchFrequency(m_name1);
        }
        
        getDatabaseHelper().AddFoodSearchFrequency(m_name2);
        
        if (dataList.size() > 0)
        {
	        m_imageViewDegree.setBackgroundResource(ResourceManager.GetDegreeId(dataList.get(0).degree));
	        m_textViewHint.setText(dataList.get(0).hint);
        }
                
        if(dataList.get(0).degree > 0)
        {
        	
        	DreamoriLog.LogFoodExpert( "m_flag > 0");
        	
//        	titleViewTxt.setText(m_name1 + getString(R.string.and) + m_name2 + getString(R.string.food_related_good));
        	setTitle(m_name1 + getString(R.string.and) + m_name2 + getString(R.string.food_related_good));
        	m_textViewHint.setTextColor( getResources().getColor(R.color.good_relationship));
        }
        else if(dataList.get(0).degree < 0)
        {
        	DreamoriLog.LogFoodExpert( "m_flag < 0");  	
        	
//        	titleViewTxt.setText(m_name1 + getString(R.string.and) + m_name2 + getString(R.string.food_related_bad));
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
					intent = new Intent(DetailInfoPage.this, DiseaseSearchPage.class);
				}
				
				startActivity(intent);
				finish();
			}
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
    	switch(mi.getItemId())
    	{
    	case R.id.menu_share:    	
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_SUBJECT, getTitle());
			intent.putExtra(Intent.EXTRA_TEXT, m_textViewHint.getText());
			startActivity(Intent.createChooser(intent, "Share"));    		
    		break;
    	default:
    		break;
    	}
        return true;
    }
}
