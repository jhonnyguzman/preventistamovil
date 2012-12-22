package com.preventista.entidades;

public class Pedidostemp {

	private int _id;
	private float montototal;
	private float montoadeudado;
	private int clientes_id;
	private int estado;
	private int tramites_id;
	private String observaciones;
	private String created_at;
	private String updated_at;
	
	public int get_id() {
		return _id;
	}
	
	public void set_id(int _id) {
		this._id = _id;
	}
	
	public float getMontototal() {
		return montototal;
	}
	
	public void setMontototal(float montototal) {
		this.montototal = montototal;
	}
	
	public float getMontoadeudado() {
		return montoadeudado;
	}
	
	public void setMontoadeudado(float montoadeudado) {
		this.montoadeudado = montoadeudado;
	}
	
	public int getClientes_id() {
		return clientes_id;
	}
	
	public void setClientes_id(int clientes_id) {
		this.clientes_id = clientes_id;
	}
	
	public int getEstado() {
		return estado;
	}
	
	public void setEstado(int estado) {
		this.estado = estado;
	}
	
	public int getTramites_id() {
		return tramites_id;
	}
	
	public void setTramites_id(int tramites_id) {
		this.tramites_id = tramites_id;
	}
	
	public String getObservaciones() {
		return observaciones;
	}
	
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
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
