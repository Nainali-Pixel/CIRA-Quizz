package com.devst.ciraquizz.adaptador;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.devst.ciraquizz.R;
import com.devst.ciraquizz.model.Comentario;
import com.devst.ciraquizz.util.Constantes;
import com.devst.ciraquizz.util.FechaUtil;

import java.util.ArrayList;
import java.util.List;

// Dibuja la lista de "Comentarios de usuarios" (item_comentario.xml)
public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.Fila> {

    // La Activity decide qué hacer cuando se toca el bote de basura
    public interface AlEliminar { void eliminar(Comentario comentario); }

    private final List<Comentario> lista = new ArrayList<>();
    private final AlEliminar alEliminar;

    public ComentarioAdapter(AlEliminar alEliminar) { this.alEliminar = alEliminar; }

    public void actualizar(List<Comentario> nueva) {
        lista.clear();
        lista.addAll(nueva);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fila onCreateViewHolder(@NonNull ViewGroup padre, int tipo) {
        View vista = LayoutInflater.from(padre.getContext())
                .inflate(R.layout.item_comentario, padre, false);
        return new Fila(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull Fila fila, int posicion) {
        Comentario c = lista.get(posicion);
        Context ctx = fila.itemView.getContext();

        boolean esAnonimo = c.getNombre() == null || c.getNombre().trim().isEmpty();
        fila.txtNombre.setText(esAnonimo ? "Anónimo" : c.getNombre());
        fila.txtFecha.setText(FechaUtil.formatear(c.getFecha()));
        fila.txtTexto.setText(c.getTexto());

        // Etiqueta de origen: verde si viene del quiz, azul si viene del menú
        boolean delQuiz = Constantes.ORIGEN_QUIZ.equals(c.getOrigen());
        fila.txtOrigen.setText(delQuiz ? "Desde el quiz" : "Desde el menú");
        fila.txtOrigen.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ctx,
                delQuiz ? R.color.cira_verde_claro : R.color.cira_azul_claro)));
        fila.txtOrigen.setTextColor(ContextCompat.getColor(ctx,
                delQuiz ? R.color.cira_verde_oscuro : R.color.cira_azul));

        fila.btnEliminar.setOnClickListener(v -> alEliminar.eliminar(c));
    }

    @Override
    public int getItemCount() { return lista.size(); }

    static class Fila extends RecyclerView.ViewHolder {
        final TextView txtNombre, txtOrigen, txtFecha, txtTexto;
        final View btnEliminar;

        Fila(@NonNull View vista) {
            super(vista);
            txtNombre   = vista.findViewById(R.id.txtNombre);
            txtOrigen   = vista.findViewById(R.id.txtOrigen);
            txtFecha    = vista.findViewById(R.id.txtFecha);
            txtTexto    = vista.findViewById(R.id.txtTexto);
            btnEliminar = vista.findViewById(R.id.btnEliminar);
        }
    }
}