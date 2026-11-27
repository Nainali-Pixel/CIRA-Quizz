package com.devst.ciraquizz.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.devst.ciraquizz.model.Puntaje;
import com.devst.ciraquizz.repositorio.PuntajeRepositorio;

import java.util.List;
import java.util.Map;

// Lógica de la pantalla de resultado: validar el nombre y guardar el puntaje
public class ResultadoViewModel extends ViewModel {

    private final PuntajeRepositorio repositorio = new PuntajeRepositorio();

    private final MutableLiveData<Boolean> guardado = new MutableLiveData<>(false);
    private final MutableLiveData<List<Puntaje>> mejores = new MutableLiveData<>();
    private final MutableLiveData<String> mensaje = new MutableLiveData<>();

    public LiveData<Boolean> getGuardado() { return guardado; }
    public LiveData<List<Puntaje>> getMejores() { return mejores; }
    public LiveData<String> getMensaje() { return mensaje; }

    public void cargarMejores() {
        repositorio.obtenerMejores(3, mejores, mensaje);
    }

    public void guardarPuntaje(String nombre, int aciertos, int total,
                               List<Map<String, Object>> respuestas) {
        // Validaciones del formulario
        String limpio = nombre == null ? "" : nombre.trim();
        if (limpio.isEmpty()) {
            mensaje.setValue("Ingresa tu nombre para guardar");
            return;
        }
        if (limpio.length() < 2 || limpio.length() > 20) {
            mensaje.setValue("El nombre debe tener entre 2 y 20 letras");
            return;
        }
        if (Boolean.TRUE.equals(guardado.getValue())) {
            mensaje.setValue("Este puntaje ya fue guardado");
            return;
        }
        Puntaje puntaje = new Puntaje(limpio, aciertos, total, respuestas);
        repositorio.guardarPuntaje(puntaje, guardado, mensaje);
    }
}