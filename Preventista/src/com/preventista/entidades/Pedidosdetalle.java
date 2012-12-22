package com.preventista.entidades;

import com.preventista.main.PreRes;

public class Pedidosdetalle {

	private int _id;
	private int pedidos_id;
	private int articulos_id;
	private String articulos_descripcion;
	private int articulos_stockreal;
	private int cantidad;
	private float montoacordado = 0;
	private float subtotal;
	private float pv;
	private String created_at;
	private String updated_at;
	PreRes preres = new PreRes();
	
	public int get_id() {
		return _id;
	}
	
	public void set_id(int _id) {
		this._id = _id;
	}
	
	public int getPedidos_id() {
		return pedidos_id;
	}
	
	public void setPedidos_id(int pedidos_id) {
		this.pedidos_id = pedidos_id;
	}
	
	public int getArticulos_id() {
		return articulos_id;
	}
	
	public void setArticulos_id(int articulos_id) {
		this.articulos_id = articulos_id;
	}
	
	public String getArticulos_descripcion() {
		return articulos_descripcion;
	}
	
	public void setArticulos_descripcion(String articulos_descripcion) {
		this.articulos_descripcion = articulos_descripcion;
	}
	
	public int getArticulos_stockreal() {
		return articulos_stockreal;
	}
	
	public void setArticulos_stockreal(int articulos_stockreal) {
		this.articulos_stockreal= articulos_stockreal;
	}
	
	public int getCantidad() {
		return cantidad;
	}
	
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	
	public float getMontoacordado() {
		return montoacordado;
	}
	
	public void setMontoacordado(float montoacordado) {
		this.montoacordado = preres.formatearFloat(montoacordado);
	}
	
	public float getSubtotal() {
		return subtotal;
	}
	
	public void setSubtotal(float subtotal) {
		//DecimalFormat dec = new DecimalFormat("####.##");
		//NumberFormat formatter = NumberFormat.getInstance(Locale.US);
		this.subtotal = preres.formatearFloat(subtotal);
	}
	
	public float getPv() {
		return pv;
	}
	
	public void setPv(float pv) {
		this.pv = pv;
	}
	
	public String getCreated_at() {
		return created_at;
	}
	
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	
	public String getUpdated_at() {
		return updated_at;
	}
	
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
}
