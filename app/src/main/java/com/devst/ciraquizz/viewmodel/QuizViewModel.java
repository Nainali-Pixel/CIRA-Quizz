package com.devst.ciraquizz.viewmodel;

import android.os.Handler;
import android.os.Looper;

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
    private final Handler handler = new Handler(Looper.getMainLooper());

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
    private int ultimaElegida = -1;

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
    public int getUltimaElegida() { return ultimaElegida; }

    // --- Acciones que llama la Vista ---

    public void cargarPreguntas() {
        repositorio.obtenerPreguntas(preguntasCargadas, mensaje);
    }

    // Mezcla las preguntas válidas y toma hasta 10 para esta partida
    public void iniciarPartida(List<Pregunta> todas) {
        if (partidaIniciada() || todas == null || todas.isEmpty()) return;
        List<Pregunta> validas = new ArrayList<>();
        for (Pregunta p : todas) {
            if (p != null && p.getOpciones() != null && p.getOpciones().size() >= 4
                    && p.getCorrecta() >= 0 && p.getCorrecta() < 4) {
                validas.add(p);
            }
        }
        if (validas.isEmpty()) return;

        Collections.shuffle(validas);
        partida.addAll(validas.subList(0, Math.min(PREGUNTAS_POR_PARTIDA, validas.size())));
        indice = 0;
        aciertos = 0;
        ultimaElegida = -1;
        respuestas.clear();
        preguntaActual.setValue(partida.get(0));
    }

    // Registra la respuesta y programa el avance a la siguiente pregunta
    public boolean responder(int opcionElegida) {
        Pregunta p = partida.get(indice);
        ultimaElegida = opcionElegida;
        boolean acierto = opcionElegida == p.getCorrecta();
        if (acierto) aciertos++;

        // Se guarda para mostrarla después en "Respuestas de participantes"
        HashMap<String, Object> r = new HashMap<>();
        r.put("pregunta", p.getResiduo() != null && !p.getResiduo().isEmpty() ? p.getResiduo() : p.getTexto());
        r.put("respondio", p.getOpciones().get(opcionElegida));
        r.put("correcta", p.getOpciones().get(p.getCorrecta()));
        r.put("acierto", acierto);
        respuestas.add(r);

        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(this::siguientePregunta, 1200);
        return acierto;
    }

    public void siguientePregunta() {
        ultimaElegida = -1;
        indice++;
        if (indice >= partida.size()) {
            terminado.setValue(true);
        } else {
            preguntaActual.setValue(partida.get(indice));
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        handler.removeCallbacksAndMessages(null);
    }
}
