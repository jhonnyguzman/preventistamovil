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

import com.preventista.entidades.Pedidosdetalle;
import com.preventista.extras.WriteLog;

public class DBPedidosdetalleAdapter {

	private static final String DATABASE_TABLE = "pedidodetalle";
	private  SQLiteDatabase db;
	private  DataBaseHelper myDbHelper;
	
	Context contexto;
	private WriteLog wl = new WriteLog();
	
	public DBPedidosdetalleAdapter(Context ctx)
	{
		this.contexto = ctx;
		this.myDbHelper = new DataBaseHelper(this.contexto);
        try {
        	 
        	this.myDbHelper.createDataBase();
 
	 	} catch (IOException ioe) {
	 
	 		throw new Error("Unable to create database");
	 
	 	}	
	}
	
	
	public boolean addPedidodetalle(Pedidosdetalle pedidodetalle)
	{	
		ContentValues valores = new ContentValues();
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();
		
		valores.put("_id", pedidodetalle.get_id());
		valores.put("pedidos_id", pedidodetalle.getPedidos_id());
		valores.put("articulos_id", pedidodetalle.getArticulos_id());
		valores.put("cantidad", pedidodetalle.getCantidad());
		valores.put("montoacordado", pedidodetalle.getMontoacordado());
		valores.put("subtotal", pedidodetalle.getSubtotal());
		valores.put("pv", pedidodetalle.getPv());
		
		valores.put("updated_at", dateFormat.format(date));
		
		long rowsid = db.insert(DATABASE_TABLE, null, valores);
		if(rowsid != -1){
			wl.wAddPedidoDetalle(pedidodetalle,rowsid,this.contexto);
			estado =  true;
		}else estado = false;
		
		this.myDbHelper.close();
		return estado;
	}
	
	
	public boolean editPedidodetalle(Pedidosdetalle pedidodetalle)
	{	
		ContentValues valores = new ContentValues();
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();
		
		String sql = "UPDATE "+ DATABASE_TABLE +" SET ";
		
		if(pedidodetalle.getPedidos_id() != 0){
			valores.put("pedidos_id", pedidodetalle.getPedidos_id());
			sql = sql + "pedidos_id = " + pedidodetalle.getPedidos_id() + ", ";
		}
		if(pedidodetalle.getArticulos_id() != 0){
			valores.put("articulos_id", pedidodetalle.getArticulos_id());
			sql = sql + "articulos_id= " + pedidodetalle.getArticulos_id() + ", ";
		}
		if(pedidodetalle.getCantidad() != 0){
			valores.put("cantidad", pedidodetalle.getCantidad());
			sql = sql + "pedidodetalle_cantidad = " + pedidodetalle.getCantidad() + ", ";
		}
		if(pedidodetalle.getMontoacordado() != 0){
			valores.put("montoacordado", pedidodetalle.getMontoacordado());
			sql = sql + "pedidodetalle_montoacordado = " + pedidodetalle.getMontoacordado() + ", ";
		}
		if(pedidodetalle.getSubtotal() != 0){
			valores.put("subtotal", pedidodetalle.getSubtotal());
			sql = sql + "pedidodetalle_subtotal = " + pedidodetalle.getSubtotal() + ", ";
		}
		
		if(pedidodetalle.getPv() != 0){
			valores.put("pv", pedidodetalle.getPv());
			sql = sql + "pedidodetalle_pv = " + pedidodetalle.getPv() + ", ";
		}
		
		valores.put("updated_at", dateFormat.format(date));
		sql = sql + "pedidodetalle_updated_at = '" + dateFormat.format(date) + "' ";
		
		
		String where = "_id = " + pedidodetalle.get_id();
		sql = sql + "WHERE pedidodetalle_id = " + pedidodetalle.get_id() + ";\n";
		
		int affectedrows = db.update(DATABASE_TABLE, valores, where,null);
		
		if(affectedrows == 1){
			wl.wEditPedidoDetalle(sql,this.contexto);
			estado = true;
		}else estado = false;
		
		myDbHelper.close();
		return estado;
	}
	
	
	/*public boolean deleteAllPedidodetalle()
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = true;
		
		db.delete(DATABASE_TABLE, null,null);
	
		myDbHelper.close();
		return estado;
	}*/
	
	
	public boolean deletePedidodetalle(int _id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = false;
		
		int cant = db.delete(DATABASE_TABLE, "_id="+_id+"",null);
		if(cant == 1){
			String sql = "DELETE FROM "+ DATABASE_TABLE +" WHERE pedidodetalle_id = " + _id;
			wl.wDeletePedidoDetalle(sql,this.contexto);
			estado = true;
		}
		
		myDbHelper.close();
		return estado;
	}
	
	
	public ArrayList<Pedidosdetalle> getPedidodetalle(int _id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		
		///String[] campos = new String[]{"_id","nombre","apellido","direccion","telefono","created_at","updated_at","clientecategoria_id"};
		//String[] args = new String[]{apellido};
		ArrayList<Pedidosdetalle> listapedidodetalle = new ArrayList<Pedidosdetalle>();
		
		//Log.d("TAG_PARAMETRO", apellido);
		//Cursor c = myDataBase.query("choferes", campos, "apellido=?",args, null, null, null);
		
		Cursor c = db.rawQuery("SELECT pd.* FROM pedidodetalle as pd where pd._id = "+_id+"",null);
		
		if (c.moveToFirst()) {
		     //Recorremos el cursor hasta que no haya más registros
		     do {
		    	 Pedidosdetalle pedidodetalle = new Pedidosdetalle();
		 		
		    	 pedidodetalle.set_id(c.getInt(0));
		    	 pedidodetalle.setPedidos_id(c.getInt(1));
		    	 pedidodetalle.setArticulos_id(c.getInt(2));
		    	 pedidodetalle.setCantidad(c.getInt(3));
		    	 pedidodetalle.setMontoacordado(c.getInt(4)); 
		    	 pedidodetalle.setSubtotal(c.getFloat(5));
		    	 pedidodetalle.setPv(c.getFloat(6));
		    	 pedidodetalle.setCreated_at(c.getString(7));
		    	 pedidodetalle.setUpdated_at(c.getString(8));
		    	  
		    	 listapedidodetalle.add(pedidodetalle);
		    	 
		    	 //System.out.println(c.getString(2));
		    	 //Log.d("REGISTRO_OK", c.getString(2));
		    	  
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No hay Lineas de Pedidos en la base de datos");
		} 
		
		c.close();
		this.myDbHelper.close();
		return listapedidodetalle;
	}
	
	
	public ArrayList<Pedidosdetalle> getByPedidoId(int pedidos_id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();

		ArrayList<Pedidosdetalle> listapedidodetalle = new ArrayList<Pedidosdetalle>();
		
		Cursor c = db.rawQuery("SELECT pd.*, a.descripcion, a.stockreal FROM pedidodetalle as pd INNER JOIN articulos as a ON a._id = pd.articulos_id where pd.pedidos_id = "+pedidos_id+"",null);
		
		if (c.moveToFirst()) {
		     do {
		    	 Pedidosdetalle pedidodetalle = new Pedidosdetalle();
		 		
		    	 pedidodetalle.set_id(c.getInt(0));
		    	 pedidodetalle.setPedidos_id(c.getInt(1));
		    	 pedidodetalle.setArticulos_id(c.getInt(2));
		    	 pedidodetalle.setCantidad(c.getInt(3));
		    	 pedidodetalle.setMontoacordado(c.getInt(4));
		    	 pedidodetalle.setSubtotal(c.getFloat(5));
		    	 pedidodetalle.setPv(c.getFloat(6));
		    	 pedidodetalle.setCreated_at(c.getString(7));
		    	 pedidodetalle.setUpdated_at(c.getString(8));
		    	 pedidodetalle.setArticulos_descripcion(c.getString(9));
		    	 pedidodetalle.setArticulos_stockreal(c.getInt(10));
		    	  
		    	 listapedidodetalle.add(pedidodetalle);
		    	  
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No hay Lineas de Pedidos en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listapedidodetalle;
	}
	
	
	public ArrayList<Pedidosdetalle> getAllPedidodetalle()
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		ArrayList<Pedidosdetalle> listapedidodetalle = new ArrayList<Pedidosdetalle>();
		
		//String[] campos = new String[]{"_id","nombre","apellido","direccion","telefono","created_at","updated_at","clientescategoria_id"};
		
		//Cursor c = db.query(DATABASE_TABLE, campos, null, null, null, null, null);
		Cursor c = db.rawQuery("SELECT pd.*, a.descripcion, a.stockreal FROM pedidodetalle as pd INNER JOIN articulos as a ON a._id = pd.articulos_id",null);
		
		if (c.moveToFirst()) {
		     do {
		    	 Pedidosdetalle pedidodetalle = new Pedidosdetalle();
			 		
		    	 pedidodetalle.set_id(c.getInt(0));
		    	 pedidodetalle.setPedidos_id(c.getInt(1));
		    	 pedidodetalle.setArticulos_id(c.getInt(2));
		    	 pedidodetalle.setCantidad(c.getInt(3));
		    	 pedidodetalle.setMontoacordado(c.getInt(4));
		    	 pedidodetalle.setSubtotal(c.getFloat(5));
		    	 pedidodetalle.setPv(c.getFloat(6));
		    	 pedidodetalle.setCreated_at(c.getString(7));
		    	 pedidodetalle.setUpdated_at(c.getString(8));
		    	 pedidodetalle.setArticulos_descripcion(c.getString(9));
		    	 pedidodetalle.setArticulos_stockreal(c.getInt(10));
		    	  
		    	 listapedidodetalle.add(pedidodetalle);
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No hay Lineas de pedidos en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listapedidodetalle;
	}
}
