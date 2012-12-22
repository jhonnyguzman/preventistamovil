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

import com.preventista.entidades.Pedidos;
import com.preventista.extras.WriteLog;

public class DBPedidosAdapter {
	
	private static final String DATABASE_TABLE = "pedidos";
	private  SQLiteDatabase db;
	private  DataBaseHelper myDbHelper;
	
	Context contexto;
	private WriteLog wl = new WriteLog();
	
	public DBPedidosAdapter(Context ctx)
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
	 * Método para agregar un nuevo pedido a la tabla
	 * @param Pedidos pedido 
	 * @return Long _id   id del pedido ingresado 
	 */
	public long addPedido(Pedidos pedido)
	{	
		ContentValues valores = new ContentValues();
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();
		
		valores.put("_id", pedido.get_id());
		valores.put("montototal", pedido.getMontototal());
		valores.put("montoadeudado", pedido.getMontototal()); // el monto adeudado tiene que ser igual al monto total del pedido 
		valores.put("clientes_id", pedido.getClientes_id());
		valores.put("estado", pedido.getEstado());
		valores.put("tramites_id", pedido.getTramites_id());
		valores.put("observaciones", pedido.getObservaciones());
		valores.put("created_at", dateFormat.format(date));
		valores.put("updated_at", dateFormat.format(date));
		
		long rowsid = db.insert(DATABASE_TABLE, null, valores);
		if(rowsid != -1){
			wl.wAddPedido(pedido,rowsid,this.contexto);
			estado =  true;
		}else estado = false;
		
		this.myDbHelper.close();
		return rowsid;
	}
	
	
	/**
	 * Método para modificar un pedido a la tabla
	 * @param Pedidos pedido 
	 * @return boolean estado TRUE si se ha actualizado correctamente, FALSE si no se ha actualizado correctamente
	 */
	public boolean editPedido(Pedidos pedido)
	{	
		ContentValues valores = new ContentValues();
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();
		
		String sql = "UPDATE "+ DATABASE_TABLE +" SET ";
		
		if(pedido.getMontototal() != 0){
			valores.put("montototal", pedido.getMontototal());
			sql = sql + "peididos_montototal = " + pedido.getMontototal() + ", ";
		}
		
		valores.put("montoadeudado", pedido.getMontoadeudado());
		sql = sql + "pedidos_montoadeudado = " + pedido.getMontoadeudado() + ", "; 
		
		if(pedido.getClientes_id() != 0){
			valores.put("clientes_id", pedido.getClientes_id());
			sql = sql + "clientes_id = " + pedido.getClientes_id() + ", ";
		}
		if(pedido.getEstado() != 0){
			valores.put("estado", pedido.getEstado());
			sql = sql + "pedidos_estado = " + pedido.getEstado() + ", ";
		}
		if(pedido.getTramites_id() != 0){
			valores.put("tramites_id", pedido.getTramites_id());
			sql = sql + "tramites_id = " + pedido.getTramites_id() + ", ";
		}
		
		if(pedido.getObservaciones() != null){
			valores.put("observaciones", pedido.getObservaciones());
			sql = sql + "pedidos_observaciones = '" + pedido.getObservaciones() + "', ";
		}
		
		valores.put("updated_at", dateFormat.format(date));
		sql = sql + "pedidos_updated_at = '" + dateFormat.format(date) + "' ";
		
		
		String where = "_id = " + pedido.get_id();
		sql = sql + "WHERE pedidos_id = " + pedido.get_id() + ";\n";
		
		int affectedrows = db.update(DATABASE_TABLE, valores, where,null);
		
		if(affectedrows == 1){
			wl.wEditPedido(sql,this.contexto);
			estado = true;
		}else estado = false;
		
		myDbHelper.close();
		return estado;
	}
	
	
	/**
	 * Método para eliminar un pedido 
	 * @param _id id del pedido 
	 * @return boolean estado TRUE si se ha eliminado correctamente, FALSE si no se ha eliminado correctamente
	 */
	public boolean deletePedido(int _id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = false;
		
		int cant = db.delete(DATABASE_TABLE, "_id="+_id+"",null);
		if(cant == 1) estado = true;
		
		myDbHelper.close();
		return estado;
	}
	
	
	/**
	 * Método para obtener un de los pedido 
	 * @param _id id del pedido 
	 * @return ArrayList<Pedidos>
	 */
	public ArrayList<Pedidos> getPedidos(int _id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		ArrayList<Pedidos> listapedidos = new ArrayList<Pedidos>();
		
		Cursor c = db.rawQuery("SELECT p.*, c.apellido, c.nombre, c.clientescategoria_id, tg.descripcion " +
				"FROM pedidos as p INNER JOIN clientes as c ON c._id = p.clientes_id " +
				"INNER JOIN tabgral as tg ON tg._id = p.estado where p._id = "+_id+"",null);
		
		if (c.moveToFirst()) {
		     do {
		    	 Pedidos pedido = new Pedidos();
		 		
		    	 pedido.set_id(c.getInt(0));
		    	 pedido.setMontototal(c.getFloat(1));
		    	 pedido.setMontoadeudado(c.getFloat(2));
		    	 pedido.setClientes_id(c.getInt(3));
		    	 pedido.setEstado(c.getInt(4));
		    	 pedido.setTramites_id(c.getInt(5));
		    	 pedido.setObservaciones(c.getString(6));
		    	 pedido.setCreated_at(c.getString(7));
		    	 pedido.setUpdated_at(c.getString(8));
		    	 pedido.setApellido(c.getString(9));
		    	 pedido.setNombre(c.getString(10));
		    	 pedido.setCategoria_id(c.getInt(11));
		    	 pedido.setEstadoDescripcion(c.getString(12));
		    	 
		    	 listapedidos.add(pedido);
		    	  
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No hay Pedidos en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listapedidos;
	}
	
	
	/**
	 * Método para obtener pedidos de una fecha dada 
	 * @param String created_at
	 * @return ArrayList<Pedidos>
	 */
	public ArrayList<Pedidos> getByFecha(String created_at)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		ArrayList<Pedidos> listapedidos = new ArrayList<Pedidos>();
		
		Cursor c = db.rawQuery("SELECT p.*, c.apellido, c.nombre, c.clientescategoria_id " +
				"FROM pedidos as p INNER JOIN clientes as c ON c._id = p.clientes_id where strftime('%Y-%m-%d', p.created_at) = '"+created_at+"' ORDER BY p.created_at DESC",null);
		
		if (c.moveToFirst()) {
		     do {
		    	 Pedidos pedido = new Pedidos();
		 		
		    	 pedido.set_id(c.getInt(0));
		    	 pedido.setMontototal(c.getFloat(1));
		    	 pedido.setMontoadeudado(c.getFloat(2));
		    	 pedido.setClientes_id(c.getInt(3));
		    	 pedido.setEstado(c.getInt(4));
		    	 pedido.setTramites_id(c.getInt(5));
		    	 pedido.setObservaciones(c.getString(6));
		    	 pedido.setCreated_at(c.getString(7));
		    	 pedido.setUpdated_at(c.getString(8));
		    	 pedido.setApellido(c.getString(9));
		    	 pedido.setNombre(c.getString(10));
		    	 pedido.setCategoria_id(c.getInt(11));
		    	 
		    	 listapedidos.add(pedido);
		    	  
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No hay Pedidos en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listapedidos;
	}
	
	
	/**
	 * Método para obtener todos los pedidos 
	 * @return ArrayList<Pedidos>
	 */
	public ArrayList<Pedidos> getAllPedidos()
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		ArrayList<Pedidos> listapedidos= new ArrayList<Pedidos>();
	
		Cursor c = db.rawQuery("SELECT p.*, c.apellido, c.nombre, c.clientescategoria_id " +
				"FROM pedidos as p INNER JOIN clientes as c ON c._id = p.clientes_id ORDER BY p.created_at DESC LIMIT 200",null);
		
		if (c.moveToFirst()) {
		     do {
		    	 Pedidos pedido = new Pedidos();
			 		
		    	 pedido.set_id(c.getInt(0));
		    	 pedido.setMontototal(c.getFloat(1));
		    	 pedido.setMontoadeudado(c.getFloat(2));
		    	 pedido.setClientes_id(c.getInt(3));
		    	 pedido.setEstado(c.getInt(4));
		    	 pedido.setTramites_id(c.getInt(5));
		    	 pedido.setObservaciones(c.getString(6));
		    	 pedido.setCreated_at(c.getString(7));
		    	 pedido.setUpdated_at(c.getString(8));
		    	 pedido.setApellido(c.getString(9));
		    	 pedido.setNombre(c.getString(10));
		    	 pedido.setCategoria_id(c.getInt(11));
		    	 
		    	 listapedidos.add(pedido);
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No se encontró el Pedido en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listapedidos;
	}
	
	
	/**
	 * Método para obtener todos los pedidos adeudados de un cliente
	 * @param int clientes_id
	 * @return ArrayList<Pedidos>
	 */
	public ArrayList<Pedidos> getPedidosAdeudadosToShow(int clientes_id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		ArrayList<Pedidos> listapedidos= new ArrayList<Pedidos>();
		
		//consultar todos los pedidos con estado 'Entregado > 8', 'Entregago y parcialmente pagago > 15' y el remito con estado 'Entregado > 13'
		/*Cursor c = db.rawQuery("SELECT p._id, p.montototal, p.montoadeudado, p.created_at " +
				"FROM pedidos as p INNER JOIN hojarutadetalle as hd ON hd.pedidos_id = p._id " +
				"INNER JOIN remitos as r ON r.hojarutadetalle_id = hd._id" +
				"WHERE and (p.estado = 8 or p.estado = 15) and r.remitos_estado = 13 ORDER BY p.created_at ASC",null);*/
		
		//consultar todos los pedidos con estado 'Entregado > 8' y 'Entregago y parcialmente pagado > 15' 
		Cursor c = db.rawQuery("SELECT _id, montototal, montoadeudado, created_at FROM pedidos AS p WHERE p.clientes_id = " + clientes_id + " AND (p.estado = 8 OR p.estado = 15)  ORDER BY p.created_at DESC",null);
		
		if (c.moveToFirst()) {
		     do {
		    	 Pedidos pedido = new Pedidos();
			 		
		    	 pedido.set_id(c.getInt(0));
		    	 pedido.setMontototal(c.getFloat(1));
		    	 pedido.setMontoadeudado(c.getFloat(2));
		    	 pedido.setCreated_at(c.getString(3));
		    	 
		    	 
		    	 listapedidos.add(pedido);
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No se encontraron Pedidos adeudados en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listapedidos;
	}
	
	
	/**
	 * Método para obtener todos los pedidos pagados de un cliente
	 * @param int clientes_id
	 * @return ArrayList<Pedidos>
	 */
	public ArrayList<Pedidos> getPedidosPagadosToShow(int clientes_id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();   
		ArrayList<Pedidos> listapedidos= new ArrayList<Pedidos>();
		
		//consultar todos los pedidos con estado 'Pagado > 16' y 'Entregago y parcialmente pagado > 15' 
		Cursor c = db.rawQuery("SELECT _id, montototal, montoadeudado, created_at FROM pedidos AS p WHERE p.clientes_id = " + clientes_id + " AND (p.estado = 15 OR p.estado = 16)  ORDER BY p.created_at DESC",null);
		
		if (c.moveToFirst()) {
		     do {
		    	 Pedidos pedido = new Pedidos();
			 		
		    	 pedido.set_id(c.getInt(0));
		    	 pedido.setMontototal(c.getFloat(1));
		    	 pedido.setMontoadeudado(c.getFloat(2));
		    	 pedido.setCreated_at(c.getString(3));
		    	 
		    	 listapedidos.add(pedido);
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No se encontraron Pedidos pagados en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listapedidos;
	}
	
	
	/**
	 * Método para obtener todos los pedidos de un cliente con estado 'Entregado' y 'Entregado y Parcialmente pagado'
	 * @param int clientes_id
	 * @return ArrayList<Pedidos>
	 */
	public ArrayList<Pedidos> getPedidosEP(int clientes_id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();   
		ArrayList<Pedidos> listapedidos= new ArrayList<Pedidos>();
		
		//consultar todos los pedidos con estado 'Entregado > 8' y 'Entregago y parcialmente pagado > 15' 
		Cursor c = db.rawQuery("SELECT * FROM pedidos AS p WHERE p.clientes_id = " + clientes_id + " AND (p.estado = 8 OR p.estado = 15)  ORDER BY p.created_at ASC",null);
		
		if (c.moveToFirst()) {
		     do {
		    	 Pedidos pedido = new Pedidos();
			 		
		    	 pedido.set_id(c.getInt(0));
		    	 pedido.setMontototal(c.getFloat(1));
		    	 pedido.setMontoadeudado(c.getFloat(2));
		    	 pedido.setClientes_id(c.getInt(3));
		    	 pedido.setEstado(c.getInt(4));
		    	 pedido.setTramites_id(c.getInt(5));
		    	 pedido.setObservaciones(c.getString(6));
		    	 pedido.setCreated_at(c.getString(7));
		    	 
		    	 listapedidos.add(pedido);
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No se encontraron Pedidos Entregados o Parcialmente Pagados en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listapedidos;
	}
	
	
	/**
	 * Método para calcular el Haber de un cliente
	 * @param int clientes_id
	 * @return ArrayList<Pedidos>
	 */
	public float getHaber(int clientes_id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();   
		float monto = 0;
		
		//consultar todos los pedidos con estado 'Pagado > 16' y 'Entregago y parcialmente pagados > 15' 
		Cursor c = db.rawQuery("SELECT * FROM pedidos AS p WHERE p.clientes_id = " + clientes_id + " AND (p.estado = 16 OR p.estado = 15)  ORDER BY p.created_at ASC",null);
		
		if (c.moveToFirst()) {
		     do {	
		    	 //monto  + (montototal - montoadeudado)
		    	 monto = monto + (c.getFloat(1) -  c.getFloat(2) );
		    	
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No se encontraron Pedidos Pagados o Parcialmente Pagados en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return monto;
	}
	
	
	/**
	 * Método para calcular el la Deuda de un cliente
	 * @param int clientes_id
	 * @return ArrayList<Pedidos>
	 */
	public float getDeuda(int clientes_id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();   
		float monto = 0;
		
		//consultar todos los pedidos con estado 'Entregado > 8' y 'Entregago y parcialmente pagados > 15' 
		Cursor c = db.rawQuery("SELECT * FROM pedidos AS p WHERE p.clientes_id = " + clientes_id + " AND (p.estado = 8 OR p.estado = 15)  ORDER BY p.created_at ASC",null);
		
		if (c.moveToFirst()) {
		     do {	
		    	 //monto +  montoadeudado
		    	 monto = monto +  c.getFloat(2);
		    	
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No se encontraron Pedidos Entregados o Parcialmente Pagados en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return monto;
	}
}
