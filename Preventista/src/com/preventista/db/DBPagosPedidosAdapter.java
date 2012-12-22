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
import com.preventista.entidades.PagosPedidos;
import com.preventista.extras.WriteLog;

public class DBPagosPedidosAdapter {

	private static final String DATABASE_TABLE = "pagospedidos";
	private  SQLiteDatabase db;
	private  DataBaseHelper myDbHelper;
	
	Context contexto;
	private WriteLog wl = new WriteLog();
	
	public DBPagosPedidosAdapter(Context ctx)
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
	 * Método para agregar un nuevo pagopedido 
	 * @param PagosPedidos pagopedido 
	 * @return Long _id   id del pago ingresado 
	 */
	public long addPagoPedido(PagosPedidos pagopedido)
	{	
		ContentValues valores = new ContentValues();
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();
		
		valores.put("_id", pagopedido.get_id());
		valores.put("pedidos_id", pagopedido.getPedidos_id());
		valores.put("pagos_id", pagopedido.getPagos_id());
		valores.put("montocubierto", pagopedido.getMontocubierto());
		valores.put("created_at", dateFormat.format(date));
		valores.put("updated_at", dateFormat.format(date));
		
		long rowsid = db.insert(DATABASE_TABLE, null, valores);
		if(rowsid != -1){
			wl.wAddPagoPedido(pagopedido,rowsid,this.contexto);
		}
		
		this.myDbHelper.close();
		return rowsid;
	}
	
	
	/**
	 * Método para modificar un pagopedido ingresado 
	 * @param PagosPedidos pagopedido 
	 * @return boolean estado TRUE si se ha actualizado correctamente, FALSE si no se ha actualizado correctamente
	 */
	public boolean editPagoPedido(PagosPedidos pagopedido)
	{	
		ContentValues valores = new ContentValues();
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();
		
		String sql = "UPDATE "+ DATABASE_TABLE +" SET ";
		
		if(pagopedido.getPedidos_id() != 0){
			valores.put("pedidos_id", pagopedido.getPedidos_id());
			sql = sql + "pedidos_id = " + pagopedido.getPedidos_id() + ", "; 
		}
		if(pagopedido.getPagos_id() != 0){
			valores.put("pagos_id", pagopedido.getPagos_id());
			sql = sql + "pagos_id = " + pagopedido.getPagos_id() + ", "; 
		}
		if(pagopedido.getMontocubierto() != 0){
			valores.put("montocubierto", pagopedido.getMontocubierto());
			sql = sql + "pagospedidos_montocubierto = " + pagopedido.getMontocubierto() + ", ";
		}
		
		valores.put("updated_at", dateFormat.format(date));
		sql = sql + "pagospedidos_updated_at = '" + dateFormat.format(date) + "' ";
		
		
		String where = "_id = " + pagopedido.get_id();
		sql = sql + "WHERE pagospedidos_id = " + pagopedido.get_id() + ";\n";
		
		int affectedrows = db.update(DATABASE_TABLE, valores, where,null);
		
		if(affectedrows == 1){
			wl.wEditPago(sql,this.contexto);
			estado = true;
		}else estado = false;
		
		myDbHelper.close();
		return estado;
	}
	
	
	/**
	 * Método para obtener todos los pagospedidos realizados por un cliente dado 
	 * @param int clientes_id
	 * @return ArrayList<Pagos>
	 */
	public ArrayList<PagosPedidos> getPagosPedidosOfCliente(int clientes_id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		ArrayList<PagosPedidos> listapagospedidos= new ArrayList<PagosPedidos>();

		Cursor c = db.rawQuery("SELECT * FROM pagospedidos AS p WHERE p.clientes_id = " + clientes_id + " ORDER BY p.created_at DESC LIMIT 50",null);
		
		if (c.moveToFirst()) {
		     do {
		    	 PagosPedidos pagopedido = new PagosPedidos();
			 		
		    	 pagopedido.set_id(c.getInt(0));
		    	 pagopedido.setPedidos_id(c.getInt(1));
		    	 pagopedido.setPagos_id(c.getInt(2));
		    	 pagopedido.setMontocubierto(c.getDouble(3));
		    	 pagopedido.setCreated_at(c.getString(4));
		    	 pagopedido.setUpdated_at(c.getString(5));
		    	 
		    	 listapagospedidos.add(pagopedido);
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No se encontraron PagosPedidos de este cliente en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listapagospedidos;
	}
}
