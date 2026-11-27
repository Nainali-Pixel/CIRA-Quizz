package com.devst.ciraquizz.repositorio;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.devst.ciraquizz.model.Puntaje;
import com.devst.ciraquizz.util.Constantes;
import com.devst.ciraquizz.util.FechaUtil;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Se comunica con Firestore para guardar y leer puntajes
public class PuntajeRepositorio {

    private static final String TAG = "PuntajeRepositorio";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // CREATE: guarda un puntaje nuevo (Firestore genera el ID con add)
    public void guardarPuntaje(Puntaje puntaje,
                               MutableLiveData<Boolean> guardado,
                               MutableLiveData<String> mensaje) {
        db.collection(Constantes.COL_PUNTAJES)
                .add(puntaje)
                .addOnSuccessListener(ref -> {
                    Log.d(TAG, "Puntaje guardado con ID: " + ref.getId());
                    mensaje.postValue("Puntaje guardado en el historial");
                    guardado.postValue(true);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error al guardar puntaje", e);
                    mensaje.postValue("Error al guardar: " + e.getMessage());
                    guardado.postValue(false);
                });
    }

    // READ: los mejores puntajes, de mayor a menor
    public void obtenerMejores(int cantidad,
                               MutableLiveData<List<Puntaje>> resultado,
                               MutableLiveData<String> mensaje) {
        db.collection(Constantes.COL_PUNTAJES)
                .orderBy("puntaje", Query.Direction.DESCENDING)
                .limit(cantidad)
                .get()
                .addOnSuccessListener(consulta -> {
                    List<Puntaje> lista = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : consulta) {
                        lista.add(doc.toObject(Puntaje.class));
                    }
                    resultado.postValue(lista);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error al leer puntajes", e);
                    mensaje.postValue("Error al cargar puntajes: " + e.getMessage());
                });
    }

    // READ: todos los puntajes ordenados por fecha (más recientes primero)
    public void obtenerHistorial(MutableLiveData<List<Puntaje>> resultado,
                                 MutableLiveData<String> mensaje) {
        db.collection(Constantes.COL_PUNTAJES)
                .get()
                .addOnSuccessListener(consulta -> {
                    List<Puntaje> lista = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : consulta) {
                        try {
                            lista.add(doc.toObject(Puntaje.class));
                        } catch (Exception e) {
                            Log.w(TAG, "Documento de puntaje inválido: " + doc.getId(), e);
                        }
                    }
                    Collections.sort(lista, (a, b) ->
                            Long.compare(FechaUtil.milisegundos(b.getFecha()),
                                    FechaUtil.milisegundos(a.getFecha())));
                    resultado.postValue(lista);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error al leer historial", e);
                    mensaje.postValue("Error al cargar historial: " + e.getMessage());
                    resultado.postValue(new ArrayList<>());
                });
    }
}