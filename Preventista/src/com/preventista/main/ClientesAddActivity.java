package com.preventista.main;

import com.preventista.db.DBClienteAdapter;
import com.preventista.entidades.Clientes;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ClientesAddActivity extends Activity implements OnClickListener{

	private EditText edtNombre, edtApellido, edtDireccion, edtTelefono, edtCategoria;
	private Button btnSave, btnCancel;
	
	//atributos para las preferencias
	private SharedPreferences sp;
	private int nextid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_form_clientes);
		
		//preferencias
		sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		nextid = Integer.parseInt(sp.getString("etPrefClientesID", "50000"));
				
		edtNombre = (EditText) findViewById(R.id.edtNombreCliente);
		edtApellido = (EditText) findViewById(R.id.edtApellidoCliente);
		edtDireccion = (EditText) findViewById(R.id.edtDireccionCliente);
		edtTelefono = (EditText) findViewById(R.id.edtTelefonoCliente);
		edtCategoria = (EditText) findViewById(R.id.edtCategoriaCliente);
		
		btnSave = (Button) findViewById(R.id.btnSaveAddCliente);
		btnCancel = (Button) findViewById(R.id.btnCancelAddCliente);
		
		btnSave.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btnSaveAddCliente:
			Clientes cliente = new Clientes();
			cliente.set_id(nextid);
			cliente.setNombre(edtNombre.getText().toString());
			cliente.setApellido(edtApellido.getText().toString());
			cliente.setDireccion(edtDireccion.getText().toString());
			cliente.setTelefono(edtTelefono.getText().toString());
			cliente.setClientescategoria_id(Integer.parseInt(edtCategoria.getText().toString()));
			
			DBClienteAdapter dba = new DBClienteAdapter(this);
			if(dba.addCliente(cliente)){
				savePreference(nextid);
				Toast.makeText(ClientesAddActivity.this, "Cliente agregado exitosamente!!", Toast.LENGTH_SHORT).show();
				Intent ir_clientes = new Intent(ClientesAddActivity.this,ClientesActivity.class); 
				startActivity(ir_clientes);
			}else{
				Toast.makeText(ClientesAddActivity.this, "Error al agregar cliente!", Toast.LENGTH_SHORT).show();
			}
			break;
		
		case R.id.btnCancelAddCliente:
			Intent ir_clientes = new Intent(ClientesAddActivity.this,ClientesActivity.class); 
			startActivity(ir_clientes);
			break;
		}
	}
	
	
	private void savePreference(int next_id)
	{
		int newid = next_id + 1;
		SharedPreferences settings =  PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("etPrefClientesID", Integer.toString(newid));
		editor.commit();
		
	}
}
