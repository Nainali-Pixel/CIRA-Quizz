package com.devst.ciraquizz.repositorio;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.devst.ciraquizz.model.Comentario;
import com.devst.ciraquizz.model.Pregunta;
import com.devst.ciraquizz.model.Puntaje;
import com.devst.ciraquizz.util.Constantes;
import com.devst.ciraquizz.util.FechaUtil;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Todo lo que usan las pantallas de administración contra Firestore:
// leer/borrar comentarios, leer partidas de participantes, y leer/crear/borrar preguntas.
public class AdminRepositorio {

    private static final String TAG = "AdminRepositorio";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // ---------------------------------------------------------------- COMENTARIOS

    // READ: colección "comentarios", los más nuevos primero
    public void obtenerComentarios(MutableLiveData<List<Comentario>> resultado,
                                   MutableLiveData<String> mensaje) {
        db.collection(Constantes.COL_COMENTARIOS)
                .get()
                .addOnSuccessListener(consulta -> {
                    List<Comentario> lista = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : consulta) {
                        try {
                            lista.add(doc.toObject(Comentario.class));
                        } catch (Exception e) {
                            Log.w(TAG, "Comentario inválido: " + doc.getId(), e);
                        }
                    }
                    Collections.sort(lista, (a, b) ->
                            Long.compare(FechaUtil.milisegundos(b.getFecha()),
                                    FechaUtil.milisegundos(a.getFecha())));
                    resultado.postValue(lista);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error al leer comentarios", e);
                    mensaje.postValue("Error al cargar comentarios: " + e.getMessage());
                    resultado.postValue(new ArrayList<>());
                });
    }

    // DELETE: borra un comentario y vuelve a leer la lista
    public void eliminarComentario(String id, MutableLiveData<List<Comentario>> resultado,
                                   MutableLiveData<String> mensaje) {
        if (id == null || id.trim().isEmpty()) {
            mensaje.postValue("No se pudo identificar el comentario");
            return;
        }
        db.collection(Constantes.COL_COMENTARIOS).document(id)
                .delete()
                .addOnSuccessListener(v -> {
                    mensaje.postValue("Comentario eliminado");
                    obtenerComentarios(resultado, mensaje);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error al eliminar comentario", e);
                    mensaje.postValue("No se pudo eliminar: " + e.getMessage());
                });
    }

    // ---------------------------------------------------------------- PARTICIPANTES

    // READ: todas las partidas guardadas en "puntajes", las más nuevas primero
    public void obtenerPuntajes(MutableLiveData<List<Puntaje>> resultado,
                                MutableLiveData<String> mensaje) {
        db.collection(Constantes.COL_PUNTAJES)
                .get()
                .addOnSuccessListener(consulta -> {
                    List<Puntaje> lista = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : consulta) {
                        try {
                            lista.add(doc.toObject(Puntaje.class));
                        } catch (Exception e) {
                            Log.w(TAG, "Puntaje inválido: " + doc.getId(), e);
                        }
                    }
                    Collections.sort(lista, (a, b) ->
                            Long.compare(FechaUtil.milisegundos(b.getFecha()),
                                    FechaUtil.milisegundos(a.getFecha())));
                    resultado.postValue(lista);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error al leer participantes", e);
                    mensaje.postValue("Error al cargar participantes: " + e.getMessage());
                    resultado.postValue(new ArrayList<>());
                });
    }

    // READ: una sola partida (con su detalle de respuestas)
    public void obtenerPuntaje(String id, MutableLiveData<Puntaje> resultado,
                               MutableLiveData<String> mensaje) {
        if (id == null || id.trim().isEmpty()) {
            mensaje.postValue("Falta el identificador del participante");
            resultado.postValue(null);
            return;
        }
        db.collection(Constantes.COL_PUNTAJES).document(id)
                .get()
                .addOnSuccessListener(doc -> {
                    Puntaje p = doc.toObject(Puntaje.class);
                    if (p == null) mensaje.postValue("No se encontró esa participación");
                    resultado.postValue(p);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error al leer la participación", e);
                    mensaje.postValue("Error al cargar respuestas: " + e.getMessage());
                    resultado.postValue(null);
                });
    }

    // ---------------------------------------------------------------- PREGUNTAS

    // READ: todas las preguntas del banco.
    public void obtenerPreguntas(MutableLiveData<List<Pregunta>> resultado,
                                 MutableLiveData<String> mensaje) {
        db.collection(Constantes.COL_PREGUNTAS)
                .get()
                .addOnSuccessListener(consulta -> {
                    List<Pregunta> lista = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : consulta) {
                        try {
                            lista.add(doc.toObject(Pregunta.class));
                        } catch (Exception e) {
                            Log.w(TAG, "Pregunta inválida: " + doc.getId(), e);
                        }
                    }
                    resultado.postValue(lista);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error al leer preguntas", e);
                    mensaje.postValue("Error al cargar preguntas: " + e.getMessage());
                    resultado.postValue(new ArrayList<>());
                });
    }

    // DELETE: quita una pregunta del banco y vuelve a leer la lista
    public void eliminarPregunta(String id, MutableLiveData<List<Pregunta>> resultado,
                                 MutableLiveData<String> mensaje) {
        if (id == null || id.trim().isEmpty()) {
            mensaje.postValue("No se pudo identificar la pregunta");
            return;
        }
        db.collection(Constantes.COL_PREGUNTAS).document(id)
                .delete()
                .addOnSuccessListener(v -> {
                    mensaje.postValue("Pregunta eliminada");
                    obtenerPreguntas(resultado, mensaje);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error al eliminar pregunta", e);
                    mensaje.postValue("No se pudo eliminar: " + e.getMessage());
                });
    }

    // CREATE: guarda una pregunta nueva (Firestore le inventa el ID)
    public void guardarPregunta(Pregunta pregunta, MutableLiveData<Boolean> guardado,
                                MutableLiveData<String> mensaje) {
        db.collection(Constantes.COL_PREGUNTAS)
                .add(pregunta)
                .addOnSuccessListener(ref -> {
                    Log.d(TAG, "Pregunta guardada: " + ref.getId());
                    guardado.postValue(true);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error al guardar pregunta", e);
                    mensaje.postValue("No se pudo guardar: " + e.getMessage());
                    guardado.postValue(false);
                });
    }
}
