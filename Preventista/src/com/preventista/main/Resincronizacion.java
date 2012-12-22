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
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.preventista.extras.ProgressInputStream;
import com.preventista.extras.WriteLogSql;


public class Resincronizacion extends Activity implements OnClickListener{
	
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
	private String nombre_copia_seguridad;
	
	Bundle bundle;
	TextView tvCopiaSeleccionada;
	Button btnResincronizar;
	
	Activity activity = Resincronizacion.this;
	
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
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.admin_resincronizacion);
		
		tvCopiaSeleccionada = (TextView) findViewById(R.id.tvCopiaSeleccionada);
		btnResincronizar = (Button) findViewById(R.id.btnResincronizar);
		
		bundle = getIntent().getExtras();
		tvCopiaSeleccionada.setText(bundle.getString("NOMBRE_ARCHIVO"));
		
		File mydir = this.getDir("backup", Context.MODE_PRIVATE);
		archivo_a_enviar = new File(mydir, bundle.getString("NOMBRE_ARCHIVO"));
		
		btnResincronizar.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btnResincronizar:
			checkConfirm();
			break;
		}
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
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	
	public void iniciarDatos()
	{
		if(checkConnection()){
			 WriteLogSql wsq = new WriteLogSql();
        	 if(wsq.existeDatosCopiaSeguridad(this,bundle.getString("NOMBRE_ARCHIVO")))
        	 {
        		 uploadFile(); 
 	        	 
        	 }else{
        		Toast.makeText(Resincronizacion.this, "No hay nada para sincronizar!", Toast.LENGTH_SHORT).show(); 
        	 }
		}
		else{ 
			Toast.makeText(Resincronizacion.this, "Sin conexión a Internet", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	public void uploadFile()
	{
		pDialog = new ProgressDialog(Resincronizacion.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setTitle("Re-Sincronizando...");
        pDialog.setIndeterminate(true);
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
			    	            //wsq.createFileInternalStorage(Resincronizacion.this); //Crear archivo de log sql en blanco
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
	            Toast.makeText(Resincronizacion.this, "Re-sincronización correcta!",Toast.LENGTH_SHORT).show();
	        }else{
	        	Toast.makeText(Resincronizacion.this, "Hubo un error al re-sincronizar. Intente de nuevo!",Toast.LENGTH_SHORT).show();
	        }
	       
	    }
	 
	    @Override
	    protected void onCancelled() 
	    {
	    	pDialog.dismiss();
	        Toast.makeText(Resincronizacion.this, "Re-sincronización cancelada!",
	        Toast.LENGTH_SHORT).show();
	        
	    }

		
	}
	
	  
	private boolean execScripPHP()
	{
		HttpClient httpClient = new DefaultHttpClient();
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
