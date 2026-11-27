package com.devst.ciraquizz;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devst.ciraquizz.adaptador.RespuestaAdapter;
import com.devst.ciraquizz.model.Puntaje;
import com.devst.ciraquizz.util.AdminGuard;
import com.devst.ciraquizz.util.FechaUtil;
import com.devst.ciraquizz.viewmodel.AdminViewModel;

// Pantalla de admin - Respuestas de un participante (ej: "Respuestas de Ana").
// La abre ParticipantesActivity y recibe el ID del documento de "puntajes".
// Los datos los leo de Firestore con ese ID, así no paso listas pesadas por el Intent.
public class RespuestasParticipanteActivity extends AppCompatActivity {

    public static final String EXTRA_ID_PARTICIPACION = "id_participacion";

    private AdminViewModel viewModel;
    private ProgressBar cargando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respuestas_participante);
        if (!AdminGuard.validar(this)) return;

        findViewById(R.id.btnAtras).setOnClickListener(v -> finish());
        cargando = findViewById(R.id.cargando);
        TextView titulo = findViewById(R.id.txtTituloAdmin);
        TextView txtFecha = findViewById(R.id.txtFecha);
        TextView txtPuntaje = findViewById(R.id.txtPuntaje);

        String id = getIntent().getStringExtra(EXTRA_ID_PARTICIPACION);
        if (id == null) { // alguien abrió la pantalla sin decir de qué participante
            Toast.makeText(this, "Falta el participante", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        RespuestaAdapter adapter = new RespuestaAdapter();
        RecyclerView lista = findViewById(R.id.listaRespuestas);
        lista.setLayoutManager(new LinearLayoutManager(this));
        lista.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        viewModel.getMensaje().observe(this, texto ->
                Toast.makeText(this, texto, Toast.LENGTH_SHORT).show());
        viewModel.getPuntaje().observe(this, (Puntaje p) -> {
            cargando.setVisibility(View.GONE);
            if (p == null) return; // el mensaje de error ya se mostró en el Toast

            String nombre = (p.getNombre() == null || p.getNombre().trim().isEmpty())
                    ? "Anónimo" : p.getNombre().trim();
            titulo.setText("Respuestas de " + nombre);
            txtFecha.setText(FechaUtil.formatear(p.getFecha()));
            txtPuntaje.setText("Puntaje: " + p.getPuntaje() + " / " + (p.getTotal() > 0 ? p.getTotal() : 10));
            adapter.actualizar(p.getRespuestas());
        });

        viewModel.cargarPuntaje(id);
    }
}