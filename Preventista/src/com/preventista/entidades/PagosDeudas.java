package com.preventista.entidades;

public class PagosDeudas {
	
	private int _id;
	private int deudas_id;
	private int pagos_id;
	private float montocubierto;
	private String created_at;
	private String updated_at;
	
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public int getDeudas_id() {
		return deudas_id;
	}
	public void setDeudas_id(int deudas_id) {
		this.deudas_id = deudas_id;
	}
	public int getPagos_id() {
		return pagos_id;
	}
	public void setPagos_id(int pagos_id) {
		this.pagos_id = pagos_id;
	}
	public float getMontocubierto() {
		return montocubierto;
	}
	public void setMontocubierto(float montocubierto) {
		this.montocubierto = montocubierto;
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
