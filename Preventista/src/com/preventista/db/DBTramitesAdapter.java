package com.preventista.db;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.preventista.entidades.Tramites;

public class DBTramitesAdapter {

	private static final String DATABASE_TABLE = "tramites";
	private  SQLiteDatabase db;
	private  DataBaseHelper myDbHelper;
	
	public DBTramitesAdapter(Context ctx)
	{
		this.myDbHelper = new DataBaseHelper(ctx);
        try {
        	 
        	this.myDbHelper.createDataBase();
 
	 	} catch (IOException ioe) {
	 
	 		throw new Error("Unable to create database");
	 
	 	}	
	}
	
	
	public ArrayList<Tramites> getTramite(int _id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
	
		ArrayList<Tramites> listatramites = new ArrayList<Tramites>();
		
		Cursor c = db.rawQuery("SELECT * FROM tramites as t  where t._id ="+_id+"",null);
		
		if (c.moveToFirst()) {
		     do {
		    	 Tramites tramite = new Tramites();
		 		
		    	 tramite.set_id(c.getInt(0));
		    	 tramite.setDescripcion(c.getString(1));
		    	 tramite.setCreated_at(c.getString(2));
		    	 tramite.setUpdated_at(c.getString(3));
		    	 listatramites.add(tramite);
		    
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No hay Trámites en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listatramites;
	}
	
	
	public ArrayList<Tramites> getAllTramites()
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		ArrayList<Tramites> listatramites = new ArrayList<Tramites>();
		Cursor c = db.rawQuery("SELECT * FROM tramites as t ",null);
		
		if (c.moveToFirst()) {
		     do {
		    	 Tramites tramite = new Tramites();
			 		
		    	 tramite.set_id(c.getInt(0));
		    	 tramite.setDescripcion(c.getString(1));
		    	 tramite.setCreated_at(c.getString(2));
		    	 tramite.setUpdated_at(c.getString(3));
		    	 listatramites.add(tramite);
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No hay clientes en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listatramites;
	}
}
