package com.devst.ciraquizz;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.devst.ciraquizz.model.Puntaje;
import com.devst.ciraquizz.util.Constantes;
import com.devst.ciraquizz.viewmodel.ResultadoViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Pantalla 4 - Quiz finalizado: muestra el puntaje y lo guarda en Firestore
public class ResultadoActivity extends AppCompatActivity {

    // Claves para recibir los datos desde QuizActivity
    public static final String EXTRA_ACIERTOS = "aciertos";
    public static final String EXTRA_TOTAL = "total";
    public static final String EXTRA_RESPUESTAS = "respuestas";

    private ResultadoViewModel viewModel;
    private EditText edtNombre;
    private Button btnGuardar;
    private LinearLayout listaMejores;
    private TextView txtSinPuntajes;

    private int aciertos, total;
    private List<Map<String, Object>> respuestas = new ArrayList<>();

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);

        // Datos que vienen del Quiz
        aciertos = getIntent().getIntExtra(EXTRA_ACIERTOS, 0);
        total = getIntent().getIntExtra(EXTRA_TOTAL, 10);
        ArrayList<HashMap<String, Object>> recibidas =
                (ArrayList<HashMap<String, Object>>) getIntent().getSerializableExtra(EXTRA_RESPUESTAS);
        if (recibidas != null) respuestas = new ArrayList<>(recibidas);

        TextView txtPuntaje = findViewById(R.id.txtPuntaje);
        edtNombre      = findViewById(R.id.edtNombre);
        btnGuardar     = findViewById(R.id.btnGuardar);
        listaMejores   = findViewById(R.id.listaMejores);
        txtSinPuntajes = findViewById(R.id.txtSinPuntajes);

        txtPuntaje.setText("Puntaje: " + aciertos + " / " + total);

        viewModel = new ViewModelProvider(this).get(ResultadoViewModel.class);

        // Observadores (MVVM)
        viewModel.getMensaje().observe(this, texto ->
                Toast.makeText(this, texto, Toast.LENGTH_SHORT).show());

        viewModel.getGuardado().observe(this, ok -> {
            if (ok) {
                btnGuardar.setEnabled(false);
                btnGuardar.setText("✓ Guardado");
                edtNombre.setEnabled(false);
                viewModel.cargarMejores(); // recargar el ranking con el nuevo puntaje
            }
        });

        viewModel.getMejores().observe(this, this::mostrarMejores);

        // Botones
        btnGuardar.setOnClickListener(v ->
                viewModel.guardarPuntaje(edtNombre.getText().toString(), aciertos, total, respuestas));

        findViewById(R.id.btnReintentar).setOnClickListener(v -> {
            startActivity(new Intent(this, QuizActivity.class));
            finish();
        });

        findViewById(R.id.btnVolverMenu).setOnClickListener(v -> finish());

        findViewById(R.id.btnComentario).setOnClickListener(v -> {
            Intent intent = new Intent(this, EnviarComentarioActivity.class);
            intent.putExtra(EnviarComentarioActivity.EXTRA_ORIGEN, Constantes.ORIGEN_QUIZ);
            intent.putExtra(EnviarComentarioActivity.EXTRA_NOMBRE, edtNombre.getText().toString().trim());
            startActivity(intent);
        });

        viewModel.cargarMejores();
    }

    // Dibuja las filas de "Mejores Puntajes" usando item_puntaje.xml
    private void mostrarMejores(List<Puntaje> lista) {
        listaMejores.removeAllViews();
        if (lista == null || lista.isEmpty()) {
            txtSinPuntajes.setVisibility(View.VISIBLE);
            return;
        }
        txtSinPuntajes.setVisibility(View.GONE);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < lista.size(); i++) {
            Puntaje p = lista.get(i);
            View fila = inflater.inflate(R.layout.item_puntaje, listaMejores, false);

            TextView txtPosicion = fila.findViewById(R.id.txtPosicion);
            txtPosicion.setText(String.valueOf(i + 1));
            if (i == 0) { // el primer lugar en amarillo
                txtPosicion.setBackgroundTintList(
                        getColorStateList(R.color.cira_amarillo));
            }
            ((TextView) fila.findViewById(R.id.txtNombre)).setText(p.getNombre());
            ((TextView) fila.findViewById(R.id.txtPuntos)).setText(String.valueOf(p.getPuntaje()));

            listaMejores.addView(fila);
        }
    }
}