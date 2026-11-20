package com.devst.ciraquizz;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Declaramos la clase con los botones
public class MenuActivity extends AppCompatActivity {

    private Button btnEscanearQr;
    private Button btnQuiz;
    private Button btnHistorial;
    private Button btnInformacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnEscanearQr = findViewById(R.id.btnEscanearQr);
        btnQuiz = findViewById(R.id.btnQuiz);
        btnHistorial = findViewById(R.id.btnHistorial);
        btnInformacion = findViewById(R.id.btnInformacion);

        // Por ahora solo mostramos un aviso.
        // Cuando creemos cada pantalla, aquí pondremos el Intent correspondiente
        btnEscanearQr.setOnClickListener(v -> avisar("Escanear QR"));
        btnQuiz.setOnClickListener(v -> avisar("Quiz"));
        btnHistorial.setOnClickListener(v -> avisar("Historial"));
        btnInformacion.setOnClickListener(v -> avisar("Información"));
    }

    private void avisar(String seccion) {
        Toast.makeText(this, "Próximamente: " + seccion, Toast.LENGTH_SHORT).show();
    }
}