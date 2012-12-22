package com.preventista.db;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.preventista.entidades.Tabgral;

public class DBTabgralAdapter {
	
	private static final String DATABASE_TABLE = "tabgral";
	private  SQLiteDatabase db;
	private  DataBaseHelper myDbHelper;
	
	public DBTabgralAdapter(Context ctx)
	{
		this.myDbHelper = new DataBaseHelper(ctx);
        try {
        	 
        	this.myDbHelper.createDataBase();
 
	 	} catch (IOException ioe) {
	 
	 		throw new Error("Unable to create database");
	 
	 	}	
	}
	
	
	public ArrayList<Tabgral> getTabgral(int grupos_tabgral_id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		
		///String[] campos = new String[]{"_id","nombre","apellido","direccion","telefono","created_at","updated_at","clientecategoria_id"};
		//String[] args = new String[]{apellido};
		ArrayList<Tabgral> listatabgral = new ArrayList<Tabgral>();
		
		//Log.d("TAG_PARAMETRO", apellido);
		//Cursor c = myDataBase.query("choferes", campos, "apellido=?",args, null, null, null);
		
		Cursor c = db.rawQuery("SELECT * FROM tabgral as tg  where tg.grupos_tabgral_id ="+grupos_tabgral_id+"",null);
		
		if (c.moveToFirst()) {
		     //Recorremos el cursor hasta que no haya más registros
		     do {
		    	 Tabgral tabgral = new Tabgral();
		 		
		    	 tabgral.set_id(c.getInt(0));
		    	 tabgral.setDescripcion(c.getString(1));
		    	 tabgral.setGrupos_tabgral_id(c.getInt(2));
		    	 listatabgral.add(tabgral);
		    	 
		    	 //System.out.println(c.getString(2));
		    	 //Log.d("REGISTRO_OK", c.getString(2));
		    	  
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No hay Registros en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listatabgral;
	}
	
	
	public ArrayList<Tabgral> getAllTabgral()
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		ArrayList<Tabgral> listatabgral = new ArrayList<Tabgral>();
		Cursor c = db.rawQuery("SELECT * FROM tabgral as tg ",null);
		
		if (c.moveToFirst()) {
		     do {
		    	 Tabgral tabgral = new Tabgral();
			 		
		    	 tabgral.set_id(c.getInt(0));
		    	 tabgral.setDescripcion(c.getString(1));
		    	 tabgral.setGrupos_tabgral_id(c.getInt(2));
		    	 listatabgral.add(tabgral);
		    	  	
		    	 listatabgral.add(tabgral);
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No hay clientes en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listatabgral;
	}
	
}
