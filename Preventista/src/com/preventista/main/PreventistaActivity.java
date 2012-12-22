package com.preventista.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class PreventistaActivity extends Activity implements OnClickListener {
	
	Button btnGPedidos,btnGClientes, btnGArticulos, btnAdministracion;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        btnGPedidos = (Button) findViewById(R.id.btnGPedidos);
        btnGClientes = (Button) findViewById(R.id.btnGClientes);
        btnGArticulos = (Button) findViewById(R.id.btnGArticulos);
        btnAdministracion = (Button) findViewById(R.id.btnAdministracion);
        
        btnGPedidos.setOnClickListener(this);
        btnGClientes.setOnClickListener(this);
        btnGArticulos.setOnClickListener(this);
        btnAdministracion.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		
		case R.id.btnGPedidos:
			Intent ir_a_pedidos = new Intent("com.preventista.main.MAINPEDIDOS");
			startActivity(ir_a_pedidos);
			break;
		
		case R.id.btnGClientes:
			Intent ir_a_clientes = new Intent("com.preventista.main.MAINCLIENTES");
			startActivity(ir_a_clientes);
			break;
		
		case R.id.btnGArticulos:
			Intent ir_a_articulos = new Intent("com.preventista.main.MAINARTICULOS");
			startActivity(ir_a_articulos);
			break;
		
		case R.id.btnAdministracion:
			Intent ir_a_administracion = new Intent("com.preventista.main.MAINADMIN");
			startActivity(ir_a_administracion);
			break;
		}
	}
	
	
	/**
	 * Opciones de Menu de la sección principal de clientes
	 * */
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{	
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu_main, menu);
		return true;
	}
	
	@Override 
	public boolean onOptionsItemSelected(MenuItem item)
	{	
		switch(item.getItemId()){
		
		case R.id.mnPreferencias:
			Intent ir_a_preferncias = new Intent("com.preventista.main.PREFERENCIAS");
			startActivity(ir_a_preferncias);
			return true;
			
		default:
            return super.onOptionsItemSelected(item);
		}
		
	}
}