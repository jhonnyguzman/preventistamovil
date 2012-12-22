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

import com.preventista.entidades.Pedidosdetalletemp;

public class DBPedidosdetalleTempAdapter {

	private static final String DATABASE_TABLE = "pedidodetalletemp";
	private  SQLiteDatabase db;
	private  DataBaseHelper myDbHelper;
	
	public DBPedidosdetalleTempAdapter(Context ctx)
	{
		this.myDbHelper = new DataBaseHelper(ctx);
        try {
        	 
        	this.myDbHelper.createDataBase();
 
	 	} catch (IOException ioe) {
	 
	 		throw new Error("Unable to create database");
	 
	 	}	
	}
	
	
	public boolean addPedidodetalletemp(Pedidosdetalletemp pedidodetalletemp)
	{	
		ContentValues valores = new ContentValues();
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss"); 
		Date date = new Date();
		
		valores.put("pedidostemp_id", pedidodetalletemp.getPedidostemp_id());
		valores.put("articulos_id", pedidodetalletemp.getArticulos_id());
		valores.put("cantidad", pedidodetalletemp.getCantidad());
		valores.put("montoacordado", pedidodetalletemp.getMontoacordado());
		valores.put("subtotal", pedidodetalletemp.getSubtotal());
		valores.put("pv", pedidodetalletemp.getPv());
		
		//valores.put("created_at", cliente.getCreated_at());
		valores.put("updated_at", dateFormat.format(date));
		
		long rowsid = db.insert(DATABASE_TABLE, null, valores);
		if(rowsid != -1){
			estado =  true;
		}else estado = false;
		
		this.myDbHelper.close();
		return estado;
	}
	
	
	public boolean editPedidodetalletemp(Pedidosdetalletemp pedidodetalletemp)
	{	
		ContentValues valores = new ContentValues();
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss"); 
		Date date = new Date();
		
		//String sql = "UPDATE "+ DATABASE_TABLE +" SET ";
		
		if(pedidodetalletemp.getPedidostemp_id() != 0){
			valores.put("montotal", pedidodetalletemp.getPedidostemp_id());
			//sql = sql + "nombre = '" + cliente.getNombre() + "', ";
		}
		if(pedidodetalletemp.getArticulos_id() != 0){
			valores.put("montoadeudado", pedidodetalletemp.getArticulos_id());
			//sql = sql + "apellido = '" + cliente.getApellido() + "', ";
		}
		if(pedidodetalletemp.getCantidad() != 0){
			valores.put("cantidad", pedidodetalletemp.getCantidad());
			//sql = sql + "direccion = '" + cliente.getDireccion() + "', ";
		}
		if(pedidodetalletemp.getMontoacordado() != 0){
			valores.put("montoacordado", pedidodetalletemp.getMontoacordado());
			//sql = sql + "telefono = '" + cliente.getTelefono() + "', ";
		}
		if(pedidodetalletemp.getSubtotal() != 0){
			valores.put("subtotal", pedidodetalletemp.getSubtotal());
			//sql = sql + "created_at = '" + cliente.getCreated_at() + "', ";
		}
		
		if(pedidodetalletemp.getPv() != 0){
			valores.put("pv", pedidodetalletemp.getPv());
			//sql = sql + "created_at = '" + cliente.getCreated_at() + "', ";
		}
		
		valores.put("updated_at", dateFormat.format(date));
		//sql = sql + "updated_at = '" + cliente.getUpdated_at() + "', ";
		
		
		String where = "_id = " + pedidodetalletemp.get_id();
		//sql = sql + "WHERE _id = " + cliente.get_id() + ";\n";
		
		int affectedrows = db.update(DATABASE_TABLE, valores, where,null);
		
		if(affectedrows == 1){
			estado = true;
		}else estado = false;
		
		myDbHelper.close();
		return estado;
	}
	
	
	public boolean deleteAllPedidodetalletemp()
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = true;
		
		db.delete(DATABASE_TABLE, null,null);
	
		myDbHelper.close();
		return estado;
	}
	
	
	public boolean deletePedidodetalletemp(int _id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = false;
		
		int cant = db.delete(DATABASE_TABLE, "_id="+_id+"",null);
		if(cant == 1) estado = true;
		
		myDbHelper.close();
		return estado;
	}
	
	public ArrayList<Pedidosdetalletemp> getPedidodetalletemp(int _id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		
		///String[] campos = new String[]{"_id","nombre","apellido","direccion","telefono","created_at","updated_at","clientecategoria_id"};
		//String[] args = new String[]{apellido};
		ArrayList<Pedidosdetalletemp> listapedidodetalletemp = new ArrayList<Pedidosdetalletemp>();
		
		//Log.d("TAG_PARAMETRO", apellido);
		//Cursor c = myDataBase.query("choferes", campos, "apellido=?",args, null, null, null);
		
		Cursor c = db.rawQuery("SELECT pt.* FROM pedidodetalletemp as pt where pt._id = '"+_id+"%'",null);
		
		if (c.moveToFirst()) {
		     //Recorremos el cursor hasta que no haya más registros
		     do {
		    	 Pedidosdetalletemp pedidodetalletemp = new Pedidosdetalletemp();
		 		
		    	 pedidodetalletemp.set_id(c.getInt(0));
		    	 pedidodetalletemp.setPedidostemp_id(c.getInt(1));
		    	 pedidodetalletemp.setArticulos_id(c.getInt(2));
		    	 pedidodetalletemp.setCantidad(c.getInt(3));
		    	 pedidodetalletemp.setMontoacordado(c.getInt(4));
		    	 pedidodetalletemp.setSubtotal(c.getFloat(5));
		    	 pedidodetalletemp.setPv(c.getFloat(6));
		    	 pedidodetalletemp.setCreated_at(c.getString(7));
		    	 pedidodetalletemp.setUpdated_at(c.getString(8));
		    	  
		    	 listapedidodetalletemp.add(pedidodetalletemp);
		    	 
		    	 //System.out.println(c.getString(2));
		    	 //Log.d("REGISTRO_OK", c.getString(2));
		    	  
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No hay Lineas de Pedidos temporales en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listapedidodetalletemp;
	}
	
	
	public ArrayList<Pedidosdetalletemp> getAllPedidodetalletemp()
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		ArrayList<Pedidosdetalletemp> listapedidodetalletemp = new ArrayList<Pedidosdetalletemp>();
		
		//String[] campos = new String[]{"_id","nombre","apellido","direccion","telefono","created_at","updated_at","clientescategoria_id"};
		
		//Cursor c = db.query(DATABASE_TABLE, campos, null, null, null, null, null);
		Cursor c = db.rawQuery("SELECT pdt.*, a.descripcion, a.stockreal FROM pedidodetalletemp as pdt INNER JOIN articulos as a ON a._id = pdt.articulos_id",null);
		
		if (c.moveToFirst()) {
		     do {
		    	 Pedidosdetalletemp pedidodetalletemp = new Pedidosdetalletemp();
			 		
		    	 pedidodetalletemp.set_id(c.getInt(0));
		    	 pedidodetalletemp.setPedidostemp_id(c.getInt(1));
		    	 pedidodetalletemp.setArticulos_id(c.getInt(2));
		    	 pedidodetalletemp.setCantidad(c.getInt(3));
		    	 pedidodetalletemp.setMontoacordado(c.getInt(4));
		    	 pedidodetalletemp.setSubtotal(c.getFloat(5));
		    	 pedidodetalletemp.setPv(c.getFloat(6));
		    	 pedidodetalletemp.setCreated_at(c.getString(7));
		    	 pedidodetalletemp.setUpdated_at(c.getString(8));
		    	 pedidodetalletemp.setArticulos_descripcion(c.getString(9));
		    	 pedidodetalletemp.setArticulos_stockreal(c.getInt(10));
		    	  
		    	 listapedidodetalletemp.add(pedidodetalletemp);
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No hay Lineas de pedidos temporales en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listapedidodetalletemp;
	}
}
