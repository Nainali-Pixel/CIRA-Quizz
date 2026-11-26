package com.devst.ciraquizz;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.devst.ciraquizz.model.Pregunta;
import com.devst.ciraquizz.viewmodel.QuizViewModel;

import java.util.List;

// Pantalla 3 - Quiz: muestra las preguntas leídas desde Firestore
public class QuizActivity extends AppCompatActivity {

    private static final String[] LETRAS = {"A", "B", "C", "D"};
    private static final int[] COLORES = {
            R.color.cira_verde, R.color.cira_azul, R.color.cira_amarillo, R.color.cira_gris
    };

    private QuizViewModel viewModel;
    private TextView txtProgreso, txtPregunta, txtImagen;
    private ProgressBar barraProgreso, cargando;
    private View contenidoPregunta;
    private final Button[] botones = new Button[4];
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        txtProgreso       = findViewById(R.id.txtProgreso);
        txtPregunta       = findViewById(R.id.txtPregunta);
        txtImagen         = findViewById(R.id.txtImagen);
        barraProgreso     = findViewById(R.id.barraProgreso);
        cargando          = findViewById(R.id.cargando);
        contenidoPregunta = findViewById(R.id.contenidoPregunta);
        botones[0] = findViewById(R.id.btnOpcionA);
        botones[1] = findViewById(R.id.btnOpcionB);
        botones[2] = findViewById(R.id.btnOpcionC);
        botones[3] = findViewById(R.id.btnOpcionD);

        findViewById(R.id.btnCerrar).setOnClickListener(v -> finish());
        for (int i = 0; i < 4; i++) {
            final int opcion = i;
            botones[i].setOnClickListener(v -> responder(opcion));
        }

        viewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        // Observadores (MVVM): la pantalla reacciona cuando cambian los datos
        viewModel.getMensaje().observe(this, texto ->
                Toast.makeText(this, texto, Toast.LENGTH_SHORT).show());

        viewModel.getPreguntasCargadas().observe(this, this::alCargarPreguntas);
        viewModel.getPreguntaActual().observe(this, this::mostrarPregunta);
        viewModel.getTerminado().observe(this, fin -> { if (fin) terminarQuiz(); });

        if (!viewModel.partidaIniciada()) {
            viewModel.cargarPreguntas();
        }
    }

    private void alCargarPreguntas(List<Pregunta> lista) {
        if (lista == null || lista.isEmpty()) {
            cargando.setVisibility(View.GONE);
            Toast.makeText(this, "No hay preguntas disponibles", Toast.LENGTH_LONG).show();
            return;
        }
        viewModel.iniciarPartida(lista);
    }

    private void mostrarPregunta(Pregunta p) {
        if (p == null) return;
        cargando.setVisibility(View.GONE);
        contenidoPregunta.setVisibility(View.VISIBLE);

        txtProgreso.setText("Pregunta " + viewModel.getNumeroActual() + "/" + viewModel.getTotal());
        barraProgreso.setMax(viewModel.getTotal());
        barraProgreso.setProgress(viewModel.getNumeroActual());
        txtPregunta.setText(p.getTexto());

        // Si es teórica, se oculta la imagen
        if (p.tieneImagen()) {
            txtImagen.setVisibility(View.VISIBLE);
            txtImagen.setText(p.getImagen());
        } else {
            txtImagen.setVisibility(View.GONE);
        }

        // Restaurar los botones a su estado normal
        for (int i = 0; i < 4; i++) {
            botones[i].setText(LETRAS[i] + "    " + p.getOpciones().get(i));
            botones[i].setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, COLORES[i])));
            botones[i].setAlpha(1f);
            botones[i].setEnabled(true);
        }
    }

    private void responder(int opcion) {
        Pregunta p = viewModel.getPreguntaActual().getValue();
        if (p == null) return;

        for (Button b : botones) b.setEnabled(false);
        boolean acierto = viewModel.responder(opcion);
        int correcta = p.getCorrecta();

        // Mostrar resultado: ✓ en la correcta, ✗ en la elegida si falló
        for (int i = 0; i < 4; i++) {
            if (i != correcta && i != opcion) botones[i].setAlpha(0.35f);
        }
        botones[correcta].setText(botones[correcta].getText() + "   ✓");
        if (!acierto) {
            botones[opcion].setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.cira_rojo)));
            botones[opcion].setText(botones[opcion].getText() + "   ✗");
        }

        // Esperar un momento y pasar a la siguiente
        handler.postDelayed(viewModel::siguientePregunta, 1200);
    }

    private void terminarQuiz() {
        // TODO (Pantalla 4): abrir ResultadoActivity con el puntaje y las respuestas
        Toast.makeText(this,
                "Puntaje: " + viewModel.getAciertos() + " / " + viewModel.getTotal(),
                Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}