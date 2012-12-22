package com.preventista.main;


import com.preventista.db.DBUsuarioAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity{
	
	public static int usuario_id;
	
	EditText edtUsuario, edtPassword;
	Button btnIngresar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_form);
		
		edtUsuario = (EditText) findViewById(R.id.edtUsuarioLogin);
		edtPassword  = (EditText) findViewById(R.id.edtPasswordLogin);
		btnIngresar = (Button) findViewById(R.id.btnIngresarLogin);
		
		btnIngresar.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				String username = edtUsuario.getText().toString().trim();
				String password = edtPassword.getText().toString().trim();
				int id = 0;
				try{
					if(username.length() > 0 && password.length() > 0){
						DBUsuarioAdapter dbUser = new DBUsuarioAdapter (LoginActivity.this);
						PreRes pr = new PreRes();
						id = dbUser.login(username, pr.md5(password));
						if(id != 0){
							usuario_id = id;
							Intent ir_a_menuprincipal = new Intent("com.preventista.main.MAINMENU");
							Toast.makeText(LoginActivity.this, "Bienvenido " + username + "!!",Toast.LENGTH_SHORT).show();
							startActivity(ir_a_menuprincipal);
						}else{
							Toast mensaje = Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrecto", Toast.LENGTH_SHORT);
							mensaje.show();		
						}
					}else{
						Toast.makeText(LoginActivity.this, "Ingrese usuario y contraseña", Toast.LENGTH_SHORT).show();
					}
					
				}catch(Exception e){
					Toast mensaje = Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
					mensaje.show();
				}
				
			}
			
		});
		
	}

}
