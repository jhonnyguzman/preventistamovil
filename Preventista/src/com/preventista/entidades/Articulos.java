package com.preventista.entidades;

import com.preventista.main.PreRes;

public class Articulos {
	
	private int _id;
	private String descripcion;
	private float preciocompra;
	private int stockreal;
	private int stockmin;
	private int stockmax;
	private int rubros_id;
	private String observaciones;
	private float precio1;
	private float precio2;
	private float precio3;
	private float porcentaje1;
	private float porcentaje2;
	private float porcentaje3;
	private int estado;
	private int marcas_id;
	private String created_at;
	private String updated_at;
	private boolean checked = false;
	private PreRes preres = new PreRes();
	
	public int get_id() {
		return _id;
	}
	
	public void set_id(int _id) {
		this._id = _id;
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public float getPreciocompra() {
		return preciocompra;
	}
	
	public void setPreciocompra(float preciocompra) {
		this.preciocompra = preres.formatearFloat(preciocompra);
	}
	
	public int getStockreal() {
		return stockreal;
	}
	
	public void setStockreal(int stockreal) {
		this.stockreal = stockreal;
	}
	
	public int getStockmin() {
		return stockmin;
	}
	
	public void setStockmin(int stockmin) {
		this.stockmin = stockmin;
	}
	
	public int getStockmax() {
		return stockmax;
	}
	
	public void setStockmax(int stockmax) {
		this.stockmax = stockmax;
	}
	
	public int getRubros_id() {
		return rubros_id;
	}
	
	public void setRubros_id(int rubros_id) {
		this.rubros_id = rubros_id;
	}
	
	public String getObservaciones() {
		return observaciones;
	}
	
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	
	public float getPrecio1() {
		return precio1;
	}
	
	public void setPrecio1(float precio1) {
		this.precio1 = preres.formatearFloat(precio1);
	}
	
	public float getPrecio2() {
		return precio2;
	}
	
	public void setPrecio2(float precio2) {
		this.precio2 = preres.formatearFloat(precio2);
	}
	
	public float getPrecio3() {
		return precio3;
	}
	
	public void setPrecio3(float precio3) {
		this.precio3 = preres.formatearFloat(precio3);
	}
	
	public float getPorcentaje1() {
		return porcentaje1;
	}
	
	public void setPorcentaje1(float porcentaje1) {
		this.porcentaje1 = porcentaje1;
	}
	
	public float getPorcentaje2() {
		return porcentaje2;
	}
	
	public void setPorcentaje2(float porcentaje2) {
		this.porcentaje2 = porcentaje2;
	}
	
	public float getPorcentaje3() {
		return porcentaje3;
	}
	
	public void setPorcentaje3(float porcentaje3) {
		this.porcentaje3 = porcentaje3;
	}
	
	public int getEstado() {
		return estado;
	}
	
	public void setEstado(int estado) {
		this.estado = estado;
	}
	
	public int getMarcas_id() {
		return marcas_id;
	}
	
	public void setMarcas_id(int marcas_id) {
		this.marcas_id = marcas_id;
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
	
	
	public boolean isChecked() {
	    return checked;
	}
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    
    public void toggleChecked() {
        this.checked = !checked ;
    }
	
}
