package com.preventista.main;

import java.util.ArrayList;

import com.preventista.db.DBPedidosAdapter;
import com.preventista.entidades.Pedidos;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PedidosAdeudadosActivity extends Activity{
	
	private Bundle bundle;
	private int clientes_id;
	
	private ListView lvPedidosAdeudados;
	private ArrayList<Pedidos> resultado = new ArrayList<Pedidos>();
	
	private AdaptadorPedidosAdeudados adaptador;
    
    static class ViewHolder {
        TextView lblPedidosAdeudadosId;
        TextView lblPedidosAdeudadosFecha;
        TextView lblPedidosAdeudadosMontoTotal;
        TextView lblPedidosAdeudadosMontoAdeudado;
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_pedidos_adeudados);
		bundle = getIntent().getExtras();
		this.clientes_id = bundle.getInt("CLIENTES_ID");
		lvPedidosAdeudados = (ListView) findViewById(R.id.lvPedidosAdeudados);
		
		showPedidosAdeudados();
	}
	
	
	public void showPedidosAdeudados()
	{
		DBPedidosAdapter dbpadapter = new DBPedidosAdapter(this);
		resultado = dbpadapter.getPedidosAdeudadosToShow(this.clientes_id);
		adaptador = new AdaptadorPedidosAdeudados(this);
		lvPedidosAdeudados.setAdapter(adaptador);
	}
	
	
    private class AdaptadorPedidosAdeudados extends ArrayAdapter<Pedidos> {
    	
    	Activity context;
    	
    	AdaptadorPedidosAdeudados(Activity context) {
    		super(context, R.layout.listitem_pedidos_adeudados, resultado);
    		this.context = context;
    	}
    	
    	public View getView(int position, View convertView, ViewGroup parent) {
			
    		View item = convertView;
		    ViewHolder holder;
		    if(item == null)
		    {
	    		LayoutInflater inflater = context.getLayoutInflater();
				item = inflater.inflate(R.layout.listitem_pedidos_adeudados, null);
				
				 holder = new ViewHolder();
				 holder.lblPedidosAdeudadosId = (TextView)item.findViewById(R.id.lblPedidosAdeudadosIdListItem);
				 holder.lblPedidosAdeudadosFecha = (TextView)item.findViewById(R.id.lblPedidosAdeudadosFechaListItem);
				 holder.lblPedidosAdeudadosMontoTotal = (TextView)item.findViewById(R.id.lblPedidosAdeudadosMontoTotalListItem);
				 holder.lblPedidosAdeudadosMontoAdeudado = (TextView)item.findViewById(R.id.lblPedidosAdeudadosMontoAdeudadolListItem);
				 item.setTag(holder);
		    }else{
		    	 holder = (ViewHolder)item.getTag();
		    }
			
		    holder.lblPedidosAdeudadosId.setText(Integer.toString(resultado.get(position).get_id()));
		    holder.lblPedidosAdeudadosFecha.setText(resultado.get(position).getCustonCreatedAt());
		    holder.lblPedidosAdeudadosMontoTotal.setText(Float.toString(resultado.get(position).getMontototal()));
		    holder.lblPedidosAdeudadosMontoAdeudado.setText(Float.toString(resultado.get(position).getMontoadeudado()));
			
			return(item);
		}
    }
}
