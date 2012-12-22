package com.preventista.db;

import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBUsuarioAdapter {
	
	private static final String DATABASE_TABLE = "usuarios";
	private  SQLiteDatabase db;
	private  DataBaseHelper myDbHelper = null;
	
	public DBUsuarioAdapter(Context ctx)
	{
		this.myDbHelper = new DataBaseHelper(ctx);
        try {
        	 
        	this.myDbHelper.createDataBase();
 
	 	} catch (IOException ioe) {
	 
	 		throw new Error("Unable to create database");
	 
	 	}	 
	}
	
	public int login(String username, String password) throws SQLException
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		
		int estado = 0;
		
		Cursor mCursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE + " WHERE username = ? AND password = ?", new String[]{username, password});
		
		if(mCursor != null){
			if(mCursor.getCount() > 0){
				mCursor.moveToFirst();
				estado = mCursor.getInt(0);
			}
		}else estado = 0;
		
		mCursor.close();
		this.myDbHelper.close();
		
		return estado;
	}
}
