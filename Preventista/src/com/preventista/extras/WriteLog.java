package com.preventista.extras;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.Log;

import com.preventista.entidades.Articulos;
import com.preventista.entidades.Clientes;
import com.preventista.entidades.CuentaCorriente;
import com.preventista.entidades.Pagos;
import com.preventista.entidades.PagosDeudas;
import com.preventista.entidades.PagosPedidos;
import com.preventista.entidades.Pedidos;
import com.preventista.entidades.Pedidosdetalle;

public class WriteLog {
	
	WriteLogSql wlq = new WriteLogSql();
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	Date date = new Date();
	
	public void wAddArticulo(Articulos articulo, Long rowid, Context contexto)
	{
		String sql = "INSERT INTO articulos (articulos_id,articulos_descripcion,articulos_preciocompra,articulos_stockreal,articulos_stockmin," +
				"articulos_stockmax,rubros_id,articulos_observaciones,articulos_precio1,articulos_precio2,articulos_precio3,articulos_porcentaje1," +
				"articulos_porcentaje2,articulos_porcentaje3,articulos_estado,marcas_id,articulos_created_at,articulos_updated_at) VALUES " +
				"("+ rowid + ",'" + articulo.getDescripcion() + "'," + articulo.getPreciocompra() + 
				"," + articulo.getStockreal() + ",null,null," + articulo.getRubros_id() + 
				",null," + articulo.getPrecio1() + "," + articulo.getPrecio2() + "," +
				""+ articulo.getPrecio3() + "," + articulo.getPorcentaje1() + "," + 
				articulo.getPorcentaje2() + "," + articulo.getPorcentaje3() + "," + 
				articulo.getEstado() + "," + articulo.getMarcas_id() + ",'" + dateFormat.format(date) + "','" + dateFormat.format(date) + "');\n";
		Log.d("SQL_INSERT", sql);
		wlq.writeFileInternalStorage(contexto,sql);
	}
	
	public void wEditArticulo(String sql,Context contexto)
	{
		Log.d("SQL_UPDATE", sql);
		wlq.writeFileInternalStorage(contexto,sql);
	}
	
	public void wAddCliente(Clientes cliente, Long rowid, Context contexto)
	{
		String sql = "INSERT INTO clientes (clientes_id,clientes_nombre,clientes_apellido,clientes_direccion," +
				"clientes_telefono,clientes_created_at,clientes_updated_at,clientescategoria_id) VALUES " +
				"("+ rowid + ",'" + cliente.getNombre() + "','" + cliente.getApellido() + 
				"','" + cliente.getDireccion() + "','" + cliente.getTelefono() + 
				"','" + dateFormat.format(date) + "','" + dateFormat.format(date) + "'," + cliente.getClientescategoria_id() + ");\n";
		Log.d("SQL_INSERT", sql);
		wlq.writeFileInternalStorage(contexto,sql);
	}
	
	public void wEditCliente(String sql, Context contexto)
	{
		Log.d("SQL_UPDATE", sql);
		wlq.writeFileInternalStorage(contexto,sql);
	}
	
	public void wAddCuentaCorriente(CuentaCorriente cuentacorriente, Long rowid, Context contexto)
	{
		String sql = "INSERT INTO cuentacorriente (cuentacorriente_id,clientes_id,cuentacorriente_haber,cuentacorriente_debe," +
				"cuentacorriente_saldo,cuentacorriente_created_at,cuentacorriente_updated_at) VALUES " +
				"("+ rowid + "," + cuentacorriente.getClientes_id() + "," + cuentacorriente.getHaber() + 
				"," + cuentacorriente.getDebe() + "," + cuentacorriente.getSaldo() + 
				",'"+ cuentacorriente.getUpdated_at() +"','"+ cuentacorriente.getUpdated_at() +"');\n";
		Log.d("SQL_INSERT", sql);
		wlq.writeFileInternalStorage(contexto,sql);
	}
	
	public void wEditCuentaCorriente(String sql, Context contexto)
	{
		Log.d("SQL_UPDATE", sql);
		wlq.writeFileInternalStorage(contexto,sql);
	}
	
	public void wAddPedido(Pedidos pedido, Long rowid, Context contexto)
	{
		String sql = "INSERT INTO pedidos (pedidos_id,peididos_montototal,pedidos_montoadeudado,clientes_id," +
				"pedidos_estado,tramites_id, pedidos_observaciones, pedidos_created_at,pedidos_updated_at) VALUES " +
				"("+ rowid + "," + pedido.getMontototal() + "," + pedido.getMontototal() + 
				"," + pedido.getClientes_id() + "," + pedido.getEstado() + ", " + pedido.getTramites_id() + ",'" + pedido.getObservaciones() + "'" +
				",'" + dateFormat.format(date) + "','" + dateFormat.format(date) + "');\n";
		Log.d("SQL_INSERT", sql);
		wlq.writeFileInternalStorage(contexto,sql);
	}
	
	public void wEditPedido(String sql, Context contexto)
	{
		Log.d("SQL_UPDATE", sql);
		wlq.writeFileInternalStorage(contexto,sql);
	}
	
	public void wAddPedidoDetalle(Pedidosdetalle pedidodetalle, Long rowid, Context contexto)
	{
		String sql = "INSERT INTO pedidodetalle (pedidodetalle_id,pedidos_id,articulos_id,pedidodetalle_cantidad," +
				"pedidodetalle_montoacordado,pedidodetalle_subtotal,pedidodetalle_pv,pedidodetalle_created_at,pedidodetalle_updated_at) VALUES " +
				"("+ rowid + "," + pedidodetalle.getPedidos_id() + "," + pedidodetalle.getArticulos_id() + 
				"," + pedidodetalle.getCantidad() + "," + pedidodetalle.getMontoacordado() + ", " + pedidodetalle.getSubtotal() + ", " +  pedidodetalle.getPv() + 
				", '" + dateFormat.format(date) + "','" + dateFormat.format(date) + "');\n";
		Log.d("SQL_INSERT", sql);
		wlq.writeFileInternalStorage(contexto,sql);
	}
	
	public void wEditPedidoDetalle(String sql, Context contexto)
	{
		Log.d("SQL_UPDATE", sql);
		wlq.writeFileInternalStorage(contexto,sql);
	}
	
	public void wDeletePedidoDetalle(String sql, Context contexto)
	{
		Log.d("SQL_DELETE", sql);
		wlq.writeFileInternalStorage(contexto,sql);
	}
	
	public void wAddPago(Pagos pago, Long rowid, Context contexto)
	{
		String sql = "INSERT INTO pagos (pagos_id,pagos_monto,clientes_id,usuarios_id,pagos_created_at,pagos_updated_at) VALUES " +
				"("+ rowid + "," + pago.getMonto() + "," + pago.getClientes_id() + "," + pago.getUsuarios_id() + ", '" + dateFormat.format(date) + "','" + dateFormat.format(date) + "');\n";
		Log.d("SQL_INSERT", sql);
		wlq.writeFileInternalStorage(contexto,sql);
	}
	
	public void wEditPago(String sql, Context contexto)
	{
		Log.d("SQL_UPDATE", sql);
		wlq.writeFileInternalStorage(contexto,sql);
	}
	
	public void wAddPagoPedido(PagosPedidos pagopedido, Long rowid, Context contexto)
	{
		String sql = "INSERT INTO pagospedidos (pagospedidos_id,pedidos_id, pagos_id, pagospedidos_montocubierto,pagospedidos_created_at,pagospedidos_updated_at) VALUES " +
				"("+ rowid + "," + pagopedido.getPedidos_id() + "," + pagopedido.getPagos_id() + "," + pagopedido.getMontocubierto() + ", '" + dateFormat.format(date) + "','" + dateFormat.format(date) + "');\n";
		Log.d("SQL_INSERT", sql);
		wlq.writeFileInternalStorage(contexto,sql);
	}
	
	public void wAddPagoDeuda(PagosDeudas pagodeuda, Long rowid, Context contexto)
	{
		String sql = "INSERT INTO pagosdeudas (pagosdeudas_id,deudas_id, pagos_id, pagosdeudas_montocubierto,pagosdeudas_created_at,pagosdeudas_updated_at) VALUES " +
				"("+ rowid + "," + pagodeuda.getDeudas_id() + "," + pagodeuda.getPagos_id() + "," + pagodeuda.getMontocubierto() + ", '" + dateFormat.format(date) + "','" + dateFormat.format(date) + "');\n";
		Log.d("SQL_INSERT", sql);
		wlq.writeFileInternalStorage(contexto,sql);
	}
	
	public void wEditDeuda(String sql, Context contexto)
	{
		Log.d("SQL_UPDATE", sql);
		wlq.writeFileInternalStorage(contexto,sql);
	}
}
