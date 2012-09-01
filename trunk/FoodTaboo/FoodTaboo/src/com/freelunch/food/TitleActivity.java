package com.freelunch.food;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class TitleActivity extends Activity {
	public static final int SETTING_DIALOG = 0x113;
	public static final String DATABASE_NAME = "food.db";
	public static final String CONFIG_FILENAME = "/config.xml";
	
	public Context m_context;
		
	public String GetConfigFileName()
	{
		return getApplicationContext().getFilesDir().getAbsoluteFile().toString() + CONFIG_FILENAME;
	}
	
	@Override
	public Dialog onCreateDialog(int id, Bundle state)
	{
		// �ж���Ҫ�����������͵ĶԻ���
		switch (id)
		{
			case SETTING_DIALOG:
				Builder b = new AlertDialog.Builder(this);
				// ���öԻ����ͼ��
				b.setIcon(R.drawable.ic_launcher);
				// ���öԻ���ı���
				b.setTitle("����");
				final boolean[] checkStatus = new boolean[2];
	        	
	        	if(ConfigData.GetSystemTip(GetConfigFileName()) == 0)
	        	{
	        		checkStatus[0] = false;
	        	}
	        	else
	        	{
	        		checkStatus[0] = true;
	        	}
	        	
	        	if(ConfigData.GetSystemDisplay(GetConfigFileName()) == 0)
	        	{
	        		checkStatus[1] = false;
	        	}
	        	else
	        	{
	        		checkStatus[1] = true;
	        	}
	        	
				// Ϊ�Ի������ö���б�
				b.setMultiChoiceItems(new String[] { "��ʼ��ʾ", "�б���ʾ" }
				// ����Ĭ�Ϲ�ѡ����Щ�б���
					, checkStatus
					// Ϊ�б���ĵ����¼����ü�����
					, new OnMultiChoiceClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which,
							boolean isChecked)
						{
				        	String filename = GetConfigFileName();
				        	ConfigData configData = new ConfigData();
				        	
				        	configData.tip = checkStatus[0] ? 1 : 0;
				        	configData.display = checkStatus[1] ? 1 : 0;
				        	XmlOperation.WriteXml(filename, configData);
						}
					});
				// ���һ����ȷ������ť�����ڹرոöԻ���
				b.setPositiveButton("�˳�", null);
				// �����Ի���
				return b.create();
		}
		return null;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
}
