package com.example.clienteapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    private EditText etNombre, etEdad;
    private TextView tvFechaRegistro;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNombre = findViewById(R.id.etNombre);
        etEdad = findViewById(R.id.etEdad);
        tvFechaRegistro = findViewById(R.id.tvFechaRegistro);
        Button btnInsertData = findViewById(R.id.btnInsertData);
        Button btnFechaRegistro = findViewById(R.id.btnFechaRegistro);

        calendar = Calendar.getInstance();

        btnFechaRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        Button btnVerdatos = findViewById(R.id.btnVerdatos);
        btnVerdatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Iniciar la actividad ListaDatosActivity
                Intent intent = new Intent(MainActivity.this, ListaDatosActivity.class);
                startActivity(intent);
            }
        });

        btnInsertData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtén los valores de los campos
                String nombre = etNombre.getText().toString();
                String edad = etEdad.getText().toString();
                String fechaRegistro = tvFechaRegistro.getText().toString();

                // Llama a una función para enviar los datos al servidor
                sendDataToServer(nombre, edad, fechaRegistro);
            }
        });
    }



    private void showDatePickerDialog() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(year, month, day);
                String formattedDate = String.format("%04d-%02d-%02d", year, month + 1, day);
                tvFechaRegistro.setText(formattedDate);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private void sendDataToServer(final String nombre, final String edad, final String fechaRegistro) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL("https://prubafidelll.000webhostapp.com/crud/insert.php");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);

                    // Construye los datos a enviar en el cuerpo de la solicitud
                    String data = "nombre=" + nombre + "&edad=" + edad + "&fecharegistro=" + fechaRegistro;

                    DataOutputStream os = new DataOutputStream(connection.getOutputStream());
                    os.writeBytes(data);
                    os.flush();
                    os.close();

                    int responseCode = connection.getResponseCode();

                    if (responseCode == 200) {
                        Scanner scanner = new Scanner(connection.getInputStream());
                        String response = scanner.useDelimiter("\\A").next();
                        scanner.close();
                        return response;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return "Error: No se pudo conectar al servidor.";
            }

            protected void onPostExecute(String response) {
                // Aquí puedes manejar la respuesta del servidor
                // Por ejemplo, mostrar un mensaje al usuario
                // o realizar alguna acción en función de la respuesta.

                if (response.equals("Datos insertados correctamente.")) {
                    // La operación fue exitosa, muestra un mensaje de éxito
                    Toast.makeText(MainActivity.this, "Datos insertados correctamente.", Toast.LENGTH_SHORT).show();

                    // También podrías restablecer los campos del formulario
                    etNombre.setText("");
                    etEdad.setText("");
                    tvFechaRegistro.setText("");
                } else if (response.startsWith("Error:")) {
                    // Hubo un error en la operación, muestra un mensaje de error
                    Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
                } else {
                    // Manejar otros escenarios según la respuesta
                    // Por ejemplo, redirigir a otra actividad si es necesario
                }
            }
        }.execute();
    }
}
