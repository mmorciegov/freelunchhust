package com.freelunch.foodtaboo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class FoodTaboo extends Activity {
	private AutoCompleteTextView m_autoCompleteTextView;
	private ListView m_listview;
	private Context context;
	private List<PriData> m_data;
	private List<String> m_szdata;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.foodtaboo_main);
        
        context = this;
        
        m_data = new ArrayList<PriData>();
        String filename = GetXmlFile();  
        File targetFile = new File(filename);
    	if (targetFile == null || !targetFile.exists())        
    	{
			try {
				copyBigDataBase();
				Log.v("Debug", "copyBigDataBase");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
        
        XmlOperation.ReadXML(filename, m_data);

        
        m_szdata = new ArrayList<String>();
        PriData2String(m_data, m_szdata);
        
        m_autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);   
	    ArrayAdapter<String> textViewAdapter = new ArrayAdapter<String>(this,   
	            android.R.layout.simple_dropdown_item_1line, m_szdata);   
	    m_autoCompleteTextView.setAdapter(textViewAdapter);  	    
	    m_autoCompleteTextView.setThreshold(1); 
	    
	    m_autoCompleteTextView.setOnItemClickListener(new OnItemClickListener()
	    {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Log.v("Click", m_autoCompleteTextView.getText().toString());
		        List<Map<String, Object>> data = ListViewBindData.setData(m_autoCompleteTextView.getText().toString(), m_data);
		        ListAdapter adapter = new ListViewAdapter(context, data);
		        m_listview.setAdapter(adapter);					
			}
	    });
	    
	    m_listview = (ListView)findViewById(R.id.curlistView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.foodtaboo_main, menu);
        return true;
    }
    
    private void copyBigDataBase() throws IOException {  
        InputStream myInput;  
        String outFileName = GetXmlFile();
        OutputStream myOutput = new FileOutputStream(outFileName);  

        myInput = getAssets().open("data.xml");  
        byte[] buffer = new byte[1024];  
        int length;  
        while ((length = myInput.read(buffer)) > 0) {  
            myOutput.write(buffer, 0, length);  
        }  
        myOutput.flush();  
        myOutput.close();
        myInput.close();  
    }  	    
    
    private String GetXmlFile()
    {
    	String targetFile = null;
    	String filename = "/data.xml";
//    	try
//		{
//			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
//			{
//				File sdCardDir = Environment.getExternalStorageDirectory();
//				targetFile = sdCardDir.getCanonicalPath()+filename;
//			}
//		}
//		catch(IOException e)
//		{
//			e.printStackTrace();					
//		}
    	
    	targetFile = getApplicationContext().getFilesDir().getAbsoluteFile().toString() + filename;
    	
    	
    	
    	return targetFile;
    }  
    
    private boolean IsExistInList(String str, List<String> strList)
    {
    	for(int i=0; i<strList.size(); i++)
    	{
    		if (str.equalsIgnoreCase(strList.get(i)))
    		{
    			return true;
    		}
    	}
    	return false;
    }
    
    private void PriData2String(List<PriData> dataList, List<String> strList)
    {
    	for (int i=0; i<dataList.size(); i++)
    	{
    		if (!IsExistInList(dataList.get(i).srcName, strList))
    		{
    			strList.add(dataList.get(i).srcName);
    		}
    		if (!IsExistInList(dataList.get(i).dstName, strList))
    		{
    			strList.add(dataList.get(i).dstName);
    		}		
    	}
    }
}
