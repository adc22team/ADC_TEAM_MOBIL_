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
         * En el caso de que no se ingrese ningun valor en campos usuario y/o contraseña
         * se controla a través del método validaUsuario()
         */
        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //llamamiento al método validaUsuario con el valor recogido en campos usuario y contraseña
                validaUsuario(etUsuario.getText().toString(), etClave.getText().toString());
                //Luego de validar que el usario ha ingresado un usuario y su contraseña, ejecuta toda la acción para hacer un Login
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
         * Antes de comenzar el hilo, el botón de Login no es visible, osea no está activo en un primer momento
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
                sc = new Socket("192.168.0.19", 5000);
                DataInputStream in = new DataInputStream(sc.getInputStream());
                DataOutputStream out = new DataOutputStream(sc.getOutputStream());

                //Variable que recibirá la respuesta del servidor una vez establecida la conexión
                // Enviament de la clau pública del servidor
                out.writeUTF("Enviament de la clau pública del client");
                // Llegim la clau pública del servidor
                String resposta_svr = in.readUTF();

                //Métodos Log que muestran información en el logcat

                // Se recibe la cable pública del servidor
                Log.i(TAG, resposta_svr);

                //Muestra la cadena del Usuario
                Log.i(TAG, String.valueOf(String.valueOf(etUsuario.getText().toString())));
                //Muestra la cadena del password
                Log.i(TAG, String.valueOf(String.valueOf(etClave.getText().toString())));

                //Enviamos respuesta al servidor con el usuario, contraseña y valor 0
                out.writeUTF("0,LOGIN," +String.valueOf(etUsuario.getText().toString()) + "," + String.valueOf(etClave.getText().toString()));

                //Variable a la que se le asigna el valor entero del id de conexión del usuario y que recibimos del Servidor al establecer conexión
                resposta_id = in.readInt();
                //Método Log que muestra el valor de la respuesta
                Log.i(TAG, "El usuario tiene el id asignado: " + String.valueOf(resposta_id));

                //si el server nos ha proporcionado un id válido, es decir un número diferente de cero, implica que
                //se trata de un usario registrado en la base de datos
                if (resposta_id != 0) {
                    //entonces asignamos en la variable resposta_rol, el valor recogido por el server (rol 1, 2 ó 3)
                    resposta_rol = in.readInt();
                }

            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "ERRROOOO", Toast.LENGTH_LONG).show();
                Log.i(TAG,"ERROR......");
                e.printStackTrace();


            }

            return strings[0];
        }

        /**
         * Método que una vez realizada la conección, valida el rol y ejecuta diversas opciones dependiendo
         * del caso.
         * @param s
         */
        @Override
        protected void onPostExecute(String s) {

            progressBar.setVisibility(View.INVISIBLE);
            btnIniciarSesion.setEnabled(true);

             //si el server nos ha proporcionado un id válido, es decir un número diferente de cero, implica que
            //se trata de un usario registrado en la base de datos
            if (resposta_id != 0) {
                Log.i(TAG, "El usuario tiene el rol: " + String.valueOf(resposta_rol));
                // si el rol es 3, eso es que un Usuario normal, puede acceder a la aplicación móvil
                if (resposta_rol == 3) {
                    //ejecutamos las acciones correspondientes para acceder a la pantalla Prinicipal
                    Intent intent = new Intent(MainActivity.this, PantallaPrincipal.class);

                    intent.putExtra("usuari", etUsuario.getText().toString());
                    intent.putExtra("pwd", etClave.getText().toString());
                    intent.putExtra("id", String.valueOf(resposta_id));
                    intent.putExtra("rol", String.valueOf(resposta_rol));
                    startActivity(intent);
                } else {
                    //Si el rol es distinto de 3, ejecutamos las acciones correspondientes:
                    //mostramos dos mensajes de rol inválido y a su vez ejecutamos la actividad (clase DesconectaRolInvalido)
                    //para forzar al usuario de rol incorrecto a abandonar el acceso a la aplicación, desconectándolo por completo.

                    Toast.makeText(getApplicationContext(), "Rol invalido para la aplicación móvil", Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Ingrese desde la aplicación de escritorio.", Toast.LENGTH_LONG).show();
                    Intent intent2 =new Intent(MainActivity.this, DesconectaRolInvalido.class);
                    intent2.putExtra("usuari", etUsuario.getText().toString());
                    intent2.putExtra("id", String.valueOf(resposta_id));
                    intent2.putExtra("rol", String.valueOf(resposta_rol));

                    startActivity(intent2);

                }
            } else{
                //Exista otra posibilidad y es que el usuario haya ingresado mal alguna de sus credenciales
                //en este caso, se muestra un mensaje por pantalla de Credenciales incorrectas.
                Toast.makeText(getApplicationContext(), "Credenciales incorrectas", Toast.LENGTH_LONG).show();
                Log.i(TAG, "El usuario ha ingresado mal las credenciales: "+ "User:" + String.valueOf(String.valueOf(etUsuario.getText().toString())) + "  Pass: "+String.valueOf(String.valueOf(etClave.getText().toString())));
            }
        }
    }

    /**
     * Método que permite valorar en una primera instancia que el usuario ingrese usuario y contraseña, ambos valores
     * @param usuario
     * @param clave
     * @return
     */
    public boolean validaUsuario(String usuario, String clave) {

        if (usuario.isEmpty() || clave.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Ingrese usuario y contraseña.", Toast.LENGTH_LONG).show();
            usarioValido = false;
        } else {
            usarioValido = true;
        }
        return usarioValido;
    }
    }


