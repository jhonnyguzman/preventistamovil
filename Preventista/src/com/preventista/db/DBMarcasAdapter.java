package com.preventista.db;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.preventista.entidades.Marcas;

public class DBMarcasAdapter {
	
	private static final String DATABASE_TABLE = "marcas";
	private  SQLiteDatabase db;
	private  DataBaseHelper myDbHelper;
	
	
	public DBMarcasAdapter(Context ctx)
	{
		this.myDbHelper = new DataBaseHelper(ctx);
        try {
        	 
        	this.myDbHelper.createDataBase();
 
	 	} catch (IOException ioe) {
	 
	 		throw new Error("Unable to create database");
	 
	 	}	
	}
	
	
	public ArrayList<Marcas> getAllMarcas()
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		ArrayList<Marcas> listamarcas = new ArrayList<Marcas>();
		
		String[] campos = new String[]{"_id","parent","descripcion","estado","created_at","updated_at"};
		
		Cursor c = db.query(DATABASE_TABLE, campos, null, null, null, null, null);
		if (c.moveToFirst()) {
		     do {
		    	 Marcas marca = new Marcas();
		 		
		    	  marca.set_id(c.getInt(0));
		    	  marca.setParent(c.getInt(1));
		    	  marca.setDescripcion(c.getString(2));
		    	  marca.setEstado(c.getInt(3));
		    	  marca.setCreated_at(c.getString(4));
		    	  marca.setUpdated_at(c.getString(5));
		    	  	
		    	 listamarcas.add(marca);
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No hay Marcas en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listamarcas;
	}
}
