package com.adc.team.adc_team_mobil;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Clase de soporte que Desconecta un usuario de rol #1 o #2 del servidor
 *  @author Daniela Gutierrez
 */
public class DesconectaRolInvalido extends AppCompatActivity {
    //Declaración de variables
    private TextView tvResultado, tvUsuari, tvPwd, tvId, tvRol;
    Button btnLogOut;
    private static final String TAG = "Resposta server :";

    /**
     * Método que recoge los valores del usuario desde el MainActivity, es decir cuando se ha logueado
     * y recoge los valores de acceso que le ha otorgado el server
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);

        tvUsuari = (TextView) findViewById(R.id.tvUsuari);
        tvPwd = (TextView) findViewById(R.id.tvPwd);
        tvId = (TextView) findViewById(R.id.tvId);
        tvRol = (TextView) findViewById(R.id.tvRol);

        String usuari = getIntent().getStringExtra("usuari");
        String pwd = getIntent().getStringExtra("pwd");
        String id = getIntent().getStringExtra("id");
        String rol = getIntent().getStringExtra("rol");

        tvUsuari.setText(usuari);
        tvPwd.setText(pwd);
        tvId.setText(id);
        tvRol.setText(rol);

        new Task3().execute(tvUsuari.getText().toString());
    }
    /**
     * Método que ejecuta la acción de Desconexión
     */
    class Task3 extends AsyncTask<String,Void, String> {

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                //Intentamos establecer conexión con el servidor
                Socket sc;
                sc = new Socket("192.168.0.15", 5000);
                DataInputStream in = new DataInputStream(sc.getInputStream());
                DataOutputStream out = new DataOutputStream(sc.getOutputStream());

                //Variable que recibirá la respuesta del servidor una vez establecida la conexión
                String resposta_svr = in.readUTF();
                Log.i(TAG,resposta_svr);
                Log.i(TAG, "Se desconecta el usuario de rol inválido: " + String.valueOf(String.valueOf(tvUsuari.getText().toString())) );


                //Enviamos respuesta al servidor con el usuario, contraseña y valor de id asignado
                out.writeUTF("LOGIN,"+ String.valueOf(tvUsuari.getText().toString()) + ","
                        + String.valueOf(tvPwd.getText().toString()) + ","
                        + String.valueOf(tvId.getText().toString()));

                //Ejecutamos la consulta de USER_EXIT
                out.writeUTF("USER_EXIT");

            } catch (IOException  e) {
                e.printStackTrace();
            }
            return strings[0];
        }

        /**
         * Método que retorna a la pantalla del login
         * @param s del método doInBackground
         */

        @Override
        protected void onPostExecute(String s){

            Intent intent2 = new Intent(DesconectaRolInvalido.this,MainActivity.class);
            startActivity(intent2);
        }
    }
}
