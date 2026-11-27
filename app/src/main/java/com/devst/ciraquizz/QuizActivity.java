package com.devst.ciraquizz;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.devst.ciraquizz.model.Pregunta;
import com.devst.ciraquizz.util.ImagenUtil;
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

    private ImageView imgResiduo;
    private final Button[] botones = new Button[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        txtProgreso       = findViewById(R.id.txtProgreso);
        txtPregunta       = findViewById(R.id.txtPregunta);
        txtImagen         = findViewById(R.id.txtImagen);
        imgResiduo        = findViewById(R.id.imgResiduo);
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
            finish();
            return;
        }
        viewModel.iniciarPartida(lista);
        if (!viewModel.partidaIniciada()) {
            cargando.setVisibility(View.GONE);
            Toast.makeText(this, "Las preguntas no tienen un formato válido", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void mostrarPregunta(Pregunta p) {
        if (p == null || p.getOpciones() == null || p.getOpciones().size() < 4) return;
        cargando.setVisibility(View.GONE);
        contenidoPregunta.setVisibility(View.VISIBLE);

        txtProgreso.setText("Pregunta " + viewModel.getNumeroActual() + "/" + viewModel.getTotal());
        barraProgreso.setMax(viewModel.getTotal());
        barraProgreso.setProgress(viewModel.getNumeroActual());
        txtPregunta.setText(p.getTexto());

        Bitmap foto = p.tieneFoto() ? ImagenUtil.base64ABitmap(p.getImagenBase64()) : null;
        if (foto != null) {
            imgResiduo.setImageBitmap(foto);
            imgResiduo.setVisibility(View.VISIBLE);
            txtImagen.setVisibility(View.GONE);
        } else if (p.tieneImagen()) {
            imgResiduo.setVisibility(View.GONE);
            txtImagen.setVisibility(View.VISIBLE);
            txtImagen.setText(p.getImagen());
        } else {
            imgResiduo.setVisibility(View.GONE);
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

        // Si se recreó la pantalla durante la pausa tras responder, mantener el feedback visual
        int elegida = viewModel.getUltimaElegida();
        if (elegida >= 0 && elegida < 4) {
            pintarResultado(elegida, p.getCorrecta(), elegida == p.getCorrecta());
        }
    }

    private void responder(int opcion) {
        Pregunta p = viewModel.getPreguntaActual().getValue();
        if (p == null || viewModel.getUltimaElegida() >= 0) return;

        boolean acierto = viewModel.responder(opcion);
        pintarResultado(opcion, p.getCorrecta(), acierto);
    }

    private void pintarResultado(int opcion, int correcta, boolean acierto) {
        for (Button b : botones) b.setEnabled(false);
        for (int i = 0; i < 4; i++) {
            if (i != correcta && i != opcion) botones[i].setAlpha(0.35f);
        }
        botones[correcta].setText(botones[correcta].getText() + "   ✓");
        if (!acierto) {
            botones[opcion].setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.cira_rojo)));
            botones[opcion].setText(botones[opcion].getText() + "   ✗");
        }
    }

    // Al terminar: abrir la pantalla de resultado con el puntaje y las respuestas
    private void terminarQuiz() {
        Intent intent = new Intent(this, ResultadoActivity.class);
        intent.putExtra(ResultadoActivity.EXTRA_ACIERTOS, viewModel.getAciertos());
        intent.putExtra(ResultadoActivity.EXTRA_TOTAL, viewModel.getTotal());
        intent.putExtra(ResultadoActivity.EXTRA_RESPUESTAS, viewModel.getRespuestas());
        startActivity(intent);
        finish();
    }
}