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

import com.preventista.entidades.Pagos;
import com.preventista.extras.WriteLog;
import com.preventista.main.PreRes;

public class DBPagosAdapter {

	private static final String DATABASE_TABLE = "pagos";
	private  SQLiteDatabase db;
	private  DataBaseHelper myDbHelper;
	
	Context contexto;
	private WriteLog wl = new WriteLog();
	
	public DBPagosAdapter(Context ctx)
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
	 * Método para agregar un nuevo pago 
	 * @param Pagos pago 
	 * @return Long _id   id del pago ingresado 
	 */
	public long addPago(Pagos pago)
	{	
		ContentValues valores = new ContentValues();
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();
		
		valores.put("_id", pago.get_id());
		valores.put("monto", pago.getMonto());
		valores.put("clientes_id", pago.getClientes_id()); 
		valores.put("usuarios_id", pago.getUsuarios_id());
		valores.put("created_at", dateFormat.format(date));
		valores.put("updated_at", dateFormat.format(date));
		
		long rowsid = db.insert(DATABASE_TABLE, null, valores);
		if(rowsid != -1){
			wl.wAddPago(pago,rowsid,this.contexto);
		}
		
		this.myDbHelper.close();
		return rowsid;
	}
	
	
	/**
	 * Método para modificar un pago ingresado 
	 * @param Pagos pago 
	 * @return boolean estado TRUE si se ha actualizado correctamente, FALSE si no se ha actualizado correctamente
	 */
	public boolean editPago(Pagos pago)
	{	
		ContentValues valores = new ContentValues();
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();
		
		String sql = "UPDATE "+ DATABASE_TABLE +" SET ";
		
		if(pago.getMonto() != 0){
			valores.put("monto", pago.getMonto());
			sql = sql + "pagos_monto = " + pago.getMonto() + ", ";
		}
		if(pago.getClientes_id() != 0){
			valores.put("clientes_id", pago.getClientes_id());
			sql = sql + "clientes_id = " + pago.getClientes_id() + ", "; 
		}
		if(pago.getUsuarios_id() != 0){
			valores.put("usuarios_id", pago.getUsuarios_id());
			sql = sql + "usuarios_id = " + pago.getUsuarios_id() + ", ";
		}
		
		valores.put("updated_at", dateFormat.format(date));
		sql = sql + "pagos_updated_at = '" + dateFormat.format(date) + "' ";
		
		
		String where = "_id = " + pago.get_id();
		sql = sql + "WHERE pago_id = " + pago.get_id() + ";\n";
		
		int affectedrows = db.update(DATABASE_TABLE, valores, where,null);
		
		if(affectedrows == 1){
			wl.wEditPago(sql,this.contexto);
			estado = true;
		}else estado = false;
		
		myDbHelper.close();
		return estado;
	}
	
	
	/**
	 * Método para obtener todos los pagos realizados por un cliente dado 
	 * @param int clientes_id
	 * @return ArrayList<Pagos>
	 */
	public ArrayList<Pagos> getPagosOfCliente(int clientes_id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		ArrayList<Pagos> listapagos= new ArrayList<Pagos>();

		Cursor c = db.rawQuery("SELECT _id, monto, clientes_id, usuarios_id, updated_at FROM pagos AS p WHERE p.clientes_id = " + clientes_id + " ORDER BY p.created_at DESC LIMIT 50",null);
		
		if (c.moveToFirst()) {
		     do {
		    	 Pagos pago = new Pagos();
			 		
		    	 pago.set_id(c.getInt(0));
		    	 pago.setMonto(c.getDouble(1));
		    	 pago.setClientes_id(c.getInt(2));
		    	 pago.setUsuarios_id(c.getInt(3));
		    	 pago.setCreated_at(c.getString(4));
		    	 
		    	 listapagos.add(pago);
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No se encontraron Pagos de este cliente en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listapagos;
	}
}
