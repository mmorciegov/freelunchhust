package com.dreamori.daodejing;

import java.util.Random;

import com.dreamori.daodejing.DatabaseHelper;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.view.Window; 

public class DaoDeJing extends Activity {
	private DatabaseHelper m_dbHelper = null;	
	
	private ContentView contentView = null;
	
    static int m_currentImageIndex = Const.m_minImageIndex;
	
    public DatabaseHelper getDatabaseHelper()
	{
		if( m_dbHelper == null )
		{
			m_dbHelper = DatabaseHelper.getInstance(getApplicationContext());
		}
		
		return m_dbHelper;
	}
	
	public void ShowNextImage(View v)
	{
		contentView.ShowNextImage();		
	}
	
	public void ShowPreviosImage(View v)
	{	
		contentView.ShowPreviosImage();		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
        contentView =(ContentView)findViewById(R.id.img_content);
        m_currentImageIndex = getDatabaseHelper().GetLastImageIndex();    
    }
    
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		m_dbHelper.SetLastImageIndex(m_currentImageIndex);
	}
    
    
	
}
