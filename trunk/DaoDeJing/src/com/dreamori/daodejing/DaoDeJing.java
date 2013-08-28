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
	private ArrayList<Hotspot> hotspots = new ArrayList<Hotspot>();
	
	private Rect imgPosRect = new Rect();

    private int m_currentImageIndex = Const.m_minImageIndex;
	
	public DatabaseHelper getDatabaseHelper()
	{
		if( m_dbHelper == null )
		{
			m_dbHelper = DatabaseHelper.getInstance(getApplicationContext());
		}
		
		return m_dbHelper;
	}
	
	private ImageView imgContentImageView = null;
	
	private void UpdateImageAndHotspot()
	{
		hotspots.clear();
		imgContentImageView.setImageResource(  ResourceManager.GetIcon(this, getDatabaseHelper().GetImageContentName(m_currentImageIndex)));
		
		m_dbHelper.GetImageHotSpot(m_currentImageIndex, hotspots);
		for (Hotspot hotspot : hotspots) {
			hotspot.GetHotspotUiPosition();
		}		
	}
	
	public void ShowNextImage(View v)
	{
		m_currentImageIndex++;
		if( m_currentImageIndex > Const.m_maxImageIndex )
		{
			m_currentImageIndex = Const.m_maxImageIndex;
			return;
		}

		UpdateImageAndHotspot();
	}
	
	
	public void ShowPreviosImage(View v)
	{	
		m_currentImageIndex--;
		if( m_currentImageIndex < Const.m_minImageIndex )
		{
			m_currentImageIndex = Const.m_minImageIndex;
			return;
		}
		
		UpdateImageAndHotspot();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		int []location = new int[2];
		imgContentImageView.getLocationOnScreen(location);
	
		
//        imgPosRect.left = imgContentImageView.getLeft();
//        imgPosRect.top = imgContentImageView.getTop();
//        imgPosRect.right = imgContentImageView.getRight();
//        imgPosRect.bottom = imgContentImageView.getBottom();
		
		imgPosRect.left = location[0];
        imgPosRect.top = location[1];
        imgPosRect.right = imgContentImageView.getWidth() + imgPosRect.left;
        imgPosRect.bottom = imgContentImageView.getHeight() + imgPosRect.top;
        
        UpdateImageAndHotspot();
                
        //Need update later? when showing ad?
        Hotspot.IniUiImageSize(imgPosRect);   
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        imgContentImageView =(ImageView)findViewById(R.id.img_content);
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
    
    

	public int IsHit(float x, float y)
	{				
		
		for (int i=0; i< hotspots.size(); i++)
		{
			if( hotspots.get(i).posUiRect.contains((int)x, (int)y))
			{
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				int index = IsHit(x,y);
				if (index != -1)
				{
					Toast toast =  Toast.makeText(getApplicationContext(), hotspots.get(index).contentString, Toast.LENGTH_LONG);
					toast.show();
				}				
				break;
			default:
				break;
		}
		
		return true;
	}
	
}
