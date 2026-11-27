package com.devst.ciraquizz;

import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.devst.ciraquizz.model.RegistroReciclaje;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegistrosActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String uid;
    private LinearLayout lista;
    private ListenerRegistration listener;
    private static final String COLECCION = "registros_reciclaje";

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            Toast.makeText(this, "Debes iniciar sesión para ver tus registros", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        db = FirebaseFirestore.getInstance();

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(28, 24, 28, 24);
        root.setBackgroundColor(0xFFF1F8F2);
        root.setFitsSystemWindows(true);

        TextView title = new TextView(this);
        title.setText("Mis registros de reciclaje");
        title.setTextSize(23);
        title.setTextColor(0xFF174B3A);
        root.addView(title);

        TextView info = new TextView(this);
        info.setText("Cada cuenta solo puede consultar y administrar sus propios registros.");
        root.addView(info);

        Button nuevo = new Button(this);
        nuevo.setText("＋ Agregar registro");
        root.addView(nuevo);

        ScrollView scroll = new ScrollView(this);
        lista = new LinearLayout(this);
        lista.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(lista);
        root.addView(scroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));

        Button volver = new Button(this);
        volver.setText("Volver");
        root.addView(volver);

        setContentView(root);
        nuevo.setOnClickListener(v -> formulario(null));
        volver.setOnClickListener(v -> finish());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (db == null || uid == null) return;
        listener = db.collection(COLECCION).whereEqualTo("ownerUid", uid)
                .addSnapshotListener((snap, error) -> {
                    if (error != null) {
                        aviso("Error al consultar: " + error.getLocalizedMessage());
                        return;
                    }
                    lista.removeAllViews();
                    if (snap == null || snap.isEmpty()) {
                        TextView vacio = new TextView(this);
                        vacio.setText("Aún no tienes registros. Agrega el primero.");
                        lista.addView(vacio);
                        return;
                    }
                    List<RegistroReciclaje> registros = new ArrayList<>();
                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        RegistroReciclaje r = doc.toObject(RegistroReciclaje.class);
                        if (r != null) {
                            r.setId(doc.getId());
                            registros.add(r);
                        }
                    }
                    Collections.sort(registros, (a, b) -> Long.compare(b.getActualizado(), a.getActualizado()));
                    for (RegistroReciclaje r : registros) {
                        tarjeta(r);
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listener != null) {
            listener.remove();
            listener = null;
        }
    }

    private void tarjeta(RegistroReciclaje r) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(18, 14, 18, 14);
        card.setBackgroundColor(0xFFFFFFFF);

        TextView t = new TextView(this);
        t.setText(r.getNombre() + " · " + r.getCategoria());
        t.setTextSize(18);
        t.setTextColor(0xFF174B3A);

        TextView d = new TextView(this);
        String detalleTxt = (r.getDetalle() == null || r.getDetalle().trim().isEmpty()) ? "—" : r.getDetalle();
        d.setText("Cantidad: " + r.getCantidad() + "\nDetalle: " + detalleTxt);
        card.addView(t);
        card.addView(d);

        LinearLayout acciones = new LinearLayout(this);
        acciones.setOrientation(LinearLayout.HORIZONTAL);
        Button editar = new Button(this);
        editar.setText("Editar");
        Button borrar = new Button(this);
        borrar.setText("Eliminar");
        acciones.addView(editar, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        acciones.addView(borrar, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        card.addView(acciones);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 12, 0, 4);
        lista.addView(card, lp);

        editar.setOnClickListener(v -> formulario(r));
        borrar.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Eliminar registro")
                .setMessage("¿Deseas eliminar “" + r.getNombre() + "”?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar", (a, b) -> eliminar(r))
                .show());
    }

    private void formulario(RegistroReciclaje actual) {
        LinearLayout box = new LinearLayout(this);
        box.setPadding(24, 8, 24, 0);
        box.setOrientation(LinearLayout.VERTICAL);

        EditText nombre = edit("Residuo / material", box);
        Spinner categoria = new Spinner(this);
        String[] cats = {"Cartón", "Aluminio", "Plástico", "Vidrio", "Otros"};
        categoria.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cats));
        box.addView(categoria);

        EditText cantidad = edit("Cantidad (número entero)", box);
        cantidad.setInputType(InputType.TYPE_CLASS_NUMBER);
        EditText detalle = edit("Detalle (opcional)", box);

        if (actual != null) {
            nombre.setText(actual.getNombre());
            cantidad.setText(String.valueOf(actual.getCantidad()));
            detalle.setText(actual.getDetalle());
            for (int i = 0; i < cats.length; i++) {
                if (cats[i].equals(actual.getCategoria())) {
                    categoria.setSelection(i);
                    break;
                }
            }
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(actual == null ? "Nuevo registro" : "Editar registro")
                .setView(box)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Guardar", null)
                .create();

        dialog.setOnShowListener(x -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String n = nombre.getText().toString().trim();
            String q = cantidad.getText().toString().trim();
            if (n.isEmpty()) {
                nombre.setError("Campo obligatorio");
                return;
            }
            long cantVal;
            try {
                cantVal = Long.parseLong(q);
            } catch (NumberFormatException e) {
                cantidad.setError("Ingresa un número entero válido");
                return;
            }
            if (cantVal <= 0) {
                cantidad.setError("Ingresa una cantidad mayor que cero");
                return;
            }
            RegistroReciclaje r = new RegistroReciclaje(
                    n,
                    categoria.getSelectedItem().toString(),
                    cantVal,
                    detalle.getText().toString().trim(),
                    uid,
                    System.currentTimeMillis());
            guardar(r, actual, dialog);
        }));
        dialog.show();
    }

    private EditText edit(String hint, LinearLayout box) {
        EditText e = new EditText(this);
        e.setHint(hint);
        box.addView(e);
        return e;
    }

    private void guardar(RegistroReciclaje r, RegistroReciclaje anterior, AlertDialog dialog) {
        DocumentReference ref;
        if (anterior == null) {
            ref = db.collection(COLECCION).document();
        } else {
            if (anterior.getId() == null || anterior.getId().trim().isEmpty()) {
                aviso("No se pudo identificar el registro a actualizar");
                return;
            }
            ref = db.collection(COLECCION).document(anterior.getId());
        }
        ref.set(r.toMap())
                .addOnSuccessListener(v -> {
                    dialog.dismiss();
                    aviso(anterior == null ? "Registro creado" : "Registro actualizado");
                })
                .addOnFailureListener(e -> aviso("No se pudo guardar: " + e.getLocalizedMessage()));
    }

    private void eliminar(RegistroReciclaje r) {
        if (r.getId() == null || r.getId().trim().isEmpty()) {
            aviso("No se pudo identificar el registro a eliminar");
            return;
        }
        db.collection(COLECCION).document(r.getId()).delete()
                .addOnSuccessListener(v -> aviso("Registro eliminado"))
                .addOnFailureListener(e -> aviso("No se pudo eliminar: " + e.getLocalizedMessage()));
    }

    private void aviso(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }
}
