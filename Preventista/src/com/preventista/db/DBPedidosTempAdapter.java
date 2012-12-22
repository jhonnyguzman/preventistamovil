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

import com.preventista.entidades.Pedidostemp;


public class DBPedidosTempAdapter {
	
	private static final String DATABASE_TABLE = "pedidostemp";
	private  SQLiteDatabase db;
	private  DataBaseHelper myDbHelper;
	
	public DBPedidosTempAdapter(Context ctx)
	{
		this.myDbHelper = new DataBaseHelper(ctx);
        try {
        	 
        	this.myDbHelper.createDataBase();
 
	 	} catch (IOException ioe) {
	 
	 		throw new Error("Unable to create database");
	 
	 	}	
	}
	
	
	public boolean addPedidostemp(Pedidostemp pedidotemp)
	{	
		ContentValues valores = new ContentValues();
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss"); 
		Date date = new Date();
		
		valores.put("montototal", pedidotemp.getMontototal());
		valores.put("montoadeudado", pedidotemp.getMontoadeudado());
		valores.put("clientes_id", pedidotemp.getClientes_id());
		valores.put("estado", pedidotemp.getEstado());
		valores.put("tramites_id", pedidotemp.getTramites_id());
		valores.put("obervaciones", pedidotemp.getObservaciones());
		
		valores.put("updated_at", dateFormat.format(date));
		
		long rowsid = db.insert(DATABASE_TABLE, null, valores);
		if(rowsid != -1){
			/*String sql = "INSERT INTO choferes (_id,cuil,apellido,nombre) VALUES ("+ rowsid + "," + chofer.getCuil() + ",'" + chofer.getApellido() + "','" + chofer.getNombre() + "');\n";
			Log.d("SQL_INSERT", sql);
			WriteLogSql wlq = new WriteLogSql();
			wlq.writeFile("log_sql.txt", sql);*/
			estado =  true;
		}else estado = false;
		
		this.myDbHelper.close();
		return estado;
	}
	
	
	public boolean editPedidostemp(Pedidostemp pedidotemp)
	{	
		ContentValues valores = new ContentValues();
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss"); 
		Date date = new Date();
		
		//String sql = "UPDATE "+ DATABASE_TABLE +" SET ";
		
		if(pedidotemp.getMontototal() != 0){
			valores.put("montotal", pedidotemp.getMontototal());
			//sql = sql + "nombre = '" + cliente.getNombre() + "', ";
		}
		if(pedidotemp.getMontoadeudado() != 0){
			valores.put("montoadeudado", pedidotemp.getMontoadeudado());
			//sql = sql + "apellido = '" + cliente.getApellido() + "', ";
		}
		if(pedidotemp.getClientes_id() != 0){
			valores.put("clientes_id", pedidotemp.getClientes_id());
			//sql = sql + "direccion = '" + cliente.getDireccion() + "', ";
		}
		if(pedidotemp.getEstado() != 0){
			valores.put("estado", pedidotemp.getEstado());
			//sql = sql + "telefono = '" + cliente.getTelefono() + "', ";
		}
		if(pedidotemp.getTramites_id() != 0){
			valores.put("tramites_id", pedidotemp.getTramites_id());
			//sql = sql + "created_at = '" + cliente.getCreated_at() + "', ";
		}
		
		if(pedidotemp.getObservaciones() != null){
			valores.put("observaciones", pedidotemp.getObservaciones());
			//sql = sql + "created_at = '" + cliente.getCreated_at() + "', ";
		}
		
		valores.put("updated_at", dateFormat.format(date));
		//sql = sql + "updated_at = '" + cliente.getUpdated_at() + "', ";
		
		
		String where = "_id = " + pedidotemp.get_id();
		//sql = sql + "WHERE _id = " + cliente.get_id() + ";\n";
		
		int affectedrows = db.update(DATABASE_TABLE, valores, where,null);
		
		if(affectedrows == 1){
			//Log.d("SQL_UPDATE", sql);
			/*WriteLogSql wlq = new WriteLogSql();
			wlq.writeFile("log_sql.txt", sql);*/
			estado = true;
		}else estado = false;
		
		myDbHelper.close();
		return estado;
	}
	
	
	public ArrayList<Pedidostemp> getPedidostemp(int _id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		
		///String[] campos = new String[]{"_id","nombre","apellido","direccion","telefono","created_at","updated_at","clientecategoria_id"};
		//String[] args = new String[]{apellido};
		ArrayList<Pedidostemp> listapedidostemp = new ArrayList<Pedidostemp>();
		
		//Log.d("TAG_PARAMETRO", apellido);
		//Cursor c = myDataBase.query("choferes", campos, "apellido=?",args, null, null, null);
		
		Cursor c = db.rawQuery("SELECT pt.* " +
				"FROM pedidostemp as pt INNER JOIN clientes as c ON c.clientes_id = pt.clientes_id where pt._id = '"+_id+"%'",null);
		
		if (c.moveToFirst()) {
		     //Recorremos el cursor hasta que no haya más registros
		     do {
		    	 Pedidostemp pedidotemp = new Pedidostemp();
		 		
		    	 pedidotemp.set_id(c.getInt(0));
		    	 pedidotemp.setMontototal(c.getFloat(1));
		    	 pedidotemp.setMontoadeudado(c.getFloat(2));
		    	 pedidotemp.setClientes_id(c.getInt(3));
		    	 pedidotemp.setEstado(c.getInt(4));
		    	 pedidotemp.setTramites_id(c.getInt(5));
		    	 pedidotemp.setObservaciones(c.getString(6));
		    	 pedidotemp.setCreated_at(c.getString(7));
		    	 pedidotemp.setUpdated_at(c.getString(8));
		    	  
		    	 listapedidostemp.add(pedidotemp);
		    	 
		    	 //System.out.println(c.getString(2));
		    	 //Log.d("REGISTRO_OK", c.getString(2));
		    	  
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No hay Pedidos temporales en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listapedidostemp;
	}
	
	
	public ArrayList<Pedidostemp> getAllPedidostemp()
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		ArrayList<Pedidostemp> listapedidostemp = new ArrayList<Pedidostemp>();
		
		//String[] campos = new String[]{"_id","nombre","apellido","direccion","telefono","created_at","updated_at","clientescategoria_id"};
		
		//Cursor c = db.query(DATABASE_TABLE, campos, null, null, null, null, null);
		Cursor c = db.rawQuery("SELECT pt.* " +
				"FROM pedidostemp as pt INNER JOIN clientes as c ON c.clientes_id = pt.clientes_id",null);
		
		if (c.moveToFirst()) {
		     do {
		    	 Pedidostemp pedidotemp = new Pedidostemp();
			 		
		    	 pedidotemp.set_id(c.getInt(0));
		    	 pedidotemp.setMontototal(c.getFloat(1));
		    	 pedidotemp.setMontoadeudado(c.getFloat(2));
		    	 pedidotemp.setClientes_id(c.getInt(3));
		    	 pedidotemp.setEstado(c.getInt(4));
		    	 pedidotemp.setTramites_id(c.getInt(5));
		    	 pedidotemp.setObservaciones(c.getString(6));
		    	 pedidotemp.setCreated_at(c.getString(7));
		    	 pedidotemp.setUpdated_at(c.getString(8));
		    	  
		    	 listapedidostemp.add(pedidotemp);
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No hay Pedidos temporales en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listapedidostemp;
	}
}
