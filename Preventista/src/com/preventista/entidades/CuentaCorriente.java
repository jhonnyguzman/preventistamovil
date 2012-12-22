package com.preventista.entidades;

import com.preventista.main.PreRes;

public class CuentaCorriente {
	
	private int _id;
	private int clientes_id;
	private double haber;
	private double debe;
	private double saldo;
	private String created_at;
	private String updated_at;
	private PreRes preres = new PreRes();
	
	public int get_id() {
		return _id;   
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public int getClientes_id() {
		return clientes_id;
	}
	public void setClientes_id(int clientes_id) {
		this.clientes_id = clientes_id;
	}
	public double getHaber() {
		return haber;
	}
	public void setHaber(double haber) {
		this.haber = preres.roundDouble(haber);
	}
	public double getDebe() {
		return debe;
	}
	public void setDebe(double debe) {
		this.debe = preres.roundDouble(debe);
	}
	public double getSaldo() {
		return saldo;
	}
	public void setSaldo(double saldo) {
		this.saldo = preres.roundDouble(saldo);
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
