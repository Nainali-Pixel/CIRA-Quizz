package com.devst.ciraquizz.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;
import java.util.Map;

// Representa el resultado de una partida (colección "puntajes" en Firestore)
public class Puntaje {

    @DocumentId
    private String id;                          // ID del documento (lo pone Firestore)
    private String nombre;                      // Nombre del jugador
    private int puntaje;                        // Respuestas correctas
    private int total;                          // Total de preguntas (10)
    @ServerTimestamp
    private Date fecha;                         // Fecha: la pone el servidor de Firebase
    private List<Map<String, Object>> respuestas; // Detalle de cada respuesta

    // IMPORTANTE: constructor vacío requerido por Firestore
    public Puntaje() {}

    public Puntaje(String nombre, int puntaje, int total, List<Map<String, Object>> respuestas) {
        this.nombre = nombre;
        this.puntaje = puntaje;
        this.total = total;
        this.respuestas = respuestas;
    }

    // --- Getters y Setters (requeridos por Firestore) ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getPuntaje() { return puntaje; }
    public void setPuntaje(int puntaje) { this.puntaje = puntaje; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public List<Map<String, Object>> getRespuestas() { return respuestas; }
    public void setRespuestas(List<Map<String, Object>> respuestas) { this.respuestas = respuestas; }
}