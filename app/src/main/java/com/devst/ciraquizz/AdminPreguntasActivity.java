package com.devst.ciraquizz;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devst.ciraquizz.adaptador.PreguntaAdminAdapter;
import com.devst.ciraquizz.model.Pregunta;
import com.devst.ciraquizz.util.AdminGuard;
import com.devst.ciraquizz.viewmodel.AdminViewModel;

import java.util.ArrayList;

// Pantalla de admin - Administrar preguntas: busca y elimina preguntas del banco ("preguntas").
// Se abre desde el panel de administración (otro compañero):
//     startActivity(new Intent(this, AdminPreguntasActivity.class));
// PENDIENTE (compañero del panel): poner ahí el botón que abre AgregarPreguntaActivity.
public class AdminPreguntasActivity extends AppCompatActivity {

    private AdminViewModel viewModel;
    private PreguntaAdminAdapter adapter;
    private ProgressBar cargando;
    private TextView txtVacio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_preguntas);
        if (!AdminGuard.validar(this)) return;

        ((TextView) findViewById(R.id.txtTituloAdmin)).setText(R.string.titulo_admin_preguntas);
        findViewById(R.id.btnAtras).setOnClickListener(v -> finish());
        cargando = findViewById(R.id.cargando);
        txtVacio = findViewById(R.id.txtVacio);

        adapter = new PreguntaAdminAdapter(this::mostrarDialogoEliminar);
        RecyclerView lista = findViewById(R.id.listaPreguntas);
        lista.setLayoutManager(new LinearLayoutManager(this));
        lista.setAdapter(adapter);

        // Buscador: filtra la lista mientras se escribe
        EditText etBuscar = findViewById(R.id.etBuscar);
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                adapter.filtrar(s.toString());
                actualizarVacio();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        viewModel.getMensaje().observe(this, texto ->
                Toast.makeText(this, texto, Toast.LENGTH_SHORT).show());
        viewModel.getPreguntas().observe(this, preguntas -> {
            cargando.setVisibility(View.GONE);
            adapter.actualizar(preguntas == null ? new ArrayList<>() : preguntas);
            // Si había algo escrito en el buscador, lo vuelvo a aplicar a la lista nueva
            adapter.filtrar(etBuscar.getText().toString());
            actualizarVacio();
        });
    }

    // Recargo al volver a esta pantalla, así aparece la pregunta recién creada en AgregarPreguntaActivity
    @Override
    protected void onResume() {
        super.onResume();
        if (viewModel != null) viewModel.cargarPreguntas();
    }

    private void actualizarVacio() {
        txtVacio.setVisibility(adapter.estaVacia() && cargando.getVisibility() == View.GONE
                ? View.VISIBLE : View.GONE);
    }

    // Diálogo "¿Eliminar pregunta?" del mockup (dialog_eliminar_pregunta.xml)
    private void mostrarDialogoEliminar(Pregunta pregunta) {
        View vista = getLayoutInflater().inflate(R.layout.dialog_eliminar_pregunta, null);
        ((TextView) vista.findViewById(R.id.txtPreguntaEliminar))
                .setText(PreguntaAdminAdapter.textoVisible(pregunta));

        AlertDialog dialogo = new AlertDialog.Builder(this).setView(vista).create();
        // Fondo transparente para que se vea solo mi tarjeta redondeada
        if (dialogo.getWindow() != null) {
            dialogo.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        vista.findViewById(R.id.btnCancelar).setOnClickListener(v -> dialogo.dismiss());
        vista.findViewById(R.id.btnConfirmarEliminar).setOnClickListener(v -> {
            viewModel.eliminarPregunta(pregunta.getId()); // borra en Firestore y recarga la lista
            dialogo.dismiss();
        });
        dialogo.show();
    }
}