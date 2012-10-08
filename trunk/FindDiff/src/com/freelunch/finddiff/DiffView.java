package com.freelunch.finddiff;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DiffView extends View {
	
	List<MatchRecord> m_data;
	Bitmap m_cacheBitmap = null;
	Rect   m_srcRect = null;
	Rect   m_dstRect = null;
	
	SyncData m_syncInterface = null;

	public DiffView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	private void InitRect()
	{
		m_srcRect = new Rect();
		m_srcRect.left = m_srcRect.top = 0;
		m_srcRect.right = m_cacheBitmap.getWidth(); 
		m_srcRect.bottom = m_cacheBitmap.getHeight();
		
		m_dstRect = new Rect();
		m_dstRect.left = m_dstRect.top = 0;
		m_dstRect.right = getWidth(); 
		m_dstRect.bottom = getHeight();	
		
		for (int i=0; i<m_data.size(); i++)
		{
			m_data.get(i).rect.left = (m_data.get(i).rect.left * m_dstRect.right) / m_srcRect.right;
			m_data.get(i).rect.right = (m_data.get(i).rect.right * m_dstRect.right) / m_srcRect.right;
			m_data.get(i).rect.top = (m_data.get(i).rect.top * m_dstRect.bottom) / m_srcRect.bottom;
			m_data.get(i).rect.bottom = (m_data.get(i).rect.bottom * m_dstRect.bottom) / m_srcRect.bottom;
		}
	}
	
	public void SetData(List<MatchRecord> data, Bitmap inputImage, SyncData sync)
	{
		//setBackgroundDrawable(id); 
		//getResources().getDrawable(R.drawable.lijiang)
		m_cacheBitmap = inputImage;
		m_syncInterface = sync;

		m_data = new ArrayList<MatchRecord>();
		for (int i=0; i<data.size(); i++)
		{
			MatchRecord rec = new MatchRecord();
			rec.rect = new Rect();
			rec.rect.left = data.get(i).rect.left;
			rec.rect.right = data.get(i).rect.right;
			rec.rect.top = data.get(i).rect.top;
			rec.rect.bottom = data.get(i).rect.bottom;
			rec.isHit = false;
			
			m_data.add(rec);
		}
		
		m_dstRect = null;
	}
	
	public int IsHit(float x, float y)
	{
		for (int i=0; i<m_data.size(); i++)
		{
			Point pt = new Point();
			if (m_data.get(i).rect.contains((int)x, (int)y))
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
					m_syncInterface.Sync(index);
				}				
				break;
			default:
				break;
		}
		
		return true;
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		if (m_dstRect == null)
		{
			InitRect();
		}
		canvas.drawBitmap(m_cacheBitmap, m_srcRect, m_dstRect, null);
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.RED);
		paint.setStrokeWidth(2);
		paint.setStyle(Paint.Style.STROKE);
		
		for (int i=0; i<m_data.size(); i++)
		{
			if (m_data.get(i).isHit)
			{
				//canvas.drawCircle(m_data.get(i).pt.x, m_data.get(i).pt.y, 5, paint);
				RectF rectf = new RectF(m_data.get(i).rect);
				canvas.drawOval(rectf, paint);
			}
		}
	}
}

