package com.preventista.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.preventista.db.UpdateDataBaseHelper;
import com.preventista.extras.WriteLogSql;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ConfigDB extends Activity implements OnClickListener{
	
	private Button btnUpdateDB;
	public static final String LOG_TAG = "DB DOWNLOAD FROM CLOUD";
    
    //initialize our progress dialog/bar
    private ProgressDialog bar;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    
    //initialize root directory
    File rootDir = Environment.getExternalStorageDirectory();
       
    //defining file name and url
    public String fileName = "preventista";  
    
    //public String fileURL = "http://192.168.2.108/repositorios/preventista/execlog/downloadDB";
    public String fileURL = "http://184.75.251.75/sys/execlog/downloadDB";
    
    
    
    Activity activity = ConfigDB.this;   
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_configdb);
		
		btnUpdateDB = (Button) findViewById(R.id.btnUpdateDB);
		btnUpdateDB.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btnUpdateDB:
			checkConfirm();
			break;
		}
	}
	
	
	private void checkConfirm()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("¿Estás seguro de realizar esta acción?")
		       .setCancelable(false)
		       .setPositiveButton("Si", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	 //check conection to internet and executing the asynctask
		               if(checkConnection()){
		               		new DownloadFileAsync().execute(fileURL);
		               }else{ 
		       				Toast.makeText(ConfigDB.this, "Sin conexión a Internet", Toast.LENGTH_SHORT).show();
		       		   }
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	
	
	  //this is our download file asynctask
    class DownloadFileAsync extends AsyncTask<String, String, String> {
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showDialog(DIALOG_DOWNLOAD_PROGRESS);
            bar = ProgressDialog.show(ConfigDB.this, "Actualizando BD ", "Espere por favor...",true);
        }

        
        @Override
        protected String doInBackground(String... aurl) {

            try {
                //connecting to url
                URL u = new URL(fileURL);
                HttpURLConnection c = (HttpURLConnection) u.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();
                
                //lenghtOfFile is used for calculating download progress
                int lenghtOfFile = c.getContentLength();
               
                //this is where the file will be seen after the download
                
                File file = new File(rootDir + "/download/");
                
                if (!file.exists()) {  
                    file.mkdirs();
                }
                
                File outputFile = new File(file, fileName); 
                FileOutputStream fos = new FileOutputStream(outputFile);
               
                //file input is from the url

                InputStream is = c.getInputStream(); 

                byte[] buffer = new byte[1024];
                int len1 = 0;
                long total = 0;
                while ((len1 = is.read(buffer)) != -1) {
                    total += len1;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    fos.write(buffer, 0, len1); // Write In FileOutputStream.
                }
                fos.flush();
                fos.close();
                is.close();
               
                
               if(copyDB()){
            	   WriteLogSql wsq = new WriteLogSql();
            	   wsq.createFileInternalStorage(ConfigDB.this); //Crear archivo de log sql en blanco
            	   activity.runOnUiThread(new Runnable() {
   	    	        	public void run() {
   	    	        		Toast.makeText(activity, "Actualización correcta!", Toast.LENGTH_SHORT).show();
   	    	        	}
   	    	    	});
                }else{
                	activity.runOnUiThread(new Runnable() {
   	    	        	public void run() {
   	    	        		Toast.makeText(activity, "Hubo un problema al actualizar la Base de Datos local. Intente de nuevo!", Toast.LENGTH_SHORT).show();
   	    	        	}
   	    	    	}); 	      
                }
                
                /*bar.dismiss();
	    	    activity.runOnUiThread(new Runnable() {
	    	        public void run() {
	    	            Toast.makeText(activity, "Actualización correcta!", Toast.LENGTH_SHORT).show();
	    	            //finish();
	    	        }
	    	    });*/
	    	    
                
            } catch (Exception e) {
                Log.d(LOG_TAG, e.getMessage());
            }
            
            return null;
        }
        
        protected void onProgressUpdate(String... progress) {
             Log.d(LOG_TAG,progress[0]);
        }

        @Override
        protected void onPostExecute(String unused) {
        	bar.dismiss();
        	//Toast.makeText(activity, "Actualización correcta!", Toast.LENGTH_SHORT).show();
            //finish();
        }
    }
    
    
    private boolean copyDB()
    {
    	try{
	  		  if(WriteLogSql.isExternalStorageAvailable())
	  		  {
	  			File file = new File(rootDir + "/download/", fileName);
	  			UpdateDataBaseHelper dbhelper = new UpdateDataBaseHelper(this);
	  			dbhelper.createDataBase(file);
	  			return true;
	  		  }
    	 } catch(Exception e){
    		 throw new Error("Medio de almacenamiento no disponible");
  		 }
    	return false;
    } 
    
    
    public boolean checkConnection() 
	{
		Context context = getApplicationContext();
		ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectMgr != null) {
			NetworkInfo[] netInfo = connectMgr.getAllNetworkInfo();
			if (netInfo != null) {
				for (NetworkInfo net : netInfo) {
					if (net.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} 
		else {
			Log.d("NETWORK", "No network available");
		}
		return false;
	}
}
