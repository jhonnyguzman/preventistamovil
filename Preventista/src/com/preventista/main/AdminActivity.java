package com.preventista.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class AdminActivity extends Activity implements OnClickListener{

	private Button btnSyncTo, btnSyncFrom, btnConfigDB, btnLogFileSql;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_admin);
		
		btnSyncTo = (Button) findViewById(R.id.btnSyncTo);
		btnSyncFrom = (Button) findViewById(R.id.btnSyncFrom);
		btnConfigDB = (Button) findViewById(R.id.btnConfigDB);
		btnLogFileSql = (Button) findViewById(R.id.btnLogFileSql);
		
		btnSyncTo.setOnClickListener(this);
		btnSyncFrom.setOnClickListener(this);
		btnConfigDB.setOnClickListener(this);
		btnLogFileSql.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.btnSyncTo:
				Intent ir_a_sync_to = new Intent("com.preventista.main.MAINSYNCTO");
				startActivity(ir_a_sync_to);
				break;
			case R.id.btnSyncFrom:
				Intent ir_a_sync_from = new Intent("com.preventista.main.MAINSYNCFROM");
				startActivity(ir_a_sync_from);
				break;
			
			case R.id.btnConfigDB:
				Intent ir_a_config_db = new Intent("com.preventista.main.MAINCONFIGDB");
				startActivity(ir_a_config_db);
				break;
			
			case R.id.btnLogFileSql:
				Intent ir_a_backuplogs = new Intent("com.preventista.main.MAINBACKUPLOGS");
				startActivity(ir_a_backuplogs);
				break;
			}
	}

}
