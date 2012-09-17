package com.dreamori.foodexpert;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class BoardImageView extends ImageView{

	private int m_boardColor = Color.rgb(255, 0, 0);
	private int m_boardWidth = 10;
	
	public BoardImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public BoardImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public void SetBorderColor(int color)
	{
		m_boardColor = color;
	}
	
	public void SetBorderWidth(int width)
	{
		m_boardWidth = width;
	}
	
	private void DrawBoard(Canvas canvas, int imgid, Rect dstRect)
	{
		BitmapDrawable topDraw = (BitmapDrawable)getResources().getDrawable(imgid);
		Bitmap topBitmap = topDraw.getBitmap();
		Rect topSrcRect = new Rect();
		topSrcRect.left = topSrcRect.top = 0;
		topSrcRect.right = topBitmap.getWidth(); 
		topSrcRect.bottom = topBitmap.getHeight();
		
		canvas.drawBitmap(topBitmap, topSrcRect, dstRect, null);		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
//		if (m_boardWidth > 0)
//		{
//			Rect rect = canvas.getClipBounds();
//			rect.bottom--;
//			rect.right--;
//			
//			Paint paint = new Paint();
//			paint.setColor(m_boardColor);
//			paint.setStyle(Paint.Style.STROKE);
//			paint.setStrokeWidth(m_boardWidth);
//			
//			canvas.drawRect(rect, paint);
//		}
		
		if (m_boardWidth > 0)
		{
			Rect rect = canvas.getClipBounds();
			rect.bottom--;
			rect.right--;	
			
			Rect topDstRect = new Rect();
			topDstRect.left = topDstRect.right = 0;
			topDstRect.right = rect.right;
			topDstRect.bottom = m_boardWidth;
			DrawBoard(canvas, R.drawable.board_imgview_top, topDstRect);
			
			Rect bottomDstRect = new Rect();
			bottomDstRect.left = 0;
			bottomDstRect.right = rect.right;
			bottomDstRect.top = rect.bottom - m_boardWidth;
			bottomDstRect.bottom = rect.bottom;
			DrawBoard(canvas, R.drawable.board_imgview_bottom, bottomDstRect);
			
			Rect leftDstRect = new Rect();
			leftDstRect.left = 0;
			leftDstRect.top = m_boardWidth;
			leftDstRect.right = m_boardWidth;
			leftDstRect.bottom = rect.bottom - m_boardWidth;
			DrawBoard(canvas, R.drawable.board_imgview_left, leftDstRect);		
			
			Rect rightDstRect = new Rect();
			rightDstRect.left = rect.right - m_boardWidth;
			rightDstRect.top = m_boardWidth;
			rightDstRect.right = rect.right;
			rightDstRect.bottom = rect.bottom - m_boardWidth;
			DrawBoard(canvas, R.drawable.board_imgview_right, rightDstRect);					
		}
	}

}
