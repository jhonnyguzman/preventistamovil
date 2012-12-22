package com.preventista.extras;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.io.FileOutputStream;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class WriteLogSql {
	
	Context contexto;
	
	private String file_name = "logsql.txt";
	
	/*public WriteLogSql(Context context){
			this.contexto = context;
	}*/
	
	//verificar que se pueda leer desde la memoria externa
	public static boolean isExternalStorageReadOnly() 
	{  
		 String extStorageState = Environment.getExternalStorageState();  
		     if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {  
		         return true;  
		     }  
		     return false;  
	}  
	
	//verificar si esta disponible la memoria externa del dispositivo
	public static boolean isExternalStorageAvailable() 
	{  
	     String extStorageState = Environment.getExternalStorageState();  
	     if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {  
	         return true;  
	     }  
	     return false;  
	} 
	
	//escribir el archivo de log sql en memoria externa en el directorio /
	/*public void writeFile(String filename, String textfile)
	{
		try {
		  if (isExternalStorageAvailable() && !isExternalStorageReadOnly()) { 
			  if(filename != null) this.file_name = filename;
			  File file = new File(Environment.getExternalStorageDirectory(), this.file_name );
			  OutputStreamWriter outw = new OutputStreamWriter(new FileOutputStream(file, true));
			  outw.write(textfile);
			  outw.close();
		  }else{
			  Log.e("ERROR_LOG", "Nose pudo escribir en archivo log");
		  }
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}*/
	
	
	/**
	 * Esta función permite crear en el directorio de datos de la aplicación
	 * que se encuentra dentro del almacenamiento interno del dispositivo
	 * un arhivo llamado logsql.txt vacio que es donde se va a guardar 
	 * todas las sentencias sql que se vayan generando en el sistema. Si el 
	 * archivo existe borra su contenido.
	 * */
	public void createFileInternalStorage(Context context)
	{
		this.contexto = context;
		
		try {
			FileOutputStream fos = contexto.openFileOutput(this.file_name, Context.MODE_PRIVATE);
			fos.close();
		} catch (Exception e) {
			Log.e("ERROR_LOG", e.toString());
			e.printStackTrace();
		} 
	}
	
	/**
	 * Esta funcion permite escribir datos de sentencias sql en un
	 * archivo de log que se encuentra almacenado en el almacenamiento
	 * interno del dispositivo.
	 * */
	public void writeFileInternalStorage(Context context, String textfile)
	{
		this.contexto = context;
		
		try {
			FileOutputStream fos = contexto.openFileOutput(this.file_name, Context.MODE_APPEND);
			fos.write(textfile.getBytes());
			fos.close();
		
		} catch (Exception e) {
			Log.e("ERROR_LOG", "Nose pudo escribir en archivo log sql");
			e.printStackTrace();
		}  
	}
	
	
	/**
	 * Esta función permite generar una copia de segurad del archivo logsql.txt.
	 * Dicha copia de seguridad es guardada dentro de un directorio llamado 'backup'
	 * que se encuentra en el almacenamiento interno del dispositivo dentro del directorio
	 * de datos de la aplicación.
	 * */
	public boolean backupFileLogSql(Context context)
	{
		this.contexto = context;
		String nombre = obtenerNombreBackupLogSql();
		
		 try {
			File mydir = context.getDir("backup", Context.MODE_PRIVATE); //Crea un directorio interno o accede a el si ya existe;
	        File f1 = new File(this.contexto.getFilesDir() + "/"+ this.file_name);
	        File f2 = new File(mydir, nombre);
	        
	        if(f1.exists()){
		        InputStream in = new FileInputStream(f1);
		        OutputStream out = new FileOutputStream(f2);
	
		        byte[] buf = new byte[1024];
		        int len;
		        while ((len = in.read(buf)) > 0){
		            out.write(buf, 0, len);
		        }
		        in.close();
		        out.close();
		        Log.d("COPIA BACKUP DE ARCHIVO LOG SQL ","Archivo de backup de lo sql copiado a " + f2.getAbsolutePath());
		     
		        return true;
	        }
	    } catch(FileNotFoundException ex){
	        Log.e("COPIA BACKUP DE ARCHIVO LOG SQL","Error al copiar archivo.",ex);
	    } catch(IOException e){
	        Log.e("COPIA BACKUP DE ARCHIVO LOG SQL","Error al copiar archivo.",e);
	    }
		
		return false;
	}
	
	
	public String readFile(String filename)
	{
		 try{
		  if(isExternalStorageAvailable()){
		   File file = new File(Environment.getExternalStorageDirectory(), filename);
		   BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		   String t = br.readLine();
		   br.close();
		   return t;
		  } else {return "";}   
		 } catch(Exception e){return "";}
	}
	
	
	/*public void deleteFile() 
	{
	   
		try{
			  if(isExternalStorageAvailable()){
			   File f = new File(Environment.getExternalStorageDirectory(), file_name);

			    // Make sure the file or directory exists and isn't write protected
			    if (!f.exists())
			      throw new IllegalArgumentException(
			          "Delete: no such file or directory: " + file_name);
		
			    if (!f.canWrite())
			      throw new IllegalArgumentException("Delete: write protected: "
			          + file_name);
		
			    // Attempt to delete it
			    boolean success = f.delete();
		
			    if (!success)
			      throw new IllegalArgumentException("Delete: deletion failed");
			    else Log.d("DELETE_LOG_SQL", "Log sql eliminado correctamente!");
			  }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	
	public void deleteFileCloud(String file_name_cloud) 
	{
	   
		try{
			  if(isExternalStorageAvailable()){
			   File f = new File(Environment.getExternalStorageDirectory(), file_name_cloud);

			    // Make sure the file or directory exists and isn't write protected
			    if (!f.exists())
			      throw new IllegalArgumentException(
			          "Delete: no such file or directory: " + file_name_cloud);
		
			    if (!f.canWrite())
			      throw new IllegalArgumentException("Delete: write protected: "
			          + file_name_cloud);
		
			    // Attempt to delete it
			    boolean success = f.delete();
		
			    if (!success)
			      throw new IllegalArgumentException("Delete: deletion failed");
			    else Log.d("DELETE_LOG_SQL", "Log sql cloud eliminado correctamente!");
			  }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Esta función permite verificar si existe el archivo logsql.txt 
	 * en el directorio de datos del almacenamiento interno del dispositivo.
	 * */
	public boolean checkExistFile(Context contex)   
	{
	   this.contexto = contex;
	   
	   try{
		    File f = new File(this.contexto.getFilesDir() + "/"+ this.file_name);
		    if (!f.exists()){
		    	Log.d("CHECK_EXIST_FILE_LOG", "No existe archivo de log sql");
		    	return false;
		    }else {
		    	return true;
		    }
	   }catch (Exception e) {
			e.printStackTrace();
			
	   }
		
		return false;
	}
	
	/**
	 * Esta función permite verificar si el archivo logsql.txt contiene
	 * datos.
	 * */
	public boolean existeDatos(Context context)
	{
		this.contexto = context;
		try{
		    File f = new File(this.contexto.getFilesDir() + "/"+ this.file_name);
		    if (f.exists()){
		    	//verificar si el archivo posee datos
		    	if(f.length() > 0){
		    		return true;
		    	}
		    }else {
		    	Log.d("CHECK_EXIST_FILE_LOG", "No existe archivo de log sql");
		    }
		 }catch (Exception e) {
			e.printStackTrace();
		 }
			
		return false;
	}
	
	/**
	 * Esta función permite verificar si una copia de seguridad de los logs sql 
	 * contiene datos.
	 * */
	public boolean existeDatosCopiaSeguridad(Context context, String filename)
	{
		this.contexto = context;
		try{
			File mydir = context.getDir("backup", Context.MODE_PRIVATE);
			File f = new File(mydir, filename);
		    if (f.exists()){
		    	//verificar si el archivo posee datos
		    	if(f.length() > 0){
		    		return true;
		    	}
		    }else {
		    	Log.d("CHECK_EXIST_FILE_LOG", "No existe archivo de copia de seguridad");
		    }
		 }catch (Exception e) {
			e.printStackTrace();
		 }
			
		return false;
	}
	
	
	public File[] getListaBackupLogs(Context context)
	{
		this.contexto = context;
		File mydir = context.getDir("backup", Context.MODE_PRIVATE);
		File[] lista = mydir.listFiles();
		if(lista == null){
			Log.d("COPIA DE SEGURIDAD", "Es mayor a cero");
		}
		
		return lista;
	}
	
	/**
	 * Esta función permite generar un nombre con fecha y hora 
	 * para los archivo de backup que se realice a partir del archivo logsql.txt
	 * 
	 * */
	public String obtenerNombreBackupLogSql()
	{
		String nuevo_nombre = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss"); 
		Date date = new Date();
		nuevo_nombre = dateFormat.format(date).toString();
		return "copia_seg_" + nuevo_nombre + ".txt";
		
	}
}
