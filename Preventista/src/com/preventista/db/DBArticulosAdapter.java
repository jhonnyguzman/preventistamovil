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

import com.preventista.entidades.Articulos;
import com.preventista.extras.WriteLog;

public class DBArticulosAdapter {
	
	private static final String DATABASE_TABLE = "articulos";
	private  SQLiteDatabase db;
	private  DataBaseHelper myDbHelper;
	
	Context contexto;
	
	private WriteLog wl = new WriteLog();
	
	public DBArticulosAdapter(Context ctx)
	{
		this.contexto = ctx;
		this.myDbHelper = new DataBaseHelper(this.contexto);
        try {
        	 
        	this.myDbHelper.createDataBase();
 
	 	} catch (IOException ioe) {
	 
	 		throw new Error("Unable to create database");
	 
	 	}	
	}
	
	
	public boolean addArticulo(Articulos articulo)
	{	
		ContentValues valores = new ContentValues();
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();
		
		valores.put("_id", articulo.get_id());
		valores.put("descripcion", articulo.getDescripcion());
		valores.put("preciocompra", articulo.getPreciocompra());
		valores.put("stockreal", articulo.getStockreal());
		valores.put("rubros_id", articulo.getRubros_id());
		valores.put("precio1", articulo.getPrecio1());
		valores.put("precio2", articulo.getPrecio2());
		valores.put("precio3", articulo.getPrecio3());
		valores.put("porcentaje1", articulo.getPorcentaje1());
		valores.put("porcentaje2", articulo.getPorcentaje2());
		valores.put("porcentaje3", articulo.getPorcentaje3());
		valores.put("estado", 20); //estado disponible segun tabgral
		valores.put("marcas_id", articulo.getMarcas_id());	
		valores.put("updated_at", dateFormat.format(date));
		
		
		long rowsid = db.insert(DATABASE_TABLE, null, valores);
		if(rowsid != -1){
			wl.wAddArticulo(articulo,rowsid, contexto);
			estado =  true;
		}else estado = false;
		
		this.myDbHelper.close();
		return estado;
	}
	
	
	public boolean editArticulo(Articulos articulo)
	{	
		ContentValues valores = new ContentValues();
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();
		
		String sql = "UPDATE "+ DATABASE_TABLE +" SET ";
		
		if(articulo.getDescripcion() != null){
			valores.put("descripcion", articulo.getDescripcion());
			sql = sql + "articulos_descripcion = '" + articulo.getDescripcion() + "', ";
		}

		valores.put("preciocompra", articulo.getPreciocompra());
		sql = sql + "articulos_preciocompra = " + articulo.getPreciocompra() + ", ";
	
		valores.put("stockreal", articulo.getStockreal());
		sql = sql + "articulos_stockreal = " + articulo.getStockreal() + ", ";
		
		if(articulo.getRubros_id() != 0){
			valores.put("rubros_id", articulo.getRubros_id());
			sql = sql + "rubros_id = " + articulo.getRubros_id() + ", ";
		}
		if(articulo.getMarcas_id() != 0){
			valores.put("marcas_id", articulo.getMarcas_id());
			sql = sql + "marcas_id = " + articulo.getMarcas_id() + ", ";
		}
	
		valores.put("porcentaje1", articulo.getPorcentaje1());
		sql = sql + "articulos_porcentaje1 = " + articulo.getPorcentaje1() + ", ";
	
		valores.put("porcentaje2", articulo.getPorcentaje2());
		sql = sql + "articulos_porcentaje2 = " + articulo.getPorcentaje2() + ", ";
	
		valores.put("porcentaje3", articulo.getPorcentaje3());
		sql = sql + "articulos_porcentaje3 = " + articulo.getPorcentaje3() + ", ";

		valores.put("precio1", articulo.getPrecio1());
		sql = sql + "articulos_precio1 = " + articulo.getPrecio1() + ", ";

		valores.put("precio2", articulo.getPrecio2());
		sql = sql + "articulos_precio2 = " + articulo.getPrecio2() + ", ";
		
		valores.put("precio3", articulo.getPrecio3());
		sql = sql + "articulos_precio2 = " + articulo.getPrecio3() + ", ";
		
		
		valores.put("updated_at", dateFormat.format(date));
		sql = sql + "articulos_updated_at = '" + dateFormat.format(date) + "' ";
		
		String where = "_id = " + articulo.get_id();
		sql = sql + "WHERE articulos_id = " + articulo.get_id() + ";\n";
		
		int affectedrows = db.update(DATABASE_TABLE, valores, where,null);
		
		if(affectedrows == 1){
			wl.wEditArticulo(sql, this.contexto);
			estado = true;
		}else estado = false;
		
		myDbHelper.close();
		return estado;
	}
	
	public ArrayList<Articulos> getArticulos(String descripcion)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		ArrayList<Articulos> listaarticulos = new ArrayList<Articulos>();
		
		//seleccion de solo articulos con estado 'disponible'
		Cursor c = db.rawQuery("SELECT _id,descripcion,preciocompra,stockreal,stockmin,stockmax,rubros_id,observaciones, " +
				"precio1, precio2, precio3, porcentaje1, porcentaje2, porcentaje3, estado, marcas_id, created_at, " +
				"updated_at FROM articulos where estado = 20 and descripcion LIKE '"+descripcion+"%' ORDER BY descripcion",null);
		
		if (c.moveToFirst()) {
		     do {
		    	 Articulos articulo = new Articulos();
		 		
		    	 articulo.set_id(c.getInt(0));
		    	  articulo.setDescripcion(c.getString(1));
		    	  articulo.setPreciocompra(c.getFloat(2));
		    	  articulo.setStockreal(c.getInt(3));
		    	  articulo.setStockmin(c.getInt(4));
		    	  articulo.setStockmax(c.getInt(5));
		    	  articulo.setRubros_id(c.getInt(6));
		    	  articulo.setObservaciones(c.getString(7));
		    	  articulo.setPrecio1(c.getFloat(8));
		    	  articulo.setPrecio2(c.getFloat(9));
		    	  articulo.setPrecio3(c.getFloat(10));
		    	  articulo.setPorcentaje1(c.getFloat(11));
		    	  articulo.setPorcentaje2(c.getFloat(12));
		    	  articulo.setPorcentaje3(c.getFloat(13));
		    	  articulo.setEstado(c.getInt(14));
		    	  articulo.setMarcas_id(c.getInt(15));
		    	  articulo.setCreated_at(c.getString(16));
		    	  articulo.setUpdated_at(c.getString(17));
		    	  	
		    	  listaarticulos.add(articulo);
		    	 
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No hay Articulos en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listaarticulos;
	}
	
	
	public Articulos getArticulosById(int _id)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		Articulos articulo = new Articulos();
		
		//seleccion de solo articulos con estado 'disponible'
		Cursor c = db.rawQuery("SELECT _id,descripcion,preciocompra,stockreal,stockmin,stockmax,rubros_id,observaciones, " +
				"precio1, precio2, precio3, porcentaje1, porcentaje2, porcentaje3, estado, marcas_id, created_at, " +
				"updated_at FROM articulos where estado = 20 and _id = "+_id,null);
		
		if (c.moveToFirst()) {
		    	 articulo.set_id(c.getInt(0));
		    	  articulo.setDescripcion(c.getString(1));
		    	  articulo.setPreciocompra(c.getFloat(2));
		    	  articulo.setStockreal(c.getInt(3));
		    	  articulo.setStockmin(c.getInt(4));
		    	  articulo.setStockmax(c.getInt(5));
		    	  articulo.setRubros_id(c.getInt(6));
		    	  articulo.setObservaciones(c.getString(7));
		    	  articulo.setPrecio1(c.getFloat(8));
		    	  articulo.setPrecio2(c.getFloat(9));
		    	  articulo.setPrecio3(c.getFloat(10));
		    	  articulo.setPorcentaje1(c.getFloat(11));
		    	  articulo.setPorcentaje2(c.getFloat(12));
		    	  articulo.setPorcentaje3(c.getFloat(13));
		    	  articulo.setEstado(c.getInt(14));
		    	  articulo.setMarcas_id(c.getInt(15));
		    	  articulo.setCreated_at(c.getString(16));
		    	  articulo.setUpdated_at(c.getString(17));

		}else{
			Log.d("REGISTRO_FAIL", "No hay Articulos en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return articulo;
	}
	
	
	public ArrayList<Articulos> getAllArticulos()
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		ArrayList<Articulos> listaarticulos = new ArrayList<Articulos>();
		 
		//seleccion de solo articulos con estado 'disponible'
		String[] campos = new String[]{"_id","descripcion","preciocompra","stockreal","stockmin","stockmax",
				"rubros_id","observaciones", "precio1","precio2","precio3","porcentaje1","porcentaje2",
				"porcentaje3","estado","marcas_id","created_at","updated_at"};
		
		Cursor c = db.query(DATABASE_TABLE, campos, "estado=20", null, null, null, "descripcion ASC");
		if (c.moveToFirst()) {
		     do {
		    	 Articulos articulo = new Articulos();
		 		
		    	  articulo.set_id(c.getInt(0));
		    	  articulo.setDescripcion(c.getString(1));
		    	  articulo.setPreciocompra(c.getFloat(2));
		    	  articulo.setStockreal(c.getInt(3));
		    	  articulo.setStockmin(c.getInt(4));
		    	  articulo.setStockmax(c.getInt(5));
		    	  articulo.setRubros_id(c.getInt(6));
		    	  articulo.setObservaciones(c.getString(7));
		    	  articulo.setPrecio1(c.getFloat(8));
		    	  articulo.setPrecio2(c.getFloat(9));
		    	  articulo.setPrecio3(c.getFloat(10));
		    	  articulo.setPorcentaje1(c.getFloat(11));
		    	  articulo.setPorcentaje2(c.getFloat(12));
		    	  articulo.setPorcentaje3(c.getFloat(13));
		    	  articulo.setEstado(c.getInt(14));
		    	  articulo.setMarcas_id(c.getInt(15));
		    	  articulo.setCreated_at(c.getString(16));
		    	  articulo.setUpdated_at(c.getString(17));
		    	  	
		    	 listaarticulos.add(articulo);
		     } while(c.moveToNext());
		}else{
			Log.d("REGISTRO_FAIL", "No hay Articulos en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return listaarticulos;
	}
	
	public boolean updateStock(Articulos articulo, int cantidad)
	{
		ContentValues valores = new ContentValues();
		boolean estado = false;
		
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();
		
		String sql = "UPDATE "+ DATABASE_TABLE +" SET ";
		
		valores.put("stockreal", articulo.getStockreal() - cantidad );
		sql = sql + "articulos_stockreal = " + (articulo.getStockreal() - cantidad) + ", ";
	
		valores.put("updated_at", dateFormat.format(date));
		sql = sql + "articulos_updated_at = '" + dateFormat.format(date) + "' ";
		
		String where = "_id = " + articulo.get_id();
		sql = sql + "WHERE articulos_id = " + articulo.get_id() + ";\n";
		
		int affectedrows = db.update(DATABASE_TABLE, valores, where,null);
		
		if(affectedrows == 1){
			wl.wEditArticulo(sql,this.contexto);
			estado = true;
		}else estado = false; 
		
		myDbHelper.close();
		return estado;
	}
	
	public boolean checkStateStock(int _id, int cantidad)
	{
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		boolean estado = false;
		
		Cursor c = db.rawQuery("SELECT _id,stockreal FROM articulos where _id = "+_id+"",null);
		
		if (c.moveToFirst()) {
	    	 if(cantidad <= c.getInt(1)){
	    		 estado = true;
	    	 }
		}else{
			Log.d("REGISTRO_FAIL", "No hay Articulos en la base de datos");
		}
		
		c.close();
		this.myDbHelper.close();
		return estado;
	}
	
	public boolean increaseStock(Articulos articulo, int cantidad)
	{
		ContentValues valores = new ContentValues();
		boolean estado = false;
		
		this.myDbHelper.openDataBase();
		db = this.myDbHelper.getDB();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();
			
		valores.put("stockreal", articulo.getStockreal() + cantidad );
		//sql = sql + "apellido = '" + cliente.getApellido() + "', ";
	
		valores.put("updated_at", dateFormat.format(date));
		//sql = sql + "updated_at = '" + cliente.getUpdated_at() + "', ";
		
		String where = "_id = " + articulo.get_id();
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
}
