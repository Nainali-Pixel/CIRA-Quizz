package com.devst.ciraquizz.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.devst.ciraquizz.model.Comentario;
import com.devst.ciraquizz.model.Puntaje;
import com.devst.ciraquizz.model.Pregunta;
import com.devst.ciraquizz.repositorio.AdminRepositorio;

import java.util.List;

// Lógica de las pantallas de administración (comentarios, participantes, preguntas).
// Cada pantalla crea su propio AdminViewModel y solo usa los LiveData que le sirven.
public class AdminViewModel extends ViewModel {

    private final AdminRepositorio repositorio = new AdminRepositorio();

    // LiveData que las Activities "observan"
    private final MutableLiveData<List<Comentario>> comentarios = new MutableLiveData<>();
    private final MutableLiveData<List<Puntaje>> participaciones = new MutableLiveData<>();
    private final MutableLiveData<Puntaje> participacion = new MutableLiveData<>();
    private final MutableLiveData<List<Pregunta>> preguntas = new MutableLiveData<>();
    private final MutableLiveData<Boolean> guardado = new MutableLiveData<>();
    private final MutableLiveData<String> mensaje = new MutableLiveData<>();

    // --- Getters para que la Vista observe ---
    public LiveData<List<Comentario>> getComentarios() { return comentarios; }
    public LiveData<List<Puntaje>> getPuntajes() { return participaciones; }
    public LiveData<Puntaje> getPuntaje() { return participacion; }
    public LiveData<List<Pregunta>> getPreguntas() { return preguntas; }
    public LiveData<Boolean> getGuardado() { return guardado; }
    public LiveData<String> getMensaje() { return mensaje; }

    // --- Acciones que llama la Vista ---
    public void cargarComentarios() { repositorio.obtenerComentarios(comentarios, mensaje); }
    public void eliminarComentario(String id) { repositorio.eliminarComentario(id, comentarios, mensaje); }

    public void cargarPuntajes() { repositorio.obtenerPuntajes(participaciones, mensaje); }
    public void cargarPuntaje(String id) { repositorio.obtenerPuntaje(id, participacion, mensaje); }

    public void cargarPreguntas() { repositorio.obtenerPreguntas(preguntas, mensaje); }
    public void eliminarPregunta(String id) { repositorio.eliminarPregunta(id, preguntas, mensaje); }
    public void guardarPregunta(Pregunta pregunta) { repositorio.guardarPregunta(pregunta, guardado, mensaje); }
}
