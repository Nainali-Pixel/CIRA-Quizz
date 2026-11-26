package com.devst.ciraquizz.repositorio;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.devst.ciraquizz.model.Pregunta;
import com.devst.ciraquizz.util.Constantes;
import com.devst.ciraquizz.util.PreguntasEjemplo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

// Se comunica con Firestore para leer las preguntas del quiz
public class PreguntaRepositorio {

    private static final String TAG = "PreguntaRepositorio";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // READ: trae todas las preguntas de la colección "preguntas"
    public void obtenerPreguntas(MutableLiveData<List<Pregunta>> resultado,
                                 MutableLiveData<String> mensaje) {
        db.collection(Constantes.COL_PREGUNTAS)
                .get()
                .addOnSuccessListener(consulta -> {
                    if (consulta.isEmpty()) {
                        // Primera vez: la colección no existe, subimos las de ejemplo
                        cargarPreguntasEjemplo(resultado, mensaje);
                        return;
                    }
                    List<Pregunta> lista = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : consulta) {
                        lista.add(doc.toObject(Pregunta.class));
                    }
                    Log.d(TAG, "Preguntas leídas: " + lista.size());
                    resultado.postValue(lista);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error al leer preguntas", e);
                    mensaje.postValue("Error al cargar preguntas: " + e.getMessage());
                    resultado.postValue(new ArrayList<>());
                });
    }

    // CREATE: sube las preguntas de ejemplo todas juntas (WriteBatch)
    private void cargarPreguntasEjemplo(MutableLiveData<List<Pregunta>> resultado,
                                        MutableLiveData<String> mensaje) {
        WriteBatch lote = db.batch();
        for (Pregunta p : PreguntasEjemplo.obtener()) {
            lote.set(db.collection(Constantes.COL_PREGUNTAS).document(), p);
        }
        lote.commit()
                .addOnSuccessListener(v -> {
                    Log.d(TAG, "Preguntas de ejemplo guardadas");
                    mensaje.postValue("Se cargaron las preguntas iniciales");
                    obtenerPreguntas(resultado, mensaje); // ahora sí las leemos
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error al guardar ejemplos", e);
                    mensaje.postValue("Error al crear preguntas: " + e.getMessage());
                    resultado.postValue(new ArrayList<>());
                });
    }
}