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

public class DeudasSinPagarActivity extends Activity{
	
	private Bundle bundle;
	private int clientes_id;
	
	private ListView lvDeudasSinPagar;
	private ArrayList<Deudas> resultado = new ArrayList<Deudas>();
	
	private AdaptadorDeudasSinPagar adaptador;
    
    static class ViewHolder {
        TextView lblDeudasSinPagarId;
        TextView lblDeudasSinPagarFecha;
        TextView lblDeudasSinPagarMontoTotal;
        TextView lblDeudasSinPagarMontoAdeudado;;
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_deudas_sinpagar);
		bundle = getIntent().getExtras();
		this.clientes_id = bundle.getInt("CLIENTES_ID");
		lvDeudasSinPagar = (ListView) findViewById(R.id.lvDeudasSinPagar);
		
		showDeudasSinPagar();
	}
	
	public void showDeudasSinPagar()
	{
		DBDeudasAdapter dbpadapter = new DBDeudasAdapter(this);
		resultado = dbpadapter.getDeudasSinPagarToShow(this.clientes_id);
		adaptador = new AdaptadorDeudasSinPagar(this);
		lvDeudasSinPagar.setAdapter(adaptador);
	}
	
	
    class AdaptadorDeudasSinPagar extends ArrayAdapter<Deudas> {
    	
    	Activity context;
    	
    	AdaptadorDeudasSinPagar(Activity context) {
    		super(context, R.layout.listitem_deudas_sinpagar, resultado);
    		this.context = context;
    	}
    	
    	public View getView(int position, View convertView, ViewGroup parent) {
			
    		View item = convertView;
		    ViewHolder holder;
		    if(item == null)
		    {
	    		LayoutInflater inflater = context.getLayoutInflater();
				item = inflater.inflate(R.layout.listitem_deudas_sinpagar, null);
				
				 holder = new ViewHolder();
				 holder.lblDeudasSinPagarId = (TextView)item.findViewById(R.id.lblDeudasSinPagarIdListItem);
				 holder.lblDeudasSinPagarFecha = (TextView)item.findViewById(R.id.lblDeudasSinPagarFechaListItem);
				 holder.lblDeudasSinPagarMontoTotal = (TextView)item.findViewById(R.id.lblDeudasSinPagarMontoTotalListItem);
				 holder.lblDeudasSinPagarMontoAdeudado = (TextView)item.findViewById(R.id.lblDeudasSinPagarMontoAdeudadolListItem);
				 item.setTag(holder);
		    }else{
		    	 holder = (ViewHolder)item.getTag();
		    }
			 
		    holder.lblDeudasSinPagarId.setText(Integer.toString(resultado.get(position).get_id()));
		    holder.lblDeudasSinPagarFecha.setText(resultado.get(position).getCustonFecha());
		    holder.lblDeudasSinPagarMontoTotal.setText(Float.toString(resultado.get(position).getMontototal()));
		    holder.lblDeudasSinPagarMontoAdeudado.setText(Float.toString(resultado.get(position).getMontoadeudado()));
			
			return(item);
		}
    }
}
