package com.devst.ciraquizz.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

// Representa un comentario de un usuario guardado en Firestore (colección "comentarios").
// Lo escriben la pantalla de sugerencias (origen MENU) y el final del quiz (origen QUIZ);
// yo solo lo leo y lo borro desde la pantalla de administración.
public class Comentario {

    @DocumentId
    private String id;          // ID del documento (lo pone Firestore)
    private String nombre;      // Nombre del usuario, vacío o null si fue anónimo
    private String texto;       // El comentario
    private String origen;      // Constantes.ORIGEN_QUIZ o Constantes.ORIGEN_MENU
    @ServerTimestamp
    private Date fecha;         // Cuándo se envió: la pone el servidor de Firebase (déjala en null al guardar)

    // IMPORTANTE: constructor vacío requerido por Firestore
    public Comentario() {}

    public Comentario(String nombre, String texto, String origen) {
        this.nombre = nombre;
        this.texto = texto;
        this.origen = origen;
        // fecha no se manda: @ServerTimestamp la rellena Firestore
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
}