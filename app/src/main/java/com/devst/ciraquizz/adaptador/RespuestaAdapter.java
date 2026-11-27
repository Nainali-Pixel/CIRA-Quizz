package com.devst.ciraquizz.adaptador;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.devst.ciraquizz.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Dibuja el detalle de una partida: cada pregunta con ✓ o ✗ (item_respuesta.xml).
// Cada respuesta es el mapa que guarda QuizViewModel: pregunta, respondio, correcta, acierto.
public class RespuestaAdapter extends RecyclerView.Adapter<RespuestaAdapter.Fila> {

    private final List<Map<String, Object>> lista = new ArrayList<>();

    public void actualizar(List<Map<String, Object>> nueva) {
        lista.clear();
        if (nueva != null) lista.addAll(nueva);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fila onCreateViewHolder(@NonNull ViewGroup padre, int tipo) {
        View vista = LayoutInflater.from(padre.getContext())
                .inflate(R.layout.item_respuesta, padre, false);
        return new Fila(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull Fila fila, int posicion) {
        Map<String, Object> r = lista.get(posicion);
        boolean acierto = Boolean.TRUE.equals(r.get("acierto"));

        fila.txtPregunta.setText((posicion + 1) + ". " + texto(r.get("pregunta")));

        int colorIcono = ContextCompat.getColor(fila.itemView.getContext(),
                acierto ? R.color.cira_verde : R.color.cira_rojo);
        fila.imgResultado.setImageResource(acierto ? R.drawable.ic_check : R.drawable.ic_cerrar);
        fila.imgResultado.setImageTintList(ColorStateList.valueOf(colorIcono));

        if (acierto) {
            fila.txtDetalle.setText("Respondió: " + texto(r.get("respondio")));
            fila.txtDetalle.setTextColor(ContextCompat.getColor(fila.itemView.getContext(),
                    R.color.cira_texto_suave));
            fila.itemView.setBackgroundResource(R.drawable.bg_redondo);
        } else {
            // Si falló, mostramos también cuál era la correcta y resaltamos la tarjeta en rojo
            fila.txtDetalle.setText("Respondió: " + texto(r.get("respondio"))
                    + " · Correcta: " + texto(r.get("correcta")));
            fila.txtDetalle.setTextColor(colorIcono);
            fila.itemView.setBackgroundResource(R.drawable.bg_respuesta_mala);
        }
    }

    // Evita mostrar "null" si algún campo no viene en el documento
    private String texto(Object valor) {
        return valor == null ? "-" : String.valueOf(valor);
    }

    @Override
    public int getItemCount() { return lista.size(); }

    static class Fila extends RecyclerView.ViewHolder {
        final ImageView imgResultado;
        final TextView txtPregunta, txtDetalle;

        Fila(@NonNull View vista) {
            super(vista);
            imgResultado = vista.findViewById(R.id.imgResultado);
            txtPregunta  = vista.findViewById(R.id.txtPregunta);
            txtDetalle   = vista.findViewById(R.id.txtDetalle);
        }
    }
}