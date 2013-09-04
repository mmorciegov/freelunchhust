package com.dreamori.daodejing;

import java.util.Random;

import android.graphics.Color;
import android.graphics.Rect;

public class Hotspot {
	
	private int [] colorTable = {Color.BLUE, Color.CYAN,  Color.GREEN,  Color.MAGENTA, Color.RED, Color.YELLOW };
	private Random colorRandom = new Random();
	
	static int imgIndex;
	
	//From DB
	static Rect imgOriginalRect = new Rect();	
	int hotspotIndex;
	Rect posRect = new Rect();
	String contentString;
	String titleString;
	int color;
	
	//From UI
	static Rect imgUiRect = new Rect();	
	Rect posUiRect = new Rect();
	
	static void SetOrignalImageSize( Rect originalImgSize )
	{
		imgOriginalRect.left = originalImgSize.left;
		imgOriginalRect.top = originalImgSize.top;
		imgOriginalRect.right = originalImgSize.right;
		imgOriginalRect.bottom = originalImgSize.bottom;
	}
	
	static void SetUiImageSize( Rect uiImgSize )
	{
		imgUiRect.left = uiImgSize.left;
		imgUiRect.top = uiImgSize.top;
		imgUiRect.right = uiImgSize.right;
		imgUiRect.bottom = uiImgSize.bottom;
	}
	
	void GetHotspotUiPosition()
	{
		posUiRect.left = imgUiRect.left + (int)(((double)posRect.left/(double)imgOriginalRect.width()) * imgUiRect.width());
		posUiRect.top = imgUiRect.top + (int)((double)((double)posRect.top/(double)imgOriginalRect.height()) * imgUiRect.height());
		
		posUiRect.right = imgUiRect.left + (int)((double)((double)posRect.right/(double)imgOriginalRect.width()) * imgUiRect.width());
		posUiRect.bottom = imgUiRect.top + (int)((double)((double)posRect.bottom/(double)imgOriginalRect.height()) * imgUiRect.height());	
		
		//Set Color here
		color = colorTable[colorRandom.nextInt(colorTable.length)];
	}
}

