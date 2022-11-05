package com.adc.team.adc_team_mobil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Clase que representa la pantalla del Login de usuario movil
 * @author Daniela Gutierrez
 */

public class MainActivity extends AppCompatActivity {

    //Declaración de variables
    Button btnIniciarSesion;
    EditText etUsuario, etClave;
    ProgressBar progressBar;
    int resposta_id;
    int resposta_rol;
    boolean usarioValido;
    private static final String TAG = "Resposta server :";

    /**
     * Inicia la actividad
     *
     * @param savedInstanceState guarda el estado de la pantalla
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //asignamos en cada variable el recurso correspondiente definido en el XML, para poder acceder a cada  uno de ellos

        btnIniciarSesion = (Button) findViewById(R.id.btn_login);
        etUsuario = (EditText) findViewById(R.id.editTextUsuari);
        etClave = (EditText) findViewById(R.id.editTextPwd);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        /**
         * Acción del botón Login para recoger el nombre del usuario
         */
        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                validaUsuario(etUsuario.getText().toString(), etClave.getText().toString());
                if (usarioValido) {
                    new Task1().execute(etUsuario.getText().toString());
                }
            }
        });
    }

    /**
     * Desarrollo de la acción recoger el nombre del usuario
     * Extiende la clase AsyncTask que permite ejecutar tareas en segundo plano, a manera de hilos
     */
    class Task1 extends AsyncTask<String, Void, String> {
        /**
         * Métdodo Override onPreExecute()
         * Antes de comenzar el hilo, el botón de Login no es visible, osea no está activo
         */
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            btnIniciarSesion.setEnabled(false);
        }


        /**
         * Método Override doInBackGround
         * Al comenzar el hilo, ejecutamos la acción en segundo plano, iniciando la conexión
         *
         * @param strings
         * @return
         */
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

                //Métodos Log que muestran información en el logcat

                // Se recibe la respuesta del server SERVER_SHOW_ELHO_established connection y se la muestra en logcat
                Log.i(TAG, resposta_svr);

                //Muestra la cadena del Usuario
                Log.i(TAG, String.valueOf(String.valueOf(etUsuario.getText().toString())));
                //Muestra la cadena del password
                Log.i(TAG, String.valueOf(String.valueOf(etClave.getText().toString())));

                //Enviamos respuesta al servidor con el usuario, contraseña y valor 0
                out.writeUTF("LOGIN," + String.valueOf(etUsuario.getText().toString()) + "," + String.valueOf(etClave.getText().toString()) + "," + "0");


                //Variable a la que se le asigna el valor entero del id de conexión del usuario y que recibimos del Servidor al establecer conexión
                resposta_id = in.readInt();
                //Método Log que muestra el valor de la respuesta
                Log.i(TAG, "El usuario tiene el id asignado: " + String.valueOf(resposta_id));

                if (resposta_id != 0) {
                    resposta_rol = in.readInt();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return strings[0];
        }

        @Override
        protected void onPostExecute(String s) {

            progressBar.setVisibility(View.INVISIBLE);
            btnIniciarSesion.setEnabled(true);

            //Log.i(TAG, "Valor de resposta_id (id asignado): "+String.valueOf(resposta_id));

            if (resposta_id != 0) {
                Log.i(TAG, "El usuario tiene el rol: " + String.valueOf(resposta_rol));

                if (resposta_rol == 3) {

                    Intent intent = new Intent(MainActivity.this, PantallaPrincipal.class);

                    intent.putExtra("usuari", etUsuario.getText().toString());
                    intent.putExtra("pwd", etClave.getText().toString());
                    intent.putExtra("id", String.valueOf(resposta_id));
                    intent.putExtra("rol", String.valueOf(resposta_rol));
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Rol invalido para la aplición móvil", Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Ingrese desde la aplición de escritorio.", Toast.LENGTH_LONG).show();
                    Intent intent2 =new Intent(MainActivity.this, DesconectaRolInvalido.class);
                    intent2.putExtra("usuari", etUsuario.getText().toString());
                    intent2.putExtra("id", String.valueOf(resposta_id));
                    intent2.putExtra("rol", String.valueOf(resposta_rol));

                    startActivity(intent2);

                }
            }

        }


    }

    public boolean validaUsuario(String usuario, String clave) {

        if (usuario.isEmpty() || clave.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Por favor ingrese usuario y contraseña", Toast.LENGTH_LONG).show();
            usarioValido = false;
        } else {
            usarioValido = true;
        }

        return usarioValido;
    }

    }


