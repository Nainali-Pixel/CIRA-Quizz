package com.devst.ciraquizz.adaptador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devst.ciraquizz.R;
import com.devst.ciraquizz.model.Puntaje;
import com.devst.ciraquizz.util.FechaUtil;

import java.util.ArrayList;
import java.util.List;

// Dibuja la lista de "Respuestas de participantes" (item_participante.xml)
public class ParticipanteAdapter extends RecyclerView.Adapter<ParticipanteAdapter.Fila> {

    // La Activity decide a dónde ir cuando se toca un participante
    public interface AlTocar { void tocar(Puntaje puntaje); }

    private final List<Puntaje> lista = new ArrayList<>();
    private final AlTocar alTocar;

    public ParticipanteAdapter(AlTocar alTocar) { this.alTocar = alTocar; }

    public void actualizar(List<Puntaje> nueva) {
        lista.clear();
        lista.addAll(nueva);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fila onCreateViewHolder(@NonNull ViewGroup padre, int tipo) {
        View vista = LayoutInflater.from(padre.getContext())
                .inflate(R.layout.item_participante, padre, false);
        return new Fila(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull Fila fila, int posicion) {
        Puntaje p = lista.get(posicion);

        String nombre = (p.getNombre() == null || p.getNombre().trim().isEmpty())
                ? "Anónimo" : p.getNombre().trim();
        fila.txtInicial.setText(nombre.substring(0, 1).toUpperCase()); // círculo con la inicial
        fila.txtNombre.setText(nombre);
        fila.txtFecha.setText(FechaUtil.formatear(p.getFecha()));
        fila.txtPuntaje.setText(p.getPuntaje() + " / " + totalDe(p));

        fila.itemView.setOnClickListener(v -> alTocar.tocar(p));
    }

    // Si el puntaje no trae "total", asumo 10 preguntas por partida
    private int totalDe(Puntaje p) { return p.getTotal() > 0 ? p.getTotal() : 10; }

    @Override
    public int getItemCount() { return lista.size(); }

    static class Fila extends RecyclerView.ViewHolder {
        final TextView txtInicial, txtNombre, txtFecha, txtPuntaje;

        Fila(@NonNull View vista) {
            super(vista);
            txtInicial = vista.findViewById(R.id.txtInicial);
            txtNombre  = vista.findViewById(R.id.txtNombre);
            txtFecha   = vista.findViewById(R.id.txtFecha);
            txtPuntaje = vista.findViewById(R.id.txtPuntaje);
        }
    }
}