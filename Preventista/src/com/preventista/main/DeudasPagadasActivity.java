package com.preventista.main;

import java.util.ArrayList;

import com.preventista.db.DBDeudasAdapter;
import com.preventista.entidades.Deudas;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DeudasPagadasActivity extends Activity{
	
	private Bundle bundle;
	private int clientes_id;
	
	private ListView lvDeudasPagadas;
	private ArrayList<Deudas> resultado = new ArrayList<Deudas>();
	
	private AdaptadorDeudasPagadas adaptador;
    
    static class ViewHolder {
        TextView lblDeudasPagadasId;
        TextView lblDeudasPagadasFecha;
        TextView lblDeudasPagadasMontoTotal;
        TextView lblDeudasPagadasMontoAdeudado;;
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_deudas_pagadas);
		bundle = getIntent().getExtras();
		this.clientes_id = bundle.getInt("CLIENTES_ID");
		lvDeudasPagadas = (ListView) findViewById(R.id.lvDeudasPagadas);
		
		showDeudasPagadas();
	}
	
	public void showDeudasPagadas()
	{
		DBDeudasAdapter dbpadapter = new DBDeudasAdapter(this);
		resultado = dbpadapter.getDeudasPagadasToShow(this.clientes_id);
		adaptador = new AdaptadorDeudasPagadas(this);
		lvDeudasPagadas.setAdapter(adaptador);
	}
	
	
    class AdaptadorDeudasPagadas extends ArrayAdapter<Deudas> {
    	
    	Activity context;
    	
    	AdaptadorDeudasPagadas(Activity context) {
    		super(context, R.layout.listitem_deudas_pagadas, resultado);
    		this.context = context;
    	}
    	
    	public View getView(int position, View convertView, ViewGroup parent) {
			
    		View item = convertView;
		    ViewHolder holder;
		    if(item == null)
		    {
	    		LayoutInflater inflater = context.getLayoutInflater();
				item = inflater.inflate(R.layout.listitem_deudas_pagadas, null);
				
				 holder = new ViewHolder();
				 holder.lblDeudasPagadasId = (TextView)item.findViewById(R.id.lblDeudasPagadasIdListItem);
				 holder.lblDeudasPagadasFecha = (TextView)item.findViewById(R.id.lblDeudasPagadasFechaListItem);
				 holder.lblDeudasPagadasMontoTotal = (TextView)item.findViewById(R.id.lblDeudasPagadasMontoTotalListItem);
				 holder.lblDeudasPagadasMontoAdeudado = (TextView)item.findViewById(R.id.lblDeudasPagadasMontoAdeudadolListItem);
				 item.setTag(holder);
		    }else{
		    	 holder = (ViewHolder)item.getTag();
		    }
			
		    holder.lblDeudasPagadasId.setText(Integer.toString(resultado.get(position).get_id()));
		    holder.lblDeudasPagadasFecha.setText(resultado.get(position).getCustonFecha());
		    holder.lblDeudasPagadasMontoTotal.setText(Float.toString(resultado.get(position).getMontototal()));
		    holder.lblDeudasPagadasMontoAdeudado.setText(Float.toString(resultado.get(position).getMontoadeudado()));
			
			return(item);
		}
    }
}
