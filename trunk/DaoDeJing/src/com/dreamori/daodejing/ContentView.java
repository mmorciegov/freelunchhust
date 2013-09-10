package com.dreamori.daodejing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.dreamori.daodejing.ParameterObject.TitleContent;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ContentView extends View   implements OnGestureListener, MediaPlayer.OnCompletionListener{
	
	private boolean m_bInit = false;
	private DatabaseHelper m_dbHelper = null;	
//	private ArrayList<Hotspot> hotspots = new ArrayList<Hotspot>();
	
	private int backgroundImageCount = 0;	
	private Random rand = new Random(5);
	
	Rect   m_srcRect = new Rect();
	Rect   m_orignalDstRect = new Rect();
	Rect   m_finalDst = new Rect();
	
	private float srcRatio = 0;
	private float dstRatio = 0;
	
	Bitmap m_bitmap = null;
	
	boolean m_needShowTouchTips = true;
	
	private GestureDetector detector;
	
    
    private static MediaPlayer mp = null;
    private boolean m_bPlaying = false;
    private int m_imageIndexInPlaying = DaoDeJing.m_currentImageIndex;
	private Button musicBtn = null;

	public ContentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		if( m_dbHelper == null )
		{
			m_dbHelper = DatabaseHelper.getInstance(context);
		}
		
		
		detector = new GestureDetector(this);
		
		backgroundImageCount = m_dbHelper.GetBackgroundImageCount();
	}
	

	public void InitRect()
	{
		m_needShowTouchTips = m_dbHelper.NeedShowTouchTips();
		
		m_orignalDstRect.left = m_orignalDstRect.top = 0;
		m_orignalDstRect.right = getWidth(); 
		m_orignalDstRect.bottom = getHeight();	
		
		dstRatio =  (float)m_orignalDstRect.width() / (float)m_orignalDstRect.height();
		
//		Hotspot.SetUiImageSize(m_orignalDstRect);  
		
		UpdateImageAndHotspot();		
	}
			
	public void UpdateImageAndHotspot()
	{
//		hotspots.clear();
		m_bitmap = BitmapFactory.decodeResource( getContext().getResources(),  ResourceManager.GetIcon(getContext(), m_dbHelper.GetImageContentName(DaoDeJing.m_currentImageIndex)));
		m_srcRect.left = m_srcRect.top = 0;
		m_srcRect.right = m_bitmap.getWidth(); 
		m_srcRect.bottom = m_bitmap.getHeight();
		srcRatio =  (float)m_srcRect.width() / (float)m_srcRect.height();
								
		m_finalDst = new Rect(m_orignalDstRect);
		
		//Recalc destRect.
		if( ( srcRatio - dstRatio ) > 0 )
		{
			float newHeight = ((float)m_orignalDstRect.width() / srcRatio);
			m_finalDst.top = (int)( (m_orignalDstRect.height() - newHeight)/2 );
			m_finalDst.bottom = m_finalDst.top + (int) newHeight;
		}
		else
		{
			float newWidth = ((float)m_orignalDstRect.height() * srcRatio);
			m_finalDst.left = (int)( (m_orignalDstRect.width() - newWidth)/2 );
			m_finalDst.right = m_finalDst.left + (int)newWidth;
		}
		
//		Hotspot.SetUiImageSize(m_finalDst);  
//		
//		m_dbHelper.GetImageHotSpot( DaoDeJing.m_currentImageIndex, hotspots);
//		for ( int i = 0; i < hotspots.size(); i++) {
//			hotspots.get(i).GetHotspotUiPosition();
//			
//			hotspots.get(i).SetRandomColor();
//			if( i>0 )
//			{
//				if( hotspots.get(i).hotspotIndex == hotspots.get(i-1).hotspotIndex)
//				{
//					hotspots.get(i).SetColor(hotspots.get(i-1).color);
//				}
//			}		
//		}			
	}
	
	public void ShowNextImage() 
	{
		InitRect();
		DaoDeJing.m_currentImageIndex++;
		if( DaoDeJing.m_currentImageIndex > Const.m_maxImageIndex )
		{
			DaoDeJing.m_currentImageIndex = Const.m_minImageIndex;
		}

		UpdateImageAndStopPlaying();		
	}
		
	public void ShowPreviosImage()
	{	
		InitRect();
		DaoDeJing.m_currentImageIndex--;
		if( DaoDeJing.m_currentImageIndex < Const.m_minImageIndex )
		{
			DaoDeJing.m_currentImageIndex = Const.m_maxImageIndex;
		}
		
		UpdateImageAndStopPlaying();
	}
	
	private void UpdateImageAndStopPlaying()
	{
		UpdateImageAndHotspot();
		
		((View) this.getParent()).setBackgroundResource(ResourceManager.GetBackgroundIcon(getContext(), m_dbHelper.GetBackgroundImageContentName(rand.nextInt(backgroundImageCount))));
		
		StopMp3();
	}
	
	public void ShowWholeExplanation()
	{
		TitleContent titleContent = new TitleContent();
		if( m_dbHelper.GetExplanationContent(DaoDeJing.m_currentImageIndex, titleContent))
		{
			ShowExplanationDialog(titleContent.title, titleContent.content );		
		}
	}

	public void StopMp3( )
	{
		if( mp != null )
		{
			m_bPlaying = false;
			mp.stop();	
			mp.release();
			mp = null;
		}
		
		if( musicBtn != null )
		{
			musicBtn.setBackgroundResource(R.drawable.music);
		}		
	}
	
	public void PlayMp3( View v)
	{
		if( m_bPlaying && m_imageIndexInPlaying == DaoDeJing.m_currentImageIndex )
		{
			StopMp3();
			return;
		}
		
		if( mp != null )
		{
			mp.release();
		}
		
		m_imageIndexInPlaying = DaoDeJing.m_currentImageIndex;
		m_bPlaying = true;
		mp = MediaPlayer.create(getContext(), ResourceManager.GetMusicId(getContext(), m_dbHelper.GetMusicName(m_imageIndexInPlaying)));
		if( mp!= null )
		{
			mp.setOnCompletionListener(this);
			mp.start();
			
			musicBtn = (Button) v;
			musicBtn.setBackgroundResource(R.drawable.music2);
		}
	}
//	
//	public int IsHit(float x, float y)
//	{			
//		for (int i=0; i< hotspots.size(); i++)
//		{
//			if( hotspots.get(i).posUiRect.contains((int)x, (int)y))
//			{
//				return i;
//			}
//		}
//		return -1;
//	}
	
	//private boolean cancleToast = false;
	
	public void ShowExplanationDialog( String title, String contentString )
	{
		AlertDialog.Builder builder = new Builder(getContext());
		
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View rootView = inflater.inflate(R.layout.explain_dialog, null);
		rootView.setBackgroundResource(ResourceManager.GetBackgroundIcon(getContext(), m_dbHelper.GetBackgroundImageContentName(rand.nextInt(backgroundImageCount))));
		
		//TextView orgTV = (TextView)rootView.findViewById(R.id.original_text);
		//orgTV.setText(title);
		TextView expTV = (TextView)rootView.findViewById(R.id.explain_text);
		expTV.setText(contentString);
		
	    builder.setView(rootView);
	    builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}});

		builder.create().show();
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if( this.detector.onTouchEvent(event) )
		{
			return true;
		}
//		
//		float x = event.getX();
//		float y = event.getY();
//		switch (event.getAction()) {
//		
//		case MotionEvent.ACTION_UP:
//			 {
//				int index = IsHit(x, y);
//				if (index != -1) {
//					
//					//ShowExplanationDialog(hotspots.get(index).titleString, hotspots.get(index).contentString );	
//					ShowExplanationDialog(null, hotspots.get(index).contentString );								
//				}
//			}
//			break;
//		default:
//			break;
//		}
		
		return true;
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		if (!m_bInit)
		{			
			InitRect();
			m_bInit = true;
		}
		canvas.drawBitmap(m_bitmap, m_srcRect, m_finalDst, null);
//		
//		Paint paint = new Paint();
//		paint.setAntiAlias(true);
//		paint.setColor(Color.GREEN);
//
//		paint.setStrokeWidth(3);
//		paint.setStyle(Paint.Style.STROKE);
//
//		if (m_needShowTouchTips) {
//			for (int i = 0; i < hotspots.size(); i++) {			
//				paint.setColor(hotspots.get(i).color);
//				RectF rectf = new RectF(hotspots.get(i).posUiRect);				
//				//DreamoriLog.LogDaoDeJing("Draw hotspots." +  "left: "+  rectf.left + "right: " + rectf.bottom + 4 + "" +  rectf.right + "" + rectf.bottom + 4);				
//				canvas.drawLine(rectf.left-2, rectf.bottom + 4,  rectf.right - 2, rectf.bottom + 4,  paint);
//			}
//		}		
//		
		invalidate();
	}

	 
    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		if( e1.getX() - e2.getX() > 120 )
		{
			ShowNextImage();
		}
		else if( e1.getX() - e2.getX() < -120 ) {
			ShowPreviosImage();
		}
		
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		StopMp3();
	}
}

