package com.dreamori.daodejing;

import java.util.ArrayList;
import com.dreamori.daodejing.DatabaseHelper;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.view.ViewGroup.MarginLayoutParams; 
import android.widget.RelativeLayout; 

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
		contentView.ShowNextImage(v);
	}
		
	public void ShowPreviosImage(View v)
	{	
		contentView.ShowPreviosImage(v);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        contentView =(ContentView)findViewById(R.id.img_content);
        m_currentImageIndex = getDatabaseHelper().GetLastImageIndex();                    
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		m_dbHelper.SetLastImageIndex(m_currentImageIndex);
	}
    
    
	
}
