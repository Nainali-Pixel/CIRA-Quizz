package com.devst.ciraquizz;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devst.ciraquizz.adaptador.ComentarioAdapter;
import com.devst.ciraquizz.model.Comentario;
import com.devst.ciraquizz.util.AdminGuard;
import com.devst.ciraquizz.util.Constantes;
import com.devst.ciraquizz.viewmodel.AdminViewModel;

import java.util.ArrayList;
import java.util.List;

// Pantalla de admin - Comentarios de usuarios (colección "comentarios" de Firestore).
// Se abre desde el panel de administración (lo hace otro compañero):
//     startActivity(new Intent(this, ComentariosActivity.class));
public class ComentariosActivity extends AppCompatActivity {

    private AdminViewModel viewModel;
    private ComentarioAdapter adapter;
    private TextView chipTodos, chipQuiz, chipMenu, txtVacio;
    private ProgressBar cargando;

    private List<Comentario> todos = new ArrayList<>(); // lista completa que llegó de Firestore
    private String filtro = null;                       // null = Todos, o ORIGEN_QUIZ / ORIGEN_MENU

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);
        if (!AdminGuard.validar(this)) return; // si no es admin, se cierra

        ((TextView) findViewById(R.id.txtTituloAdmin)).setText(R.string.titulo_comentarios);
        findViewById(R.id.btnAtras).setOnClickListener(v -> finish());

        chipTodos = findViewById(R.id.chipTodos);
        chipQuiz  = findViewById(R.id.chipQuiz);
        chipMenu  = findViewById(R.id.chipMenu);
        txtVacio  = findViewById(R.id.txtVacio);
        cargando  = findViewById(R.id.cargando);

        chipTodos.setOnClickListener(v -> seleccionarFiltro(null));
        chipQuiz.setOnClickListener(v -> seleccionarFiltro(Constantes.ORIGEN_QUIZ));
        chipMenu.setOnClickListener(v -> seleccionarFiltro(Constantes.ORIGEN_MENU));

        adapter = new ComentarioAdapter(this::confirmarEliminar);
        RecyclerView lista = findViewById(R.id.listaComentarios);
        lista.setLayoutManager(new LinearLayoutManager(this));
        lista.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        // Observadores (MVVM): la pantalla reacciona cuando cambian los datos
        viewModel.getMensaje().observe(this, texto ->
                Toast.makeText(this, texto, Toast.LENGTH_SHORT).show());
        viewModel.getComentarios().observe(this, lista2 -> {
            cargando.setVisibility(View.GONE);
            todos = lista2 == null ? new ArrayList<>() : lista2;
            mostrarSegunFiltro();
        });

        seleccionarFiltro(null); // marca "Todos" al abrir
        viewModel.cargarComentarios();
    }

    // Marca el chip tocado y vuelve a pintar la lista
    private void seleccionarFiltro(String nuevoFiltro) {
        filtro = nuevoFiltro;
        chipTodos.setSelected(filtro == null);
        chipQuiz.setSelected(Constantes.ORIGEN_QUIZ.equals(filtro));
        chipMenu.setSelected(Constantes.ORIGEN_MENU.equals(filtro));
        mostrarSegunFiltro();
    }

    // El filtro lo hago aquí en la lista que ya descargué, sin volver a pedir nada a Firestore
    private void mostrarSegunFiltro() {
        List<Comentario> visibles = new ArrayList<>();
        for (Comentario c : todos) {
            if (filtro == null || filtro.equals(c.getOrigen())) visibles.add(c);
        }
        adapter.actualizar(visibles);
        txtVacio.setVisibility(visibles.isEmpty() && cargando.getVisibility() == View.GONE
                ? View.VISIBLE : View.GONE);
    }

    private void confirmarEliminar(Comentario c) {
        new AlertDialog.Builder(this)
                .setTitle("¿Eliminar comentario?")
                .setMessage("Esta acción no se puede deshacer.")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar", (d, w) -> viewModel.eliminarComentario(c.getId()))
                .show();
    }
}