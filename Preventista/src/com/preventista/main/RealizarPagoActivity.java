package com.preventista.main;

import java.util.ArrayList;

import com.preventista.db.DBClienteAdapter;
import com.preventista.db.DBCuentaCorrienteAdapter;
import com.preventista.db.DBDeudasAdapter;
import com.preventista.db.DBPagosAdapter;
import com.preventista.db.DBPagosDeudasAdapter;
import com.preventista.db.DBPagosPedidosAdapter;
import com.preventista.db.DBPedidosAdapter;
import com.preventista.entidades.Clientes;
import com.preventista.entidades.CuentaCorriente;
import com.preventista.entidades.Deudas;
import com.preventista.entidades.Pagos;
import com.preventista.entidades.PagosDeudas;
import com.preventista.entidades.PagosPedidos;
import com.preventista.entidades.Pedidos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class RealizarPagoActivity extends Activity {
	
	private ListView lvPedidosAdeudados;
	private Bundle bundle;
	
	private ArrayList<Pedidos> pedidos = new ArrayList<Pedidos>();
	private ArrayList<Deudas> deudas = new ArrayList<Deudas>();
	
	private CuentaCorriente cuentacorriente = null;
	private PreRes preres = new PreRes();
	
	//atributos para las preferencias 
	private SharedPreferences sp;
	private int nextidpagos;
	private int nextidpagospedidos;
	private int nextidpagosdeudas;
	
	private AdaptadorPedidosAdeudados adaptador;
	
	private class ViewHolder {
        TextView lblPedidosAdeudadosId;
        TextView lblPedidosAdeudadosFecha;
        TextView lblPedidosAdeudadosMontoTotal;
        TextView lblPedidosAdeudadosMontoAdeudado;
	}
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_pagos);

		lvPedidosAdeudados = (ListView) findViewById(R.id.lvPedidosAdeudados);

		bundle = getIntent().getExtras();
		
		//preferencias
		sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		nextidpagosdeudas = Integer.parseInt(sp.getString("etPrefPagosDeudasID", "50000"));
		
		showPedidos();
	}
	
	@Override
	public void onBackPressed() 
	{  
		DBClienteAdapter dbcliente = new DBClienteAdapter(this);
		Clientes cliente = new Clientes();
		cliente = dbcliente.getById(bundle.getInt("CLIENTES_ID"));
		
		Intent ir_a_cc= new Intent("com.preventista.main.CUENTACORRIENTE");
		Bundle bolsa = new Bundle();
		bolsa.putInt("_ID", cliente.get_id());
		bolsa.putString("APELLIDO_NOMBRE", cliente.getApellidoNombre());
		
		ir_a_cc.putExtras(bolsa);
		startActivity(ir_a_cc);
	    
	}
	
	public void showPedidos()
	{
		DBPedidosAdapter dbpadapter = new DBPedidosAdapter(this);
		pedidos = dbpadapter.getPedidosAdeudadosToShow(bundle.getInt("CLIENTES_ID"));
		adaptador = new AdaptadorPedidosAdeudados(this);
		lvPedidosAdeudados.setAdapter(adaptador);
		
		lvPedidosAdeudados.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				showDialogPago(position);
				 //adaptador.notifyDataSetChanged();
			}
	 		 
	 	 });
	}
	
	
	public void showDialogPago(final int pos)
	{	
		final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_realizar_pago);
        dialog.setTitle("Ingrese el monto a pagar");
       
        final EditText etMontoPago = (EditText) dialog.findViewById(R.id.etMontoPagoDialog);
        final TextView etMontoAdeudado = (TextView) dialog.findViewById(R.id.lblMontoAdeudadoPagoDialog);
        final TextView etRestoPago = (TextView) dialog.findViewById(R.id.lblRestoPagoDialog);
        
        Button btnAdd = (Button) dialog.findViewById(R.id.btnAddPagoDialog);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancelPagoDialog);
        
        etMontoAdeudado.setText("Monto Adeudado: $ " + Float.toString(pedidos.get(pos).getMontoadeudado()));
        
        btnAdd.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) 
			{
				 double monto_pago = 0;
				 double monto_adeudado = 0;
				
				 if(etMontoPago.getText().toString().trim().length() > 0)
			     {	
					 	nextidpagos = Integer.parseInt(sp.getString("etPrefPagosID", "50000"));
			        	DBPagosAdapter dbpago = new DBPagosAdapter(RealizarPagoActivity.this);
						Pagos pago = new Pagos();
						
			        	monto_pago = Float.parseFloat(etMontoPago.getText().toString());
			        	monto_adeudado = pedidos.get(pos).getMontoadeudado();
			        	
			        	pago.set_id(nextidpagos);
						pago.setClientes_id(bundle.getInt("CLIENTES_ID"));
						pago.setUsuarios_id(LoginActivity.usuario_id);
			    		
			        	if(monto_pago > monto_adeudado){
			        		pago.setMonto(monto_adeudado);
			        	}else{
			        		pago.setMonto(monto_pago);
			        	}
			        	
			        	int rowid = (int) dbpago.addPago(pago);
						if(rowid != -1){
							savePreferencePago(rowid);
							//pago.set_id(rowid);
							if(calcDeudaClienteNew(pago,pedidos.get(pos)))
							{
								DBPedidosAdapter dbpedido = new DBPedidosAdapter(RealizarPagoActivity.this);
								double haber = dbpedido.getHaber(pedidos.get(pos).getClientes_id());
								double debe = dbpedido.getDeuda(pedidos.get(pos).getClientes_id());
								preres.updateEstadoContable(pedidos.get(pos).getClientes_id(), haber, debe,RealizarPagoActivity.this);
							
								Toast.makeText(RealizarPagoActivity.this, "Pago ingresado correctamente!", Toast.LENGTH_SHORT).show();
								alterAdapter();
								dialog.dismiss();
							}else{
								Toast.makeText(RealizarPagoActivity.this, "Error al ingresar pago!", Toast.LENGTH_SHORT).show();
								dialog.dismiss();
							}
						}else{
							Toast.makeText(RealizarPagoActivity.this, "Error al ingresar Pago!", Toast.LENGTH_SHORT).show();
							dialog.dismiss();
						}
			     }else{
			    	 Toast.makeText(RealizarPagoActivity.this, "Ingrese un monto a pagar!", Toast.LENGTH_SHORT).show();
			     }
			}
        });
        
        btnCancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
        	
        });
        
        dialog.show();
        
        
        etMontoPago.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                float monto_adeudado = 0;
                float monto_pago = 0;
                if(etMontoPago.getText().toString().trim().length() > 0){
	                monto_adeudado = pedidos.get(pos).getMontoadeudado();
	                monto_pago = Float.parseFloat(etMontoPago.getText().toString().trim());
	                if(monto_pago > monto_adeudado){
	                	etRestoPago.setText("Resto: $ " + Float.toString(preres.formatearFloat(monto_pago - monto_adeudado)));
	                }else{
	                	etRestoPago.setText("");
	                }
                }
            }
            
            @Override
            public void afterTextChanged(Editable arg0) {}

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
        });
        
	}

	public boolean calcDeudaClienteNew(Pagos pago, Pedidos pedido)
	{
		double montoadeudado;
		boolean estado = false;
		nextidpagospedidos = Integer.parseInt(sp.getString("etPrefPagosPedidosID", "50000"));
		
		PagosPedidos pagopedido = new PagosPedidos();
		pagopedido.set_id(nextidpagospedidos);
		pagopedido.setPedidos_id(pedido.get_id());
		pagopedido.setPagos_id(pago.get_id());
		pagopedido.setMontocubierto(pedido.getMontoadeudado());
		
		DBPagosPedidosAdapter dbpagopedido = new DBPagosPedidosAdapter(this);
		DBPedidosAdapter dbpedido = new DBPedidosAdapter(this);
		
		int pagopedido_id = (int) dbpagopedido.addPagoPedido(pagopedido);
		if(pagopedido_id != -1){
			savePreferencePagosPedidos(pagopedido_id);
			montoadeudado = Double.parseDouble(String.valueOf(pedido.getMontoadeudado())) - pago.getMonto();
			if(montoadeudado == 0.0){
				pedido.setEstado(16);
				pedido.setMontoadeudado(0);
				// actualizar pedido con estado = pagado y entregado
				dbpedido.editPedido(pedido);
				estado = true;
			}else{
				pedido.setEstado(15);
				pedido.setMontoadeudado((float)montoadeudado);
				// actualizar pedido con estado = entregado y parcialmente pagado
				dbpedido.editPedido(pedido);
				estado = true;
			} 
		}
		
		return estado;
	}
	
	
	private class AdaptadorPedidosAdeudados extends ArrayAdapter<Pedidos> 
	{
	    	
    	Activity context;
    	
    	AdaptadorPedidosAdeudados(Activity context) {
    		super(context, R.layout.listitem_pedidos_adeudados, pedidos);
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
			
		    holder.lblPedidosAdeudadosId.setText(Integer.toString(pedidos.get(position).get_id()));
		    holder.lblPedidosAdeudadosFecha.setText(pedidos.get(position).getCustonCreatedAt());
		    holder.lblPedidosAdeudadosMontoTotal.setText(Float.toString(pedidos.get(position).getMontototal()));
		    holder.lblPedidosAdeudadosMontoAdeudado.setText(Float.toString(pedidos.get(position).getMontoadeudado()));
			
			return(item);
		}
    }
	
	
	 private void alterAdapter() 
    {
		pedidos.clear(); 
		ArrayList<Pedidos> items;
		DBPedidosAdapter dbpadapter = new DBPedidosAdapter(this);
		items = dbpadapter.getPedidosAdeudadosToShow(bundle.getInt("CLIENTES_ID"));
		
		for (int i = 0; i < items.size(); i++) {
			pedidos.add(items.get(i));
		}
		adaptador.notifyDataSetChanged();	
    }
	 

	/*public boolean calcDeudaCliente(Pagos pago)
	{
		float saldo,  porcobrar, pagoparcial, haber, debe;
		boolean flag = false;
		int i = 0;
		
		DBCuentaCorrienteAdapter dbcc = new DBCuentaCorrienteAdapter(this);
		cuentacorriente = dbcc.getCuentaCorriente(pago.getClientes_id());
		
		if(cuentacorriente != null)
		{
			saldo = calcHistorialDeudaCliente(pago, cuentacorriente);
			
			if(saldo > 0)
			{	
				DBPedidosAdapter dbpedido = new DBPedidosAdapter(this);
				DBDeudasAdapter dbdeuda = new DBDeudasAdapter(this);
				
				//obtener pedidos entregados o entregados y parcialmente pagados
				pedidos = dbpedido.getPedidosEP(pago.getClientes_id());
				flag = true;
				
				if(pedidos.size() > 0)
				{
					//comprobamos si existe saldo negativo en la cuenta corriente del cliente
					// si existe lo sumamos al monto del pago ingresado
					if(cuentacorriente.getDebe() < 0){
						saldo = saldo + (cuentacorriente.getDebe() * (-1));
					}
					for(i = 0; i < pedidos.size(); i++)
					{
						if(flag)       
						{
							saldo = saldo - pedidos.get(i).getMontoadeudado();
							if(saldo >= 0)
							{
								PagosPedidos pagopedido = new PagosPedidos();
								pagopedido.set_id(nextidpagospedidos);
								pagopedido.setPedidos_id(pedidos.get(i).get_id());
								pagopedido.setPagos_id(pago.get_id());
								pagopedido.setMontocubierto(pedidos.get(i).getMontoadeudado());
								
								DBPagosPedidosAdapter dbpagopedido = new DBPagosPedidosAdapter(this);
								int pagopedido_id = (int) dbpagopedido.addPagoPedido(pagopedido);
								//savePreferencePagosPedidos(pagopedido_id);
								nextidpagospedidos++;
								
								// actualizar pedido con estado = pagado y entregado
								Pedidos pedido1 = new Pedidos();
								pedido1.set_id(pedidos.get(i).get_id());
								pedido1.setEstado(16);
								pedido1.setMontoadeudado(0);
								dbpedido.editPedido(pedido1);
								
								if(saldo == 0) flag = false;
							}
							else
							{
								porcobrar = saldo * (-1);
								pagoparcial = pedidos.get(i).getMontototal() -  porcobrar;
								
								PagosPedidos pagopedido = new PagosPedidos();
								pagopedido.set_id(nextidpagospedidos);
								pagopedido.setPedidos_id(pedidos.get(i).get_id());
								pagopedido.setPagos_id(pago.get_id());
								pagopedido.setMontocubierto(pagoparcial);
								
								DBPagosPedidosAdapter dbpagopedido = new DBPagosPedidosAdapter(this);
								int pagopedido_id = (int) dbpagopedido.addPagoPedido(pagopedido);
								//savePreferencePagosPedidos(pagopedido_id);
								nextidpagospedidos++;
								
								if(porcobrar != pedidos.get(i).getMontototal()){
									// actualizar pedido a estado = entregado y parcialmente pagado
									Pedidos pedido1 = new Pedidos();
									pedido1.set_id(pedidos.get(i).get_id());
									pedido1.setEstado(15);
									pedido1.setMontoadeudado(porcobrar);
									dbpedido.editPedido(pedido1);
								}
								flag = false;
							}
						}
					}
					if(saldo > 0){
						haber = dbpedido.getHaber(pago.getClientes_id()) + dbdeuda.getHaber(pago.getClientes_id());
						debe = (dbpedido.getDeuda(pago.getClientes_id()) + dbdeuda.getDeuda(pago.getClientes_id()) ) - saldo;
						preres.updateEstadoContable(pago.getClientes_id(), haber, debe,this);
					}else{
						haber = dbpedido.getHaber(pago.getClientes_id()) + dbdeuda.getHaber(pago.getClientes_id());
						debe = dbpedido.getDeuda(pago.getClientes_id()) + dbdeuda.getDeuda(pago.getClientes_id());
						preres.updateEstadoContable(pago.getClientes_id(), haber, debe,this);
					}
					
					savePreferencePagosPedidos(nextidpagospedidos);
					
				}else{
					haber = dbpedido.getHaber(pago.getClientes_id()) + dbdeuda.getHaber(pago.getClientes_id());
					if(cuentacorriente.getDebe() < 0) debe = cuentacorriente.getDebe() - saldo;
					else debe = (dbpedido.getDeuda(pago.getClientes_id()) + dbdeuda.getDeuda(pago.getClientes_id()) ) - saldo;
					preres.updateEstadoContable(pago.getClientes_id(), haber, debe,this);
				}
				
				return true;
			
			}
			return true;
		}
		
		return true;
	}*/
	
	
	/*public float calcHistorialDeudaCliente(Pagos pago, CuentaCorriente cuentacorriente)
	{
		float saldo,  porcobrar, pagoparcial, haber, debe;
		boolean flag = false;
		int i = 0;
		
		DBDeudasAdapter dbdeuda = new DBDeudasAdapter(this);
		DBPedidosAdapter dbpedido = new DBPedidosAdapter(this);
		
		//obtener deudas 'sin pagar' o 'parcialmente pagadas'
		deudas = dbdeuda.getDeudasEP(pago.getClientes_id());
		
		saldo = pago.getMonto();
		flag = true;
		
		if(deudas.size() > 0)
		{
			//comprobamos si existe saldo negativo en la cuenta corriente del cliente
			// si existe lo sumamos al monto del pago ingresado
			if(cuentacorriente.getDebe() < 0){
				saldo = saldo + (cuentacorriente.getDebe() * (-1));
			}
			for(i = 0; i < deudas.size(); i++)
			{
				if(flag)       
				{
					saldo = saldo - deudas.get(i).getMontoadeudado();
					if(saldo >= 0)
					{
						PagosDeudas pagodeuda = new PagosDeudas();
						pagodeuda.set_id(nextidpagosdeudas);
						pagodeuda.setDeudas_id(deudas.get(i).get_id());
						pagodeuda.setPagos_id(pago.get_id());
						pagodeuda.setMontocubierto(deudas.get(i).getMontoadeudado());
						
						DBPagosDeudasAdapter dbpagodeuda = new DBPagosDeudasAdapter(this);
						int pagodeuda_id = (int) dbpagodeuda.addPagoDeuda(pagodeuda);
						//savePreferencePagosDeudas(pagodeuda_id);
						nextidpagosdeudas++;
						
						// actualizar deuda con estado = pagada
						Deudas deuda1 = new Deudas();
						deuda1.set_id(deudas.get(i).get_id());
						deuda1.setEstado(28); //estado 'pagada'
						deuda1.setMontoadeudado(0);
						dbdeuda.editDeuda(deuda1);
						
						if(saldo == 0) flag = false;
					}
					else
					{
						porcobrar = saldo * (-1);
						pagoparcial = deudas.get(i).getMontototal() -  porcobrar;
						
						PagosDeudas pagodeuda = new PagosDeudas();
						pagodeuda.set_id(nextidpagosdeudas);
						pagodeuda.setDeudas_id(deudas.get(i).get_id());
						pagodeuda.setPagos_id(pago.get_id());
						pagodeuda.setMontocubierto(pagoparcial);
						
						DBPagosDeudasAdapter dbpagodeuda = new DBPagosDeudasAdapter(this);
						int pagodeuda_id = (int) dbpagodeuda.addPagoDeuda(pagodeuda);
						//savePreferencePagosDeudas(pagodeuda_id);
						nextidpagosdeudas++;
						
						if(porcobrar != deudas.get(i).getMontototal()){
							// actualizar pedido a estado = parcialmente pagada
							Deudas deuda1 = new Deudas();
							deuda1.set_id(deudas.get(i).get_id());
							deuda1.setEstado(27); // 'parcialmente pagada'
							deuda1.setMontoadeudado(porcobrar);
							dbdeuda.editDeuda(deuda1);
						}
						flag = false;
					}
				}
			}
			
			haber = dbdeuda.getHaber(pago.getClientes_id()) + dbpedido.getHaber(pago.getClientes_id());
			debe = dbdeuda.getDeuda(pago.getClientes_id()) + dbpedido.getDeuda(pago.getClientes_id());
			preres.updateEstadoContable(pago.getClientes_id(), haber, debe,this);
			
			savePreferencePagosDeudas(nextidpagosdeudas++);
			
			return saldo;
			
		}else{
			return saldo;
		}
		
	}*/
	
	
	
	
	private void savePreferencePago(int next_id)
	{
		int newid = next_id + 1;
		SharedPreferences settings =  PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("etPrefPagosID", Integer.toString(newid));
		editor.commit();
	}
	
	private void savePreferencePagosPedidos(int next_id)
	{
		int newid = next_id + 1;
		SharedPreferences settings =  PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("etPrefPagosPedidosID", Integer.toString(newid));
		editor.commit();
		
	}
	private void savePreferencePagosDeudas(int next_id)
	{
		int newid = next_id + 1;
		SharedPreferences settings =  PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("etPrefPagosDeudasID", Integer.toString(newid));
		editor.commit();
		
	}
}
