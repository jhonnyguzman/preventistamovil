package com.preventista.entidades;

public class Clientes {
	
	private int _id;
	private String nombre;
	private String apellido;
	private String direccion;
	private String telefono;
	private String created_at;
	private String updated_at;
	private int clientescategoria_id;
	private String catDescripcion;
	
	public int get_id() {
		return _id;
	}
	
	public void set_id(int _id) {
		this._id = _id;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getApellido() {
		return apellido;
	}
	
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	
	public String getDireccion() {
		return direccion;
	}
	
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	
	public String getTelefono() {
		return telefono;
	}
	
	public void setTelefono(String telefono) {
		this.telefono = telefono;
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
	
	public int getClientescategoria_id() {
		return clientescategoria_id;
	}
	
	public void setClientescategoria_id(int clientescategoria_id) {
		this.clientescategoria_id = clientescategoria_id;
	}
	
	public String getCatDescripcion() {
		return catDescripcion;
	}
	
	public void setCatDescripcion(String catDescripcion) {
		this.catDescripcion = catDescripcion;
	}
	
	public String getApellidoNombre() {
		return apellido + " " + nombre;
	}
}
