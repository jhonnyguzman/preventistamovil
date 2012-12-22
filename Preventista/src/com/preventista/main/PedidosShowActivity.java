package com.preventista.main;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.preventista.db.DBPedidosAdapter;
import com.preventista.db.DBPedidosdetalleAdapter;
import com.preventista.db.DBTramitesAdapter;
import com.preventista.entidades.Pedidos;
import com.preventista.entidades.Pedidosdetalle;
import com.preventista.entidades.Tramites;

public class PedidosShowActivity extends Activity{
	
	private int pos = -1;
	private static int _id;
	
	private boolean flag = false;
	
	private int tramites_id = -1;
	
	private ListView lvPedidoDetalle;
	
	private ArrayList<Pedidos> rpedido = new ArrayList<Pedidos>();
	private ArrayList<Pedidosdetalle> rpedidod = new ArrayList<Pedidosdetalle>();
	
	ArrayList<Tramites> tramites = new ArrayList<Tramites>();
	ArrayList<String> tramites_descripcion = new ArrayList<String>();
    
	ArrayAdapter<String> adaptador_tramites;
	
	// widgets para pedido
	private TextView tvId, tvMontoTotal, tvMontoAdeudado, tvCliente, tvEstado, tvTramite, tvFechaAlta, tvFechaActualizacion;
	private EditText etObservaciones; 
	private Button btnClose;
	
	// widgets 
    private TabHost thShowPedidos;
    private Bundle bundle = null;
    
    private AdaptadorPedidoDetalle adaptadorpd;
    
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
		setContentView(R.layout.show_form_pedidos);
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
		lvPedidoDetalle = (ListView) findViewById(R.id.lvPedidoDetalle);
		thShowPedidos = (TabHost) findViewById(R.id.thShowPedidos);
		
		
		// para los datos del pedido
		tvId = (TextView) findViewById(R.id.tvIdPedidoEdit);
		tvMontoTotal = (TextView) findViewById(R.id.tvMontoTotalPedidoEdit);
		tvMontoAdeudado = (TextView) findViewById(R.id.tvMontoAdeudadoPedidoEdit);
		tvCliente = (TextView) findViewById(R.id.tvClientePedidoEdit);
		tvEstado = (TextView) findViewById(R.id.tvEstadoPedidoEdit);
		tvTramite = (TextView) findViewById(R.id.tvTramiteDescripcion);
		tvFechaAlta = (TextView) findViewById(R.id.tvFechaAltaPedidoEdit);
		tvFechaActualizacion = (TextView) findViewById(R.id.tvFechaActualizacionPedidoEdit);
		etObservaciones = (EditText) findViewById(R.id.etObservacionesPedidoEdit);
		btnClose = (Button) findViewById(R.id.btnCerrarShowPedido);
		
		//desactivamos campo observaciones
		etObservaciones.setEnabled(false);
		etObservaciones.setFocusable(false);
		etObservaciones.setClickable(false);
		
		//configuracion del tabhost
		thShowPedidos.setup();
		TabSpec spec = thShowPedidos.newTabSpec("GENERALPEDIDO");
		spec.setIndicator("General");
		spec.setContent(R.id.tabGeneralShowPedido);
		thShowPedidos.addTab(spec);
		
		spec = thShowPedidos.newTabSpec("DETALLEPEDIDOEDIT");
		spec.setIndicator("Detalle");
		spec.setContent(R.id.tabDetallePedidoShow);
		thShowPedidos.addTab(spec);
		
		//extras desde la actividad de ClientesSeleccionActivity
		bundle = getIntent().getExtras();
		_id = Integer.parseInt(bundle.getString("_ID"));

		showPedido();
		showPedidoDetalle();
		
		lvPedidoDetalle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			 
		      @Override
		      public void onItemClick( AdapterView<?> parent, View item, 
		                               int position, long id) {
		        pos = position;
		        adaptadorpd.notifyDataSetChanged();
		      }
		});
		
		btnClose.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});
		
		
		//config height of the tabhost
		thShowPedidos.getTabWidget().getChildAt(0).getLayoutParams().height = 45;
		thShowPedidos.getTabWidget().getChildAt(1).getLayoutParams().height = 45;
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
			holder.lblSubtotalPD.setText(String.format("%.2f", pedidodetalle.getSubtotal()));
		
			//si es true colocamos un background en el item del listview
			if(pos == position) {
				item.setBackgroundColor(Color.argb(100, 100, 100, 100));
			}else{
				item.setBackgroundColor(Color.argb(0, 0, 0, 0));
			}
		
			return(item);
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
    		if(tramites.get(i).get_id() ==  tramites_id){
				tvTramite.setText(tramites.get(i).getDescripcion());
				break;
			}
    	}
    	
    }

}
