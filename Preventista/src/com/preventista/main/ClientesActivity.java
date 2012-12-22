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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.preventista.db.DBClienteAdapter;
import com.preventista.entidades.Clientes;

public class ClientesActivity extends Activity{
	
	private int pos = -1;
	private static int save = -1;
	
	private ListView lvClientes;
	private ArrayList<Clientes> resultado = new ArrayList<Clientes>();
	
	// Field where user enters his search criteria
    private EditText searchByApellido;
    
    private AdaptadorClientes adaptador;
    
    
    static class ViewHolder {
        TextView lblApellidoNombre;
        TextView lblId;
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_clientes);
		lvClientes = (ListView) findViewById(R.id.lvClientes);
		searchByApellido = (EditText) findViewById(R.id.edtSearchClienteByApellido);
		showClientes();
	}
	
	
	public void showClientes()
	{
		 DBClienteAdapter dbcliente = new DBClienteAdapter(this);
		 resultado = dbcliente.getAllClientes();
	 	 adaptador =  new AdaptadorClientes(this);
	 	 lvClientes.setAdapter(adaptador);
	 	 
	 	 lvClientes.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				 pos = position;
				 save = position; 
				 openOptionsMenu();
				 adaptador.notifyDataSetChanged();
			}
	 		 
	 	 });
	 	 
	 	searchByApellido.addTextChangedListener(new TextWatcher() {

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
		inflater.inflate(R.menu.options_menu_clientes, menu);
		return true;
	}
	
	@Override 
	public boolean onOptionsItemSelected(MenuItem item)
	{	
		switch(item.getItemId()){
		
		case R.id.mnAddCliente:
			Intent ir_a_cliente_nuevo = new Intent("com.preventista.main.ADDCLIENTE");
			startActivity(ir_a_cliente_nuevo);
			return true;
			
		case R.id.mnEditCliente:
			if(pos == -1)
				Toast.makeText(this, "Selecciona un Cliente!", Toast.LENGTH_SHORT).show();
			else{
				Intent ir_a_clientes_editar = new Intent("com.preventista.main.EDITCLIENTE");
				Bundle bolsa = new Bundle();
				bolsa.putString("ID", Integer.toString(resultado.get(pos).get_id()));
				bolsa.putString("NOMBRE", resultado.get(pos).getNombre());
				bolsa.putString("APELLIDO", resultado.get(pos).getApellido());
				bolsa.putString("DIRECCION", resultado.get(pos).getDireccion());
				bolsa.putString("TELEFONO", resultado.get(pos).getTelefono());
				bolsa.putString("CREATED_AT", resultado.get(pos).getCreated_at());
				bolsa.putString("UPDATED_AT", resultado.get(pos).getUpdated_at());
				bolsa.putString("CLIENTESCATEGORIA_ID", Integer.toString(resultado.get(pos).getClientescategoria_id()));
				ir_a_clientes_editar.putExtras(bolsa);
				startActivity(ir_a_clientes_editar);
			}
			return true;
			
		case R.id.mnCcCliente:
			if(pos == -1)
				Toast.makeText(this, "Selecciona un Cliente!", Toast.LENGTH_SHORT).show();
			else{
				Intent ir_a_clientes_cc = new Intent("com.preventista.main.CUENTACORRIENTE");
				Bundle bolsa = new Bundle();
				bolsa.putInt("_ID", resultado.get(pos).get_id());
				bolsa.putString("APELLIDO_NOMBRE", resultado.get(pos).getApellido() + " " + resultado.get(pos).getNombre());
				ir_a_clientes_cc.putExtras(bolsa);
				startActivity(ir_a_clientes_cc);
			}
			return true;
		
		case R.id.mnMainMenu:
			Intent ir_clientes = new Intent(ClientesActivity.this,PreventistaActivity.class); 
			startActivity(ir_clientes);
			return true;
			
		default:
            return super.onOptionsItemSelected(item);
		}
		
	}
	
	
	class AdaptadorClientes extends ArrayAdapter<Clientes> {
    	
    	Activity context;
    	
    	AdaptadorClientes(Activity context) {
    		super(context, R.layout.listitem_clientes, resultado);
    		this.context = context;
    	}
    	
    	public View getView(int position, View convertView, ViewGroup parent) {
			
    		View item = convertView;
		    ViewHolder holder;
		    if(item == null)
		    {
	    		LayoutInflater inflater = context.getLayoutInflater();
				item = inflater.inflate(R.layout.listitem_clientes, null);
				
				 holder = new ViewHolder();
				 holder.lblApellidoNombre = (TextView)item.findViewById(R.id.lblApellidoNombreListItem);
				 holder.lblId = (TextView)item.findViewById(R.id.lblIdListItem);
				 item.setTag(holder);
		    }else{
		    	 holder = (ViewHolder)item.getTag();
		    }
			
			holder.lblApellidoNombre.setText(resultado.get(position).getApellidoNombre());
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
		ArrayList<Clientes> items;
		DBClienteAdapter dbcliente = new DBClienteAdapter(this);
		
		if (searchByApellido.getText().toString().length() != 0){
			 items = dbcliente.getClientes(searchByApellido.getText().toString().toUpperCase());
		}else{
			 items = dbcliente.getAllClientes();
		}
		
		for (int i = 0; i < items.size(); i++) {
			resultado.add(items.get(i));
		}
		adaptador.notifyDataSetChanged();
    		
    }
}
