package com.preventista.db;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.preventista.entidades.PagosDeudas;
import com.preventista.extras.WriteLog;

public class DBPagosDeudasAdapter {

	private static final String DATABASE_TABLE = "pagosdeudas";
	private  SQLiteDatabase db;
	private  DataBaseHelper myDbHelper;
	
	Context contexto;
	private WriteLog wl = new WriteLog();
	
	public DBPagosDeudasAdapter(Context ctx)
	{
		this.contexto = ctx;
		this.myDbHelper = new DataBaseHelper(this.contexto);
        try {
        	 
        	this.myDbHelper.createDataBase();
 
	 	} catch (IOException ioe) {
	 
	 		throw new Error("Unable to create database");
	 
	 	}	
	}
	
	
	/**
	 * Método para agregar un nuevo pagodeuda 
	 * @param PagosDeudas pagodeuda 
	 * @return Long _id   id del pago ingresado 
	 */
	public long addPagoDeuda(PagosDeudas pagodeuda)
	{	
		ContentValues valores = new ContentValues();
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();
		
		valores.put("_id", pagodeuda.get_id());
		valores.put("deudas_id", pagodeuda.getDeudas_id());
		valores.put("pagos_id", pagodeuda.getPagos_id());
		valores.put("montocubierto", pagodeuda.getMontocubierto());
		valores.put("created_at", dateFormat.format(date));
		valores.put("updated_at", dateFormat.format(date));
		
		long rowsid = db.insert(DATABASE_TABLE, null, valores);
		if(rowsid != -1){
			wl.wAddPagoDeuda(pagodeuda,rowsid,this.contexto);
		}
		
		this.myDbHelper.close();
		return rowsid;
	}
	
	
	/**
	 * Método para modificar un pagodeuda ingresado 
	 * @param PagosDeudas pagodeuda 
	 * @return boolean estado TRUE si se ha actualizado correctamente, FALSE si no se ha actualizado correctamente
	 */
	public boolean editPagoDeuda(PagosDeudas pagodeuda)
	{	
		ContentValues valores = new ContentValues();
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();
		
		String sql = "UPDATE "+ DATABASE_TABLE +" SET ";
		
		if(pagodeuda.getDeudas_id() != 0){
			valores.put("deudas_id", pagodeuda.getDeudas_id());
			sql = sql + "deudas_id = " + pagodeuda.getDeudas_id() + ", "; 
		}
		if(pagodeuda.getPagos_id() != 0){
			valores.put("pagos_id", pagodeuda.getPagos_id());
			sql = sql + "pagos_id = " + pagodeuda.getPagos_id() + ", "; 
		}
		if(pagodeuda.getMontocubierto() != 0){
			valores.put("montocubierto", pagodeuda.getMontocubierto());
			sql = sql + "montocubierto = " + pagodeuda.getMontocubierto() + ", ";
		}
		
		valores.put("updated_at", dateFormat.format(date));
		sql = sql + "updated_at = '" + dateFormat.format(date) + "' ";
		
		
		String where = "_id = " + pagodeuda.get_id();
		sql = sql + "WHERE _id = " + pagodeuda.get_id() + ";\n";
		
		int affectedrows = db.update(DATABASE_TABLE, valores, where,null);
		
		if(affectedrows == 1){
			wl.wEditPago(sql,this.contexto);
			estado = true;
		}else estado = false;
		
		myDbHelper.close();
		return estado;
	}
	
	
	/**
	 * Método para obtener todos los pagosdeudas realizados por un cliente dado 
	 * @param int clientes_id
	 * @return ArrayList<Pagos>
	 */
	public ArrayList<PagosDeudas> getPagosDeudasOfCliente(int clientes_id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		ArrayList<PagosDeudas> listapagosdeudas= new ArrayList<PagosDeudas>();

		Cursor c = db.rawQuery("SELECT * FROM pagosdeudas AS p WHERE p.clientes_id = " + clientes_id + " ORDER BY p.created_at DESC",null);
		
		if (c.moveToFirst()) {
		     do {
		    	 PagosDeudas pagodeuda = new PagosDeudas();
			 		
		    	 pagodeuda.set_id(c.getInt(0));
		    	 pagodeuda.setDeudas_id(c.getInt(1));
		    	 pagodeuda.setPagos_id(c.getInt(2));
		    	 pagodeuda.setMontocubierto(c.getFloat(3));
		    	 pagodeuda.setCreated_at(c.getString(4));
		    	 pagodeuda.setUpdated_at(c.getString(5));
		    	 
		    	 listapagosdeudas.add(pagodeuda);
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No se encontraron PagosDeudas de este cliente en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listapagosdeudas;
	}
}

