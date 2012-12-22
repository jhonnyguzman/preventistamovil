package com.preventista.main;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.preventista.db.DBCuentaCorrienteAdapter;
import com.preventista.entidades.CuentaCorriente;

import android.content.Context;
import android.net.ParseException;
import android.util.Log;

/**
 * @author johnny
 *
 */
public class PreRes {
	
	public String md5(String string) {
	    byte[] hash;

	    try {
	        hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
	    } catch (NoSuchAlgorithmException e) {
	        throw new RuntimeException("Huh, MD5 should be supported?", e);
	    } catch (UnsupportedEncodingException e) {
	        throw new RuntimeException("Huh, UTF-8 should be supported?", e);
	    }

	    StringBuilder hex = new StringBuilder(hash.length * 2);
	    for (byte b : hash) {
	        if ((b & 0xFF) < 0x10) hex.append("0");
	        hex.append(Integer.toHexString(b & 0xFF));
	    }
	    return hex.toString();
	}
	
	
	public float getPrecioCompra(float pc, float p)
	{
		return pc + ((pc * p) / 100);
	}
	
	
	public Date stringToDate(String fecha) throws java.text.ParseException 
	{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy"); 
		Date d = null;
		try {
			 d = format.parse(fecha);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return d;
	}
	
	
	public String formatDateToBD(Date fecha)
	{
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd"); 
		String newdate = format2.format(fecha);
		return newdate;
	}
	
	public String formatDateToBDWithTime(Date fecha)
	{
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		String newdate = format2.format(fecha);
		return newdate; 
	}
	
	public String sToDateForShow(String fecha) throws java.text.ParseException
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date d = null;
		try {
			 d = format.parse(fecha);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy"); 
		String newdate = format2.format(d);
		
		return newdate;
	}
	
	/**
	 * Esta función formatéa un string numérico con coma flotante a un número flotante
	 * Por ejemplo dado el string: 7.525,20 devolverá: 7525.20
	 * */
	public float forStrToFloat(String numeroFlotante)
	{
		float valor = 0;
		DecimalFormat formateador = new DecimalFormat("###.##");
		 try
		 {
		    // parse() lanza una ParseException en caso de fallo que hay
		    // que capturar.
		    Number numero = formateador.parse(numeroFlotante);
		    valor = numero.floatValue();
		    // Estas dos líneas se puede abreviar con
		    // double valor = formateador.parse(texto).doubleValue();
		    

		 } catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			Log.d("ERROR CONVERSION NUMERO: ", "Error al convertir numero flotante.");
		}
		
		return valor;
	}
	
	
	/**
	 * Esta función formatéa un número de coma flotante
	 * Por ejemplo dado el número: 7.525,20 devolverá: 7525.20
	 * */
	public float formatearFloat(float numero)
	{
		DecimalFormat formateador = new DecimalFormat("####.##");
		return Float.parseFloat(formateador.format(numero));
	}
	
	public double roundDouble(Double num){
		 DecimalFormat df = new DecimalFormat("####.##");
	     return Double.parseDouble(df.format(num));
	}
	
	
	public void updateEstadoContable(int clientes_id, double haber, double debe, Context context)
	{
		CuentaCorriente cuentacorriente = new CuentaCorriente();
		cuentacorriente.setClientes_id(clientes_id);
		cuentacorriente.setHaber(haber);
		cuentacorriente.setDebe(debe);
		
		DBCuentaCorrienteAdapter dbcc = new DBCuentaCorrienteAdapter(context);
		dbcc.editCuentaCorriente(cuentacorriente);
	}
}
