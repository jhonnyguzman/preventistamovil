package com.preventista.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.preventista.db.DBLogCloudAdapter;
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
import android.widget.Toast;

public class SyncFrom extends Activity{

	public static final String LOG_TAG = "LOG DOWNLOAD FROM CLOUD";
    
    //initialize our progress dialog/bar
    private ProgressDialog bar;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    
    //initialize root directory
    File rootDir = Environment.getExternalStorageDirectory();
       
    //defining file name and url
    public String fileName = "log_sql_cloud.txt";   
   
    //public String fileURL = "http://192.168.2.108/repositorios/preventista/logsql/log_sql_cloud.txt";
    //public String fileURL = "http://preventista.servehttp.com/repositorios/preventista/logsql/log_sql_cloud.txt";
    public String fileURL = "http://184.75.251.75/sys/logsql/log_sql_cloud.txt";
    
    Activity activity = SyncFrom.this;   
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState); 
    
        //making sure the download directory exists
       // checkAndCreateDirectory("/my_downloads");
       
        checkConfirm();
        
    }
    
    
    public void checkConfirm()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("¿Estás seguro de realizar esta acción?")
		       .setCancelable(false)
		       .setPositiveButton("Si", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) 
		           {
		        	   //check conection to internet and executing the asynctask
		               if(checkConnection()){
		               		new DownloadFileAsync().execute(fileURL);
		               }else{ 
		            	   Toast.makeText(SyncFrom.this, "Sin conexión a Internet", Toast.LENGTH_SHORT).show();
		       			   finish();
		       		   }
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		                finish();
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
            bar = ProgressDialog.show(SyncFrom.this, "Sincronizando ", "Espere por favor...",true);
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
                FileOutputStream f = new FileOutputStream(new File(rootDir, fileName));
                //file input is from the url
                InputStream in = c.getInputStream();

                //here's the download code
                byte[] buffer = new byte[1024];
                int len1 = 0;
                long total = 0;
                
                while ((len1 = in.read(buffer)) > 0) {
                    total += len1; //total = total + len1
                    publishProgress("" + (int)((total*100)/lenghtOfFile));
                    f.write(buffer, 0, len1);
                }
                f.close();
                
                if(execSqlCloud()){
                	execScripPHP();
                }else{
                	bar.dismiss();
    	    	    activity.runOnUiThread(new Runnable() {
    	    	        public void run() {
    	    	            Toast.makeText(activity, "Hubo un problema al sincronizar los datos!", Toast.LENGTH_SHORT).show();
    	    	            finish();
    	    	        }
    	    	    });
                }
                
            } catch (Exception e) {
                Log.d(LOG_TAG, e.getMessage());
            }
            
            return null;
        }
        
        protected void onProgressUpdate(String... progress) {
             Log.d(LOG_TAG,progress[0]);
             //mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {
            //dismiss the dialog after the file was downloaded
            //dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
        	bar.dismiss();
            finish();
        }
    }
    
    //function to verify if directory exists
    public void checkAndCreateDirectory(String dirName){
        File new_dir = new File( rootDir + dirName );
        if( !new_dir.exists() ){
            new_dir.mkdirs();
        }
    }
    
    
    public boolean execSqlCloud()
    {

    	try{
	  		  if(WriteLogSql.isExternalStorageAvailable())
	  		  {
	  			  DBLogCloudAdapter dblog = new DBLogCloudAdapter(SyncFrom.this);
	  			  File file = new File(rootDir, fileName);
	  			  BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)),8192);
	  			  String query = br.readLine();
	  			  
	  			  while(query != null){
	  				Log.d("LINE_FILE_CLOUD", query);
	  				dblog.execQuery(query);
	  				query = br.readLine(); 
	  			  }
	  			  br.close(); 
	  			  
	  			  //eliminar archivo de log bajado desde la nube
	  			  WriteLogSql slq = new WriteLogSql();
	  			  slq.deleteFileCloud(fileName);
	  			  return true;
	  		  } else {
	  			  
	  		  }   
  		 } catch(Exception e){
  		 
  		 }
    	
    	return false;
    	
    }
    
    
    private void execScripPHP()
	{
		HttpClient httpClient = new DefaultHttpClient();
		//HttpGet del = new HttpGet("http://192.168.2.108/repositorios/preventista/execlog/deleteLinesFronFileCloud");
		HttpGet del = new HttpGet("http://184.75.251.75/sys/execlog/deleteLinesFronFileCloud");
		//HttpGet del = new HttpGet("http://preventista.servehttp.com/repositorios/preventista/execlog/deleteLinesFronFileCloud");
		del.setHeader("content-type", "application/json");

		try
		{
	        HttpResponse resp = httpClient.execute(del);
	        final String respStr = EntityUtils.toString(resp.getEntity());
	 
	       if(respStr.equals("true")){
	    	    bar.dismiss();
	    	    activity.runOnUiThread(new Runnable() {
	    	        public void run() {
	    	            Toast.makeText(activity, "Sincronización correcta!", Toast.LENGTH_SHORT).show();
	    	            finish();
	    	        }
	    	    });
	       }          
	       else{ 
	    	   bar.dismiss();
	    	   Log.d("SCRIPTPHP", "Ejecución de script php en la nube: "+ respStr);   	   
	       }
		}
		catch(Exception ex)     
		{
		        Log.e("ServicioRest","Error!", ex);
		}
		
		//finish();
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
