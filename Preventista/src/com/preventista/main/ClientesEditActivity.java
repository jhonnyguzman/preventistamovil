package com.preventista.main;

import com.preventista.db.DBClienteAdapter;
import com.preventista.entidades.Clientes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ClientesEditActivity extends Activity implements OnClickListener{
	
	private EditText edtId, edtNombre, edtApellido, edtDireccion, edtTelefono, edtCategoria;
	private Button btnSave, btnCancel;
	private Bundle bundle = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_form_clientes);
		
		edtId = (EditText) findViewById(R.id.edtIdCliente);
		edtNombre = (EditText) findViewById(R.id.edtNombreCliente);
		edtApellido = (EditText) findViewById(R.id.edtApellidoCliente);
		edtDireccion = (EditText) findViewById(R.id.edtDireccionCliente);
		edtTelefono = (EditText) findViewById(R.id.edtTelefonoCliente);
		edtCategoria = (EditText) findViewById(R.id.edtCategoriaCliente);
		
		edtId.setEnabled(false);
		edtId.setClickable(false);
		
		bundle = getIntent().getExtras();
		
		edtId.setText(bundle.getString("ID"));
		edtNombre.setText(bundle.getString("NOMBRE"));
		edtApellido.setText(bundle.getString("APELLIDO"));
		edtDireccion.setText(bundle.getString("DIRECCION"));
		edtTelefono.setText(bundle.getString("TELEFONO"));
		edtCategoria.setText(bundle.getString("CLIENTESCATEGORIA_ID"));
		
		btnSave = (Button) findViewById(R.id.btnSaveEditCliente);
		btnCancel = (Button) findViewById(R.id.btnCancelEditCliente);
		
		btnSave.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btnSaveEditCliente:
			Clientes cliente = new Clientes();
			cliente.set_id(Integer.parseInt(edtId.getText().toString()));
			cliente.setNombre(edtNombre.getText().toString());
			cliente.setApellido(edtApellido.getText().toString());
			cliente.setDireccion(edtDireccion.getText().toString());
			cliente.setTelefono(edtTelefono.getText().toString());
			cliente.setClientescategoria_id(Integer.parseInt(edtCategoria.getText().toString()));
			
			DBClienteAdapter dba = new DBClienteAdapter(this);
			if(dba.editCliente(cliente)){
				Toast.makeText(ClientesEditActivity.this, "Edicción correcta", Toast.LENGTH_SHORT).show();
				Intent ir_clientes = new Intent(ClientesEditActivity.this,ClientesActivity.class); 
				startActivity(ir_clientes);
			}else{
				Toast.makeText(ClientesEditActivity.this, "Error al editar cliente!", Toast.LENGTH_SHORT).show();
			}
			break;
		
		case R.id.btnCancelEditCliente:
			Intent ir_clientes = new Intent(ClientesEditActivity.this,ClientesActivity.class); 
			startActivity(ir_clientes);
			break;
		}
		
	}
	
	

}
