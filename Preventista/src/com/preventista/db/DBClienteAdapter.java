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

import com.preventista.entidades.Clientes;
import com.preventista.extras.WriteLog;

public class DBClienteAdapter {
	
	private static final String DATABASE_TABLE = "clientes";
	private  SQLiteDatabase db;
	private  DataBaseHelper myDbHelper;
	
	Context contexto;
	
	private WriteLog wl = new WriteLog();
	
	public DBClienteAdapter(Context ctx)
	{
		this.contexto = ctx;
		this.myDbHelper = new DataBaseHelper(this.contexto);
        try {
        	 
        	this.myDbHelper.createDataBase();
 
	 	} catch (IOException ioe) {
	 
	 		throw new Error("Unable to create database");
	 
	 	}	
	}
	
	
	public boolean addCliente(Clientes cliente)
	{	
		ContentValues valores = new ContentValues();
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();
		
		valores.put("_id", cliente.get_id());
		valores.put("nombre", cliente.getNombre());
		valores.put("apellido", cliente.getApellido());
		valores.put("direccion", cliente.getDireccion());
		valores.put("telefono", cliente.getTelefono());
		//valores.put("created_at", cliente.getCreated_at());
		valores.put("updated_at", dateFormat.format(date));
		valores.put("clientescategoria_id", cliente.getClientescategoria_id());
		
		long rowsid = db.insert(DATABASE_TABLE, null, valores);
		if(rowsid != -1){
			wl.wAddCliente(cliente,rowsid,this.contexto);
			estado =  true;
		}else estado = false;
		
		this.myDbHelper.close();
		return estado;
	}
	
	
	public boolean editCliente(Clientes cliente)
	{	
		ContentValues valores = new ContentValues();
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();
		
		String sql = "UPDATE "+ DATABASE_TABLE +" SET ";
		
		if(cliente.getNombre() != null){
			valores.put("nombre", cliente.getNombre());
			sql = sql + "clientes_nombre = '" + cliente.getNombre() + "', ";
		}
		if(cliente.getApellido() != null){
			valores.put("apellido", cliente.getApellido());
			sql = sql + "clientes_apellido = '" + cliente.getApellido() + "', ";
		}
		if(cliente.getDireccion() != null){
			valores.put("direccion", cliente.getDireccion());
			sql = sql + "clientes_direccion = '" + cliente.getDireccion() + "', ";
		}
		if(cliente.getTelefono() != null){
			valores.put("telefono", cliente.getTelefono());
			sql = sql + "clientes_telefono = '" + cliente.getTelefono() + "', ";
		}
		
		valores.put("updated_at", dateFormat.format(date));
		sql = sql + "clientes_updated_at = '" + cliente.getUpdated_at() + "', ";
		
		if(cliente.getClientescategoria_id() != 0){
			valores.put("clientescategoria_id", cliente.getClientescategoria_id());
			sql = sql + "clientescategoria_id = '" + cliente.getClientescategoria_id() + "' ";
		}
		
		String where = "_id = " + cliente.get_id();
		sql = sql + "WHERE clientes_id = " + cliente.get_id() + ";\n";
		
		int affectedrows = db.update(DATABASE_TABLE, valores, where,null);
		
		if(affectedrows == 1){
			wl.wEditCliente(sql,this.contexto);
			estado = true;
		}else estado = false;
		
		myDbHelper.close();
		return estado;
	}
	
	
	public ArrayList<Clientes> getClientes(String apellido)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		
		///String[] campos = new String[]{"_id","nombre","apellido","direccion","telefono","created_at","updated_at","clientecategoria_id"};
		//String[] args = new String[]{apellido};
		ArrayList<Clientes> listacliente = new ArrayList<Clientes>();
		
		//Log.d("TAG_PARAMETRO", apellido);
		//Cursor c = myDataBase.query("choferes", campos, "apellido=?",args, null, null, null);
		
		Cursor c = db.rawQuery("SELECT c.*, cc.descripcion " +
				"FROM clientes as c INNER JOIN clientescategoria as cc ON c.clientescategoria_id = cc._id where c.apellido LIKE '"+apellido+"%' ORDER BY c.apellido",null);
		
		if (c.moveToFirst()) {
		     //Recorremos el cursor hasta que no haya más registros
		     do {
		    	 Clientes cliente = new Clientes();
		 		
		    	  cliente.set_id(c.getInt(0));
		    	  cliente.setNombre(c.getString(1));
		    	  cliente.setApellido(c.getString(2));
		    	  cliente.setDireccion(c.getString(3));
		    	  cliente.setTelefono(c.getString(4));
		    	  cliente.setCreated_at(c.getString(5));
		    	  cliente.setUpdated_at(c.getString(6));
		    	  cliente.setClientescategoria_id(c.getInt(7));
		    	  cliente.setCatDescripcion(c.getString(8));
		    	  
		    	 listacliente.add(cliente);
		    	 
		    	 //System.out.println(c.getString(2));
		    	 //Log.d("REGISTRO_OK", c.getString(2));
		    	  
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No hay clientes en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listacliente;
	}
	
	
	public Clientes getById(int _id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		Clientes cliente = new Clientes();
		Cursor c = db.rawQuery("SELECT c.*, cc.descripcion " +
				"FROM clientes as c INNER JOIN clientescategoria as cc ON c.clientescategoria_id = cc._id where c._id = " + _id,null);
		
		if (c.moveToFirst()) {
    	  cliente.set_id(c.getInt(0));
    	  cliente.setNombre(c.getString(1));
    	  cliente.setApellido(c.getString(2));
    	  cliente.setDireccion(c.getString(3));
    	  cliente.setTelefono(c.getString(4));
    	  cliente.setCreated_at(c.getString(5));
    	  cliente.setUpdated_at(c.getString(6));
    	  cliente.setClientescategoria_id(c.getInt(7));
    	  cliente.setCatDescripcion(c.getString(8));
		    	      
		}else{
			Log.d("REGISTRO_FAIL", "No hay clientes en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return cliente;
	}
	
	
	public ArrayList<Clientes> getAllClientes()
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		ArrayList<Clientes> listacliente = new ArrayList<Clientes>();
		
		//String[] campos = new String[]{"_id","nombre","apellido","direccion","telefono","created_at","updated_at","clientescategoria_id"};
		
		//Cursor c = db.query(DATABASE_TABLE, campos, null, null, null, null, null);
		Cursor c = db.rawQuery("SELECT c.*, cc.descripcion " +
				"FROM clientes as c INNER JOIN clientescategoria as cc ON c.clientescategoria_id = cc._id ORDER BY c.apellido",null);
		
		if (c.moveToFirst()) {
		     do {
		    	 Clientes cliente = new Clientes();
		 		
		    	  cliente.set_id(c.getInt(0));
		    	  cliente.setNombre(c.getString(1));
		    	  cliente.setApellido(c.getString(2));
		    	  cliente.setDireccion(c.getString(3));
		    	  cliente.setTelefono(c.getString(4));
		    	  cliente.setCreated_at(c.getString(5));
		    	  cliente.setUpdated_at(c.getString(6));
		    	  cliente.setClientescategoria_id(c.getInt(7));
		    	  cliente.setCatDescripcion(c.getString(8));
		    	  	
		    	 listacliente.add(cliente);
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No hay clientes en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listacliente;
	}
}
