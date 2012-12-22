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

import com.preventista.entidades.Deudas;
import com.preventista.extras.WriteLog;

public class DBDeudasAdapter {
	
	private static final String DATABASE_TABLE = "deudas";
	private  SQLiteDatabase db;
	private  DataBaseHelper myDbHelper;
	
	Context contexto;
	private WriteLog wl = new WriteLog();
	
	public DBDeudasAdapter(Context ctx)
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
	 * Método para agregar una nueva deuda a la tabla 'deudas'
	 * @param Deudas deuda 
	 * @return Long _id   id de la deuda ingresada 
	 */
	/*public long addDeuda(Deudas deuda)
	{	
		ContentValues valores = new ContentValues();
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();
		
		valores.put("_id", deuda.get_id());
		valores.put("montototal", deuda.getMontototal());
		valores.put("montoadeudado", deuda.getMontototal()); // el monto adeudado tiene que ser igual al monto total del deuda 
		valores.put("clientes_id", deuda.getClientes_id());
		valores.put("estado", deuda.getEstado());
		valores.put("fecha", deuda.getFecha());
	
		
		valores.put("updated_at", dateFormat.format(date));
		
		long rowsid = db.insert(DATABASE_TABLE, null, valores);
		if(rowsid != -1){
			wl.wAddDeuda(deuda,rowsid);
			estado =  true;
		}else estado = false;
		
		this.myDbHelper.close();
		return rowsid;
	}*/
	
	
	/**
	 * Método para modificar una deuda a la tabla
	 * @param Deudas deuda 
	 * @return boolean estado TRUE si se ha actualizado correctamente, FALSE si no se ha actualizado correctamente
	 */
	public boolean editDeuda(Deudas deuda)
	{	
		ContentValues valores = new ContentValues();
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();
		
		String sql = "UPDATE "+ DATABASE_TABLE +" SET ";
		
		if(deuda.getMontototal() != 0){
			valores.put("montototal", deuda.getMontototal());
			sql = sql + "deudas_montototal = " + deuda.getMontototal() + ", ";
		}
		
		valores.put("montoadeudado", deuda.getMontoadeudado());
		sql = sql + "deudas_montoadeudado = " + deuda.getMontoadeudado() + ", "; 
		
		if(deuda.getClientes_id() != 0){
			valores.put("clientes_id", deuda.getClientes_id());
			sql = sql + "clientes_id = " + deuda.getClientes_id() + ", ";
		}
		if(deuda.getEstado() != 0){
			valores.put("estado", deuda.getEstado());
			sql = sql + "deudas_estado = " + deuda.getEstado() + ", ";
		}
		
		valores.put("updated_at", dateFormat.format(date));
		sql = sql + "deudas_updated_at = '" + dateFormat.format(date) + "' ";
		
		
		String where = "_id = " + deuda.get_id();
		sql = sql + "WHERE deudas_id = " + deuda.get_id() + ";\n";
		
		int affectedrows = db.update(DATABASE_TABLE, valores, where,null);
		
		if(affectedrows == 1){
			wl.wEditDeuda(sql,this.contexto);
			estado = true;
		}else estado = false;
		
		myDbHelper.close();
		return estado;
	}
	
	
	/**
	 * Método para eliminar un deuda 
	 * @param _id id del deuda 
	 * @return boolean estado TRUE si se ha eliminado correctamente, FALSE si no se ha eliminado correctamente
	 */
	/*public boolean deletedeuda(int _id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = false;
		
		int cant = db.delete(DATABASE_TABLE, "_id="+_id+"",null);
		if(cant == 1) estado = true;
		
		myDbHelper.close();
		return estado;
	}*/
	
	
	/**
	 * Método para obtener una deuda 
	 * @param _id id de la deuda 
	 * @return ArrayList<Deudas>
	 */
	public ArrayList<Deudas> getDeudas(int _id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		ArrayList<Deudas> listaDeudas = new ArrayList<Deudas>();
		
		Cursor c = db.rawQuery("SELECT d.*, c.apellido, c.nombre, tg.descripcion " +
				"FROM deudas as d INNER JOIN clientes as c ON c._id = d.clientes_id " +
				"INNER JOIN tabgral as tg ON tg._id = d.estado where d._id = "+_id+"",null);
		
		if (c.moveToFirst()) {
		     do {
		    	 Deudas deuda = new Deudas();
		 		
		    	 deuda.set_id(c.getInt(0));
		    	 deuda.setMontototal(c.getFloat(1));
		    	 deuda.setFecha(c.getString(2));
		    	 deuda.setClientes_id(c.getInt(3));
		    	 deuda.setCreated_at(c.getString(4));
		    	 deuda.setUpdated_at(c.getString(5));
		    	 deuda.setUsuarios_id(c.getInt(6));
		    	 deuda.setEstado(c.getInt(7));
		    	 deuda.setMontoadeudado(c.getFloat(8));
		    	 deuda.setApellidoCliente(c.getString(9));
		    	 deuda.setNombreCliente(c.getString(10));
		    	 deuda.setEstadoDescripcion(c.getString(11));
		    	 
		    	 listaDeudas.add(deuda);
		    	  
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No existen Deudas en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listaDeudas;
	}
	
	
	
	/**
	 * Método para obtener todos los Deudas 
	 * @return ArrayList<Deudas>
	 */
	public ArrayList<Deudas> getAllDeudas()
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		ArrayList<Deudas> listaDeudas= new ArrayList<Deudas>();
	
		Cursor c = db.rawQuery("SELECT d.*, c.apellido, c.nombre" +
				"FROM deudas as d INNER JOIN clientes as c ON c._id = d.clientes_id ORDER BY p.created_at DESC",null);
		
		if (c.moveToFirst()) {
		     do {
		    	 Deudas deuda = new Deudas();
			 		
		    	 deuda.set_id(c.getInt(0));
		    	 deuda.setMontototal(c.getFloat(1));
		    	 deuda.setFecha(c.getString(2));
		    	 deuda.setClientes_id(c.getInt(3));
		    	 deuda.setCreated_at(c.getString(4));
		    	 deuda.setUpdated_at(c.getString(5));
		    	 deuda.setUsuarios_id(c.getInt(6));
		    	 deuda.setEstado(c.getInt(7));
		    	 deuda.setMontoadeudado(c.getFloat(8));
		    	 deuda.setApellidoCliente(c.getString(9));
		    	 deuda.setNombreCliente(c.getString(10));
		    	 deuda.setEstadoDescripcion(c.getString(11));
		    	 
		    	 listaDeudas.add(deuda);
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No se encontró deudas en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listaDeudas;
	}
	
	
	/**
	 * Método para obtener todos los Deudas sin pagar de un cliente
	 * @param int clientes_id
	 * @return ArrayList<Deudas>
	 */
	public ArrayList<Deudas> getDeudasSinPagarToShow(int clientes_id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		ArrayList<Deudas> listaDeudas= new ArrayList<Deudas>();
		
		//consultar todos las Deudas con estado 'Sin pagar > 26' y 'Parcialmente pagada > 27' 
		Cursor c = db.rawQuery("SELECT _id, montototal, montoadeudado, fecha FROM deudas AS d WHERE d.clientes_id = " + clientes_id + " AND (d.estado = 26 OR d.estado = 27)  ORDER BY d.created_at DESC",null);
		
		if (c.moveToFirst()) {
		     do {
		    	 Deudas deuda = new Deudas();
			 		
		    	 deuda.set_id(c.getInt(0));
		    	 deuda.setMontototal(c.getFloat(1));
		    	 deuda.setMontoadeudado(c.getFloat(2));
		    	 deuda.setFecha(c.getString(3));
		    	 
		    	 
		    	 listaDeudas.add(deuda);
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No se encontraron Deudas Sin Pagar en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listaDeudas;
	}
	
	
	/**
	 * Método para obtener todas las Deudas pagadas de un cliente
	 * @param int clientes_id
	 * @return ArrayList<Deudas>
	 */
	public ArrayList<Deudas> getDeudasPagadasToShow(int clientes_id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();   
		ArrayList<Deudas> listaDeudas= new ArrayList<Deudas>();
		
		//consultar todas las Deudas con estado 'Pagada > 28' y 'Parcialmente pagada > 27' 
		Cursor c = db.rawQuery("SELECT _id, montototal, montoadeudado, fecha FROM deudas AS d WHERE d.clientes_id = " + clientes_id + " AND (d.estado = 28 OR d.estado = 27)  ORDER BY d.created_at DESC",null);
		
		if (c.moveToFirst()) {
		     do {
		    	 Deudas deuda = new Deudas();
			 		
		    	 deuda.set_id(c.getInt(0));
		    	 deuda.setMontototal(c.getFloat(1));
		    	 deuda.setMontoadeudado(c.getFloat(2));
		    	 deuda.setFecha(c.getString(3));
		    	 
		    	 listaDeudas.add(deuda);
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No se encontraron Deudas pagadas en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listaDeudas;
	}
	
	
	/**
	 * Método para obtener todas las Deudas de un cliente con estado 'Sin Pagar' y 'Parcialmente pagada'
	 * @param int clientes_id
	 * @return ArrayList<Deudas>
	 */
	public ArrayList<Deudas> getDeudasEP(int clientes_id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();   
		ArrayList<Deudas> listaDeudas= new ArrayList<Deudas>();
		
		//consultar todas las Deudas con estado 'Sin Pagar > 26' y 'Parcialmente pagada > 27' 
		Cursor c = db.rawQuery("SELECT * FROM deudas AS d WHERE d.clientes_id = " + clientes_id + " AND (d.estado = 26 OR d.estado = 27)  ORDER BY d.created_at ASC",null);
		
		if (c.moveToFirst()) {
		     do {
		    	 Deudas deuda = new Deudas();
			 		
		    	 deuda.set_id(c.getInt(0));
		    	 deuda.setMontototal(c.getFloat(1));
		    	 deuda.setFecha(c.getString(2));
		    	 deuda.setClientes_id(c.getInt(3));
		    	 deuda.setCreated_at(c.getString(4));
		    	 deuda.setUsuarios_id(c.getInt(6));
		    	 deuda.setEstado(c.getInt(7));
		    	 deuda.setMontoadeudado(c.getFloat(8));
		    	 
		    	 listaDeudas.add(deuda);
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No se encontraron Deudas Sin Pagar o Parcialmente Pagadas en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listaDeudas;
	}
	
	
	/**
	 * Método para calcular el Haber de un cliente
	 * @param int clientes_id
	 * @return ArrayList<Deudas>
	 */
	public float getHaber(int clientes_id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();   
		float monto = 0;
		
		//consultar todas las Deudas con estado 'Pagada > 28' y 'Parcialmente pagada > 27'
		Cursor c = db.rawQuery("SELECT * FROM deudas AS d WHERE d.clientes_id = " + clientes_id + " AND (d.estado = 28 OR d.estado = 27)  ORDER BY d.created_at ASC",null);
		
		if (c.moveToFirst()) {
		     do {	
		    	 //monto  + (montototal - montoadeudado)
		    	 monto = monto + (c.getFloat(1) -  c.getFloat(8) );
		    	
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No se encontraron Deudas Pagadas o Parcialmente Pagadas en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return monto;
	}
	
	
	/**
	 * Método para calcular la Deuda de un cliente
	 * @param int clientes_id
	 * @return ArrayList<Deudas>
	 */
	public float getDeuda(int clientes_id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();   
		float monto = 0;
		
		//consultar todas las Deudas con estado 'Sin Pagar > 26' y 'Parcialmente pagada > 27' 
		Cursor c = db.rawQuery("SELECT * FROM deudas AS d WHERE d.clientes_id = " + clientes_id + " AND (d.estado = 26 OR d.estado = 27)  ORDER BY d.created_at ASC",null);
		
		if (c.moveToFirst()) {
		     do {	
		    	 //monto +  montoadeudado
		    	 monto = monto +  c.getFloat(8);
		    	
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No se encontraron Deudas Sin Pagar o Parcialmente Pagadas en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return monto;
	}
}
