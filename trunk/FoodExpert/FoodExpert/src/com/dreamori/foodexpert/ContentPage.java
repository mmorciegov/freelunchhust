package com.dreamori.foodexpert;

import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.widget.TextView;

public class ContentPage extends TitleActivity {
	public String m_name1 = null;
	public String m_name2 = null;
	public int m_flag = 0;
	
	private BitmapDrawable bdLeft = null;
	private BitmapDrawable bdRight = null;
	private BitmapDrawable bdDegree = null;
	
	public TextView m_textViewHint;	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if( bdLeft != null )
		{
			bdLeft.setCallback(null);
			bdLeft.getBitmap().recycle();	
			bdLeft = null;
		}
		
		if( bdRight != null )
		{
			bdRight.setCallback(null);
			bdRight.getBitmap().recycle();	
			bdRight = null;
		}
		
		if( bdDegree != null )
		{
			bdDegree.setCallback(null);
			bdDegree.getBitmap().recycle();	
			bdDegree = null;
		}
		
		m_textViewHint = null;		
	}

	public void InitLeftValue(ImageView image, TextView text, String name)
	{
		bdLeft = ResourceManager.GetBitmapDrawable(this,  getDatabaseHelper().getIconName(name) );
		image.setImageDrawable(bdLeft);
		text.setText(name);
	}
	
	public void InitRightValue(ImageView image, TextView text, String name)
	{
		bdRight = ResourceManager.GetBitmapDrawable(this,  getDatabaseHelper().getIconName(name) );
		image.setImageDrawable(bdRight);
		text.setText(name);
	}
	
	public void InitDegreeInfo(ImageView image, int degree )
	{
		bdDegree = ResourceManager.GetBitmapDrawable(this, ResourceManager.GetDegreeIconName(degree));
		image.setImageDrawable(bdDegree);
	}
}
