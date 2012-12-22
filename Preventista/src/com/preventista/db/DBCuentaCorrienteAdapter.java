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

import com.preventista.entidades.CuentaCorriente;
import com.preventista.extras.WriteLog;

public class DBCuentaCorrienteAdapter {

	private static final String DATABASE_TABLE = "cuentacorriente";
	private  SQLiteDatabase db;
	private  DataBaseHelper myDbHelper;
	
	Context contexto;
	private WriteLog wl = new WriteLog();
	
	public DBCuentaCorrienteAdapter(Context ctx)
	{
		this.contexto = ctx;
		this.myDbHelper = new DataBaseHelper(this.contexto);
        try {
        	 
        	this.myDbHelper.createDataBase();
 
	 	} catch (IOException ioe) {
	 
	 		throw new Error("Unable to create database");
	 
	 	}	
	}
	
	
	public boolean addCuentaCorriente(CuentaCorriente cuentacorriente)
	{	
		ContentValues valores = new ContentValues();
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();
		
		valores.put("_id", cuentacorriente.get_id());
		valores.put("clientes_id", cuentacorriente.getClientes_id());
		valores.put("haber", cuentacorriente.getHaber());
		valores.put("debe", cuentacorriente.getDebe());
		valores.put("saldo", cuentacorriente.getSaldo());
		valores.put("updated_at", dateFormat.format(date));
		
		long rowsid = db.insert(DATABASE_TABLE, null, valores);
		if(rowsid != -1){
			wl.wAddCuentaCorriente(cuentacorriente,rowsid,this.contexto);
			estado =  true;
		}else estado = false;
		
		this.myDbHelper.close();
		return estado;
	}
	
	
	public boolean editCuentaCorriente(CuentaCorriente cuentacorriente)
	{	
		ContentValues valores = new ContentValues();
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();
		
		String sql = "UPDATE "+ DATABASE_TABLE +" SET ";
		
		/*if(cuentacorriente.getClientes_id() != 0){
			valores.put("clientes_id", cuentacorriente.getClientes_id());
			sql = sql + "clientes_id = " + cuentacorriente.getClientes_id() + ", ";
		}*/
		
		valores.put("haber", cuentacorriente.getHaber());
		sql = sql + "cuentacorriente_haber= " + cuentacorriente.getHaber() + ", ";
	
		valores.put("debe", cuentacorriente.getDebe());
		sql = sql + "cuentacorriente_debe = " + cuentacorriente.getDebe() + ", ";
			
		valores.put("saldo", cuentacorriente.getSaldo());
		sql = sql + "cuentacorriente_saldo = " + cuentacorriente.getSaldo() + ", ";
	
		valores.put("updated_at", dateFormat.format(date));
		sql = sql + "cuentacorriente_updated_at = '" + dateFormat.format(date) + "' ";
		
		
		String where = "clientes_id = " + cuentacorriente.getClientes_id();
		sql = sql + "WHERE clientes_id = " + cuentacorriente.getClientes_id() + ";\n";
		
		int affectedrows = db.update(DATABASE_TABLE, valores, where,null);
		
		if(affectedrows == 1){
			wl.wEditCuentaCorriente(sql,this.contexto);
			estado = true;
		}else estado = false;
		
		myDbHelper.close();
		return estado;
	}
	
	
	public CuentaCorriente getCuentaCorriente(int clientes_id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		CuentaCorriente cuentacorriente = new CuentaCorriente();
		Cursor c = db.rawQuery("SELECT * FROM cuentacorriente WHERE clientes_id = " + clientes_id,null);
		
		if (c.moveToFirst()) {
	    	 cuentacorriente.set_id(c.getInt(0));
	    	 cuentacorriente.setClientes_id(c.getInt(1));
	    	 cuentacorriente.setHaber(c.getDouble(2));
	    	 cuentacorriente.setDebe(c.getDouble(3));
	    	 cuentacorriente.setSaldo(c.getDouble(4));
	    	 cuentacorriente.setCreated_at(c.getString(5));
	    	 cuentacorriente.setUpdated_at(c.getString(6));
		    	  	
		}else{
			Log.d("REGISTRO_FAIL", "No existe la cuenta corriente con el id dado");
		}
		
		c.close();
		this.myDbHelper.close();
		return cuentacorriente;
	}
	
	
	public ArrayList<CuentaCorriente> getAllCuentasCorrientes()
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		ArrayList<CuentaCorriente> listacuentacorriente = new ArrayList<CuentaCorriente>();
		Cursor c = db.rawQuery("SELECT * FROM cuentacorriente",null);
		
		if (c.moveToFirst()) {
		     do {
		    	 CuentaCorriente cuentacorriente = new CuentaCorriente();
		 		
		    	 cuentacorriente.set_id(c.getInt(0));
		    	 cuentacorriente.setClientes_id(c.getInt(1));
		    	 cuentacorriente.setHaber(c.getInt(2));
		    	 cuentacorriente.setDebe(c.getInt(3));
		    	 cuentacorriente.setSaldo(c.getInt(4));
		    	 cuentacorriente.setCreated_at(c.getString(5));
		    	 cuentacorriente.setUpdated_at(c.getString(6));
		    	  	
		    	 listacuentacorriente.add(cuentacorriente);
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No hay cuentas corrientes en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listacuentacorriente;
	}
}
