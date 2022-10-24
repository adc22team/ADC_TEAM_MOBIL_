package com.adc.team.adc_team_mobil;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Clase de la pantalla principal del login
 */

public class PantallaPrincipal extends AppCompatActivity {
    //Declaración de variables
    private TextView tvResultado,tvUsuari,tvPwd,tvId,tvRol;
    Button btnLogOut;
    private static final String TAG = "Resposta server :";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);

        tvUsuari    = (TextView) findViewById(R.id.tvUsuari);
        tvPwd       = (TextView) findViewById(R.id.tvPwd);
        tvId        = (TextView) findViewById(R.id.tvId);
        tvRol       = (TextView) findViewById(R.id.tvRol);

        String usuari  = getIntent().getStringExtra("usuari");
        String pwd     = getIntent().getStringExtra("pwd");
        String  id     = getIntent().getStringExtra("id");
        String  rol    = getIntent().getStringExtra("rol");

        tvUsuari.setText(usuari);
        tvPwd.setText(pwd);
        tvId.setText(id);
        tvRol.setText(rol);

        btnLogOut = (Button) findViewById(R.id.btn_logout);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Task2().execute(tvUsuari.getText().toString());
            }
        });
    }

    class Task2 extends AsyncTask<String,Void, String> {

        @Override
        protected void onPreExecute(){
            btnLogOut.setEnabled(false);
        }

        @Override
        protected String doInBackground(String... strings) {

            try {

                Socket sc;
                sc = new Socket("192.168.0.15", 5000);
                DataInputStream in = new DataInputStream(sc.getInputStream());
                DataOutputStream out = new DataOutputStream(sc.getOutputStream());

                // Llegir la resposta del servidor al establir la connexió
                String resposta_svr = in.readUTF();
                Log.i(TAG,resposta_svr);
                Log.i(TAG, String.valueOf(String.valueOf(tvUsuari.getText().toString())) );
                Log.i(TAG, String.valueOf(String.valueOf( tvPwd.getText().toString())) );

                //Enviem resposta al servidor amb el usuari i la contrasenya i 0
                out.writeUTF("LOGIN,"+ String.valueOf(tvUsuari.getText().toString()) + ","
                        + String.valueOf(tvPwd.getText().toString()) + ","
                        + String.valueOf(tvId.getText().toString()));

                //Executem la consulta de la crida per sortir
                out.writeUTF("USER_EXIT");

            } catch (IOException  e) {
                e.printStackTrace();
            }
            return strings[0];
        }
        @Override
        protected void onPostExecute(String s){

            Intent intent = new Intent(PantallaPrincipal.this,MainActivity.class);
            startActivity(intent);
        }
    }
}
