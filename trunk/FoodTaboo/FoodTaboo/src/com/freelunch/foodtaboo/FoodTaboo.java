package com.freelunch.foodtaboo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.freelunch.foodtaboo.ComboBox.ListViewItemClickListener;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.widget.ListAdapter;
import android.widget.ListView;

public class FoodTaboo extends Activity {
	private ComboBox m_combobox;
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
        XmlOperation.ReadXML(filename, m_data);
        
        m_szdata = new ArrayList<String>();
        PriData2String(m_data, m_szdata);
        
	    m_combobox = (ComboBox)findViewById(R.id.id_comboBox);
	    m_combobox.setData(m_szdata);
	    
	    m_combobox.setListViewOnClickListener(new ListViewItemClickListener() {
			@Override
			public void onItemClick(int postion) {
		        List<Map<String, Object>> data = ListViewBindData.setData(m_combobox.getText(), m_data);
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
    
    private String GetXmlFile()
    {
    	String targetFile = null;
    	String filename = "/myxmltest.xml";
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
