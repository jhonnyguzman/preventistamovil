package com.preventista.main;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.preventista.db.DBArticulosAdapter;
import com.preventista.db.DBPedidosAdapter;
import com.preventista.db.DBPedidosdetalleAdapter;
import com.preventista.db.DBPedidosdetalleTempAdapter;
import com.preventista.db.DBTramitesAdapter;
import com.preventista.entidades.Articulos;
import com.preventista.entidades.Pedidos;
import com.preventista.entidades.Pedidosdetalle;
import com.preventista.entidades.Pedidosdetalletemp;
import com.preventista.entidades.Tramites;

public class PedidosAddActivity extends Activity{
	
	private static final int CATCLIENTE1 = 1;
	private static final int CATCLIENTE2 = 2;
	private static final int CATCLIENTE3 = 3;
	
	private static final int ESTADO_PEDIDO = 6; //estado solicitado
	
	private int pos = -1;
	
	private static int catcliente;
	private static String apellido_cliente;
	private static String nombre_cliente;
	private static int id_cliente;
	
	private boolean[] itemSelection;
	private boolean flag = false;
	
	private int tramites_id = -1;
	
	private ListView lvArticulos, lvPedidoDetalle;
	
	private ArrayList<Integer> itemsSeleccionados = new ArrayList<Integer>();
	private ArrayList<Articulos> resultado = new ArrayList<Articulos>();
	private ArrayList<Pedidosdetalletemp> rpedidod = new ArrayList<Pedidosdetalletemp>();
	
	ArrayList<Tramites> tramites = new ArrayList<Tramites>();
	ArrayList<String> tramites_descripcion = new ArrayList<String>();
    
	ArrayAdapter<String> adaptador_tramites;
	
	// widgets
    private EditText searchByDescripcion, etCantidad, etSubtotal, etMtoAcordado;
    private ImageButton btnSaveLinea;
    private Button btnEditPedidoDetalle, btnDeletePedidoLinea;
    private TabHost thPedidos;
    private Bundle bundle = null;
    
    private AdaptadorSeleccionArticulos adaptador;
    private AdaptadorPedidoDetalle adaptadorpd;
    
    private PreRes preres = new PreRes();
    
    //atributos para las preferencias
  	private SharedPreferences sp;
  	private int nextidpedido, nextidpedidodetalle;
	
    private static class ViewHolder {
        TextView lblDescripcion;
        TextView lblStockReal;
        TextView lblPrecioVenta;
        TextView lblId;
    }
    
    private static class ViewHolderPedidoDetalle {
        TextView lblDescripcionPD;
        TextView lblCantidadPD;
        TextView lblSubtotalPD;
        TextView lblIdPD;
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//Con esto, eliminamos la barra de tareas de Android y hacemos que fullscreen de la actividad
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.add_form_pedidos);
		
		//preferencias
		sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		nextidpedido = Integer.parseInt(sp.getString("etPrefPedidosID", "50000"));
		nextidpedidodetalle = Integer.parseInt(sp.getString("etPrefPedidoDetalleID", "50000"));
				
		lvArticulos = (ListView) findViewById(R.id.lvSeleccionArticulos);
		lvPedidoDetalle = (ListView) findViewById(R.id.lvPedidoDetalle);
		thPedidos = (TabHost) findViewById(R.id.thPedidos);
		searchByDescripcion = (EditText) findViewById(R.id.edtSearchByDescripcionArticulo);
		etCantidad = (EditText) findViewById(R.id.etCantidadArticulo);
		etMtoAcordado = (EditText) findViewById(R.id.etMontoAcordadoArticulo);
		etSubtotal = (EditText) findViewById(R.id.etSubtotalPedido);
		btnSaveLinea = (ImageButton) findViewById(R.id.btnSaveLineaPedido);
		
		//desactivamos campo subtotal
		etSubtotal.setEnabled(false);
		etSubtotal.setFocusable(false);
		etSubtotal.setClickable(false);
				
		//botones de la seccion de detalle del pediddo
		btnEditPedidoDetalle = (Button) findViewById(R.id.btnEditPedidoDetalle);
		btnDeletePedidoLinea = (Button) findViewById(R.id.btnDeletePedidoDetalle);
		
		//configuracion del tabhost
		thPedidos.setup();
		TabSpec spec = thPedidos.newTabSpec("SELECCIONARTICULO");
		spec.setIndicator("Selección");
		spec.setContent(R.id.tabSeleccionArticulo);
		thPedidos.addTab(spec);
		
		spec = thPedidos.newTabSpec("DETALLEPEDIDO");  
		spec.setIndicator("Detalle");
		spec.setContent(R.id.tabDetallePedido);
		thPedidos.addTab(spec);
		
		//extras desde la actividad de ClientesSeleccionActivity
		bundle = getIntent().getExtras();
		
		catcliente = Integer.parseInt(bundle.getString("CLIENTESCATEGORIA_ID"));
		apellido_cliente = bundle.getString("APELLIDO");
		nombre_cliente = bundle.getString("NOMBRE");
		id_cliente = Integer.parseInt(bundle.getString("ID"));
		
		checkCategoriaCliente();
		updateStockArticulo();
		showArticulos();
		
		// When item is tapped, toggle checked properties of CheckBox and Articulos.
		lvArticulos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		 
	      @Override
	      public void onItemClick( AdapterView<?> parent, View item, 
	                               int position, long id) {
	        Articulos articulo = adaptador.getItem( position );
	        articulo.toggleChecked();
	        
	        itemSelection[position] = articulo.isChecked();
	        checkItemSeleccionados(articulo);
	        
	        adaptador.notifyDataSetChanged();
	        
	        setF(etCantidad,false);
	        
	      }
	    });
		
		
		lvPedidoDetalle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			 
		      @Override
		      public void onItemClick( AdapterView<?> parent, View item, 
		                               int position, long id) {
		        pos = position;
		        adaptadorpd.notifyDataSetChanged();
		      }
		});
		
		btnSaveLinea.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(checkStockOnline()){
					saveLinea();
				}
			}
			
		});
		
		etCantidad.addTextChangedListener(new TextWatcher() {
			
	            // As the user types in the search field, the list is
	            @Override
	            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	                if(arg0.length() > 0){
	                	checkStockOnline();
	                }
	            }

	            @Override
	            public void afterTextChanged(Editable arg0) { }
	            @Override
	            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
	    });
		
		
		//eventos onclick para los botones de la seccion de detalle de pedidos
		
		btnEditPedidoDetalle.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(rpedidod.size()>0){
					showDialogEditPedidoDetalle();
				}else{
					Toast.makeText(PedidosAddActivity.this, "No hay Lineas de pedido", Toast.LENGTH_SHORT).show();
				}
			}
			
		});
		
		btnDeletePedidoLinea.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(rpedidod.size()>0){
					deletePedidoLinea();
				}else{
					Toast.makeText(PedidosAddActivity.this, "No hay Lineas de pedido", Toast.LENGTH_SHORT).show();
				}
			}
			
		});
		
		//config height of the tabhost
		thPedidos.getTabWidget().getChildAt(0).getLayoutParams().height = 45;
		thPedidos.getTabWidget().getChildAt(1).getLayoutParams().height = 45;
	}
	
	@Override
    protected void onResume() {
        super.onResume();

        searchByDescripcion.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                InputMethodManager keyboard = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(searchByDescripcion, 0);
            }
        },200);
        
    }
	
	@Override
	protected void onDestroy()
	{
		//deletePedidoDetalleTemp();
	    super.onDestroy();
	}
	
	@Override
	public void onBackPressed() 
	{  
		if(checkCargaPedidos() == true)
		{
		    AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    builder.setMessage("Haz agregado articulos al pedido actual, ¿Deseas salir de todas formas?")
		           .setCancelable(false)
		           .setPositiveButton("Si", new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int id) {
		            	   updateStockArticulo();
		            	   PedidosAddActivity.super.onBackPressed();
		               }
		           })
		           .setNegativeButton("No", new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int id) {
		                    dialog.cancel();
		               }
		           }).create().show();
	   
		}
	    
	}
	
	public void showArticulos()
	{
		 DBArticulosAdapter dbarticulo = new DBArticulosAdapter(this);
		 resultado = dbarticulo.getAllArticulos();
	 	 adaptador =  new AdaptadorSeleccionArticulos(this);
	 	 lvArticulos.setAdapter(adaptador);
	 	 
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
		inflater.inflate(R.menu.options_menu_pedidos, menu);
		return true;
	}
	
	@Override 
	public boolean onOptionsItemSelected(MenuItem item)
	{	
		switch(item.getItemId()){
		
		case R.id.mnFinalizarCargaPedido:
			if(rpedidod.size()>0){
				showDialogPedidoFinish();
			}else{
				Toast.makeText(PedidosAddActivity.this, "No haz agregado articulos", Toast.LENGTH_SHORT).show();
			}
			
			return true;
			
		case R.id.mnIrSeleccionCliente:
			if(checkCargaPedidos() == true)
			{
			    AlertDialog.Builder builder = new AlertDialog.Builder(this);
			    builder.setMessage("Haz agregado articulos al pedido actual, ¿Deseas salir de todas formas?")
			           .setCancelable(false)
			           .setPositiveButton("Si", new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int id) {
			            	   updateStockArticulo();
			            	   Intent ir_a_seleccion_cliente = new Intent("com.preventista.main.SELECCIONCLIENTE");
			       			   startActivity(ir_a_seleccion_cliente);
			               }
			           })
			           .setNegativeButton("No", new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int id) {
			                    dialog.cancel();
			               }
			           }).create().show();
		   
			}else{
				Intent ir_a_seleccion_cliente = new Intent("com.preventista.main.SELECCIONCLIENTE");
    			startActivity(ir_a_seleccion_cliente);
			}
			
			return true;
		
		case R.id.mnMainMenu:
			if(checkCargaPedidos() == true)
			{
			    AlertDialog.Builder builder = new AlertDialog.Builder(this);
			    builder.setMessage("Haz agregado articulos al pedido actual, ¿Deseas salir de todas formas?")
			           .setCancelable(false)
			           .setPositiveButton("Si", new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int id) {
			            	   updateStockArticulo();
			            	   Intent ir_main_menu = new Intent(PedidosAddActivity.this,PreventistaActivity.class); 
			       			   startActivity(ir_main_menu);
			               }
			           })
			           .setNegativeButton("No", new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int id) {
			                    dialog.cancel();
			               }
			           }).create().show();
		   
			}else{
				Intent ir_main_menu = new Intent(PedidosAddActivity.this,PreventistaActivity.class); 
    			startActivity(ir_main_menu);
			}
			
			return true;
		
		case R.id.mnCCPedidos:
			Intent ir_a_clientes_cc = new Intent("com.preventista.main.CUENTACORRIENTE");
			Bundle bolsa = new Bundle();
			bolsa.putInt("_ID", id_cliente);
			bolsa.putString("APELLIDO_NOMBRE", apellido_cliente + " " + nombre_cliente);
			ir_a_clientes_cc.putExtras(bolsa);
			startActivity(ir_a_clientes_cc);
			
			return true;
			
		default:
            return super.onOptionsItemSelected(item);
		}
		
	}
	
	
	private class AdaptadorSeleccionArticulos extends ArrayAdapter<Articulos>{
    	
    	//private LayoutInflater inflater;
    	Activity context;
    	
    	
    	AdaptadorSeleccionArticulos(Activity context) {
    		super(context, R.layout.listitem_seleccion_articulos, resultado);
    		//inflater = LayoutInflater.from(context) ;
    		this.context = context;
    		itemSelection = new boolean[resultado.size()];
    	}
    	
    	public View getView(final int position, View convertView, ViewGroup parent) {
    		Articulos articulo = (Articulos) this.getItem( position ); 
    	    View item = convertView;
		    final ViewHolder holder;
		    
		    if(item == null)
		    {
		    	LayoutInflater inflater = context.getLayoutInflater();
				item = inflater.inflate(R.layout.listitem_seleccion_articulos, null);
				
				holder = new ViewHolder();
				
				holder.lblDescripcion = (TextView) item.findViewById(R.id.lblDescripcionArticuloSeleccionListItem);
				holder.lblStockReal = (TextView) item.findViewById(R.id.lblStockRealArticuloSeleccionListItem);
				holder.lblPrecioVenta = (TextView) item.findViewById(R.id.lblPrecioVentaArticuloSeleccionListItem);
				holder.lblId = (TextView) item.findViewById(R.id.lblIdArticuloSeleccionListItem);
				//holder.checkBoxArticulo = (CheckBox) item.findViewById(R.id.CheckBoxArticuloSeleccion);

				item.setTag(holder);
				
		    }else{
		    	holder = (ViewHolder)item.getTag();
		    }
		    
		   /* holder.checkBoxArticulo.setOnCheckedChangeListener(new OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					itemSelection[position] = holder.checkBoxArticulo.isChecked();
					//checkItemSeleccionados(articulo);
					//Log.d("CHECK", Boolean.toString(itemSelection[position]));
				}
			});*/
		    
		  
		    //Display articulo data
		    holder.lblDescripcion.setText(articulo.getDescripcion());
			holder.lblStockReal.setText(Integer.toString(articulo.getStockreal()));
			
			holder.lblId.setText(Integer.toString(articulo.get_id()));
			
			if(catcliente == CATCLIENTE1) holder.lblPrecioVenta.setText(Float.toString(articulo.getPrecio1()));
			else if(catcliente == CATCLIENTE2) holder.lblPrecioVenta.setText(Float.toString(articulo.getPrecio2()));
			else if(catcliente == CATCLIENTE3) holder.lblPrecioVenta.setText(Float.toString(articulo.getPrecio3()));
			
			//holder.checkBoxArticulo.setChecked(itemSelection[position]);
			
			//si es true colocamos un background en el item del listview
			if(itemSelection[position]) {
				item.setBackgroundColor(Color.argb(200, 49, 176, 17));
			}else{
				item.setBackgroundColor(Color.argb(0, 0, 0, 0));
			}
		
			return(item);
		}
    }
	
	
	
	private class AdaptadorPedidoDetalle extends ArrayAdapter<Pedidosdetalletemp>{
    	
    	//private LayoutInflater inflater;
    	Activity context;
    	
    	
    	AdaptadorPedidoDetalle(Activity context) {
    		super(context, R.layout.listitem_pedido_detalle, rpedidod);
    		//inflater = LayoutInflater.from(context) ;
    		this.context = context;
    	}
    	
    	public View getView(final int position, View convertView, ViewGroup parent) {
    		Pedidosdetalletemp pedidodetalle = (Pedidosdetalletemp) this.getItem( position ); 
    	    View item = convertView;
		    final ViewHolderPedidoDetalle holder;
		    
		    if(item == null)
		    {
		    	LayoutInflater inflater = context.getLayoutInflater();
				item = inflater.inflate(R.layout.listitem_pedido_detalle, null);
				
				holder = new ViewHolderPedidoDetalle();
				
				holder.lblDescripcionPD = (TextView) item.findViewById(R.id.lblDescripcionPedidoDetalleListItem);
				holder.lblCantidadPD = (TextView) item.findViewById(R.id.lblCantidadPedidoDetalleListItem);
				holder.lblSubtotalPD = (TextView) item.findViewById(R.id.lblSubtotalPedidoDetalleListItem);
				holder.lblIdPD = (TextView) item.findViewById(R.id.lblIdPedidoDetalleListItem);

				item.setTag(holder);
				
		    }else{
		    	holder = (ViewHolderPedidoDetalle)item.getTag();
		    }
		    
		    holder.lblDescripcionPD.setText(pedidodetalle.getArticulos_descripcion());
			holder.lblCantidadPD.setText(Integer.toString(pedidodetalle.getCantidad()));	
			holder.lblIdPD.setText(Integer.toString(pedidodetalle.get_id()));
			holder.lblSubtotalPD.setText(Float.toString(preres.formatearFloat(pedidodetalle.getSubtotal())));
		
			//si es true colocamos un background en el item del listview
			if(pos == position) {
				item.setBackgroundColor(Color.argb(200, 49, 176, 17));
			}else{
				item.setBackgroundColor(Color.argb(0, 0, 0, 0));
			}
		
			return(item);
		}
    }



	 // Filters list of articulos based on user search criteria. If no information is filled in, contact list will be blank.
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
		
		//ponemos a false cada item del array de los estados de los checkboxs
		for(int i=0; i < itemSelection.length; i++){
			itemSelection[i] = false;
		}
		
		//verificamos cuales de los items ya seleccionados estan dentro 
		//del conjunto de resultados filtrados y
		//en caso de algunos de los items seleccionados se encuentren
		//ponemos a true el iten correspondiente del array de booleanos de los checkboxs
		for (int i=0; i<itemsSeleccionados.size(); i++){
			for(int j=0; j<items.size(); j++){
				if(itemsSeleccionados.get(i).intValue() == items.get(j).get_id()){
					itemSelection[j] = true;
				}
			}
		}
		adaptador.notifyDataSetChanged();
    		
    }
    
    
    private void checkCategoriaCliente()
    {
    	TextView lblPrecioVenta = (TextView) findViewById(R.id.lblPrecioVentaArticuloSeleccion);
    	
    	switch(catcliente){
    	case CATCLIENTE1:
    		lblPrecioVenta.setText("PV1");
    		break;
    	case CATCLIENTE2:
    		lblPrecioVenta.setText("PV2");
    		break;
    	case CATCLIENTE3:
    		lblPrecioVenta.setText("PV3");
    		break;
    	}
    }
    
    
    private void checkItemSeleccionados(Articulos articulo)
    {
    	for(int i = 0; i < itemsSeleccionados.size(); i++){
        	if(itemsSeleccionados.get(i).intValue() == articulo.get_id()){
        		flag = true;
        		Log.d("ItemsDeleted", Integer.toString(itemsSeleccionados.get(i).intValue()));
        		itemsSeleccionados.remove(i); 		
        	}
        }
        if(flag == false){
        	itemsSeleccionados.add(articulo.get_id());  
        	//Toast.makeText(PedidosAddActivity.this, "Item seleccionado: " + Integer.toString(articulo.get_id()), Toast.LENGTH_SHORT).show();
        	//Log.d("Items", Integer.toString(itemsSeleccionados.size()));
        }else{
        	//Toast.makeText(PedidosAddActivity.this, "Item no seleccionado: " + Integer.toString(articulo.get_id()), Toast.LENGTH_SHORT).show();
        	flag = false;
        }
    }
    
    private boolean checkStockOnline()
    {
    	ArrayList<Articulos> statusStock = new ArrayList<Articulos>();
    	String cantidad = etCantidad.getText().toString().trim();
    	String msg = "";
    	
    	if(cantidad.length() > 0)
    	{
	    	for(int i=0; i < itemsSeleccionados.size(); i++)
	    	{
	    		for(int j= 0; j < resultado.size(); j++){
	    			if(resultado.get(j).get_id() == itemsSeleccionados.get(i)){
	    				if(calcStock(cantidad,resultado.get(j).getStockreal()) == false)
	    					statusStock.add(resultado.get(j));
	    			}
	    		}
	    	}
	    	if(statusStock.size() > 0){
	    		msg = "Error: \n";
	    		for(int i=0;i<statusStock.size();i++){
	    			msg = msg + statusStock.get(i).getDescripcion().concat(" stock insuficiente, \n");
	    		}
	    		setF(etCantidad, true);
	    		
	    		final Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
	    		toast.show();
	    		
	    		Handler handler = new Handler();
		            handler.postDelayed(new Runnable() {
		               @Override
		               public void run() {
		                   toast.cancel(); 
		               }
		        }, 600);
		        
		        return false;
	    	}else{
	    		return true;
	    	}
    	}else{
    		final Toast toast = Toast.makeText(this, "Ingrese cantidad", Toast.LENGTH_SHORT);
    		toast.show();
    		
    		Handler handler = new Handler();
	            handler.postDelayed(new Runnable() {
	               @Override
	               public void run() {
	                   toast.cancel(); 
	               }
	        }, 600);
	        
	        return false;
    	}
    }
    
    
    /**
     * Este metodo coloca el foco de un elemento y muestra el teclado
     * */
    private void setF(EditText et, boolean empty)
    {
    	 et.requestFocus();
    	 InputMethodManager keyboard = (InputMethodManager)
         getSystemService(Context.INPUT_METHOD_SERVICE);
         keyboard.showSoftInput(et, 0);
         if(empty) et.setText("");
         else et.selectAll();
    }
    
    
    /**
     * Este metodo guarda una  linea de pedido temporal
     * */
    private void saveLinea()
    {
    	String montoacordado = etMtoAcordado.getText().toString().trim();
    	String cantidad = etCantidad.getText().toString().trim();
    	
    	if(itemsSeleccionados.size() > 0)
    	{
	    	if(cantidad.length() > 0)
	    	{
	    		//recorremos array con los articulos seleccionados
		    	for(int i=0; i < itemsSeleccionados.size(); i++)
		    	{
		    		Pedidosdetalletemp pedidodetalletemp = new Pedidosdetalletemp();
		    		
		    		//traemos al articulo de la bd para obtener precio de venta y calcular el stock.
		    		DBArticulosAdapter dbarticulo = new DBArticulosAdapter(this);
		    		Articulos articulo = new Articulos();
		    		articulo = dbarticulo.getArticulosById(itemsSeleccionados.get(i));
		    		
		    		pedidodetalletemp.setArticulos_id(itemsSeleccionados.get(i));
		    		pedidodetalletemp.setCantidad(Integer.parseInt(cantidad));
		    		
		    		//si existe monto acordamos lo agregamos
		    		if(montoacordado.length() > 0 ){
		    			pedidodetalletemp.setMontoacordado(Float.parseFloat(montoacordado));
		    		}
		    		
		    		pedidodetalletemp.setPv(setPv2(articulo));
		    		//calcular subtotal
					pedidodetalletemp.setSubtotal(setSubtotal(Integer.parseInt(cantidad), pedidodetalletemp.getPv(), montoacordado));
		    		
		    		//guardamos lineas de pedido
		    		DBPedidosdetalleTempAdapter dbadapter = new DBPedidosdetalleTempAdapter(this); 
		    		if(dbadapter.addPedidodetalletemp(pedidodetalletemp)){
		    			if(dbarticulo.updateStock(articulo, Integer.parseInt(cantidad)) == false){
		    				Toast.makeText(this, "Error al actualizar stock", Toast.LENGTH_SHORT).show();
			    			setF(searchByDescripcion, true);
		    			}
		    		}else{
		    			Toast.makeText(this, "Error al agregar articulos", Toast.LENGTH_SHORT).show();
		    			setF(searchByDescripcion,false);
		    		}
		    		
		    		/*Log.d("articulo_id", Integer.toString(pedidodetalletemp.getArticulos_id()));
		    		Log.d("cantidad", Integer.toString(pedidodetalletemp.getCantidad()));
		    		Log.d("pv", Float.toString(pedidodetalletemp.getPv()));
		    		Log.d("subtotal", Float.toString(pedidodetalletemp.getSubtotal()));*/
		    		
		    		
		    		
		    	}
		    	
		    	//minimizar tiempo de aparicion de mensaje toast
		    	/*final Toast toast = Toast.makeText(this, "Articulos agregados correctamente", Toast.LENGTH_SHORT);
	    		toast.show();
	    		
	    		Handler handler = new Handler();
		            handler.postDelayed(new Runnable() {
		               @Override
		               public void run() {
		                   toast.cancel(); 
		               }
		        }, 900);*/
		            
		    	setF(searchByDescripcion, true);
		    	
		    	destroyItemsSeleccionados();
		    	calcTotal();
		    	showPedidoDetalle();
		    	etCantidad.setText("1");
		    	etMtoAcordado.setText("");
		    	
		    	
	    	}else{
	    		Toast.makeText(PedidosAddActivity.this, "Ingrese cantidad", Toast.LENGTH_SHORT).show();
	    		setF(etCantidad, false);
	    	}
    	}else{
    		Toast.makeText(PedidosAddActivity.this, "Selecciona un Articulo", Toast.LENGTH_SHORT).show();
    		setF(searchByDescripcion, false);
    	}
    	
    }
    
    
    /**
     * Este metodo determina la categoria del cliente
     * y en base a eso retorna el precio adecuado 
     * para mostrar en la parte de seleccion  de articulos
     * */
    /*private float setPv(int j)
    {
    	float pv = 0;
    	
    	if(catcliente == CATCLIENTE1)  
			pv = resultado.get(j).getPrecio1();
		if(catcliente == CATCLIENTE2)
			pv = resultado.get(j).getPrecio2();
		if(catcliente == CATCLIENTE3)
			pv = resultado.get(j).getPrecio3();
		return pv;
    }*/
    
    
    /**
     * Este metodo determina la categoria del cliente
     * y en base a eso retorna el precio de venta adecuado 
     * para mostrar en la parte de seleccion  de articulos
     * */
    private float setPv2(Articulos articulo)
    {
    	float pv = 0;
    	
    	if(catcliente == CATCLIENTE1)  
			pv = articulo.getPrecio1();
		if(catcliente == CATCLIENTE2)
			pv = articulo.getPrecio2();
		if(catcliente == CATCLIENTE3)
			pv = articulo.getPrecio3();
		return pv;
    }
    
    
    /**
     * Este metodo  calcula y retorna el subtotal de cada linea de pedido que
     * se va ingresando 
     * */
    public float setSubtotal(int cant, float pv, String montoacordado)
    {
    	float sub = 0;
    	float mt = 0;
    	
    	if(montoacordado.length() > 0 ){
	    	mt = Float.parseFloat(montoacordado);
	    	if(mt >= 0 ){
	    			sub = cant * mt;
	    	}else{
	    		sub = cant * pv;
	    	}
    	}else{
    		sub = cant * pv;
    	}
    	return preres.formatearFloat(sub);
    }
    
    
    /**
     * Este metodo calcula si la cantidad ingresa es menor al
     * stockreal del articulo seleccionado, retorna verdadero si la 
     * condicion es verdadera
     * */
    private boolean calcStock(String cantidad, int stockreal)
    {
    	int res = stockreal - Integer.parseInt(cantidad); 
    	if(res >= 0) return true;
    	else return false;
    }
    
    
    /**
     * Este metodo elimina todas las lineas de pedidos temporales
     * */
    private void deletePedidoDetalleTemp()
    {
    	DBPedidosdetalleTempAdapter dbpedidodetalle = new DBPedidosdetalleTempAdapter(this);
    	if(dbpedidodetalle.deleteAllPedidodetalletemp()){
    		//Toast.makeText(this, "Pedidos detalle temp delete!", Toast.LENGTH_SHORT).show();
    		//Log.d("PEDIDODETALLETEMP", "Los registros de la tabla pedidos detalle temporal se han eliminado correctamente!");
    	}else{
    		//Toast.makeText(this, "Pedidos detalle temp NO delete!", Toast.LENGTH_SHORT).show();
    		//Log.d("PEDIDODETALLETEMP", "Los registros de la tabla pedidos detalle temporal no se han eliminado correctamente!");
    	}
    }
    
    
    /**
     * Este metodo verifica si existen lineas de pedidos temporales que han quedado 
     * huerfanas por algun error que haya surgido en el sistema. Si existen actualiza
     * el stockreal de cada articulo con la cantidad de cada linea de pedido temporal.
     * Por ejemplo si cuando se estuvo realizando la carga de un pedido, el usuario
     * ha salido de  la aplicacion o a vuelto a la parte de seleccion de cliente o la aplicacion
     * se ha cerrado de forma inesperada, cuando se vuelva a la seccion de seleccion de 
     * articulos, este metodo sera ejecutado al momento de crearse la actividad de nuevo pedido, el cual
     * realizara el control adecuado para mantener uniforme el stock de los articulos. 
     * */
    private void updateStockArticulo()
    {
    	ArrayList<Pedidosdetalletemp> pdt = new ArrayList<Pedidosdetalletemp>();
    	DBPedidosdetalleTempAdapter dbpedidodetalle = new DBPedidosdetalleTempAdapter(this);
    	pdt = dbpedidodetalle.getAllPedidodetalletemp();
    	if(pdt.size()>0){
    		for(int i=0; i < pdt.size(); i++){
    			Articulos articulo = new Articulos();
    			articulo.set_id(pdt.get(i).getArticulos_id());
    			articulo.setStockreal(pdt.get(i).getArticulos_stockreal() + pdt.get(i).getCantidad());
    			
    			DBArticulosAdapter dbarticulo = new DBArticulosAdapter(this);
    			dbarticulo.updateStock(articulo,0);
    		}
    		if(dbpedidodetalle.deleteAllPedidodetalletemp()){
    			//Toast.makeText(this, "Pedidos detalle temp delete!", Toast.LENGTH_SHORT).show();
    			//Log.d("PEDIDODETALLETEMP", "Los registros de la tabla pedidos detalle temporal se han eliminado correctamente!");
    		}else{
    			//Toast.makeText(this, "Pedidos detalle temp NO delete!", Toast.LENGTH_SHORT).show();
    			//Log.d("PEDIDODETALLETEMP", "Los registros de la tabla pedidos detalle temporal no se han eliminado correctamente!");
    		}
    	}
    }
    
    
    /**
     * Este método verifica si existen datos en la tabla pedidodetalletemp con el
     * objetivo de poder controlar la tecla de volver al momento de realizar una carga
     * de un pedido. Por ejemplo si mientras se esta dando de alta un pedido, el usuario
     * presiona la tecla back sin querer le saldra un cuadro de confirmación para que
     * el mismo decida proceder o no con la acción realizada.
     * */
    private boolean checkCargaPedidos()
    {
    	ArrayList<Pedidosdetalletemp> pdt = new ArrayList<Pedidosdetalletemp>();
    	DBPedidosdetalleTempAdapter dbpedidodetalle = new DBPedidosdetalleTempAdapter(this);
    	pdt = dbpedidodetalle.getAllPedidodetalletemp();
    	if(pdt.size()>0){
    		return true;
    	}
    	return false;
    }
    
    
    /**
     * Este metodo elimina todos los articulos
     * seleccionados cuando se hace click en el boton de
     * añadir linea
     * */
    private void destroyItemsSeleccionados()
    {
    	itemsSeleccionados.clear();
    	for(int i = 0; i < itemSelection.length; i++){
    		itemSelection[i] = false;
    	}
    	//adaptador.notifyDataSetChanged();
    }
    
    
    /**
     * Este metodo calcula y muestra el total del pedido cargado
     * hasta el momento, a partir de las lineas de pedidos temporales
     * */
    private void calcTotal()
    {
    	float sub = 0;
    	ArrayList<Pedidosdetalletemp> pedidodetalletemp = new ArrayList<Pedidosdetalletemp>(); 
    	DBPedidosdetalleTempAdapter dbpedidodetalle = new DBPedidosdetalleTempAdapter(this);
    	pedidodetalletemp = dbpedidodetalle.getAllPedidodetalletemp();
    	for(int i=0; i < pedidodetalletemp.size(); i++){
    		sub = sub + pedidodetalletemp.get(i).getSubtotal();
    	}
    	etSubtotal.setText(Float.toString(preres.formatearFloat(sub)));
    }
    
    
    /**
     * Este metodo calcula y devuelve el total del pedido cargado
     * hasta el momento, a partir de las lineas de pedidos temporales
     * */
    private float getTotalPedido()
    {
    	float sub = 0;
    	ArrayList<Pedidosdetalletemp> pedidodetalletemp = new ArrayList<Pedidosdetalletemp>(); 
    	DBPedidosdetalleTempAdapter dbpedidodetalle = new DBPedidosdetalleTempAdapter(this);
    	pedidodetalletemp = dbpedidodetalle.getAllPedidodetalletemp();
    	for(int i=0; i < pedidodetalletemp.size(); i++){
    		sub = sub + pedidodetalletemp.get(i).getSubtotal();
    	}
    	return sub;
    }
    
    
    /**
     * Este metodo muestra todas las lineas de pedido temporales 
     * cargadas hasta el momento en la solpa detalle del tabhost
     * */
    private void showPedidoDetalle()
    {
    	 DBPedidosdetalleTempAdapter dbpedidodetalle = new DBPedidosdetalleTempAdapter(this);
		 rpedidod= dbpedidodetalle.getAllPedidodetalletemp();
	 	 adaptadorpd =  new AdaptadorPedidoDetalle(this);
	 	 lvPedidoDetalle.setAdapter(adaptadorpd);
    }
    
    
    /**
     * Este metodo muestra el dialogo para editar una linea de pedido
     * */
    private void showDialogEditPedidoDetalle()
    {
    	if(pos == -1)
			Toast.makeText(this, "Selecciona un Articulo!", Toast.LENGTH_SHORT).show();
    	else{
	    	final Dialog dialog = new Dialog(this);
	        dialog.setContentView(R.layout.dialog_pedido_detalle);
	        dialog.setTitle("Edición de pedido");
	       
	        final EditText etCantidadPDDialog = (EditText) dialog.findViewById(R.id.etCantidadPedidoDetalleDialog);
	        final TextView etCantidadOldPDDialog = (TextView) dialog.findViewById(R.id.etCantidadOldPedidoDetalleDialog);
	        final EditText etMtoAcordadoPDDialog = (EditText) dialog.findViewById(R.id.etMtoAcordadoPedidoDetalleDialog);
	        final TextView tvIdPDDialog = (TextView) dialog.findViewById(R.id.lblIdPedidoDetalleDialog);
	        Button btnEditPDDialog = (Button) dialog.findViewById(R.id.btnEditPedidoDetalleDialog);
	        Button btnCancelPDDialog = (Button) dialog.findViewById(R.id.btnCancelPedidoDetalleDialog);
	        
	        etCantidadPDDialog.setText(Integer.toString(rpedidod.get(pos).getCantidad()));
	        etCantidadOldPDDialog.setText(Integer.toString(rpedidod.get(pos).getCantidad()));
	        if(rpedidod.get(pos).getMontoacordado() > 0){
	        	etMtoAcordadoPDDialog.setText(Float.toString(rpedidod.get(pos).getMontoacordado()));
	        }
	        else{
	        	etMtoAcordadoPDDialog.setText(""); 
	        }
	        
	        tvIdPDDialog.setText(Integer.toString(rpedidod.get(pos).get_id()));
	        
	        btnEditPDDialog.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//cantidad ingresada tiene que ser mayor que 0
					if(etCantidadPDDialog.getText().toString().length() > 0)
					{
						int idPDD = Integer.parseInt(tvIdPDDialog.getText().toString());
						int cantidadPDD = Integer.parseInt(etCantidadPDDialog.getText().toString());
						int cantidadOldPDD = Integer.parseInt(etCantidadOldPDDialog.getText().toString());
						float mtoAcordPDD;
						if(etMtoAcordadoPDDialog.getText().toString().trim().length() == 0 ){
							mtoAcordPDD = 0;
			    		}else{
			    			mtoAcordPDD = Float.parseFloat(etMtoAcordadoPDDialog.getText().toString());
			    		}
						//controlamos que existe stock 
						if(checkStock(rpedidod.get(pos).getArticulos_id(), cantidadPDD - cantidadOldPDD))
						{
							Pedidosdetalletemp pedidodetalle = new Pedidosdetalletemp();
							pedidodetalle.set_id(idPDD);
							pedidodetalle.setCantidad(cantidadPDD);
							pedidodetalle.setMontoacordado(mtoAcordPDD);
							pedidodetalle.setSubtotal(setSubtotal(cantidadPDD,rpedidod.get(pos).getPv(),etMtoAcordadoPDDialog.getText().toString()));
							
							//actualizamos linea de pedido
							DBPedidosdetalleTempAdapter dbpd = new DBPedidosdetalleTempAdapter(PedidosAddActivity.this);
							if(dbpd.editPedidodetalletemp(pedidodetalle)){
								DBArticulosAdapter dbarticulo = new DBArticulosAdapter(PedidosAddActivity.this);
								
								Articulos articulo = new Articulos();
								articulo.set_id(rpedidod.get(pos).getArticulos_id());
								articulo.setStockreal(rpedidod.get(pos).getArticulos_stockreal());
								
								//actualizamos stock del articulo teniendo en cuenta la cantidad vieja 
				    			if(dbarticulo.updateStock(articulo, cantidadPDD - cantidadOldPDD )){
				    				Toast.makeText(PedidosAddActivity.this, "Modificación correcta", Toast.LENGTH_SHORT).show();
				    				showPedidoDetalle();  //actualizamos vista del detalle
				    				showArticulos(); //actualimos vista de seleccion de articulo para reflejar el stock real
				    				calcTotal(); //calculamos el total del pedido
				    				dialog.dismiss(); //cerramos el dialog
				    			}
								
							}else{
								Toast.makeText(PedidosAddActivity.this, "Error al modificar!", Toast.LENGTH_SHORT).show();
							}
						}else{
							Toast.makeText(PedidosAddActivity.this, " Stock No disponible!", Toast.LENGTH_SHORT).show();
						}
					}else{
						Toast.makeText(PedidosAddActivity.this, "Ingresa la cantidad!", Toast.LENGTH_SHORT).show();
					}
				}
	        	
	        });
	        
	        btnCancelPDDialog.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
	        	
	        });
	        
	        dialog.show();
    	}
    }
    
    /**
     * Verifica que la cantidad ingresada no sea mayor al 
     * stockreal disponible cuando se esta
     * modificando una linea de pedido
     * */
    private boolean checkStock(int articulo_id, int cantidad)
    {
    	DBArticulosAdapter dbarticulo = new DBArticulosAdapter(this);
    	if(dbarticulo.checkStateStock(articulo_id, cantidad)){
    		return true;
    	}else return false;
    }
    
    
    /**
     * Elimina una linea de pedido temporal
     * */
    private void deletePedidoLinea()
    {
    	if(pos == -1)
			Toast.makeText(this, "Selecciona un Articulo!", Toast.LENGTH_SHORT).show();
    	else{
    		Articulos articulo = new Articulos();
    		articulo.set_id(rpedidod.get(pos).getArticulos_id());
    		articulo.setStockreal(rpedidod.get(pos).getArticulos_stockreal());
    		
    		DBArticulosAdapter dbarticulo = new DBArticulosAdapter(this);
    		if(dbarticulo.increaseStock(articulo, rpedidod.get(pos).getCantidad())){
	    		DBPedidosdetalleTempAdapter dbpd = new DBPedidosdetalleTempAdapter(this);
	    		if(dbpd.deletePedidodetalletemp(rpedidod.get(pos).get_id())){
	    			Toast.makeText(PedidosAddActivity.this, "Linea eliminada correctamente!", Toast.LENGTH_SHORT).show();
	    			showPedidoDetalle();  //actualizamos vista del detalle
    				showArticulos(); //actualimos vista de seleccion de articulo para reflejar el stock real
    				calcTotal(); //calculamos el total del pedido
	    		}else{
	    			Toast.makeText(PedidosAddActivity.this, "Error al eliminar linea!", Toast.LENGTH_SHORT).show();
	    		}
    		}else{
    			Toast.makeText(PedidosAddActivity.this, "Error al actualizar stock!", Toast.LENGTH_SHORT).show();
    		}
    	}
    }
    
    
    /**
     *  Muestra el dialogo para configurar el tramite y finalizar
     *  la carga del pedido
     * */
    private void showDialogPedidoFinish()
    {
    	final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_pedido_finish);
        dialog.setTitle("Seleccione trámite");
        
        Spinner cmbTramites= (Spinner) dialog.findViewById(R.id.cmbTramitesPedidoDialog);
        final EditText etObservacionPedido = (EditText) dialog.findViewById(R.id.etPedidoObservacionDialog);
        Button btnSavePedidoFinish = (Button) dialog.findViewById(R.id.btnSavePedidoFinishDialog);
        Button btnCancelPedidoFinish = (Button) dialog.findViewById(R.id.btnCancelPedidoFinishDialog);
        
        setTramites(cmbTramites);
        
    	cmbTramites.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				// TODO Auto-generated method stub
				tramites_id = tramites.get(position).get_id();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
    	});
    	
    	dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
    	{
    	    @Override
			public void onCancel(DialogInterface dialog)
    	    {
    	    	//eliminar lista de tramites del adaptor para no superponer
    	    	//al volver a crear la lista
    	    	adaptador_tramites.clear(); 
    	    }
    	});
    	
    	btnSavePedidoFinish.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String obsPedido = etObservacionPedido.getText().toString().trim();
				//si se a guardado correctamente el pedido, eliminamos las lineas de pedidos temporales
				if(savePedidoReal(obsPedido)){
					savePreferencePedido(nextidpedido);
					savePreferencePedidoDetalle(nextidpedidodetalle); 
					Toast.makeText(PedidosAddActivity.this, "Pedido guardado correctamente", Toast.LENGTH_SHORT).show();
					deletePedidoDetalleTemp(); //eliminamos todas las lineas de pedidos temporales
	    			/*showPedidoDetalle();*/
	    			dialog.dismiss(); //cerramos el dialog
	    			/*etSubtotal.setText("");
	    			setF(searchByDescripcion,false);*/
					Intent ir_a_seleccion_clientes = new Intent("com.preventista.main.SELECCIONCLIENTE");
					startActivity(ir_a_seleccion_clientes);
				}else{
					Toast.makeText(PedidosAddActivity.this, "Error al guardar pedido", Toast.LENGTH_SHORT).show();
	    			dialog.dismiss(); //cerramos el dialog
				}
			}	
    	});
    	
    	btnCancelPedidoFinish.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				adaptador_tramites.clear(); 
	    		dialog.dismiss(); //cerramos el dialog		
			}	
    	});
    	
        dialog.show();
    }
    
    
    /**
     * Este metodo carga los tramites existentes en la tabla
     * tramites en el control spinner 
     * */
    private void setTramites(Spinner cmbTramite)
    {
    	DBTramitesAdapter dbtramite = new DBTramitesAdapter(this);
    	
    	tramites = dbtramite.getAllTramites();
    	for(int i=0;i<tramites.size();i++ ){
    		tramites_descripcion.add(tramites.get(i).getDescripcion());
    	}
    	
    	adaptador_tramites =
		        new ArrayAdapter<String>(this,
		            android.R.layout.simple_spinner_item, tramites_descripcion);
    	adaptador_tramites.setDropDownViewResource(
		        android.R.layout.simple_spinner_dropdown_item);
    	cmbTramite.setAdapter(adaptador_tramites);
    
    }
    
    
    /**
     * Este metodo crea el pedido real una vez que el 
     * usuario presiona el boton finalizar. A su ves
     * crea las lineas de pedido reales 
     * */
	private boolean savePedidoReal(String obsPedido)
    {
    	ArrayList<Boolean> stPedido = new ArrayList<Boolean>(); 
    	Pedidos pedido = new Pedidos();
    	pedido.set_id(nextidpedido);
    	pedido.setClientes_id(Integer.parseInt(bundle.getString("ID"))); //viene de seleccion clientes
    	pedido.setEstado(ESTADO_PEDIDO);
    	pedido.setMontototal(getTotalPedido());
    	pedido.setObservaciones(obsPedido);
    	pedido.setTramites_id(tramites_id);
    	
    	DBPedidosAdapter dbpedido = new DBPedidosAdapter(this);
    	long pedidos_id = dbpedido.addPedido(pedido);
    	if(pedidos_id != -1){
    		for(int i=0; i<rpedidod.size(); i++){
    			Pedidosdetalle pedidodetalle = new Pedidosdetalle();
    			pedidodetalle.set_id(nextidpedidodetalle);
    			pedidodetalle.setPedidos_id((int)pedidos_id);
    			pedidodetalle.setArticulos_id(rpedidod.get(i).getArticulos_id());
    			pedidodetalle.setCantidad(rpedidod.get(i).getCantidad());
    			pedidodetalle.setMontoacordado(rpedidod.get(i).getMontoacordado());
    			pedidodetalle.setSubtotal(rpedidod.get(i).getSubtotal());
    			pedidodetalle.setPv(rpedidod.get(i).getPv());
    			
    			DBPedidosdetalleAdapter dbpd = new DBPedidosdetalleAdapter(this);
    			if(dbpd.addPedidodetalle(pedidodetalle) == false){
    				stPedido.add(true);
    			}
    			nextidpedidodetalle++;
    		}
    		
    		if(stPedido.size() > 0){
    			Toast.makeText(PedidosAddActivity.this, "Hubo " + Integer.toString(stPedido.size()) + " errores al guardar el pedido", Toast.LENGTH_SHORT).show();
    			return false;
    		}else{
    			return true;
    		}
    	}else{
    		return false;
    	}
    }
	
	
	/**
	 * Este metodo guarda en el archivo de preferencias el
	 * proximo numero de id a ser insertado en la 
	 * tabla pedidos
	 * */
	private void savePreferencePedido(int next_id)
	{
		int newid = next_id + 1;
		SharedPreferences settings =  PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("etPrefPedidosID", Integer.toString(newid));
		editor.commit();
		
	}
	
	
	/**
	 * Este metodo guarda en el archivo de preferencias el
	 * proximo numero de id a ser insertado en la 
	 * tabla pedidodetalle
	 * */
	private void savePreferencePedidoDetalle(int next_id)
	{
		int newid = next_id;
		SharedPreferences settings =  PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("etPrefPedidoDetalleID", Integer.toString(newid));
		editor.commit();
		
	}
}
