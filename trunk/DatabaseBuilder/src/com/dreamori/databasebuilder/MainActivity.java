package com.dreamori.databasebuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import SQLite3.Constants;
import SQLite3.Database;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	static String srcDBName = "food.db";
	static String desDBName = "libFoodFuncion.so";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        File dbFile = getDatabasePath(desDBName);
		if (!dbFile.getParentFile().exists()) {
			dbFile.getParentFile().mkdirs();
		}
    	
    	String databaseFileName = getDatabasePath(desDBName).getAbsolutePath().toString();  
    	
        File targetFile = new File(databaseFileName);

		try {
			targetFile.createNewFile();
			copyBigDataBase(this,databaseFileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		try {
			Database database = new Database();
			database.open(dbFile.getPath(), Constants.SQLITE_OPEN_CREATE | Constants.SQLITE_OPEN_READWRITE);
			
			String uri = (String) database.getdb()+"SQLITE_OPEN_READWRITE";
			database.rekey(uri);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.i("DatabaseBuilder", e.getMessage());
		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
	

   static private void copyBigDataBase(Context context, String databaseFileNameString ) throws IOException {  
        InputStream myInput;  
        OutputStream myOutput = new FileOutputStream(databaseFileNameString);  

        myInput = context.getAssets().open(srcDBName);  
        
        byte[] buffer = new byte[1024];  
        int length;  
        while ((length = myInput.read(buffer)) > 0) {  
            myOutput.write(buffer, 0, length);  
        }  
        myOutput.flush();  
        myOutput.close();
        myInput.close();  
    }
}
