package com.preventista.db;

import java.io.IOException;
import java.util.ArrayList;

import com.preventista.entidades.Rubros;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBRubrosAdapter {

	private static final String DATABASE_TABLE = "rubros";
	private  SQLiteDatabase db;
	private  DataBaseHelper myDbHelper;
	
	
	public DBRubrosAdapter(Context ctx)
	{
		this.myDbHelper = new DataBaseHelper(ctx);
        try {
        	 
        	this.myDbHelper.createDataBase();
 
	 	} catch (IOException ioe) {
	 
	 		throw new Error("Unable to create database");
	 
	 	}	
	}
	
	
	public ArrayList<Rubros> getAllRubros()
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		ArrayList<Rubros> listarubros = new ArrayList<Rubros>();
		
		String[] campos = new String[]{"_id","descripcion","estado","created_at","updated_at"};
		
		Cursor c = db.query(DATABASE_TABLE, campos, null, null, null, null, null);
		if (c.moveToFirst()) {
		     do {
		    	 Rubros rubro = new Rubros();
		 		
		    	  rubro.set_id(c.getInt(0));
		    	  rubro.setDescripcion(c.getString(1));
		    	  rubro.setEstado(c.getInt(2));
		    	  rubro.setCreated_at(c.getString(3));
		    	  rubro.setUpdated_at(c.getString(4));
		    	  	
		    	 listarubros.add(rubro);
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No hay Rubros en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listarubros;
	}
	
}
