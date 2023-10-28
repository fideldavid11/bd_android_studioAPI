package com.example.clienteapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.clienteapp.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class editarDATOS extends AppCompatActivity {
    private EditText etNombre, etEdad;
    private TextView tvFechaRegistro;
    private int año, mes, día;
    private String usuarioID; // Agregar una variable para almacenar el ID del usuario que se está editando

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_datos);

        etNombre = findViewById(R.id.etNombre);
        etEdad = findViewById(R.id.etEdad);
        tvFechaRegistro = findViewById(R.id.tvFechaRegistro);

        Button btnFechaRegistro = findViewById(R.id.btnFechaRegistro);
        Button btnActualizarDatos = findViewById(R.id.btnActualizarDatos);

        // Recuperar los datos del usuario seleccionado
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuarioID = extras.getString("usuarioID");
            String nombreUsuario = extras.getString("nombreUsuario");
            String edadUsuario = extras.getString("edadUsuario");
            String fechaRegistroUsuario = extras.getString("fechaRegistroUsuario");

            // Cargar los datos en los campos de edición
            etNombre.setText(nombreUsuario);
            etEdad.setText(edadUsuario);
            tvFechaRegistro.setText(fechaRegistroUsuario);
        }



        btnFechaRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarCalendario();
            }
        });

        btnActualizarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarDatos();
            }
        });
    }

    private void mostrarCalendario() {
        final Calendar calendario = Calendar.getInstance();
        año = calendario.get(Calendar.YEAR);
        mes = calendario.get(Calendar.MONTH);
        día = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        año = year;
                        mes = monthOfYear;
                        día = dayOfMonth;
                        actualizarFecha();
                    }
                }, año, mes, día);
        datePickerDialog.show();
    }



    private void actualizarFecha() {
        tvFechaRegistro.setText(día + "/" + (mes + 1) + "/" + año);
    }

    private void actualizarDatos() {
        String nuevoNombre = etNombre.getText().toString();
        String nuevaEdad = etEdad.getText().toString();
        String nuevaFechaRegistro = tvFechaRegistro.getText().toString();

        // Configurar la URL de la API de actualización
        String url = "https://prubafidelll.000webhostapp.com/crud/update.php";

        // Crear una solicitud POST para enviar los datos actualizados
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                // Mostrar un mensaje de éxito al usuario
                                Toast.makeText(editarDATOS.this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                            } else {
                                // Mostrar un mensaje de error en caso de fallo en la actualización
                                Toast.makeText(editarDATOS.this, "Error al actualizar los datos", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Mostrar un mensaje de error en caso de problemas con la respuesta
                            Toast.makeText(editarDATOS.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Mostrar un mensaje de error en caso de problemas con la solicitud
                        Toast.makeText(editarDATOS.this, "Error al actualizar los datos", Toast.LENGTH_SHORT).show();
                    }
                }) {
            // Agregar los parámetros necesarios para la solicitud, incluyendo el ID del usuario
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("ID", usuarioID); // ID del usuario que se está editando
                params.put("nombre", nuevoNombre);
                params.put("edad", nuevaEdad);
                params.put("fecharegistro", nuevaFechaRegistro);
                return params;
            }
        };

        // Agregar la solicitud a la cola de solicitudes de Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}