package com.devst.ciraquizz.adaptador;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.devst.ciraquizz.R;
import com.devst.ciraquizz.model.Pregunta;
import com.devst.ciraquizz.util.Constantes;

import java.util.ArrayList;
import java.util.List;

// Dibuja la lista de "Administrar preguntas" (item_pregunta_admin.xml) y filtra por el buscador
public class PreguntaAdminAdapter extends RecyclerView.Adapter<PreguntaAdminAdapter.Fila> {

    // La Activity decide qué hacer cuando se toca el bote de basura (abre el diálogo de confirmación)
    public interface AlEliminar { void eliminar(Pregunta pregunta); }

    private final List<Pregunta> todas = new ArrayList<>();     // lista completa del banco
    private final List<Pregunta> visibles = new ArrayList<>();  // lo que queda después de buscar
    private final AlEliminar alEliminar;
    private String busqueda = "";

    public PreguntaAdminAdapter(AlEliminar alEliminar) { this.alEliminar = alEliminar; }

    public void actualizar(List<Pregunta> nueva) {
        todas.clear();
        todas.addAll(nueva);
        aplicarBusqueda();
    }

    public void filtrar(String texto) {
        busqueda = texto == null ? "" : texto.trim().toLowerCase();
        aplicarBusqueda();
    }

    private void aplicarBusqueda() {
        visibles.clear();
        for (Pregunta p : todas) {
            if (busqueda.isEmpty() || textoVisible(p).toLowerCase().contains(busqueda)) {
                visibles.add(p);
            }
        }
        notifyDataSetChanged();
    }

    // Las preguntas con imagen del banco inicial dicen siempre "¿En qué contenedor va este residuo?",
    // así que agrego el nombre del residuo entre paréntesis para que el admin las distinga.
    // También lo usa el diálogo de eliminar.
    public static String textoVisible(Pregunta p) {
        String texto = p.getTexto() == null ? "" : p.getTexto();
        String residuo = p.getResiduo();
        if (Constantes.TIPO_IMAGEN.equals(p.getTipo()) && residuo != null
                && !residuo.isEmpty() && !texto.contains(residuo)) {
            return texto + " (" + residuo + ")";
        }
        return texto;
    }

    public boolean estaVacia() { return visibles.isEmpty(); }

    @NonNull
    @Override
    public Fila onCreateViewHolder(@NonNull ViewGroup padre, int tipo) {
        View vista = LayoutInflater.from(padre.getContext())
                .inflate(R.layout.item_pregunta_admin, padre, false);
        return new Fila(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull Fila fila, int posicion) {
        Pregunta p = visibles.get(posicion);
        boolean conImagen = Constantes.TIPO_IMAGEN.equals(p.getTipo());

        fila.txtTexto.setText(textoVisible(p));
        fila.txtTipo.setText(conImagen ? "Con imagen" : "Teórica");
        fila.txtTipo.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(
                fila.itemView.getContext(),
                conImagen ? R.color.cira_azul_claro : R.color.cira_verde_claro)));
        fila.txtTipo.setTextColor(ContextCompat.getColor(fila.itemView.getContext(),
                conImagen ? R.color.cira_azul : R.color.cira_verde_oscuro));

        fila.btnEliminar.setOnClickListener(v -> alEliminar.eliminar(p));
    }

    @Override
    public int getItemCount() { return visibles.size(); }

    static class Fila extends RecyclerView.ViewHolder {
        final TextView txtTipo, txtTexto;
        final View btnEliminar;

        Fila(@NonNull View vista) {
            super(vista);
            txtTipo     = vista.findViewById(R.id.txtTipo);
            txtTexto    = vista.findViewById(R.id.txtTexto);
            btnEliminar = vista.findViewById(R.id.btnEliminar);
        }
    }
}