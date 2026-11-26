package com.devst.ciraquizz.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.devst.ciraquizz.model.Pregunta;
import com.devst.ciraquizz.repositorio.PreguntaRepositorio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

// Lógica del quiz: elige 10 preguntas, cuenta aciertos y guarda cada respuesta
public class QuizViewModel extends ViewModel {

    public static final int PREGUNTAS_POR_PARTIDA = 10;

    private final PreguntaRepositorio repositorio = new PreguntaRepositorio();

    // LiveData que la Activity "observa"
    private final MutableLiveData<List<Pregunta>> preguntasCargadas = new MutableLiveData<>();
    private final MutableLiveData<Pregunta> preguntaActual = new MutableLiveData<>();
    private final MutableLiveData<Boolean> terminado = new MutableLiveData<>(false);
    private final MutableLiveData<String> mensaje = new MutableLiveData<>();

    // Estado de la partida
    private final List<Pregunta> partida = new ArrayList<>();
    private final ArrayList<HashMap<String, Object>> respuestas = new ArrayList<>();
    private int indice = 0;
    private int aciertos = 0;

    // --- Getters para que la Vista observe ---
    public LiveData<List<Pregunta>> getPreguntasCargadas() { return preguntasCargadas; }
    public LiveData<Pregunta> getPreguntaActual() { return preguntaActual; }
    public LiveData<Boolean> getTerminado() { return terminado; }
    public LiveData<String> getMensaje() { return mensaje; }

    public int getNumeroActual() { return indice + 1; }
    public int getTotal() { return partida.size(); }
    public int getAciertos() { return aciertos; }
    public ArrayList<HashMap<String, Object>> getRespuestas() { return respuestas; }
    public boolean partidaIniciada() { return !partida.isEmpty(); }

    // --- Acciones que llama la Vista ---

    public void cargarPreguntas() {
        repositorio.obtenerPreguntas(preguntasCargadas, mensaje);
    }

    // Mezcla las preguntas y toma 10 para esta partida
    public void iniciarPartida(List<Pregunta> todas) {
        if (partidaIniciada() || todas == null || todas.isEmpty()) return;
        List<Pregunta> copia = new ArrayList<>(todas);
        Collections.shuffle(copia);
        partida.addAll(copia.subList(0, Math.min(PREGUNTAS_POR_PARTIDA, copia.size())));
        indice = 0;
        aciertos = 0;
        respuestas.clear();
        preguntaActual.setValue(partida.get(0));
    }

    // Registra la respuesta y devuelve true si fue correcta
    public boolean responder(int opcionElegida) {
        Pregunta p = partida.get(indice);
        boolean acierto = opcionElegida == p.getCorrecta();
        if (acierto) aciertos++;

        // Se guarda para mostrarla después en "Respuestas de participantes"
        HashMap<String, Object> r = new HashMap<>();
        r.put("pregunta", p.getResiduo() != null ? p.getResiduo() : p.getTexto());
        r.put("respondio", p.getOpciones().get(opcionElegida));
        r.put("correcta", p.getOpciones().get(p.getCorrecta()));
        r.put("acierto", acierto);
        respuestas.add(r);

        return acierto;
    }

    public void siguientePregunta() {
        indice++;
        if (indice >= partida.size()) {
            terminado.setValue(true);
        } else {
            preguntaActual.setValue(partida.get(indice));
        }
    }
}