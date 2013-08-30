package com.dreamori.daodejing;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class ContentView extends View {
	
	private DatabaseHelper m_dbHelper = null;	
	private ArrayList<Hotspot> hotspots = new ArrayList<Hotspot>();
	Rect   m_srcRect = null;
	Rect   m_dstRect = null;
	
	Bitmap m_bitmap = null;
	
	boolean m_needShowTouchTips = true;

	public ContentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		if( m_dbHelper == null )
		{
			m_dbHelper = DatabaseHelper.getInstance(context);
		}
		
		m_needShowTouchTips = m_dbHelper.NeedShowTouchTips();
	}
	

	private void InitRect()
	{
		hotspots.clear();
		m_bitmap = BitmapFactory.decodeResource( getContext().getResources(),  ResourceManager.GetIcon(getContext(), m_dbHelper.GetImageContentName(DaoDeJing.m_currentImageIndex)));
		
		m_srcRect = new Rect();
		m_srcRect.left = m_srcRect.top = 0;
		m_srcRect.right = m_bitmap.getWidth(); 
		m_srcRect.bottom = m_bitmap.getHeight();
		
		m_dstRect = new Rect();
		m_dstRect.left = m_dstRect.top = 0;
		m_dstRect.right = getWidth(); 
		m_dstRect.bottom = getHeight();	
		
		Hotspot.IniUiImageSize(m_dstRect);   	
		
		m_dbHelper.GetImageHotSpot( DaoDeJing.m_currentImageIndex, hotspots);
		for (Hotspot hotspot : hotspots) {
			hotspot.GetHotspotUiPosition();
		}
	}
			
	public void UpdateImageAndHotspot()
	{
		hotspots.clear();
		m_bitmap = BitmapFactory.decodeResource( getContext().getResources(),  ResourceManager.GetIcon(getContext(), m_dbHelper.GetImageContentName(DaoDeJing.m_currentImageIndex)));

		m_dbHelper.GetImageHotSpot( DaoDeJing.m_currentImageIndex, hotspots);
		for (Hotspot hotspot : hotspots) {
			hotspot.GetHotspotUiPosition();
		}			
	}
	
	public void ShowNextImage(View v)
	{
		DaoDeJing.m_currentImageIndex++;
		if( DaoDeJing.m_currentImageIndex > Const.m_maxImageIndex )
		{
			DaoDeJing.m_currentImageIndex = Const.m_maxImageIndex;
			return;
		}

		UpdateImageAndHotspot();
	}
	
	
	public void ShowPreviosImage(View v)
	{	
		DaoDeJing.m_currentImageIndex--;
		if( DaoDeJing.m_currentImageIndex < Const.m_minImageIndex )
		{
			DaoDeJing.m_currentImageIndex = Const.m_minImageIndex;
			return;
		}
		
		UpdateImageAndHotspot();
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
	
	private boolean cancleToast = false;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
		
		case MotionEvent.ACTION_DOWN:
			 {
				int index = IsHit(x, y);
				if (index != -1) {
					DreamoriToast.killToast();
					// Toast toast = Toast.makeText(getContext(),
					// hotspots.get(index).contentString, Toast.LENGTH_LONG);
					// toast.show();
					//if (!cancleToast) {
						DreamoriToast.invokeLongTimeToast(getContext(),
								hotspots.get(index).contentString);
						
						DreamoriLog.LogDaoDeJing("Toast Show");
					//	cancleToast = true;
					//}
				}
				else 
				{
					//if (cancleToast) {
						DreamoriToast.killToast();
						DreamoriLog.LogDaoDeJing("Toast Killed");
					//	cancleToast = false;
					//} 					
				}
			}
			break;
		default:
			break;
		}
		
		return true;
	}
	

	public void killToast() {
		DreamoriToast.killToast();
		DreamoriLog.LogDaoDeJing("Toast Killed");
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		if (m_dstRect == null)
		{
			InitRect();
		}
		canvas.drawBitmap(m_bitmap, m_srcRect, m_dstRect, null);
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.GREEN);
		paint.setStrokeWidth(2);
		paint.setStyle(Paint.Style.STROKE);

		if (m_needShowTouchTips) {
			for (int i = 0; i < hotspots.size(); i++) {				
				
				// canvas.drawCircle(m_data.get(i).pt.x, m_data.get(i).pt.y, 5,
				// paint);
				RectF rectf = new RectF(hotspots.get(i).posUiRect);

				
				DreamoriLog.LogDaoDeJing("Draw hotspots." +  "left: "+  rectf.left + "right: " + rectf.bottom + 4 + "" +  rectf.right + "" + rectf.bottom + 4);
				
				canvas.drawLine(rectf.left, rectf.bottom + 4,  rectf.right, rectf.bottom + 4,  paint);
			}
		}		
		
		invalidate();
	}
}

