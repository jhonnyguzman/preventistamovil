package com.preventista.main;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.preventista.db.DBArticulosAdapter;
import com.preventista.db.DBMarcasAdapter;
import com.preventista.db.DBRubrosAdapter;
import com.preventista.entidades.Articulos;
import com.preventista.entidades.Marcas;
import com.preventista.entidades.Rubros;

public class ArticulosEditActivity extends Activity implements OnClickListener, OnItemSelectedListener{
	
	private EditText edtId, edtDescripcion, edtPrecioCompra, edtStockReal, edtPorcentaje1, edtPorcentaje2, edtPorcentaje3, edtPrecio1, edtPrecio2, edtPrecio3;
	private Spinner cmbRubros, cmbMarcas;
	private Button btnSave, btnCancel;
	private Bundle bundle = null;
	
	private ArrayList<Rubros> rubros = new ArrayList<Rubros>();
	private ArrayList<String> rubro_descripcion = new ArrayList<String>();
	private ArrayList<Marcas> marcas = new ArrayList<Marcas>();
	private ArrayList<String> marca_descripcion = new ArrayList<String>();
	
	private int rubros_id, marcas_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_form_articulos);
		
		edtId = (EditText) findViewById(R.id.edtIdArticulo);
		edtDescripcion = (EditText) findViewById(R.id.edtDescripcionArticulo);
		edtPrecioCompra = (EditText) findViewById(R.id.edtPrecioCompraArticulo);
		edtStockReal = (EditText) findViewById(R.id.edtStockRealArticulo);
		edtPorcentaje1 = (EditText) findViewById(R.id.edtPorcentaje1Articulo);
		edtPorcentaje2 = (EditText) findViewById(R.id.edtPorcentaje2Articulo);
		edtPorcentaje3 = (EditText) findViewById(R.id.edtPorcentaje3Articulo);
		edtPrecio1 = (EditText) findViewById(R.id.edtPrecio1Articulo);
		edtPrecio2 = (EditText) findViewById(R.id.edtPrecio2Articulo);
		edtPrecio3 = (EditText) findViewById(R.id.edtPrecio3Articulo);
		
		cmbRubros = (Spinner) findViewById(R.id.CmbRubrosEditArticulo);
		cmbMarcas = (Spinner) findViewById(R.id.CmbMarcasEditArticulo);
		
		edtId.setEnabled(false);
		edtId.setClickable(false);
		
		bundle = getIntent().getExtras();
		
		edtId.setText(bundle.getString("ID"));
		edtDescripcion.setText(bundle.getString("DESCRIPCION"));
		edtPrecioCompra.setText(bundle.getString("PRECIOCOMPRA"));
		edtStockReal.setText(bundle.getString("STOCKREAL"));
		edtPorcentaje1.setText(bundle.getString("PORCENTAJE1"));
		edtPorcentaje2.setText(bundle.getString("PORCENTAJE2"));
		edtPorcentaje3.setText(bundle.getString("PORCENTAJE3"));
		edtPrecio1.setText(bundle.getString("PRECIO1"));
		edtPrecio2.setText(bundle.getString("PRECIO2"));
		edtPrecio3.setText(bundle.getString("PRECIO3"));
		
		
		btnSave = (Button) findViewById(R.id.btnSaveEditArticulo);
		btnCancel = (Button) findViewById(R.id.btnCancelEditArticulo);
		cmbRubros.setOnItemSelectedListener(this);
		cmbMarcas.setOnItemSelectedListener(this);
		
		btnSave.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		
		setRubros();
		setMarcas();
		
		edtPrecioCompra.addTextChangedListener(new TextWatcher() {
		    public void afterTextChanged(Editable s) {
		    	float pv1, pv2, pv3;
		    	if(s.length()>0){
		    		PreRes pr = new PreRes();
		    		pv1 = pr.getPrecioCompra(Float.parseFloat(s.toString()), Float.parseFloat(edtPorcentaje1.getText().toString()));
		    		pv2 = pr.getPrecioCompra(Float.parseFloat(s.toString()), Float.parseFloat(edtPorcentaje2.getText().toString()));
		    		pv3 = pr.getPrecioCompra(Float.parseFloat(s.toString()), Float.parseFloat(edtPorcentaje3.getText().toString()));
		    		edtPrecio1.setText(Float.toString(pv1));
		    		edtPrecio2.setText(Float.toString(pv2));
		    		edtPrecio3.setText(Float.toString(pv3));
		    	}
		    }

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			};
		});
		
		edtPorcentaje1.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				float pv1;
		    	if(s.length()>0){
		    		PreRes pr = new PreRes();
		    		pv1 = pr.getPrecioCompra(Float.parseFloat(edtPrecioCompra.getText().toString()), Float.parseFloat(s.toString()));
		    		edtPrecio1.setText(Float.toString(pv1));
		    	}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		edtPorcentaje2.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				float pv2;
		    	if(s.length()>0){
		    		PreRes pr = new PreRes();
		    		pv2 = pr.getPrecioCompra(Float.parseFloat(edtPrecioCompra.getText().toString()), Float.parseFloat(s.toString()));
		    		edtPrecio2.setText(Float.toString(pv2));
		    	}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		edtPorcentaje3.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				float pv3;
		    	if(s.length()>0){
		    		PreRes pr = new PreRes();
		    		pv3 = pr.getPrecioCompra(Float.parseFloat(edtPrecioCompra.getText().toString()), Float.parseFloat(s.toString()));
		    		edtPrecio3.setText(Float.toString(pv3));
		    	}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}
			
		});
		

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btnSaveEditArticulo:
			Articulos articulo = new Articulos();
			articulo.set_id(Integer.parseInt(edtId.getText().toString()));
			articulo.setDescripcion(edtDescripcion.getText().toString());
			articulo.setPreciocompra(Float.parseFloat(edtPrecioCompra.getText().toString()));
			articulo.setStockreal(Integer.parseInt(edtStockReal.getText().toString()));
			articulo.setRubros_id(this.rubros_id);
			articulo.setMarcas_id(this.marcas_id);
			articulo.setPorcentaje1(Float.parseFloat(edtPorcentaje1.getText().toString()));
			articulo.setPorcentaje2(Float.parseFloat(edtPorcentaje2.getText().toString()));
			articulo.setPorcentaje3(Float.parseFloat(edtPorcentaje3.getText().toString()));
			articulo.setPrecio1(Float.parseFloat(edtPrecio1.getText().toString()));
			articulo.setPrecio2(Float.parseFloat(edtPrecio2.getText().toString()));
			articulo.setPrecio3(Float.parseFloat(edtPrecio3.getText().toString()));
			
			DBArticulosAdapter dbarticulo = new DBArticulosAdapter(this);
			if(dbarticulo.editArticulo(articulo)){
				Toast.makeText(ArticulosEditActivity.this, "Articulo modificado correctamente!!", Toast.LENGTH_SHORT).show();
				Intent ir_a_articulos = new Intent(ArticulosEditActivity.this,ArticulosActivity.class); 
				startActivity(ir_a_articulos);
			}else{
				Toast.makeText(ArticulosEditActivity.this, "Error al modificar articulo!", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.btnCancelEditArticulo:
			Intent ir_a_articulos = new Intent(ArticulosEditActivity.this,ArticulosActivity.class); 
			startActivity(ir_a_articulos);
			break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id) {
		// TODO Auto-generated method stub
		switch(parent.getId()){
		
		case R.id.CmbRubrosEditArticulo:
			this.rubros_id = rubros.get(position).get_id();
			break;
		case R.id.CmbMarcasEditArticulo:
			this.marcas_id = marcas.get(position).get_id();
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
	public void setRubros()
	{
		DBRubrosAdapter dbrubro = new DBRubrosAdapter(this);
		rubros = dbrubro.getAllRubros();
		for (int i = 0; i < rubros.size(); i++) {
 			rubro_descripcion.add(rubros.get(i).getDescripcion());
 		}
		
		ArrayAdapter<String> adaptador_rubros =
		        new ArrayAdapter<String>(this,
		            android.R.layout.simple_spinner_item, rubro_descripcion);
		adaptador_rubros.setDropDownViewResource(
		        android.R.layout.simple_spinner_dropdown_item);
		cmbRubros.setAdapter(adaptador_rubros);
		
		//iten preseleccionado segun campo de la bd
		for(int i=0; i < rubros.size(); i++){
			if(rubros.get(i).get_id() == Integer.parseInt(bundle.getString("RUBROS_ID"))){
				cmbRubros.setSelection(i);
				break;
			}
		}
	}
	
	public void setMarcas()
	{
		DBMarcasAdapter dbmarca = new DBMarcasAdapter(this);
		marcas = dbmarca.getAllMarcas();
		for (int i = 0; i < marcas.size(); i++) {
 			marca_descripcion.add(marcas.get(i).getDescripcion());
 		}
		
		ArrayAdapter<String> adaptador_marcas =
		        new ArrayAdapter<String>(this,
		            android.R.layout.simple_spinner_item, marca_descripcion);
		adaptador_marcas.setDropDownViewResource(
		        android.R.layout.simple_spinner_dropdown_item);
		cmbMarcas.setAdapter(adaptador_marcas);
		
		//iten preseleccionado segun campo de la bd
		for(int i=0; i < marcas.size(); i++){
			if(marcas.get(i).get_id() == Integer.parseInt(bundle.getString("MARCAS_ID"))){
				cmbMarcas.setSelection(i);
				break;
			}
		}
	}
	
}
