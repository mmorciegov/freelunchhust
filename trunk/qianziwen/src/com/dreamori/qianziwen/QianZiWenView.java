package com.dreamori.qianziwen;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class QianZiWenView extends RelativeLayout {

	public final static int TEXTVIEW_COUNT = 32;
	
	private int mHeight = 754;
	private Context mContext = null;
	private TextView mTextTitle = null;
	private TextView [] mTxtViewGroup = new TextView[TEXTVIEW_COUNT];
	
	public QianZiWenView(Context context) {
		this(context, null);
	}
	
	public QianZiWenView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater.from(context).inflate(R.layout.qianziwen_view, this, true);  

		mTextTitle = (TextView)findViewById(R.id.textTitle);
		mContext = context;
		
		Typeface face = null;
        
		try {
			face = Typeface.createFromAsset(mContext.getAssets(), mContext.getString(R.string.font_file));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (face == null) {
			face = Typeface.DEFAULT;
		}
		mTextTitle.setTypeface(face);
		
		int index = 0;
		for( int i = 1; i <= 8; i++ )
		{
			for( int j = 1; j<=4; j++ )
			{
				mTxtViewGroup[index] =  (TextView)findViewById(ResourceManager.GetViewId(mContext, "text"+i+j));
				if(i%2 == 0)
				{
					mTxtViewGroup[index].setTypeface(face);
				}
				index++;
			}
		}
		
		
		
		for( int i = 1; i<= 4; i++)
		{
			TextView tv = (TextView)findViewById(ResourceManager.GetViewId(mContext, "pun"+i));
			tv.setTypeface(face);
		}
        
		ViewTreeObserver viewTree = getViewTreeObserver();
//		viewTree.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//			
//			@Override
//			public void onGlobalLayout() {
//				int curHeight = getHeight();
//				if(mHeight != curHeight)
//				{
//					if(mHeight == 0)
//						return;
//					
//					float ratio = (float) (1.0 * curHeight / mHeight);
//					
//					mTextTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextTitle.getTextSize() * ratio);
//					
//					TableLayout textTable = (TableLayout)findViewById(R.id.text_table);
//					LayoutParams tableLP = (LayoutParams)textTable.getLayoutParams();
//					tableLP.leftMargin *= ratio;
//					textTable.setLayoutParams(tableLP);
//					
//					int index = 0;
//					for( int i = 1; i <= 8; i++ )
//					{
//						TextView v = (TextView)findViewById(ResourceManager.GetViewId(mContext, "pun"+i));
//						if(v != null)
//						{
//							v.setTextSize(TypedValue.COMPLEX_UNIT_PX, v.getTextSize()* ratio);
//						}
//						
//						if(i%2 == 1)
//						{
//							TableRow row = (TableRow)findViewById(ResourceManager.GetViewId(mContext, "table_row"+i));
//							TableLayout.LayoutParams lp = (TableLayout.LayoutParams)row.getLayoutParams();
//							lp.topMargin *= ratio;
//							row.setLayoutParams(lp);
//						}
//						
//						for( int j = 1; j<=4; j++ )
//						{
//							mTxtViewGroup[index].setTextSize(TypedValue.COMPLEX_UNIT_PX, mTxtViewGroup[index].getTextSize()* ratio);
//							
//							index++;
//						}
//						
//					}
//					
//					mHeight = curHeight;
//				}
//				
//			}
//		});
	}
	
	public void updateContent(String[] textContent, String contentIndexString, int outputSpellCount)
	{

		for( int i = 0; i < outputSpellCount*2; i++ )
		{
			mTxtViewGroup[i].setText(textContent[i]);

			if((i + 1) % 8 == 0)
			{
				View v = findViewById(ResourceManager.GetViewId(mContext, "pun"+(i + 1)/8));
				if(v != null)
				{
					v.setVisibility(View.VISIBLE);
				}
			}
		}
		
		if( outputSpellCount*2 < TEXTVIEW_COUNT )
		{
			for( int j = outputSpellCount*2; j < TEXTVIEW_COUNT; j ++ )
			{
				mTxtViewGroup[j].setText("");

				if((j + 1) % 8 == 0)
				{
					View v = findViewById(ResourceManager.GetViewId(mContext, "pun"+(j + 1)/8));
					if(v != null)
					{
						v.setVisibility(View.INVISIBLE);
					}
				}
			}
		}
		
		mTextTitle.setText( String.format(this.getResources().getString(R.string.content_title), contentIndexString) );
	}

}
