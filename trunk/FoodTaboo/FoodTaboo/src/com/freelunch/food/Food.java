package com.freelunch.food;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListAdapter;

public class Food extends Activity {
	private final int SETTING_DIALOG = 0x113;
	
	private Context m_context;
	private GridView m_gridview;
	
	public static final String DATABASE_NAME = "food.db";
	
	private void InitGrid()
	{
        List<GridViewHolderData> dataList = new ArrayList<GridViewHolderData>();
        
        GridViewHolderData inq_data = new GridViewHolderData();
        inq_data.icon = R.drawable.food;
        inq_data.name = "查询";
        inq_data.degree = 0;
        dataList.add(inq_data);
        
        GridViewHolderData disease_data = new GridViewHolderData();
        disease_data.icon = R.drawable.food;
        disease_data.name = "食疗";
        disease_data.degree = 0;       
        dataList.add(disease_data);
        
        ListAdapter adapter = new GridViewAdapter(m_context, dataList);
        m_gridview.setAdapter(adapter);	
	}
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_main);
        
        m_context = this;

        
        m_gridview = (GridView)findViewById(R.id.ui_main_grid);
        InitGrid();
        m_gridview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				switch (arg2)
				{
				case 0:
					Intent intent = new Intent(Food.this, InquireSearchPage.class);
					startActivity(intent);
					break;
					
				case 1:
				{
			    	String filename = getApplicationContext().getFilesDir().getAbsoluteFile().toString() + "/config.xml";
			    	ConfigData configData = new ConfigData();
			    	XmlOperation.ReadXML(filename, configData);	
			    	
					Intent intent2 = null;
					
					if (configData.display == 0)
					{
						intent2 = new Intent(Food.this, DiseaseSearchPage.class);
					}
					else
					{
						intent2 = new Intent(Food.this, DiseaseSearchListPage.class);
					}
					startActivity(intent2);
				}
					break;
				
				default:
					break;
				}
			}
        });
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem mi)
    {
    	switch(mi.getItemId())
    	{
		default:
			showDialog(SETTING_DIALOG);
			break;
    	}
    	return true;
    }
    
	@Override
	public Dialog onCreateDialog(int id, Bundle state)
	{
		// 判断需要生成哪种类型的对话框
		switch (id)
		{
			case SETTING_DIALOG:
				Builder b = new AlertDialog.Builder(this);
				// 设置对话框的图标
				b.setIcon(R.drawable.ic_launcher);
				// 设置对话框的标题
				b.setTitle("配置");
				final boolean[] checkStatus = new boolean[2];
				
	        	String filename = getApplicationContext().getFilesDir().getAbsoluteFile().toString() + "/config.xml";
	        	ConfigData configData = new ConfigData();
	        	XmlOperation.ReadXML(filename, configData);	
	        	
	        	if(configData.tip == 0)
	        	{
	        		checkStatus[0] = false;
	        	}
	        	else
	        	{
	        		checkStatus[0] = true;
	        	}
	        	
	        	if(configData.display == 0)
	        	{
	        		checkStatus[1] = false;
	        	}
	        	else
	        	{
	        		checkStatus[1] = true;
	        	}
	        	
				// 为对话框设置多个列表
				b.setMultiChoiceItems(new String[] { "初始提示", "列表显示" }
				// 设置默认勾选了哪些列表项
					, checkStatus
					// 为列表项的单击事件设置监听器
					, new OnMultiChoiceClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which,
							boolean isChecked)
						{
				        	String filename = getApplicationContext().getFilesDir().getAbsoluteFile().toString() + "/config.xml";
				        	ConfigData configData = new ConfigData();
				        	
				        	configData.tip = checkStatus[0] ? 1 : 0;
				        	configData.display = checkStatus[1] ? 1 : 0;
				        	XmlOperation.WriteXml(filename, configData);
						}
					});
				// 添加一个“确定”按钮，用于关闭该对话框
				b.setPositiveButton("退出", null);
				// 创建对话框
				return b.create();
		}
		return null;
	}    
}
