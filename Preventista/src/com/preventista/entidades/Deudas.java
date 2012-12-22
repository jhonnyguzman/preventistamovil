package com.preventista.entidades;

import java.text.ParseException;

import com.preventista.main.PreRes;

public class Deudas {
	
	private int _id;
	private float montototal;
	private String fecha;
	private int clientes_id;
	private String created_at;
	private String updated_at;
	private int usuarios_id;
	private int estado;
	private float montoadeudado;
	private String apellidoCliente;
	private String nombreCliente;
	private String estadoDescripcion;
	
	
	public String getApellidoCliente() {
		return apellidoCliente;
	}
	public void setApellidoCliente(String apellidoCliente) {
		this.apellidoCliente = apellidoCliente;
	}
	public String getNombreCliente() {
		return nombreCliente;
	}
	public void setNombreCliente(String nombreCliente) {
		this.nombreCliente = nombreCliente;
	}
	public String getEstadoDescripcion() {
		return estadoDescripcion;
	}
	public void setEstadoDescripcion(String estadoDescripcion) {
		this.estadoDescripcion = estadoDescripcion;
	}
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
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public int getClientes_id() {
		return clientes_id;
	}
	public void setClientes_id(int clientes_id) {
		this.clientes_id = clientes_id;
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
	public float getMontoadeudado() {
		return montoadeudado;
	}
	public void setMontoadeudado(float montoadeudado) {
		this.montoadeudado = montoadeudado;
	}
	
	public String getCustonFecha()
	{
		PreRes pr = new PreRes();
		try {
			return pr.sToDateForShow(this.fecha);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public String getCustonCreatedAt()
	{
		PreRes pr = new PreRes();
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
		PreRes pr = new PreRes();
		try {
			return pr.sToDateForShow(this.updated_at);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
}
