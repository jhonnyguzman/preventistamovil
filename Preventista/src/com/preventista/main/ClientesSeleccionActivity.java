package com.preventista.main;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.preventista.db.DBClienteAdapter;
import com.preventista.entidades.Clientes;

public class ClientesSeleccionActivity extends Activity {

	private int pos = -1;
	private static int save = -1;
	
	private ListView lvClientes;
	private ArrayList<Clientes> resultado = new ArrayList<Clientes>();
	
	// Field where user enters his search criteria
    private EditText searchByApellido;
    
    private AdaptadorSeleccionClientes adaptador;
    
    
    static class ViewHolder {
        TextView lblApellidoNombre;
        TextView lblCategoria;
        TextView lblId;
        TextView lblCatId;
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seleccion_clientes);
		lvClientes = (ListView) findViewById(R.id.lvSeleccionClientes);
		searchByApellido = (EditText) findViewById(R.id.edtSearchClienteByApellido);
		
		showClientes();
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        //searchByApellido.requestFocus();

        searchByApellido.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                InputMethodManager keyboard = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(searchByApellido, 0);
            }
        },200);
    }
	
	public void showClientes()
	{
		 DBClienteAdapter dbcliente = new DBClienteAdapter(this);
		 resultado = dbcliente.getAllClientes();
	 	 adaptador =  new AdaptadorSeleccionClientes(this);
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
		inflater.inflate(R.menu.options_menu_seleccion_clientes, menu);
		return true;
	}
	
	@Override 
	public boolean onOptionsItemSelected(MenuItem item)
	{	
		switch(item.getItemId()){
			
		case R.id.mnNextStep:
			if(pos == -1)
				Toast.makeText(this, "Selecciona un Cliente!", Toast.LENGTH_SHORT).show();
			else{
				Intent ir_a_nuevo_pedido = new Intent("com.preventista.main.ADDPEDIDO");
				Bundle bolsa = new Bundle();
				bolsa.putString("ID", Integer.toString(resultado.get(pos).get_id()));
				bolsa.putString("NOMBRE", resultado.get(pos).getNombre());
				bolsa.putString("APELLIDO", resultado.get(pos).getApellido());
				bolsa.putString("DIRECCION", resultado.get(pos).getDireccion());
				bolsa.putString("TELEFONO", resultado.get(pos).getTelefono());
				bolsa.putString("CLIENTESCATEGORIA_ID", Integer.toString(resultado.get(pos).getClientescategoria_id()));
				ir_a_nuevo_pedido.putExtras(bolsa);
				startActivity(ir_a_nuevo_pedido);
			}
			return true;
		
		case R.id.mnMainMenu:
			Intent ir_a_main = new Intent(ClientesSeleccionActivity.this,PreventistaActivity.class); 
			startActivity(ir_a_main);
			return true;
			
		default:
            return super.onOptionsItemSelected(item);
		}
		
	}
	
	
	class AdaptadorSeleccionClientes extends ArrayAdapter<Clientes>{
    	
    	Activity context;
    	
    	AdaptadorSeleccionClientes(Activity context) {
    		super(context, R.layout.listitem_seleccion_clientes, resultado);
    		this.context = context;
    	}
    	
    	public View getView(int position, View convertView, ViewGroup parent) {
			
    		View item = convertView;
		    ViewHolder holder;
		    if(item == null)
		    {
	    		LayoutInflater inflater = context.getLayoutInflater();
				item = inflater.inflate(R.layout.listitem_seleccion_clientes, null);
				
				 holder = new ViewHolder();
				 holder.lblApellidoNombre = (TextView)item.findViewById(R.id.lblApellidoNombreListItem);
				 holder.lblCategoria = (TextView)item.findViewById(R.id.lblCatClienteListItem);
				 holder.lblId = (TextView)item.findViewById(R.id.lblIdListItem);
				 holder.lblCatId = (TextView)item.findViewById(R.id.lblCatClienteIdListItem);
				 item.setTag(holder);
		    }else{
		    	 holder = (ViewHolder)item.getTag();
		    }
			
			holder.lblApellidoNombre.setText(resultado.get(position).getApellidoNombre());
			holder.lblCategoria.setText(resultado.get(position).getCatDescripcion());
			holder.lblId.setText(Integer.toString(resultado.get(position).get_id()));
			holder.lblCatId.setText(Integer.toString(resultado.get(position).getClientescategoria_id()));
			
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
