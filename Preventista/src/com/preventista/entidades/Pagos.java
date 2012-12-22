package com.preventista.entidades;

import java.text.ParseException;

import com.preventista.main.PreRes;

public class Pagos {

	private int _id;
	private double monto;
	private int clientes_id;
	private int usuarios_id;
	private int estado;
	private String fechaingreso;
	private String created_at;
	private String updated_at;
	private String custonCreatedAt;
	private String custonUpdatedAt;
	private String custonMonto;
	private PreRes pr = new PreRes();
	
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public double getMonto() {
		return monto;
	}
	public void setMonto(double monto) {
		this.monto = pr.roundDouble(monto);
	}
	public int getClientes_id() {
		return clientes_id;
	}
	public void setClientes_id(int clientes_id) {
		this.clientes_id = clientes_id;
	}
	public int getUsuarios_id() {
		return usuarios_id;
	}
	public void setUsuarios_id(int usuarios_id) {
		this.usuarios_id = usuarios_id;
	}
	public int getEstado() {
		return estado;
	}
	public void setEstado(int estado) {
		this.estado = estado;
	}
	public String getFechaingreso() {
		return fechaingreso;
	}
	public void setFechaingreso(String fechaingreso) {
		this.fechaingreso = fechaingreso;
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
	
	public String getCustonCreatedAt()
	{
		try {
			return pr.sToDateForShow(this.created_at);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public String getCustonUpdatedAt()
	{
		try {
			return pr.sToDateForShow(this.updated_at);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public String getCustonMonto() {
		return "$ " + Double.toString(monto);
	}
}
