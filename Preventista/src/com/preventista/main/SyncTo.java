package com.preventista.main;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.preventista.extras.ProgressInputStream;
import com.preventista.extras.WriteLogSql;
import com.preventista.main.ConfigDB.DownloadFileAsync;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class SyncTo extends Activity{
	
	//produccion
	private static String SERVER = "184.75.251.75";
	private static String USERNAME = "marcos";
	private static String PASSWORD = "marcospreventista28009";
	private static String PATH = "sys/logsql/";
	
	//desarrollo
	/*private static String SERVER = "192.168.2.108";
	private static String USERNAME = "johnny";
	private static String PASSWORD = "pedro40114481";
	private static String PATH = "../../var/www/repositorios/preventista/logsql/";*/
	
	private File archivo_a_enviar;
	
	Activity activity = SyncTo.this;
	
	ProgressDialog pDialog; 
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			//bar.incrementProgressBy((int) msg.getData().getLong("progress_update"));
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState); 
		
		archivo_a_enviar = new File( this.getFilesDir() + "/logsql.txt");
		checkConfirm();
	}
	
	
	public void checkConfirm()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("¿Estás seguro de realizar esta acción?")
		       .setCancelable(false)
		       .setPositiveButton("Si", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   iniciarDatos();
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
	
	
	public void iniciarDatos()
	{
		if(checkConnection()){
			 WriteLogSql wsq = new WriteLogSql();
	         if(wsq.checkExistFile(this))
	         {
	        	 if(wsq.existeDatos(this))
	        	 {
	        		 if(wsq.backupFileLogSql(this)){
	 	        		uploadFile(); 
	 	        	 }else{
	 	        		Toast.makeText(SyncTo.this, "Hubo un error al crear copia de seguridad de los datos!", Toast.LENGTH_SHORT).show(); 
	 					finish();
	 	        	 }
	        	 }else{
	        		Toast.makeText(SyncTo.this, "No hay nada para sincronizar!", Toast.LENGTH_SHORT).show(); 
	 				finish();
	        	 }
			 }else{
				 Toast.makeText(SyncTo.this, "Error al cargar archivo de datos!", Toast.LENGTH_SHORT).show(); 
				 finish();
			 }
		}
		else{ 
			Toast.makeText(SyncTo.this, "Sin conexión a Internet", Toast.LENGTH_SHORT).show();
			finish();
		}
	}
	
	
	public void uploadFile()
	{
		pDialog = new ProgressDialog(SyncTo.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setTitle("Sincronizando...");
        pDialog.setMessage("Espere por favor...");
        pDialog.setCancelable(true);
        
        //ejecutar tarea de subir archivo de log sql
        SubirArchivo subirArchivo = new SubirArchivo();
        subirArchivo.execute();
	}
	
	
	private class SubirArchivo extends AsyncTask<Void,String,Boolean> {
		
	    @Override
	    protected Boolean doInBackground(Void... params) 
	    {
	    	try{
				
				FTPClient ftpClient = new FTPClient();
				 
				try {
				    ftpClient.connect(InetAddress.getByName(SERVER),21);
				    
				    //if (ftpClient.getReplyString().contains("250")) {
				     if(ftpClient.login(USERNAME, PASSWORD))
				     {
				        ftpClient.changeWorkingDirectory(PATH);
				        ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
				        BufferedInputStream buffIn = null;
				        buffIn = new BufferedInputStream(new FileInputStream(archivo_a_enviar));
				        ftpClient.enterLocalPassiveMode();
				        ProgressInputStream progressInput = new ProgressInputStream(buffIn, handler);
				 
				        boolean result = ftpClient.storeFile("log_sql.txt", progressInput);
				        if (result){
				        	Log.v("upload result", "succeeded");
				        	publishProgress("Ejecutando Actualización");
				        	if(execScripPHP() == true ){
				        		WriteLogSql wsq = new WriteLogSql();
			    	            wsq.createFileInternalStorage(SyncTo.this); //Crear archivo de log sql en blanco
				        	}
				        }else{
				        	Log.e("upload result", "se ha producido un error al subir el archivo de log sql");
				        	return false;
				        }	  
				        
				        buffIn.close();
				        Log.d("status",ftpClient.getStatus()); 
						ftpClient.logout(); 
						ftpClient.disconnect();  
				     }
					        
				} catch (SocketException e) {
				    Log.e("UPLOAD_FTP", e.getStackTrace().toString());
				    return false;
				    
				} catch (UnknownHostException e) {
				    Log.e("UPLOAD_FTP", e.getStackTrace().toString());
				   return false;
				    
				} catch (IOException e) {
				    Log.e("UPLOAD_FTP", e.getStackTrace().toString());
				    return false;
				}
	
			}catch(Throwable t){
				return false;
			}
	        
	 
	        return true;
	    }
	 
	    @Override
	    protected void onProgressUpdate(String... values) 
	    {
	        String mensaje = values[0];
	        pDialog.setMessage(mensaje);
	    }
	 
	    @Override
	    protected void onPreExecute() 
	    {
	    	//cancelar la tarea al presionar boton atras
	    	pDialog.setOnCancelListener(new OnCancelListener() {
	            @Override
	            public void onCancel(DialogInterface dialog) {
	            	SubirArchivo.this.cancel(true);
	            }
	    	});
	    	
	    	pDialog.show();
	    }
	 
	    @Override
	    protected void onPostExecute(Boolean result) 
	    {
	    	pDialog.dismiss();
	    	if(result){
	            Toast.makeText(SyncTo.this, "Sincronización correcta!",Toast.LENGTH_SHORT).show();
	        }else{
	        	Toast.makeText(SyncTo.this, "Hubo un error al sincronizar. Intente de nuevo!",Toast.LENGTH_SHORT).show();
	        }
	        finish();
	    }
	 
	    @Override
	    protected void onCancelled() 
	    {
	    	pDialog.dismiss();
	        Toast.makeText(SyncTo.this, "Sincronización cancelada!",
	        Toast.LENGTH_SHORT).show();
	        finish();
	    }

		
	}
	
	  
	private boolean execScripPHP()
	{
		HttpClient httpClient = new DefaultHttpClient();
		//HttpGet del = new HttpGet("http://" + SERVER + "/repositorios/preventista/execlog");
	    HttpGet del = new HttpGet("http://" + SERVER + "/sys/execlog");
		
		del.setHeader("content-type", "application/json");

		try
		{
	        HttpResponse resp = httpClient.execute(del);
	        final String respStr = EntityUtils.toString(resp.getEntity());
	 
	       if(respStr.equals("true")){
	    	   return true;
	       }          
	       else{ 
	    	   Log.d("SCRIPTPHP", "Ejecución de script php: "+ respStr);   	   
	       }
		}
		catch(Exception ex)     
		{
		        Log.e("ServicioRest","Error!", ex);
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
