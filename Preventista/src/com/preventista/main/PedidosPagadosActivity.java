package com.preventista.main;

import java.util.ArrayList;

import com.preventista.db.DBPedidosAdapter;
import com.preventista.entidades.Pedidos;
import com.preventista.main.PedidosAdeudadosActivity.ViewHolder;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PedidosPagadosActivity extends Activity{

	private Bundle bundle;
	private int clientes_id; 
	
	private ListView lvPedidosPagados;
	private ArrayList<Pedidos> resultado = new ArrayList<Pedidos>();
	
	private AdaptadorPedidosPagados adaptador;
    
    static class ViewHolder {
        TextView lblPedidosPagadosId;
        TextView lblPedidosPagadosFecha;
        TextView lblPedidosPagadosMontoTotal;
        TextView lblPedidosPagadosMontoAdeudado;;
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_pedidos_pagados);
		bundle = getIntent().getExtras();
		this.clientes_id = bundle.getInt("CLIENTES_ID");
		lvPedidosPagados = (ListView) findViewById(R.id.lvPedidosPagados);
		
		showPedidosPagados();
	}
	
	public void showPedidosPagados()
	{
		DBPedidosAdapter dbpadapter = new DBPedidosAdapter(this);
		resultado = dbpadapter.getPedidosPagadosToShow(this.clientes_id);
		adaptador = new AdaptadorPedidosPagados(this);
		lvPedidosPagados.setAdapter(adaptador);
	}
	
	
    class AdaptadorPedidosPagados extends ArrayAdapter {
    	
    	Activity context;
    	
    	AdaptadorPedidosPagados(Activity context) {
    		super(context, R.layout.listitem_pedidos_pagados, resultado);
    		this.context = context;
    	}
    	
    	public View getView(int position, View convertView, ViewGroup parent) {
			
    		View item = convertView;
		    ViewHolder holder;
		    if(item == null)
		    {
	    		LayoutInflater inflater = context.getLayoutInflater();
				item = inflater.inflate(R.layout.listitem_pedidos_pagados, null);
				
				 holder = new ViewHolder();
				 holder.lblPedidosPagadosId = (TextView)item.findViewById(R.id.lblPedidosPagadosIdListItem);
				 holder.lblPedidosPagadosFecha = (TextView)item.findViewById(R.id.lblPedidosPagadosFechaListItem);
				 holder.lblPedidosPagadosMontoTotal = (TextView)item.findViewById(R.id.lblPedidosPagadosMontoTotalListItem);
				 holder.lblPedidosPagadosMontoAdeudado = (TextView)item.findViewById(R.id.lblPedidosPagadosMontoAdeudadolListItem);
				 item.setTag(holder);
		    }else{
		    	 holder = (ViewHolder)item.getTag();
		    }
			
		    holder.lblPedidosPagadosId.setText(Integer.toString(resultado.get(position).get_id()));
		    holder.lblPedidosPagadosFecha.setText(resultado.get(position).getCustonCreatedAt());
		    holder.lblPedidosPagadosMontoTotal.setText(Float.toString(resultado.get(position).getMontototal()));
		    holder.lblPedidosPagadosMontoAdeudado.setText(Float.toString(resultado.get(position).getMontoadeudado()));
			
			return(item);
		}
    }
}
