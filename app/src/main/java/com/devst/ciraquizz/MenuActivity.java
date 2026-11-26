package com.devst.ciraquizz;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Pantalla 1 - Menú principal de CIRA-Quizz
public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Tarjetas grandes
        View cardEscanearQr = findViewById(R.id.cardEscanearQr);
        View cardQuiz       = findViewById(R.id.cardQuiz);
        View cardHistorial  = findViewById(R.id.cardHistorial);
        View cardMateriales = findViewById(R.id.cardMateriales);

        // Botones de abajo
        View btnOpinion        = findViewById(R.id.btnOpinion);
        View btnSobreNosotros  = findViewById(R.id.btnSobreNosotros);
        View btnAdministracion = findViewById(R.id.btnAdministracion);

        // Por ahora muestran un aviso. Cuando exista cada pantalla,
        // se cambia por: startActivity(new Intent(this, QuizActivity.class));
        cardEscanearQr.setOnClickListener(v -> avisar("Escanear QR"));
        cardQuiz.setOnClickListener(v -> avisar("Quiz"));
        cardHistorial.setOnClickListener(v -> avisar("Historial"));
        cardMateriales.setOnClickListener(v -> avisar("Materiales"));
        btnOpinion.setOnClickListener(v -> avisar("Opinión / Sugerencias"));
        btnSobreNosotros.setOnClickListener(v -> avisar("Sobre nosotros"));
        btnAdministracion.setOnClickListener(v -> avisar("Administración"));
    }

    private void avisar(String seccion) {
        Toast.makeText(this, "Próximamente: " + seccion, Toast.LENGTH_SHORT).show();
    }
}