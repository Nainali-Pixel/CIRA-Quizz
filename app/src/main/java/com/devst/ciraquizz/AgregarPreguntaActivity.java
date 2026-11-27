package com.devst.ciraquizz;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.ImageView.ScaleType;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.devst.ciraquizz.model.Pregunta;
import com.devst.ciraquizz.util.AdminGuard;
import com.devst.ciraquizz.util.Constantes;
import com.devst.ciraquizz.util.ImagenUtil;
import com.devst.ciraquizz.viewmodel.AdminViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Pantalla de admin - Agregar pregunta: crea un documento nuevo en la colección "preguntas".
// Se abre desde el panel de administración (otro compañero):
//     startActivity(new Intent(this, AgregarPreguntaActivity.class));
//
// Cómo queda guardada en Firestore (mismos campos que model/Pregunta.java):
//   texto, opciones (4), correcta (0-3), tipo (IMAGEN / TEORICA), creadoPor (uid del admin),
//   imagenBase64 (foto del residuo, solo si lleva imagen).
// El quiz ya las lee solo, no hay que tocar nada más (QuizActivity muestra la foto si existe).
public class AgregarPreguntaActivity extends AppCompatActivity {

    private AdminViewModel viewModel;
    private EditText etTexto;
    private final EditText[] opciones = new EditText[4];
    private final RadioButton[] radios = new RadioButton[4];
    private TextView chipConImagen, chipSoloTexto;
    private View panelImagen;
    private ImageView imgPrevia;
    private Button btnGuardar;

    private int correcta = 0;           // opción marcada como correcta (la A viene marcada)
    private boolean llevaImagen = true; // el mockup abre con "Sí, lleva imagen"
    private Uri imagenUri = null;       // foto elegida de la galería

    // Selector de galería: devuelve la Uri de la imagen elegida (o null si se cancela)
    private final ActivityResultLauncher<String> selectorImagen =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri == null) return;
                imagenUri = uri;
                // Muestro la foto elegida en lugar del ícono
                imgPrevia.setPadding(0, 0, 0, 0);
                imgPrevia.setImageTintList(null);
                imgPrevia.setScaleType(ScaleType.CENTER_CROP);
                imgPrevia.setClipToOutline(true); // respeta las esquinas redondeadas
                imgPrevia.setImageURI(uri);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_pregunta);
        if (!AdminGuard.validar(this)) return;

        ((TextView) findViewById(R.id.txtTituloAdmin)).setText(R.string.titulo_agregar_pregunta);
        findViewById(R.id.btnAtras).setOnClickListener(v -> finish());

        etTexto       = findViewById(R.id.etTextoPregunta);
        opciones[0]   = findViewById(R.id.etOpcionA);
        opciones[1]   = findViewById(R.id.etOpcionB);
        opciones[2]   = findViewById(R.id.etOpcionC);
        opciones[3]   = findViewById(R.id.etOpcionD);
        radios[0]     = findViewById(R.id.rbOpcionA);
        radios[1]     = findViewById(R.id.rbOpcionB);
        radios[2]     = findViewById(R.id.rbOpcionC);
        radios[3]     = findViewById(R.id.rbOpcionD);
        chipConImagen = findViewById(R.id.chipConImagen);
        chipSoloTexto = findViewById(R.id.chipSoloTexto);
        panelImagen   = findViewById(R.id.panelImagen);
        imgPrevia     = findViewById(R.id.imgPrevia);
        btnGuardar    = findViewById(R.id.btnGuardar);

        // Los 4 radios están en filas separadas, así que los controlo a mano (solo uno marcado a la vez)
        for (int i = 0; i < 4; i++) {
            final int posicion = i;
            radios[i].setOnClickListener(v -> marcarCorrecta(posicion));
        }
        marcarCorrecta(0);

        chipConImagen.setOnClickListener(v -> elegirTipo(true));
        chipSoloTexto.setOnClickListener(v -> elegirTipo(false));
        elegirTipo(true);

        findViewById(R.id.btnElegirImagen).setOnClickListener(v -> selectorImagen.launch("image/*"));
        btnGuardar.setOnClickListener(v -> guardar());

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        viewModel.getMensaje().observe(this, texto -> {
            Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
            btnGuardar.setEnabled(true); // si hubo error, puede volver a intentar
        });
        viewModel.getGuardado().observe(this, ok -> {
            if (Boolean.TRUE.equals(ok)) {
                Toast.makeText(this, "Pregunta guardada", Toast.LENGTH_SHORT).show();
                finish(); // vuelve a la pantalla anterior
            }
        });
    }

    private void marcarCorrecta(int posicion) {
        correcta = posicion;
        for (int i = 0; i < 4; i++) radios[i].setChecked(i == posicion);
    }

    // Alterna entre "Sí, lleva imagen" y "No, solo texto"
    private void elegirTipo(boolean conImagen) {
        llevaImagen = conImagen;
        chipConImagen.setSelected(conImagen);
        chipSoloTexto.setSelected(!conImagen);
        panelImagen.setVisibility(conImagen ? View.VISIBLE : View.GONE);
    }

    private void guardar() {
        String texto = etTexto.getText().toString().trim();
        if (texto.isEmpty()) {
            etTexto.setError("Escribe la pregunta");
            return;
        }

        // Las 4 opciones son obligatorias porque el quiz siempre muestra A, B, C y D
        List<String> listaOpciones = new ArrayList<>();
        for (EditText et : opciones) {
            String opcion = et.getText().toString().trim();
            if (opcion.isEmpty()) {
                et.setError("Escribe la opción");
                return;
            }
            listaOpciones.add(opcion);
        }

        // Si dice que lleva imagen, tiene que haber elegido una
        String imagenBase64 = "";
        if (llevaImagen) {
            if (imagenUri == null) {
                Toast.makeText(this, "Elige la imagen del residuo", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                // La foto se reduce primero (máx. 400 px) y se guarda como texto dentro de la pregunta
                imagenBase64 = ImagenUtil.uriABase64(this, imagenUri);
            } catch (IOException e) {
                Toast.makeText(this, "No se pudo leer la imagen", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // creadoPor: el UID del admin con sesión; "admin" si todavía no está el login (modo pruebas)
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        String creadoPor = usuario != null ? usuario.getUid() : "admin";

        // El campo "residuo" lo usa el historial de respuestas. El mockup no tiene un campo para
        // el nombre del residuo, así que uso el mismo texto de la pregunta.
        Pregunta pregunta = new Pregunta(texto, listaOpciones, correcta,
                llevaImagen ? Constantes.TIPO_IMAGEN : Constantes.TIPO_TEORICA,
                "", texto, creadoPor);
        pregunta.setImagenBase64(imagenBase64);

        btnGuardar.setEnabled(false); // evita guardarla dos veces si toca el botón rápido
        viewModel.guardarPregunta(pregunta);
    }
}