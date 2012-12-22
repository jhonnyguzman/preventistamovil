package com.preventista.entidades;

import java.text.DecimalFormat;

import com.preventista.main.PreRes;

public class Pedidosdetalletemp {
	
	private int _id;
	private int pedidostemp_id;
	private int articulos_id;
	private String articulos_descripcion;
	private int articulos_stockreal;
	private int cantidad;
	private float montoacordado = 0;
	private float subtotal;
	private float pv;
	private String created_at;
	private String updated_at;
	private PreRes preres = new PreRes();
	
	public int get_id() {
		return _id;
	}
	
	public void set_id(int _id) {
		this._id = _id;
	}
	
	public int getPedidostemp_id() {
		return pedidostemp_id;
	}
	
	public void setPedidostemp_id(int pedidostemp_id) {
		this.pedidostemp_id = pedidostemp_id;
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
		this.montoacordado = montoacordado;
	}
	
	public float getSubtotal() {
		return subtotal;
	}
	
	public void setSubtotal(float subtotal) {
		this.subtotal = subtotal;
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
