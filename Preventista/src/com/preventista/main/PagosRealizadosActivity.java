package com.preventista.main;

import java.util.ArrayList;

import com.preventista.db.DBPagosAdapter;
import com.preventista.entidades.Pagos;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PagosRealizadosActivity extends Activity{
	
	private Bundle bundle;
	private int clientes_id;
	
	private ListView lvPagosRealizados;
	private ArrayList<Pagos> resultado = new ArrayList<Pagos>();
	
	private AdaptadorPagosRealizados adaptador;
    
    static class ViewHolder {
        TextView lblPagosRealizadosFecha;
        TextView lblPagosRealizadosMonto;
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_pagos_realizados);
		bundle = getIntent().getExtras();
		this.clientes_id = bundle.getInt("CLIENTES_ID");
		lvPagosRealizados = (ListView) findViewById(R.id.lvPagosRealizados);
		
		showPagosRealizados();
	}
	
	
	public void showPagosRealizados()
	{
		DBPagosAdapter dbpadapter = new DBPagosAdapter(this);
		resultado = dbpadapter.getPagosOfCliente(this.clientes_id);
		adaptador = new AdaptadorPagosRealizados(this);
		lvPagosRealizados.setAdapter(adaptador);
	}
	
	
    class AdaptadorPagosRealizados extends ArrayAdapter {
    	
    	Activity context;
    	
    	AdaptadorPagosRealizados(Activity context) {
    		super(context, R.layout.listitem_pagos_realizados, resultado);
    		this.context = context;     
    	}
    	
    	public View getView(int position, View convertView, ViewGroup parent) {
			
    		View item = convertView;
		    ViewHolder holder;
		    if(item == null)
		    {
	    		LayoutInflater inflater = context.getLayoutInflater();
				item = inflater.inflate(R.layout.listitem_pagos_realizados, null);
				
				 holder = new ViewHolder();
				 holder.lblPagosRealizadosFecha = (TextView)item.findViewById(R.id.lblPagosRealizadosFechaListItem);
				 holder.lblPagosRealizadosMonto = (TextView)item.findViewById(R.id.lblPagosRealizadosMontoListItem);
				 item.setTag(holder);
		    }else{
		    	 holder = (ViewHolder)item.getTag();
		    }
			
		    holder.lblPagosRealizadosFecha.setText(resultado.get(position).getCustonCreatedAt());
		    holder.lblPagosRealizadosMonto.setText(resultado.get(position).getCustonMonto());
			
			return(item);
		}
    }
}
