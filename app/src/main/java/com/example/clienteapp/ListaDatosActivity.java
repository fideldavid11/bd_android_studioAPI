package com.example.clienteapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListaDatosActivity extends AppCompatActivity {
    private ListView listViewDatos;
    private ArrayList<String> datosList;
    private DatosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_datos);

        listViewDatos = findViewById(R.id.listViewDatos);
        datosList = new ArrayList<>();
        adapter = new DatosAdapter(this, datosList);
        listViewDatos.setAdapter(adapter);

        obtenerDatosDeAPI();

        listViewDatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtener los datos del usuario seleccionado
                String usuarioSeleccionado = datosList.get(position);

                // Extraer los datos (nombre, edad, fecha de registro) de la cadena
                String nombreUsuario = obtenerNombreDeUsuario(usuarioSeleccionado);
                String edadUsuario = obtenerEdadDeUsuario(usuarioSeleccionado);
                String fechaRegistroUsuario = obtenerFechaRegistroDeUsuario(usuarioSeleccionado);

                // Crear un Intent para abrir editarDATOS
                Intent intent = new Intent(ListaDatosActivity.this, editarDATOS.class);

                // Pasar los datos del usuario como extras al Intent
                intent.putExtra("nombreUsuario", nombreUsuario);
                intent.putExtra("edadUsuario", edadUsuario);
                intent.putExtra("fechaRegistroUsuario", fechaRegistroUsuario);

                // Iniciar la actividad editarDATOS
                startActivity(intent);
            }
        });

        // Agregar un OnItemLongClickListener para mostrar el diálogo de eliminación
        listViewDatos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String elementoAEliminar = datosList.get(position);
                mostrarDialogoDeConfirmacion(elementoAEliminar);
                return true;
            }
        });
    }

    private void obtenerDatosDeAPI() {
        String url = "https://prubafidelll.000webhostapp.com/crud/get.php";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                datosList.clear(); // Limpiar la lista antes de agregar nuevos elementos

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String nombre = jsonObject.getString("nombre");
                                    String edad = jsonObject.getString("edad");
                                    String fechaRegistro = jsonObject.getString("fecharegistro");
                                    datosList.add("Nombre: " + nombre + "\nEdad: " + edad + "\nFecha de Registro: " + fechaRegistro);
                                }

                                adapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ListaDatosActivity.this, "Error al obtener datos de la API", Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void mostrarDialogoDeConfirmacion(final String elementoAEliminar) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar Elemento");
        builder.setMessage("¿Está seguro de que desea eliminar este elemento?");

        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Llama al método para eliminar el elemento
                eliminarElemento(elementoAEliminar);
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void eliminarElemento(final String elementoAEliminar) {
        String url = "https://prubafidelll.000webhostapp.com/crud/delete.php";

        // Crear una solicitud POST para enviar el elemento a eliminar
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Manejar la respuesta del servidor después de la eliminación
                        if (response != null && response.contains("Registro eliminado correctamente")) {
                            // Eliminar el elemento de la lista y notificar al adaptador
                            datosList.remove(elementoAEliminar);
                            adapter.notifyDataSetChanged();

                            // Mostrar un mensaje al usuario
                            Toast.makeText(ListaDatosActivity.this, "Elemento eliminado correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            // Mostrar un mensaje de error en caso de fallo en la eliminación
                            Toast.makeText(ListaDatosActivity.this, "Error al eliminar el elemento", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Mostrar un mensaje de error en caso de problemas con la solicitud
                        Toast.makeText(ListaDatosActivity.this, "Error al eliminar el elemento", Toast.LENGTH_SHORT).show();
                    }
                }) {
            // Agregar los parámetros necesarios para la solicitud, por ejemplo, el elemento a eliminar
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("elementoAEliminar", elementoAEliminar);
                return params;
            }
        };

        // Agregar la solicitud a la cola de solicitudes de Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    // Función para obtener el nombre del usuario desde la cadena
    private String obtenerNombreDeUsuario(String usuario) {
        // Dividir la cadena por saltos de línea
        String[] partes = usuario.split("\n");

        // Obtener la primera parte después de "Nombre: "
        if (partes.length > 0) {
            String nombreParte = partes[0];
            if (nombreParte.startsWith("Nombre: ")) {
                return nombreParte.substring("Nombre: ".length());
            }
        }

        return "";
    }

    // Función para obtener la edad del usuario desde la cadena
    private String obtenerEdadDeUsuario(String usuario) {
        // Dividir la cadena por saltos de línea
        String[] partes = usuario.split("\n");

        // Obtener la segunda parte después de "Edad: "
        if (partes.length > 1) {
            String edadParte = partes[1];
            if (edadParte.startsWith("Edad: ")) {
                return edadParte.substring("Edad: ".length());
            }
        }

        return "";
    }

    // Función para obtener la fecha de registro del usuario desde la cadena
    private String obtenerFechaRegistroDeUsuario(String usuario) {
        // Dividir la cadena por saltos de línea
        String[] partes = usuario.split("\n");

        // Obtener la tercera parte después de "Fecha de Registro: "
        if (partes.length > 2) {
            String fechaParte = partes[2];
            if (fechaParte.startsWith("Fecha de Registro: ")) {
                return fechaParte.substring("Fecha de Registro: ".length());
            }
        }

        return "";
    }
}
