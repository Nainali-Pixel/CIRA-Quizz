package com.devst.ciraquizz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devst.ciraquizz.adaptador.ParticipanteAdapter;
import com.devst.ciraquizz.util.AdminGuard;
import com.devst.ciraquizz.viewmodel.AdminViewModel;

import java.util.ArrayList;

// Pantalla de admin - Respuestas de participantes: lista de partidas guardadas en "puntajes".
// Al tocar una abre RespuestasParticipanteActivity con el detalle.
// Se abre desde el panel de administración (otro compañero):
//     startActivity(new Intent(this, ParticipantesActivity.class));
public class ParticipantesActivity extends AppCompatActivity {

    private AdminViewModel viewModel;
    private ProgressBar cargando;
    private TextView txtVacio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participantes);
        if (!AdminGuard.validar(this)) return;

        ((TextView) findViewById(R.id.txtTituloAdmin)).setText(R.string.titulo_participantes);
        findViewById(R.id.btnAtras).setOnClickListener(v -> finish());
        cargando = findViewById(R.id.cargando);
        txtVacio = findViewById(R.id.txtVacio);

        ParticipanteAdapter adapter = new ParticipanteAdapter(p -> {
            Intent intent = new Intent(this, RespuestasParticipanteActivity.class);
            intent.putExtra(RespuestasParticipanteActivity.EXTRA_ID_PARTICIPACION, p.getId());
            startActivity(intent);
        });
        RecyclerView lista = findViewById(R.id.listaParticipantes);
        lista.setLayoutManager(new LinearLayoutManager(this));
        lista.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        viewModel.getMensaje().observe(this, texto ->
                Toast.makeText(this, texto, Toast.LENGTH_SHORT).show());
        viewModel.getPuntajes().observe(this, participaciones -> {
            cargando.setVisibility(View.GONE);
            adapter.actualizar(participaciones == null ? new ArrayList<>() : participaciones);
            txtVacio.setVisibility(participaciones == null || participaciones.isEmpty()
                    ? View.VISIBLE : View.GONE);
        });

        viewModel.cargarPuntajes();
    }
}