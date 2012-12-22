package com.preventista.main;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.preventista.db.DBArticulosAdapter;
import com.preventista.db.DBPedidosAdapter;
import com.preventista.db.DBPedidosdetalleAdapter;
import com.preventista.entidades.Articulos;
import com.preventista.entidades.Pedidos;
import com.preventista.entidades.Pedidosdetalle;



public class PedidosSearchActivity extends Activity{
	
	private static final int ESTADO_SOLICITADO = 6; //estado de pedido 'solicitado'
	private int pos = -1;
	
	private ListView lvSearchPedidos;
	private ArrayList<Pedidos> rpedidos = new ArrayList<Pedidos>();
	
	private AdaptadorPedidos adaptador;
    
	//declaraciones para el datepicker
	private EditText etSearchFechaAlta; 
	private int mYear; 
	private int mMonth; 
	private int mDay; 
	static final int DATE_DIALOG_ID = 0;
	
	private DatePickerDialog.OnDateSetListener  mDateSetListener = 
			new DatePickerDialog.OnDateSetListener(){

				@Override
				public void onDateSet(DatePicker view, int year,
						int monthOfYear, int dayOfMonth) {
					// TODO Auto-generated method stub
					mYear = year;
					mMonth = monthOfYear;
					mDay = dayOfMonth;
					updateDisplay();
				}
		
	};
	
	static class ViewHolder {
        TextView lblCliente;
        TextView lblFechaAlta;
        TextView lblMontoTotal;
        TextView lblId;
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_pedidos);
		
		lvSearchPedidos = (ListView) findViewById(R.id.lvSearchPedidos);
		etSearchFechaAlta = (EditText) findViewById(R.id.edtSearchByFechaAltaPedido);
		
		etSearchFechaAlta.setClickable(true);
		etSearchFechaAlta.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DATE_DIALOG_ID);
			}
			
		});
		
		//obtener la fecha actual
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		
		//visualizar la fecha actual 
		//updateDisplay();
		showPedidos();
		
		lvSearchPedidos.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				 pos = position;
				 adaptador.notifyDataSetChanged();
				 openOptionsMenu(); 
			}
	 		 
	 	 });
	}
	
	
	/**
	 * Metodo para visualizar la fecha actual en un editext
	 * 
	 * */
	private void updateDisplay()
	{
		etSearchFechaAlta.setText(
				new StringBuilder()
				//el mes empieza en 0, entonces sumar 1
				.append(mDay).append("/") 
				.append(mMonth + 1).append("/") 
				.append(mYear).append(" ")
				);
	}


	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch(id){
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this,mDateSetListener,
					mYear, mMonth, mDay);
		}
		return null;
	}
	
	public void showPedidos()
	{
		DBPedidosAdapter dbapedido = new DBPedidosAdapter(this);
		rpedidos = dbapedido.getAllPedidos();
	 	adaptador =  new AdaptadorPedidos(this);
	 	lvSearchPedidos.setAdapter(adaptador);
	 	
	 	etSearchFechaAlta.addTextChangedListener(new TextWatcher() {

            // As the user types in the search field, the list is
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
           
					AlterAdapter();
            }

            @Override
            public void afterTextChanged(Editable arg0) {}
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
        });
	}
	
	private class AdaptadorPedidos extends ArrayAdapter<Pedidos>{
    	
    	//private LayoutInflater inflater;
    	Activity context;
    	
    	
    	AdaptadorPedidos(Activity context) {
    		super(context, R.layout.listitem_pedidos, rpedidos);
    		//inflater = LayoutInflater.from(context) ;
    		this.context = context;
    	}
    	
    	public View getView(final int position, View convertView, ViewGroup parent) {
    		Pedidos pedido = (Pedidos) this.getItem( position ); 
    	    View item = convertView;
		    final ViewHolder holder;
		    
		    if(item == null)
		    {
		    	LayoutInflater inflater = context.getLayoutInflater();
				item = inflater.inflate(R.layout.listitem_pedidos, null);
				
				holder = new ViewHolder();
				
				holder.lblCliente = (TextView) item.findViewById(R.id.lblClientePedidoListItem);
				holder.lblFechaAlta = (TextView) item.findViewById(R.id.lblFechaAltaPedidoListItem);
				holder.lblMontoTotal = (TextView) item.findViewById(R.id.lblMontoTotalPedidoListItem);
				holder.lblId = (TextView) item.findViewById(R.id.lblIdPedidoListItem);

				item.setTag(holder);
				
		    }else{
		    	holder = (ViewHolder)item.getTag();
		    }
		    
		    holder.lblCliente.setText(pedido.getApellnom());
			holder.lblFechaAlta.setText(pedido.getCustonCreatedAt());	
			holder.lblMontoTotal.setText(Float.toString(pedido.getMontototal()));
			holder.lblId.setText(Float.toString(pedido.get_id()));
		
			//si es true colocamos un background en el item del listview
			if(pos == position) {
				item.setBackgroundColor(Color.argb(200, 49, 176, 17));
			}else{
				item.setBackgroundColor(Color.argb(0, 0, 0, 0));
			}
		
			return(item);
		}
    }

	
	private void AlterAdapter()
    {
		
		rpedidos.clear(); 
		ArrayList<Pedidos> items;
		String fecha = null;
		
		//convertir a date la fecha ingresada en el campo etSearchFechaAlta
		PreRes pr = new PreRes();
		Date d = null;
		try {
			d = pr.stringToDate(etSearchFechaAlta.getText().toString().trim());
			fecha = pr.formatDateToBD(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		DBPedidosAdapter dbpedido = new DBPedidosAdapter(this);
		
		if (etSearchFechaAlta.getText().toString().length() != 0){
			 items = dbpedido.getByFecha(fecha);
		}else{
			 items = dbpedido.getAllPedidos();
		}
		
		for (int i = 0; i < items.size(); i++) {
			rpedidos.add(items.get(i));
		}
		adaptador.notifyDataSetChanged();
		
    }
	
	
	/**
	 * Opciones de Menu de la sección de busqueda de pedidos
	 * */
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{	
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu_search_pedidos, menu);
		return true;
	}
	
	@Override 
	public boolean onOptionsItemSelected(MenuItem item)
	{	
		switch(item.getItemId()){
		
		case R.id.mnShowPedido:
			if(pos == -1)
				Toast.makeText(this, "Selecciona un Pedido!", Toast.LENGTH_SHORT).show();
			else{
				Intent ir_a_ver_pedido = new Intent("com.preventista.main.SHOWPEDIDO");
				Bundle bolsaShowPedido = new Bundle();
				bolsaShowPedido.putString("_ID",Integer.toString(rpedidos.get(pos).get_id()));
				bolsaShowPedido.putString("CLIENTES_ID",Integer.toString(rpedidos.get(pos).getClientes_id()));
				bolsaShowPedido.putString("CLIENTESCATEGORIA_ID",Integer.toString(rpedidos.get(pos).getCategoria_id()));
				ir_a_ver_pedido.putExtras(bolsaShowPedido);
				startActivity(ir_a_ver_pedido);
			}
			return true;
			
		case R.id.mnEditPedido:
			if(pos == -1)
				Toast.makeText(this, "Selecciona un Pedido!", Toast.LENGTH_SHORT).show();
			else{
				if(rpedidos.get(pos).getEstado() == ESTADO_SOLICITADO)
				{ 
					Intent ir_a_editar_pedido = new Intent("com.preventista.main.EDITPEDIDO");
					Bundle bolsa = new Bundle();
					bolsa.putString("_ID",Integer.toString(rpedidos.get(pos).get_id()));
					bolsa.putString("CLIENTES_ID",Integer.toString(rpedidos.get(pos).getClientes_id()));
					bolsa.putString("CLIENTESCATEGORIA_ID",Integer.toString(rpedidos.get(pos).getCategoria_id()));
					ir_a_editar_pedido.putExtras(bolsa);
					startActivity(ir_a_editar_pedido);
				}else{
					Toast.makeText(this, "Sólo puedes editar Pedidos con estado 'SOLICITADO'", Toast.LENGTH_SHORT).show();
				}
			}
			return true;
		
		case R.id.mnDeletePedido:
			if(pos == -1){
				Toast.makeText(this, "Selecciona un Pedido!", Toast.LENGTH_SHORT).show();
			}else{
				if(rpedidos.get(pos).getEstado() == ESTADO_SOLICITADO ){
					checkConfirm();
				}else{
					Toast.makeText(this, "Solo puedes eliminar pedidos con estado 'SOLICITADO'", Toast.LENGTH_SHORT).show();
				}
			}
			return true;
		
		case R.id.mnMainMenuSearchPedidos:
			Intent ir_main_menu = new Intent(PedidosSearchActivity.this,PreventistaActivity.class); 
			startActivity(ir_main_menu);
			return true;
			
		default:
            return super.onOptionsItemSelected(item);
		}
		
	}
	
	
	private void checkConfirm()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("¿Estás seguro de eliminar este pedido?")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   if(deletePedido(rpedidos.get(pos).get_id())){
						   Toast.makeText(PedidosSearchActivity.this, "Pedido eliminado correctamente", Toast.LENGTH_SHORT).show();
		        	   }else{
		        		   Toast.makeText(PedidosSearchActivity.this, "Error al eliminar el pedido", Toast.LENGTH_SHORT).show();
		        	   }
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	
	private boolean deletePedido(int _id)
	{
		
		ArrayList<Pedidosdetalle> rpedidodetalle = new ArrayList<Pedidosdetalle>();
		DBPedidosAdapter dbpedidodelete = new DBPedidosAdapter(this);
		DBPedidosdetalleAdapter dbpd = new DBPedidosdetalleAdapter(this);
		rpedidodetalle = dbpd.getByPedidoId(_id);
		
		if(rpedidodetalle.size() > 0)
		{
			for(int i=0; i<rpedidodetalle.size(); i++)
			{
				Articulos articulo = new Articulos();
				articulo.set_id(rpedidodetalle.get(i).getArticulos_id());
				articulo.setStockreal(rpedidodetalle.get(i).getArticulos_stockreal());
				
				//actualizamos el stock
	    		DBArticulosAdapter dbarticulo = new DBArticulosAdapter(this);
	    		if(dbarticulo.increaseStock(articulo, rpedidodetalle.get(i).getCantidad())){
		    		//DBPedidosdetalleAdapter dbpd = new DBPedidosdetalleAdapter(this);
		    		dbpd.deletePedidodetalle(rpedidodetalle.get(i).get_id());
	    		}
			}
		}
		
		if(dbpedidodelete.deletePedido(_id)){
			return true;
		}else{
			return false;
		}
		
		
				
	}
}
