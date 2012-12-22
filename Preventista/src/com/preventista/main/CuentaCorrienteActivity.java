package com.preventista.main;

import com.preventista.db.DBCuentaCorrienteAdapter;
import com.preventista.db.DBDeudasAdapter;
import com.preventista.db.DBPedidosAdapter;
import com.preventista.entidades.CuentaCorriente;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CuentaCorrienteActivity extends Activity implements OnClickListener{
	
	TextView tvApellNombreCliente, tvCCHaber, tvCCDebe;
	Button btnPedidosPagados, btnPedidosAdeudados,btnDeudasPagadas,btnDeudasSinPagar, btnPagosRealizados, btnRealizarPago, btnVolver;
	
	private Bundle bundle = null;
	private int clientes_id;
	private String apellNombre = null;
	private double saldo, haber, debe;
	private PreRes preres = new PreRes();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_cuenta_corriente);
		
		tvApellNombreCliente = (TextView) findViewById(R.id.tvApellNombreCliente);
		tvCCHaber = (TextView) findViewById(R.id.tvCCHaber);
		tvCCDebe = (TextView) findViewById(R.id.tvCCDebe);
		btnPedidosPagados = (Button) findViewById(R.id.btnPedidosPagados);
		btnPedidosAdeudados = (Button) findViewById(R.id.btnPedidosAdeudados);
		//btnDeudasPagadas = (Button) findViewById(R.id.btnDeudasPagadas);
		//btnDeudasSinPagar = (Button) findViewById(R.id.btnDeudasSinPagar);
		btnPagosRealizados = (Button) findViewById(R.id.btnPagosRealizados);
		btnRealizarPago = (Button) findViewById(R.id.btnRealizarPago);
		btnVolver = (Button) findViewById(R.id.btnVolver);
		
		btnPedidosPagados.setOnClickListener(this);
		btnPedidosAdeudados.setOnClickListener(this);
		//btnDeudasPagadas.setOnClickListener(this);
		//btnDeudasSinPagar.setOnClickListener(this);
		btnPagosRealizados.setOnClickListener(this);
		btnRealizarPago.setOnClickListener(this);
		btnVolver.setOnClickListener(this);
		
		bundle = getIntent().getExtras();
		this.clientes_id = bundle.getInt("_ID");
		this.apellNombre = bundle.getString("APELLIDO_NOMBRE");
		tvApellNombreCliente.setText(this.apellNombre);
		
		//buscar cuenta corriente del cliente
		getCC();
		
	}

	
	public void getCC()
	{
		DBPedidosAdapter dbpedido = new DBPedidosAdapter(this);
		
		haber = dbpedido.getHaber(this.clientes_id);
		debe = dbpedido.getDeuda(this.clientes_id);
		preres.updateEstadoContable(this.clientes_id, haber, debe,this);
		
		DBCuentaCorrienteAdapter dbccadapter = new DBCuentaCorrienteAdapter(this);
		CuentaCorriente cuentacorriente = dbccadapter.getCuentaCorriente(this.clientes_id);
		
		if(cuentacorriente.get_id() > 0){
			tvCCHaber.setText(Double.toString(cuentacorriente.getHaber()));
			tvCCDebe.setText(Double.toString(cuentacorriente.getDebe()));
		}else{
			Toast.makeText(this, "No existe cuenta corriente del cliente!", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btnPedidosPagados:
			Intent ir_a_pedidos_pagados = new Intent("com.preventista.main.PEDIDOSPAGADOS");
			Bundle bolsa1 = new Bundle();
			bolsa1.putInt("CLIENTES_ID", this.clientes_id);
			ir_a_pedidos_pagados.putExtras(bolsa1);
			startActivity(ir_a_pedidos_pagados);
			break;
			
		case R.id.btnPedidosAdeudados:
			Intent ir_a_pedidos_adeudados = new Intent("com.preventista.main.PEDIDOSADEUDADOS");
			Bundle bolsa2 = new Bundle();
			bolsa2.putInt("CLIENTES_ID", this.clientes_id);
			ir_a_pedidos_adeudados.putExtras(bolsa2);
			startActivity(ir_a_pedidos_adeudados);
			break;
		
		/*case R.id.btnDeudasPagadas:
			Intent ir_a_deudas_pagadas = new Intent("com.preventista.main.DEUDASPAGADAS");
			Bundle bolsa5 = new Bundle();
			bolsa5.putInt("CLIENTES_ID", this.clientes_id);
			ir_a_deudas_pagadas.putExtras(bolsa5);
			startActivity(ir_a_deudas_pagadas);
			break;
		
		case R.id.btnDeudasSinPagar:
			Intent ir_a_deudas_sin_pagar= new Intent("com.preventista.main.DEUDASSINPAGAR");
			Bundle bolsa6 = new Bundle();
			bolsa6.putInt("CLIENTES_ID", this.clientes_id);
			ir_a_deudas_sin_pagar.putExtras(bolsa6);
			startActivity(ir_a_deudas_sin_pagar);
			break;*/
			
		case R.id.btnPagosRealizados:
			Intent ir_a_pagos_realizados = new Intent("com.preventista.main.PAGOSREALIZADOS");
			Bundle bolsa3 = new Bundle();
			bolsa3.putInt("CLIENTES_ID", this.clientes_id);
			ir_a_pagos_realizados.putExtras(bolsa3);
			startActivity(ir_a_pagos_realizados);
			break;
		
		case R.id.btnRealizarPago:
			Intent ir_a_realizar_pago = new Intent("com.preventista.main.REALIZARPAGO");
			Bundle bolsa4 = new Bundle();
			bolsa4.putInt("CLIENTES_ID", this.clientes_id);
			ir_a_realizar_pago.putExtras(bolsa4);
			startActivity(ir_a_realizar_pago);
			break;
			
		case R.id.btnVolver:
			Intent ir_a_clientes = new Intent(CuentaCorrienteActivity.this,ClientesActivity.class); 
			startActivity(ir_a_clientes);
			break;
			
		}
	}

}
