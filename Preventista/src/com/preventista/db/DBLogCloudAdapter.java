package com.preventista.db;

import java.io.IOException;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBLogCloudAdapter {

	private  SQLiteDatabase db;
	private  DataBaseHelper myDbHelper;
	
	public DBLogCloudAdapter(Context ctx)
	{
		this.myDbHelper = new DataBaseHelper(ctx);
        try {
        	 
        	this.myDbHelper.createDataBase();
 
	 	} catch (IOException ioe) {
	 
	 		throw new Error("Unable to create database");
	 
	 	}	
	}
	
	
	public void execQuery(String query)
	{	
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		
		try{
			db.execSQL(query);      
			
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		this.myDbHelper.close();
	}
}
