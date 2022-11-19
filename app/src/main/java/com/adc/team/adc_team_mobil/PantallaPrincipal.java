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
 *  @author Daniela Gutierrez
 */

public class PantallaPrincipal extends AppCompatActivity {
    //Declaración de variables
    private TextView tvResultado,tvUsuari,tvPwd,tvId,tvRol;
    Button btnLogOut;
    private static final String TAG = "Resposta server :";
    private  String id_conn;

    /**
     * Método que recoge los valores del usuario
     * @param savedInstanceState
     */
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
        id_conn        = getIntent().getStringExtra("id");
        String  rol    = getIntent().getStringExtra("rol");

        tvUsuari.setText(usuari);
        tvPwd.setText(pwd); // Este valor está de modo oculto de tal manera de que no sea visible al entrar a la pantalla Principal
        tvId.setText(id_conn);
        tvRol.setText(rol);

        btnLogOut = (Button) findViewById(R.id.btn_logout);

        /**
        * Método del botón Logout que ejecuta la acción explicita en Task2, que es básicamente hacer el logout
         * desde la aplicación móvil
        */
        btnLogOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //lleva como parámetro el usuario en formato String, que se desconectará
                new Task2().execute(tvUsuari.getText().toString());
            }
        });
    }

    /**
     * Método que ejecuta la acción de Logout
     */
    class Task2 extends AsyncTask<String,Void, String> {

        @Override
        protected void onPreExecute(){
            btnLogOut.setEnabled(false);
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                //Intentamos establecer conexión con el servidor
                Socket sc;
                sc = new Socket("192.168.0.19", 5000);
                DataInputStream in = new DataInputStream(sc.getInputStream());
                DataOutputStream out = new DataOutputStream(sc.getOutputStream());

                //Variable que recibirá la respuesta del servidor una vez establecida la conexión
                // Enviament de la clau pública del servidor
                out.writeUTF("Enviament de la clau pública del client");
                // Llegim la clau pública del servidor
                String resposta_svr = in.readUTF();

                Log.i(TAG,resposta_svr);
                Log.i(TAG, "Se desconecta el usuario: "+String.valueOf(String.valueOf(tvUsuari.getText().toString())) );

                //Enviamos respuesta al servidor con el usuario, contraseña y valor 0
                //Ejecutamos la consulta de USER_EXIT
                out.writeUTF(id_conn + ",USER_EXIT");

            } catch (IOException  e) {
                e.printStackTrace();
            }
            return strings[0];
        }

        /**
         * Método que retorna a la pantalla del login
         * una vez ejecutada la acción del logout
         * @param s del método doInBackground
         */

        @Override
        protected void onPostExecute(String s){

            Intent intent = new Intent(PantallaPrincipal.this,MainActivity.class);
            startActivity(intent);
        }
    }
}