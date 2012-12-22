package com.preventista.main;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


import com.preventista.db.DBArticulosAdapter;
import com.preventista.entidades.Articulos;



public class ArticulosActivity extends Activity {
	
	private int pos = -1;
	private static int save = -1;
	
	private ListView lvArticulos;
	private ArrayList<Articulos> resultado = new ArrayList<Articulos>();
	
	// Field where user enters his search criteria
    private EditText searchByDescripcion;
    
    private AdaptadorArticulos adaptador;
    
    
    static class ViewHolder {
        TextView lblDescripcion;
        TextView lblStockreal;
        TextView lblPrecio3;
        TextView lblId;
    }
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_articulos);
		lvArticulos = (ListView) findViewById(R.id.lvArticulos);
		searchByDescripcion = (EditText) findViewById(R.id.edtSearchByDescripcionArticulo);
		showArticulos();
	}
	
	public void showArticulos()
	{
		 DBArticulosAdapter dbarticulo = new DBArticulosAdapter(this);
		 resultado = dbarticulo.getAllArticulos();
	 	 adaptador =  new AdaptadorArticulos(this);
	 	 lvArticulos.setAdapter(adaptador);
	 	 
	 	lvArticulos.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				 pos = position;
				 save = position; 
				 openOptionsMenu();
				 adaptador.notifyDataSetChanged();
			}
	 		 
	 	 });
	 	 
	 	searchByDescripcion.addTextChangedListener(new TextWatcher() {

            // As the user types in the search field, the list is
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                AlterAdapter();
            }

            // Not used for this program
            @Override
            public void afterTextChanged(Editable arg0) {

            }

            // Not uses for this program
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub

            }
        });
	}
	
	
	/**
	 * Opciones de Menu de la sección principal de clientes
	 * */
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{	
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu_articulos, menu);
		return true;
	}
	
	@Override 
	public boolean onOptionsItemSelected(MenuItem item)
	{	
		switch(item.getItemId()){
		
		case R.id.mnAddArticulo:
			Intent ir_a_articulo_nuevo = new Intent("com.preventista.main.ADDARTICULO");
			startActivity(ir_a_articulo_nuevo);
			return true;
			
		case R.id.mnEditArticulo:
			if(pos == -1)
				Toast.makeText(this, "Selecciona un Articulo!", Toast.LENGTH_SHORT).show();
			else{
				Intent ir_a_articulos_editar = new Intent("com.preventista.main.EDITARTICULO");
				Bundle bolsa = new Bundle();
				bolsa.putString("ID", Integer.toString(resultado.get(pos).get_id()));
				bolsa.putString("DESCRIPCION", resultado.get(pos).getDescripcion());
				bolsa.putString("PRECIOCOMPRA", Float.toString(resultado.get(pos).getPreciocompra()));
				bolsa.putString("STOCKREAL", Integer.toString(resultado.get(pos).getStockreal()));
				bolsa.putString("STOCKMIN", Integer.toString(resultado.get(pos).getStockmin()));
				bolsa.putString("STOCKMAX", Integer.toString(resultado.get(pos).getStockmax()));
				bolsa.putString("RUBROS_ID", Integer.toString(resultado.get(pos).getRubros_id()));
				bolsa.putString("OBSERVACIONES", resultado.get(pos).getObservaciones());
				bolsa.putString("PRECIO1", Float.toString(resultado.get(pos).getPrecio1()));
				bolsa.putString("PRECIO2", Float.toString(resultado.get(pos).getPrecio2()));
				bolsa.putString("PRECIO3", Float.toString(resultado.get(pos).getPrecio3()));
				bolsa.putString("PORCENTAJE1", Float.toString(resultado.get(pos).getPorcentaje1()));
				bolsa.putString("PORCENTAJE2", Float.toString(resultado.get(pos).getPorcentaje2()));
				bolsa.putString("PORCENTAJE3", Float.toString(resultado.get(pos).getPorcentaje3()));
				bolsa.putString("ESTADO", Integer.toString(resultado.get(pos).getEstado()));
				bolsa.putString("MARCAS_ID", Integer.toString(resultado.get(pos).getMarcas_id()));
				bolsa.putString("CREATED_AT", resultado.get(pos).getUpdated_at());
				bolsa.putString("UPDATED_AT", resultado.get(pos).getUpdated_at());
				
				ir_a_articulos_editar.putExtras(bolsa);
				startActivity(ir_a_articulos_editar);
			}
			return true;
		
		case R.id.mnMainMenu:
			Intent ir_main_menu = new Intent(ArticulosActivity.this,PreventistaActivity.class); 
			startActivity(ir_main_menu);
			return true;
			
		default:
            return super.onOptionsItemSelected(item);
		}
		
	}
	
	
	class AdaptadorArticulos extends ArrayAdapter<Articulos> {
    	
    	Activity context;
    	
    	AdaptadorArticulos(Activity context) {
    		super(context, R.layout.listitem_articulos, resultado);
    		this.context = context;
    	}
    	
    	public View getView(int position, View convertView, ViewGroup parent) {
			
    		View item = convertView;
		    ViewHolder holder;
		    if(item == null)
		    {
	    		LayoutInflater inflater = context.getLayoutInflater();
				item = inflater.inflate(R.layout.listitem_articulos, null);
				
				 holder = new ViewHolder();
				 holder.lblDescripcion = (TextView)item.findViewById(R.id.lblDescripcionArticuloListItem);
				 holder.lblStockreal = (TextView)item.findViewById(R.id.lblStockRealArticuloListItem);
				 holder.lblPrecio3 = (TextView)item.findViewById(R.id.lblPrecio3ArticuloListItem);
				 holder.lblId = (TextView)item.findViewById(R.id.lblIdListItem);
				 item.setTag(holder);
		    }else{
		    	 holder = (ViewHolder)item.getTag();
		    }
			
			holder.lblDescripcion.setText(resultado.get(position).getDescripcion());
			holder.lblStockreal.setText(Integer.toString(resultado.get(position).getStockreal()));
			holder.lblPrecio3.setText(Float.toString(resultado.get(position).getPrecio3()));
			holder.lblId.setText(Integer.toString(resultado.get(position).get_id()));
			
			//si es true colocamos un background en el item del listview
			if(pos == position) {
				item.setBackgroundColor(Color.argb(200, 49, 176, 17));
			}else{
				item.setBackgroundColor(Color.argb(0, 0, 0, 0));
			}
			
			return(item);
		}
    }
	
	
	 // Filters list of contacts based on user search criteria. If no information is filled in, contact list will be blank.
    private void AlterAdapter() 
    {
		resultado.clear(); 
		ArrayList<Articulos> items;
		DBArticulosAdapter dbarticulo = new DBArticulosAdapter(this);
		
		if (searchByDescripcion.getText().toString().length() != 0){
			 items = dbarticulo.getArticulos(searchByDescripcion.getText().toString().toUpperCase());
		}else{
			 items = dbarticulo.getAllArticulos();
		}
		
		for (int i = 0; i < items.size(); i++) {
			resultado.add(items.get(i));
		}
		adaptador.notifyDataSetChanged();
    		
    }
}
