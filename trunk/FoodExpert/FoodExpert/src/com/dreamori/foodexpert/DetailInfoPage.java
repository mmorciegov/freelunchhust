package com.dreamori.foodexpert;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailInfoPage extends ContentPage {
	
	View mItem1 = null;
	View mItem2 = null;
	Boolean mIsItem1Click = false;
	Boolean mIsItem2Click = false;
	
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		m_textViewHint = null;
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_detail_info);        
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
                
        
        m_textViewHint = (TextView)findViewById(R.id.ui_detail_hint);
        m_textViewHint.setMovementMethod(new ScrollingMovementMethod());


        ImageView imageView1 = (ImageView)findViewById(R.id.ui_detail_icon_1);
        ImageView imageView2 = (ImageView)findViewById(R.id.ui_detail_icon_2);
        TextView textView1 = (TextView)findViewById(R.id.ui_detail_name_1);
        TextView textView2 = (TextView)findViewById(R.id.ui_detail_name_2);
        
        TextView textViewHowToEat= (TextView)findViewById(R.id.ui_detail_how_to_eat);  
        
        InitImageAndTextIcon(imageView1, textView1, m_name1);
        InitImageAndTextIcon(imageView2, textView2, m_name2);
        
        // Get hint and degree from database
        // m_flag : 0 Food Relationship
        // m_flag : 1 Disease Relationship
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
            ImageView imageViewDegree = (ImageView)findViewById(R.id.ui_detail_degree);
           
            InitDegreeIcon(imageViewDegree, ResourceManager.GetDegreeIconName(dataList.get(0).degree));
	        m_textViewHint.setText(dataList.get(0).hint);
        }
        else
        {
        	return;
        }
        
      //For disease page, if the relationship is good, we should tell user how to eat 
        if( (m_flag == FoodConst.ACTIVITY_TYPE_DISEASE) && (dataList.get(0).degree > 0) )
        {        	
        	textViewHowToEat.setText(getDatabaseHelper().getFoodEatMethod(m_name1, m_name2));
        }
        else
        {
        	textViewHowToEat.setVisibility(View.GONE);
        }
                        
        if(dataList.get(0).degree > 0)
        {
        	
        	DreamoriLog.LogFoodExpert( "m_flag > 0");
        	
//        	titleViewTxt.setText(m_name1 + getString(R.string.and) + m_name2 + getString(R.string.food_related_good));
        	setTitle(m_name1 + getString(R.string.and) + m_name2 + getString(R.string.food_related_good));
        	m_textViewHint.setTextColor( getResources().getColor(R.color.good_relationship));
        	
            //For disease page, if the relationship is good, we should tell user how to eat 
            if(m_flag == FoodConst.ACTIVITY_TYPE_DISEASE) 
            {    
            	String howToEat=getDatabaseHelper().getFoodEatMethod(m_name1, m_name2);
            	if( howToEat != null && howToEat != "" && howToEat.length() > 0)
            	{
                	textViewHowToEat.setText(getResources().getString(R.string.text_how_to_eat) + howToEat);
            	}
            	else
            	{
            		textViewHowToEat.setVisibility(View.GONE);
            	}
            }
            else
            {
            	textViewHowToEat.setVisibility(View.GONE);
            }
        }
        else if(dataList.get(0).degree < 0)
        {
        	DreamoriLog.LogFoodExpert( "m_flag < 0");  	
        	
//        	titleViewTxt.setText(m_name1 + getString(R.string.and) + m_name2 + getString(R.string.food_related_bad));
        	setTitle(m_name1 + getString(R.string.and) + m_name2 + getString(R.string.food_related_bad));
        	m_textViewHint.setTextColor( getResources().getColor(R.color.bad_relationship));
		}

		mItem1 = findViewById(R.id.linearLayout1);
		mItem2 = findViewById(R.id.linearLayout2);
		
		mItem1.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					v.setBackgroundColor(0x7F33B6EA);
					mItem2.setBackgroundColor(0);
					mIsItem1Click = true;
					mIsItem2Click = false;
					break;
				case MotionEvent.ACTION_MOVE:
					if( event.getX() < 0 || event.getY() < 0
							|| event.getX() > v.getWidth() || event.getY() > v.getHeight())
					{
						v.setBackgroundColor(0);
						mIsItem1Click = false;
					}
					break;
				case MotionEvent.ACTION_UP:
					v.setBackgroundColor(0);
					if( mIsItem1Click && event.getX() > 0 && event.getY() > 0
							&& event.getX() < v.getWidth() && event.getY() < v.getHeight())
					{
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
					break;
				}
				return true;
				}
			});

		mItem2.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					v.setBackgroundColor(0x7F33B6EA);
					mItem1.setBackgroundColor(0);
					mIsItem2Click = true;
					mIsItem1Click = false;
					break;
				case MotionEvent.ACTION_MOVE:
					if( event.getX() < 0 || event.getY() < 0
							|| event.getX() > v.getWidth() || event.getY() > v.getHeight())
					{
						v.setBackgroundColor(0);
						mIsItem2Click = false;
					}
					break;
				case MotionEvent.ACTION_UP:
					v.setBackgroundColor(0);
					if( mIsItem2Click && event.getX() > 0 && event.getY() > 0
							&& event.getX() < v.getWidth() && event.getY() < v.getHeight())
					{
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
					break;
				}
				return true;
				}
			});
		

        View search = findViewById(R.id.ui_detail_search);
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
