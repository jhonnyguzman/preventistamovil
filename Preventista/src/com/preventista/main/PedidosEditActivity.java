package com.preventista.main;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import com.preventista.db.DBTramitesAdapter;
import com.preventista.entidades.Articulos;
import com.preventista.entidades.Pedidos;
import com.preventista.entidades.Pedidosdetalle;
import com.preventista.entidades.Tramites;

public class PedidosEditActivity extends Activity{

	private static final int CATCLIENTE1 = 1;
	private static final int CATCLIENTE2 = 2;
	private static final int CATCLIENTE3 = 3;
	
	private int pos = -1;
	private static int catcliente;
	private static int clientes_id;
	private static int _id;
	
	private boolean[] itemSelection;
	private boolean flag = false;
	
	private int tramites_id = -1;
	
	private ListView lvArticulos, lvPedidoDetalle;
	
	private ArrayList<Integer> itemsSeleccionados = new ArrayList<Integer>();
	private ArrayList<Pedidos> rpedido = new ArrayList<Pedidos>();
	private ArrayList<Articulos> resultado = new ArrayList<Articulos>();
	private ArrayList<Pedidosdetalle> rpedidod = new ArrayList<Pedidosdetalle>();
	
	ArrayList<Tramites> tramites = new ArrayList<Tramites>();
	ArrayList<String> tramites_descripcion = new ArrayList<String>();
    
	ArrayAdapter<String> adaptador_tramites;
	
	private PreRes preres = new PreRes();
	
	// widgets para pedido
	private TextView tvId, tvMontoTotal, tvMontoAdeudado, tvCliente, tvEstado, tvTramite, tvFechaAlta, tvFechaActualizacion;
	private EditText etObservaciones; 
	private Spinner cmbTramitesPedido;
	private Button btnSaveEditPedido;
	
	// widgets 
    private EditText searchByDescripcion, etCantidad, etSubtotal, etMtoAcordado;
    private ImageButton btnSaveLinea;
    private Button btnEditPedidoDetalle, btnDeletePedidoLinea;
    private TabHost thEditPedidos;
    private Bundle bundle = null;
    
    private AdaptadorSeleccionArticulos adaptador;
    private AdaptadorPedidoDetalle adaptadorpd;
    
    //atributos para las preferencias
  	private SharedPreferences sp;
  	private int nextidpedidodetalle;
	
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
		setContentView(R.layout.edit_form_pedidos);
		
		//preferencias
		sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		nextidpedidodetalle = Integer.parseInt(sp.getString("etPrefPedidoDetalleID", "50000"));
				
		lvArticulos = (ListView) findViewById(R.id.lvSeleccionArticulos);
		lvPedidoDetalle = (ListView) findViewById(R.id.lvPedidoDetalle);
		thEditPedidos = (TabHost) findViewById(R.id.thEditPedidos);
		searchByDescripcion = (EditText) findViewById(R.id.edtSearchByDescripcionArticulo);
		etCantidad = (EditText) findViewById(R.id.etCantidadArticulo);
		etMtoAcordado = (EditText) findViewById(R.id.etMontoAcordadoArticulo);
		etSubtotal = (EditText) findViewById(R.id.etSubtotalPedido);
		btnSaveLinea = (ImageButton) findViewById(R.id.btnSaveLineaPedido);
		
		// para los datos del pedido
		tvId = (TextView) findViewById(R.id.tvIdPedidoEdit);
		tvMontoTotal = (TextView) findViewById(R.id.tvMontoTotalPedidoEdit);
		tvMontoAdeudado = (TextView) findViewById(R.id.tvMontoAdeudadoPedidoEdit);
		tvCliente = (TextView) findViewById(R.id.tvClientePedidoEdit);
		tvEstado = (TextView) findViewById(R.id.tvEstadoPedidoEdit);
		cmbTramitesPedido = (Spinner) findViewById(R.id.cmbTramitesPedidoEdit);
		tvFechaAlta = (TextView) findViewById(R.id.tvFechaAltaPedidoEdit);
		tvFechaActualizacion = (TextView) findViewById(R.id.tvFechaActualizacionPedidoEdit);
		etObservaciones = (EditText) findViewById(R.id.etObservacionesPedidoEdit);
		btnSaveEditPedido = (Button) findViewById(R.id.btnSaveEditPedido);
		
		//botones de la seccion de detalle del pediddo
		btnEditPedidoDetalle = (Button) findViewById(R.id.btnEditPedidoDetalle);
		btnDeletePedidoLinea = (Button) findViewById(R.id.btnDeletePedidoDetalle);
		
		//configuracion del tabhost
		thEditPedidos.setup();
		TabSpec spec = thEditPedidos.newTabSpec("GENERALPEDIDO");
		spec.setIndicator("General");
		spec.setContent(R.id.tabGeneralPedido);
		thEditPedidos.addTab(spec);
		
		spec = thEditPedidos.newTabSpec("DETALLEPEDIDOEDIT");
		spec.setIndicator("Detalle");
		spec.setContent(R.id.tabDetallePedidoEdit);
		thEditPedidos.addTab(spec);
		
		spec = thEditPedidos.newTabSpec("SELECCIONARTICULOEDIT");
		spec.setIndicator("Selección");
		spec.setContent(R.id.tabSeleccionArticuloEdit);
		thEditPedidos.addTab(spec);
		
		//extras desde la actividad de ClientesSeleccionActivity
		bundle = getIntent().getExtras();
		
		_id = Integer.parseInt(bundle.getString("_ID"));
		clientes_id = Integer.parseInt(bundle.getString("CLIENTES_ID"));
		catcliente = Integer.parseInt(bundle.getString("CLIENTESCATEGORIA_ID"));
		
		
		checkCategoriaCliente();
		
		showPedido();
		showArticulos();
		showPedidoDetalle();
		
		btnSaveEditPedido.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updatePedido();//actualizar el pedido
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
		
		lvPedidoDetalle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			 
		      @Override
		      public void onItemClick( AdapterView<?> parent, View item, 
		                               int position, long id) {
		        pos = position;
		        adaptadorpd.notifyDataSetChanged();
		      }
		});
		
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
					Toast.makeText(PedidosEditActivity.this, "No hay Lineas de pedido", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(PedidosEditActivity.this, "No hay Lineas de pedido", Toast.LENGTH_SHORT).show();
				}
			}
			
		});
		
		cmbTramitesPedido.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				tramites_id = tramites.get(arg2).get_id();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
			
		});
		
		//config height of the tabhost
		thEditPedidos.getTabWidget().getChildAt(0).getLayoutParams().height = 45;
		thEditPedidos.getTabWidget().getChildAt(1).getLayoutParams().height = 45;
		thEditPedidos.getTabWidget().getChildAt(2).getLayoutParams().height = 45;
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
	
	
	/**
	 * Este método busca en la bd y muestra los datos del pedido 
	 * seleccionado
	 * */
	public void showPedido()
	{	

		DBPedidosAdapter dbpedido = new DBPedidosAdapter(this);
		rpedido = dbpedido.getPedidos(_id);
		for(int i=0; i <rpedido.size(); i++){
			tvId.setText(Integer.toString(rpedido.get(i).get_id()));
			tvMontoTotal.setText(String.format("%.2f",rpedido.get(i).getMontototal())); 
			tvMontoAdeudado.setText(String.format("%.2f",rpedido.get(i).getMontoadeudado()));
			tvCliente.setText(rpedido.get(i).getApellnom());
			tvEstado.setText(rpedido.get(i).getEstadoDescripcion());
			tvFechaAlta.setText(rpedido.get(i).getCustonCreatedAt());
			tvFechaActualizacion.setText(rpedido.get(i).getCustonUpdatedAt());
			etObservaciones.setText(rpedido.get(i).getObservaciones());
			setTramites(rpedido.get(i).getTramites_id());
		}
		
	}
	
	
	/**
     * Este metodo muestra todas las lineas de pedido que tiene el pedido 
     * en la solpa detalle del tabhost
     * */
    private void showPedidoDetalle()
    {
    	 DBPedidosdetalleAdapter dbpedidodetalle = new DBPedidosdetalleAdapter(this);
		 rpedidod = dbpedidodetalle.getByPedidoId(_id);
	 	 adaptadorpd =  new AdaptadorPedidoDetalle(this);
	 	 lvPedidoDetalle.setAdapter(adaptadorpd);
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

            @Override
            public void afterTextChanged(Editable arg0) {}
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
        });
	}
	
	
	/**
	 * Opciones de Menu de la sección principal de clientes
	 * */
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{	
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu_pedidos_edit, menu);
		return true;
	}
	
	@Override 
	public boolean onOptionsItemSelected(MenuItem item)
	{	
		switch(item.getItemId()){
		
		case R.id.mnSearchPedidoEdit:
			Intent ir_a_busqueda_pedidos = new Intent("com.preventista.main.SEARCHPEDIDOS");
			startActivity(ir_a_busqueda_pedidos);
			
			return true;
			
		case R.id.mnIrMainPedido:
			Intent ir_a_main_pedidos = new Intent("com.preventista.main.MAINPEDIDOS");
			startActivity(ir_a_main_pedidos);
			return true;
		
		case R.id.mnMainMenu:
			Intent ir_main_menu = new Intent(PedidosEditActivity.this,PreventistaActivity.class); 
			startActivity(ir_main_menu);
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
    		final Articulos articulo = (Articulos) this.getItem( position ); 
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

				item.setTag(holder);
				
		    }else{
		    	holder = (ViewHolder)item.getTag();
		    }
		    
			
		    //Display articulo data
		    holder.lblDescripcion.setText(articulo.getDescripcion());
			holder.lblStockReal.setText(Integer.toString(articulo.getStockreal()));
			
			holder.lblId.setText(Integer.toString(articulo.get_id()));
			
			if(catcliente == CATCLIENTE1) holder.lblPrecioVenta.setText(Float.toString(articulo.getPrecio1()));
			else if(catcliente == CATCLIENTE2) holder.lblPrecioVenta.setText(Float.toString(articulo.getPrecio2()));
			else if(catcliente == CATCLIENTE3) holder.lblPrecioVenta.setText(Float.toString(articulo.getPrecio3()));
			
			//si es true colocamos un background en el item del listview
			if(itemSelection[position]) {
				item.setBackgroundColor(Color.argb(200, 49, 176, 17));
			}else{
				item.setBackgroundColor(Color.argb(0, 0, 0, 0));
			}
		
			return(item);
		}
    }
	
	
	
	private class AdaptadorPedidoDetalle extends ArrayAdapter<Pedidosdetalle>{
    	
    	//private LayoutInflater inflater;
    	Activity context;
    	
    	
    	AdaptadorPedidoDetalle(Activity context) {
    		super(context, R.layout.listitem_pedido_detalle, rpedidod);
    		//inflater = LayoutInflater.from(context) ;
    		this.context = context;
    	}
    	
    	public View getView(final int position, View convertView, ViewGroup parent) {
    		Pedidosdetalle pedidodetalle = (Pedidosdetalle) this.getItem( position ); 
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
        	//Toast.makeText(PedidosEditActivity.this, "Item seleccionado: " + Integer.toString(articulo.get_id()), Toast.LENGTH_SHORT).show();
        	//Log.d("Items", Integer.toString(itemsSeleccionados.size()));
        }else{
        	//Toast.makeText(PedidosEditActivity.this, "Item no seleccionado: " + Integer.toString(articulo.get_id()), Toast.LENGTH_SHORT).show();
        	flag = false;
        }
    }
    
    private boolean checkStockOnline()
    {
    	ArrayList<Articulos> statusStock = new ArrayList<Articulos>();
    	String cantidad = etCantidad.getText().toString().trim();
    	String msg;
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
	    		setF(etCantidad, false);
	    		
	    		final Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
	    		toast.show();
	    		
	    		Handler handler = new Handler();
		            handler.postDelayed(new Runnable() {
		               @Override
		               public void run() {
		                   toast.cancel(); 
		               }
		        }, 1000);
		        return false;
	    	}else{
	    		return true;
	    	}
    	}else{
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
     * Este metodo guarda una  linea de pedido
     * */
    private void saveLinea()
    {
    	String montoacordado = etMtoAcordado.getText().toString().trim();
    	String cantidad = etCantidad.getText().toString().trim();
    	
    	if(itemsSeleccionados.size() > 0)
    	{
	    	if(cantidad.length() > 0)
	    	{
	    		//recorremos array con los items seleccionados
		    	for(int i=0; i < itemsSeleccionados.size(); i++)
		    	{
		    		Pedidosdetalle pedidodetalle = new Pedidosdetalle();
		    		//traemos al articulo de la bd para obtener precio de venta y calcular el stock.
		    		DBArticulosAdapter dbarticulo = new DBArticulosAdapter(this);
		    		Articulos articulo = new Articulos();
		    		articulo = dbarticulo.getArticulosById(itemsSeleccionados.get(i));
		    		
		    		pedidodetalle.set_id(nextidpedidodetalle);
		    		pedidodetalle.setPedidos_id(_id); //id del pedido
		    		pedidodetalle.setArticulos_id(itemsSeleccionados.get(i));
		    		pedidodetalle.setCantidad(Integer.parseInt(cantidad));
		    		
		    		//si existe monto acordamos lo agregamos
		    		if(montoacordado.length() > 0 ){
		    			pedidodetalle.setMontoacordado(Float.parseFloat(montoacordado));
		    		}
		    		
		    		pedidodetalle.setPv(setPv2(articulo));
		    		//calcular subtotal
		    		pedidodetalle.setSubtotal(setSubtotal(Integer.parseInt(cantidad), pedidodetalle.getPv(), montoacordado));
		    		
		    		//guardamos lineas de pedido
		    		DBPedidosdetalleAdapter dbadapter = new DBPedidosdetalleAdapter(this); 
		    		if(dbadapter.addPedidodetalle(pedidodetalle)){
		    			if(dbarticulo.updateStock(articulo, Integer.parseInt(cantidad)) == false){
		    				Toast.makeText(this, "Error al actualizar stock", Toast.LENGTH_SHORT).show();
			    			setF(searchByDescripcion, true);
		    			}
		    		}else{
		    			Toast.makeText(this, "Error al agregar articulos", Toast.LENGTH_SHORT).show();
		    			setF(searchByDescripcion,false);
		    		}
		    		
		    		
		    		Log.d("nextidpedidodetalle ", Integer.toString(nextidpedidodetalle));
		    		Log.d("pedido_id ", Integer.toString(_id));
		    		Log.d("articulo_id", Integer.toString(pedidodetalle.getArticulos_id()));
		    		Log.d("cantidad", Integer.toString(pedidodetalle.getCantidad()));
		    		Log.d("pv", Float.toString(pedidodetalle.getPv()));
		    		Log.d("subtotal", Float.toString(pedidodetalle.getSubtotal()));
		    		nextidpedidodetalle++;
		    	}
		    	
		    	Toast.makeText(this, "Articulos agregados correctamente", Toast.LENGTH_SHORT).show();
		    	setF(searchByDescripcion, true);
		    	
		    	destroyItemsSeleccionados();
		    	calcTotal();
		    	showPedidoDetalle();
		    	savePreferencePedidoDetalle(nextidpedidodetalle);
		    	adaptador_tramites.clear();
		    	updatePedido();
		    	showPedido();
		    	etCantidad.setText("1");
		    	etMtoAcordado.setText("");
		    	
		    	
	    	}else{
	    		Toast.makeText(PedidosEditActivity.this, "Ingrese cantidad", Toast.LENGTH_SHORT).show();
	    		setF(etCantidad, false);
	    	}
    	}else{
    		Toast.makeText(PedidosEditActivity.this, "Selecciona un Articulo", Toast.LENGTH_SHORT).show();
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
     * hasta el momento, a partir de las lineas de pedidos 
     * */
    private void calcTotal()
    {
    	float sub = 0;
    	ArrayList<Pedidosdetalle> pedidodetalle = new ArrayList<Pedidosdetalle>(); 
    	DBPedidosdetalleAdapter dbpedidodetalle = new DBPedidosdetalleAdapter(this);
    	pedidodetalle = dbpedidodetalle.getByPedidoId(_id);
    	for(int i=0; i < pedidodetalle.size(); i++){
    		sub = sub + pedidodetalle.get(i).getSubtotal();
    	}
    	etSubtotal.setText(Float.toString(preres.formatearFloat(sub)));
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
					//cantidad ingresada tiene que ser mayot que 0
					if(etCantidadPDDialog.getText().toString().length() > 0)
					{
						int idPDD = Integer.parseInt(tvIdPDDialog.getText().toString());
						int cantidadPDD = Integer.parseInt(etCantidadPDDialog.getText().toString());
						int cantidadOldPDD = Integer.parseInt(etCantidadOldPDDialog.getText().toString());
						
						
						//controlamos que existe stock 
						if(checkStock(rpedidod.get(pos).getArticulos_id(), cantidadPDD - cantidadOldPDD))
						{
							Pedidosdetalle pedidodetalle = new Pedidosdetalle();
							pedidodetalle.set_id(idPDD);
							pedidodetalle.setCantidad(cantidadPDD);
							//si existe monto acordamos lo agregamos
				    		if(etMtoAcordadoPDDialog.getText().toString().trim().length() == 0 ){
				    			pedidodetalle.setMontoacordado(0);
				    		}else{
				    			pedidodetalle.setMontoacordado(Float.parseFloat(etMtoAcordadoPDDialog.getText().toString()));
				    		}
				    		
							
				    		pedidodetalle.setSubtotal(setSubtotal(cantidadPDD,rpedidod.get(pos).getPv(),etMtoAcordadoPDDialog.getText().toString()));
							
							//actualizamos linea de pedido
							DBPedidosdetalleAdapter dbpd = new DBPedidosdetalleAdapter(PedidosEditActivity.this);
							if(dbpd.editPedidodetalle(pedidodetalle)){
								DBArticulosAdapter dbarticulo = new DBArticulosAdapter(PedidosEditActivity.this);
								
								Articulos articulo = new Articulos();
								articulo.set_id(rpedidod.get(pos).getArticulos_id());
								articulo.setStockreal(rpedidod.get(pos).getArticulos_stockreal());
								
								//actualizamos stock del articulo teniendo en cuenta la cantidad vieja 
				    			if(dbarticulo.updateStock(articulo, cantidadPDD - cantidadOldPDD )){
				    				Toast.makeText(PedidosEditActivity.this, "Modificación correcta", Toast.LENGTH_SHORT).show();
				    				showPedidoDetalle();  //actualizamos vista del detalle
				    				showArticulos(); //actualimos vista de seleccion de articulo para reflejar el stock real
				    				calcTotal(); //calculamos el total del pedido
				    				adaptador_tramites.clear();
				    				updatePedido();
				    		    	showPedido();
				    				dialog.dismiss(); //cerramos el dialog
				    			}
								
							}else{
								Toast.makeText(PedidosEditActivity.this, "Error al modificar!", Toast.LENGTH_SHORT).show();
							}
						}else{
							Toast.makeText(PedidosEditActivity.this, " Stock No disponible!", Toast.LENGTH_SHORT).show();
						}
					}else{
						Toast.makeText(PedidosEditActivity.this, "Ingresa la cantidad!", Toast.LENGTH_SHORT).show();
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
     * Elimina una linea de pedido
     * */
    private void deletePedidoLinea()
    {
    	if(pos == -1)
			Toast.makeText(this, "Selecciona un Articulo!", Toast.LENGTH_SHORT).show();
    	else{
    		Articulos articulo = new Articulos();
    		articulo.set_id(rpedidod.get(pos).getArticulos_id());
    		articulo.setStockreal(rpedidod.get(pos).getArticulos_stockreal());
    		
    		//primero actualizamos el stock
    		DBArticulosAdapter dbarticulo = new DBArticulosAdapter(this);
    		if(dbarticulo.increaseStock(articulo, rpedidod.get(pos).getCantidad())){
	    		DBPedidosdetalleAdapter dbpd = new DBPedidosdetalleAdapter(this);
	    		if(dbpd.deletePedidodetalle(rpedidod.get(pos).get_id())){
	    			Toast.makeText(PedidosEditActivity.this, "Linea eliminada correctamente!", Toast.LENGTH_SHORT).show();
	    			showPedidoDetalle();  //actualizamos vista del detalle
    				showArticulos(); //actualimos vista de seleccion de articulo para reflejar el stock real
    				calcTotal(); //calculamos el total del pedido
    				updatePedido(); //actualizanis el pedido
	    		}else{
	    			Toast.makeText(PedidosEditActivity.this, "Error al eliminar linea!", Toast.LENGTH_SHORT).show();
	    		}
    		}else{
    			Toast.makeText(PedidosEditActivity.this, "Error al actualizar stock!", Toast.LENGTH_SHORT).show();
    		}
    	}
    }
    
    
    /**
     * Este metodo carga los tramites existentes en la tabla
     * tramites en el control spinner
     * */
    private void setTramites(int tramites_id)
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
    	cmbTramitesPedido.setAdapter(adaptador_tramites);
    	
    	//iten preseleccionado segun campo de la bd
		for(int i=0; i < tramites.size(); i++){
			if(tramites.get(i).get_id() ==  tramites_id){
				cmbTramitesPedido.setSelection(i);
				break;
			}
    	}
    }
    
    
    /**
     * Este metodo crea el pedido real una vez que el 
     * usuario presiona el boton finalizar. A su ves
     * crea las lineas de pedido reales 
     * */
	private void updatePedido()
    {
		//calculo del monto total
    	float mt = 0; //monto total
		ArrayList<Pedidosdetalle> temppedidodetalle = new ArrayList<Pedidosdetalle>();
		DBPedidosdetalleAdapter dbpd = new DBPedidosdetalleAdapter(this);
		temppedidodetalle = dbpd.getByPedidoId(_id);
		for(int i = 0; i < temppedidodetalle.size(); i++){
			mt = mt + temppedidodetalle.get(i).getSubtotal();
		}
		
    	Pedidos pedido = new Pedidos();
    	
    	pedido.set_id(_id);
    	pedido.setTramites_id(tramites_id);
    	pedido.setMontototal(mt);
    	pedido.setMontoadeudado(mt);
    	pedido.setObservaciones(etObservaciones.getText().toString().trim());
    	
    	DBPedidosAdapter dbpedido = new DBPedidosAdapter(this);
    	
    	if(dbpedido.editPedido(pedido)){
    		tvMontoTotal.setText(Float.toString(mt));
    		tvMontoAdeudado.setText(Float.toString(mt));
    		Toast.makeText(this, "Pedido Actualizado correctamente", Toast.LENGTH_SHORT).show();
    	}else{
    		Toast.makeText(this, "Error al actualizar pedido", Toast.LENGTH_SHORT).show();
    	}
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
