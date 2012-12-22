package com.preventista.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class UpdateDataBaseHelper extends SQLiteOpenHelper{

	//La ruta de sistema por defecto de android hacia mi base de datos
	private static String DB_PATH = "/data/data/com.preventista.main/databases/";
	private static String DB_NAME = "preventista";
	private SQLiteDatabase myDataBase;
	private final Context myContext;
	
	/**
	 * Constructor
	 * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
	 * @param context
	 */
	public UpdateDataBaseHelper(Context context){
		super(context,DB_NAME, null, 1);
		this.myContext = context;
	}
	
	
	/**
     * Creates a empty database on the system and rewrites it with your own database.
     * 
     */
	public void createDataBase(File file) throws IOException{
		
		//boolean dbExist = checkDataBase();
		
		//if(dbExist){
			
			//no hacemos nada - la base de datos existe
			
		//}else{
			
			//By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
			
			this.getReadableDatabase();
			
			try{
				
				this.close();
				copyDataBase(file);
				
			}catch (IOException e){
				
				throw new Error("Error al copiar la base de datos");
				
			}
		//}
	}
	
	
	/**
	 * Check if the database already exist to avoid re-copying the file each time you open the application.
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase(){
		
		SQLiteDatabase checkDB = null;
		
		try{
			
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
			
		}catch(SQLiteException e){
			
			//la base de datos todavia no existe
		}
		
		if(checkDB != null){
			
			checkDB.close();
			
		}
		
		return checkDB != null ? true : false;
		
	}
	
	
	/**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    private void copyDataBase(File file) throws IOException{
 
    	//Open your local db as the input stream
    	InputStream myInput = new FileInputStream(file);
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
 
    }
    
 
	@Override
	public void onCreate(SQLiteDatabase db) {
 
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 
	}
	
	
    // Add your public helper methods to access and get content from the database.
   // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
   // to you to create adapters for your views.
}