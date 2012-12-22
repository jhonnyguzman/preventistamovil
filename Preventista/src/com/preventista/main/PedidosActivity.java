package com.preventista.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PedidosActivity extends Activity implements OnClickListener{
	
	Button btnNew, btnSearch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_pedidos);
		
		btnNew = (Button) findViewById(R.id.btnNewPedido);
		btnSearch = (Button) findViewById(R.id.btnSearchPedido);
		
		btnNew.setOnClickListener(this);
		btnSearch.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btnNewPedido:
			Intent ir_a_seleccion_cliente = new Intent("com.preventista.main.SELECCIONCLIENTE");
			startActivity(ir_a_seleccion_cliente);
			break;
		
		case R.id.btnSearchPedido:
			Intent ir_a_busqueda_de_pedidos = new Intent("com.preventista.main.SEARCHPEDIDOS");
			startActivity(ir_a_busqueda_de_pedidos);
			break;
		}
	}
	
}
