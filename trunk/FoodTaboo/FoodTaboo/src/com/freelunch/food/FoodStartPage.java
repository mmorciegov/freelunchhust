package com.freelunch.food;

import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;

public class FoodStartPage extends TitleActivity {
		
	private String firstItemName = "";
	private String secondItemName = "";
	
	private String firstFoodClass = "";
	private String secondFoodClass = "";
	
	private int firstFoodRelativeFlag = 0;
	private int secondFoodRelatvieFlag = 0;

	private ImageView firstItemImageView;
	private ImageView secondItemImageView;
	private ImageButton searchButton;
	
	private static String itemPostionString = FoodConst.DATA_ITEM_LEFT;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_start_page);
                
        firstItemImageView = (ImageView) findViewById(R.id.firstItemImageView);
        secondItemImageView = (ImageView)findViewById(R.id.secondItemImageView);
        searchButton = (ImageButton) findViewById(R.id.showDetailBtn);
        
        firstItemImageView.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				
				itemPostionString = FoodConst.DATA_ITEM_LEFT;
				
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				
				if( secondItemName != null && !secondItemName.equals(""))
				{
					intent.setClass(FoodStartPage.this, InquireSearchPage.class);
					bundle.putString(FoodConst.KYE_ITEM_NAME, secondItemName);
					bundle.putString(FoodConst.KEY_ITEM_TYPE, secondFoodClass);
					intent.putExtras(bundle);
					
					startActivityForResult(intent, FoodConst.INTENT_RESULT_FIRST_SEARCH_PAGE);	
				}
				else 
				{
					intent.setClass(FoodStartPage.this, InquireResultPage.class);
					bundle.putString(FoodConst.KYE_ITEM_NAME, secondItemName);
					bundle.putString(FoodConst.KEY_ITEM_TYPE, secondFoodClass);
					bundle.putInt(FoodConst.KEY_ITEM_RELATIVE, secondFoodRelatvieFlag);
					intent.putExtras(bundle);
					
					startActivityForResult(intent, FoodConst.INTENT_RESULT_SECOND_SEARCH_PAGE);	
				}
			}});
        
        secondItemImageView.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				
				itemPostionString = FoodConst.DATA_ITEM_RIGHT;
				
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				
				if( firstItemName != null && !firstItemName.equals("") )
				{
					intent.setClass(FoodStartPage.this, InquireSearchPage.class);
					bundle.putString(FoodConst.KYE_ITEM_NAME, firstItemName);
					bundle.putString(FoodConst.KEY_ITEM_TYPE, firstFoodClass);
					intent.putExtras(bundle);
					
					startActivityForResult(intent, FoodConst.INTENT_RESULT_FIRST_SEARCH_PAGE);	
				}
				else 
				{
					intent.setClass(FoodStartPage.this, InquireResultPage.class);
					bundle.putString(FoodConst.KYE_ITEM_NAME, firstItemName);
					bundle.putString(FoodConst.KEY_ITEM_TYPE, firstFoodClass);
					bundle.putInt(FoodConst.KEY_ITEM_RELATIVE, firstFoodRelativeFlag);
					intent.putExtras(bundle);
					
					startActivityForResult(intent, FoodConst.INTENT_RESULT_SECOND_SEARCH_PAGE);	
				}
			}});        
    }

    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Bundle bundle;
		
		switch (resultCode) {
		case FoodConst.INTENT_RESULT_FIRST_SEARCH_PAGE:
			bundle = data.getExtras();
			
			if( itemPostionString.equals(FoodConst.DATA_ITEM_LEFT))
			{
				firstItemName = bundle.getString(FoodConst.KYE_ITEM_NAME);
				firstFoodClass = bundle.getString(FoodConst.KEY_ITEM_TYPE);
				firstItemImageView.setImageResource(ResourceManager.GetIcon(this, firstItemName));	
			}
			else
			{
				secondItemName = bundle.getString(FoodConst.KYE_ITEM_NAME);
				secondFoodClass = bundle.getString(FoodConst.KEY_ITEM_TYPE);
				secondItemImageView.setImageResource(ResourceManager.GetIcon(this, firstItemName));									
			}

			
			break;
			
		case FoodConst.INTENT_RESULT_SECOND_SEARCH_PAGE:
			
			bundle = data.getExtras();
			
			if( itemPostionString.equals(FoodConst.DATA_ITEM_LEFT))
			{
				firstItemName = bundle.getString(FoodConst.KYE_ITEM_NAME);
				firstFoodClass = bundle.getString(FoodConst.KEY_ITEM_TYPE);
				firstFoodRelativeFlag = bundle.getInt(FoodConst.KEY_ITEM_RELATIVE);
				firstItemImageView.setImageResource(ResourceManager.GetIcon(this, firstItemName));	
			}
			else
			{
				secondItemName = bundle.getString(FoodConst.KYE_ITEM_NAME);
				secondFoodClass = bundle.getString(FoodConst.KEY_ITEM_TYPE);
				secondFoodRelatvieFlag = bundle.getInt(FoodConst.KEY_ITEM_RELATIVE);
				secondItemImageView.setImageResource(ResourceManager.GetIcon(this, secondItemName));									
			}
			
			break;
		default:
			break;
		}

	}    
    
}
