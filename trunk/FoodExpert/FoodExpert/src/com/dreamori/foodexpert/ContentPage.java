package com.dreamori.foodexpert;

import android.widget.ImageView;
import android.widget.TextView;

public class ContentPage extends TitleActivity {
	public String m_name1 = null;
	public String m_name2 = null;
	public int m_flag = 0;
	
	public ImageView m_imageView1;
	public ImageView m_imageView2;
	public TextView m_textView1;
	public TextView m_textView2;
	
	public ImageView m_imageViewDegree;
	public TextView m_textViewHint;
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		m_textViewHint = null;
		m_textView1 = null;
		m_textView2 = null;		
		m_imageView1 = null;
		m_imageView2 = null;
		m_imageViewDegree = null;
		
	}
	



	public void InitFood(ImageView image, TextView text, String name)
	{
		image.setBackgroundResource(ResourceManager.GetIcon(this, getDatabaseHelper().getIconName(name)));
		text.setText(name);
	}
}
