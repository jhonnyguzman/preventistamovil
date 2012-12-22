package com.preventista.entidades;

import com.preventista.main.PreRes;

public class PagosPedidos {
	
	private int _id;
	private int pedidos_id;
	private int pagos_id;
	private double montocubierto;
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
	public int getPagos_id() {
		return pagos_id;
	}
	public void setPagos_id(int pagos_id) {
		this.pagos_id = pagos_id;
	}
	public double getMontocubierto() {
		return montocubierto;
	}
	public void setMontocubierto(double montocubierto) {
		this.montocubierto = preres.roundDouble(montocubierto);
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
