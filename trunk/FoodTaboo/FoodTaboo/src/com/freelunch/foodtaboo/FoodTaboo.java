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
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnHoverListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class FoodTaboo extends Activity {
	private AutoCompleteTextView m_autoCompleteTextView;
	private ListView m_listview;
	private Context context;
	private List<PriData> m_data;
	private List<String> m_szdata;
	private GridView m_gridview;
	private Toast m_toast;
	
	private void UpdateView()
	{
        List<Map<String, Object>> data = GridViewBindData.setData(m_autoCompleteTextView.getText().toString(), m_data);
        ListAdapter adapter = new GridAdapter(context, data);
        m_gridview.setAdapter(adapter);			
	}
	
	private void InitView()
	{
        List<Map<String, Object>> data = GridViewBindData.setData(m_szdata);
        ListAdapter adapter = new GridAdapter(context, data);
        m_gridview.setAdapter(adapter);		  
	}

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
				UpdateView();			        
			}
	    });
	    
	    m_autoCompleteTextView.addTextChangedListener(new TextWatcher()
	    {
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				Log.v("afterTextChanged", m_autoCompleteTextView.getText().toString());
				if (m_autoCompleteTextView.getText().toString().trim().length() == 0)
				{
					InitView();
				}
				else
				{
					UpdateView();		
				}
				
				m_autoCompleteTextView.setThreshold(1); 
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}
	    });
	    
	    //m_listview = (ListView)findViewById(R.id.curlistView);
	    m_gridview = (GridView)findViewById(R.id.my_grid);
        m_gridview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
				GridViewHolder holder = null;
				holder = (GridViewHolder) arg1.getTag();
				
				if (holder.degree.getBackground() == null)
				{
					// Select
					// Shutdown dropdown now.
					m_autoCompleteTextView.setThreshold(100);
					m_autoCompleteTextView.setText(holder.name.getText());	
				}
				else
				{
					// Toast
					//Toast toast = Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_LONG);
					if ((holder.photo.getTag() != null) && (holder.photo.getTag().toString().length() > 0))
					{
						LayoutInflater inflater = LayoutInflater.from(context);
						if (m_toast == null)
						{
							m_toast = new Toast(context);
						}
						View toastRoot = inflater.inflate(R.layout.toast_item, null);
						TextView tv = (TextView)toastRoot.findViewById(R.id.toast_text);
						tv.setText(holder.photo.getTag().toString());

						m_toast.setView(toastRoot);
					
						// Set toast pos
						Rect frame = new Rect();
						getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
						int statusBarHeight = frame.top;
						int contentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
						int titleBarHeight = contentTop - statusBarHeight;
						
						m_toast.setGravity(Gravity.TOP | Gravity.LEFT, 
								(arg2%3)*arg1.getWidth() + arg1.getWidth()/2, 
								titleBarHeight + m_autoCompleteTextView.getHeight() + (arg2/3)*arg1.getHeight() + arg1.getHeight()/2);
						m_toast.setDuration(Toast.LENGTH_LONG);
						m_toast.show();	
					}
				}
			}
        });
        
        InitView();
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
