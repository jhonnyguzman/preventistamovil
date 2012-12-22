package com.preventista.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.preventista.extras.WriteLogSql;

public class Backuplogs extends Activity {
	
	private ListView lvBackupLogs;
	private File[] archivos;
	private ArrayList<String> lista = new ArrayList<String>();
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.admin_backuplogs);
		
		lvBackupLogs = (ListView) findViewById(R.id.lvBackupLogs);
		cargarLista();
		
		lvBackupLogs.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				checkConfirm(position);
			}
			
		});
		
	}
	
	
	public void cargarLista()
	{
		WriteLogSql wlq = new WriteLogSql();
		archivos = wlq.getListaBackupLogs(this);
		
		if(archivos.length > 0){
			for( int i=0; i< archivos.length; i++)
		    {
		        lista.add(archivos[i].getName() );
		    }
		}
		
		//Collections.reverse(lista);
		lvBackupLogs.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lista));
		
	}
	
	
	public void checkConfirm(final int position)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("¿Deseas ir a la sección de Re-sincronización?")
		       .setCancelable(false)
		       .setPositiveButton("Si", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   Intent ir_a_resincronizacion = new Intent("com.preventista.main.RESINCRONIZACION");
		        	   Bundle bolsa = new Bundle();
		        	   bolsa.putString("NOMBRE_ARCHIVO", lista.get(position));
		        	   ir_a_resincronizacion.putExtras(bolsa);
					   startActivity(ir_a_resincronizacion);
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
}
