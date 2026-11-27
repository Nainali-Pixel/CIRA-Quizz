package com.devst.ciraquizz.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.util.List;

// Representa una pregunta del quiz guardada en Firestore (colección "preguntas")
public class Pregunta {

    @DocumentId
    private String id;              // ID del documento en Firestore (lo pone Firestore)
    private String texto;           // Enunciado de la pregunta
    private List<String> opciones;  // Siempre 4 opciones (A, B, C, D)
    private int correcta;           // Posición de la opción correcta: 0, 1, 2 o 3
    private String tipo;            // "IMAGEN" o "TEORICA"
    private String imagen;          // Emoji del residuo, o "" si no lleva imagen
    private String residuo;         // Nombre corto, para el historial (ej: "Lata de aluminio")
    private String creadoPor;       // UID del administrador que la creó

    private String imagenBase64;    // Foto del residuo subida por el admin (JPEG en Base64)

    // IMPORTANTE: constructor vacío requerido por Firestore
    public Pregunta() {}

    public Pregunta(String texto, List<String> opciones, int correcta,
                    String tipo, String imagen, String residuo, String creadoPor) {
        this.texto = texto;
        this.opciones = opciones;
        this.correcta = correcta;
        this.tipo = tipo;
        this.imagen = imagen;
        this.residuo = residuo;
        this.creadoPor = creadoPor;
    }

    // --- Getters y Setters (requeridos por Firestore) ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public List<String> getOpciones() { return opciones; }
    public void setOpciones(List<String> opciones) { this.opciones = opciones; }

    public int getCorrecta() { return correcta; }
    public void setCorrecta(int correcta) { this.correcta = correcta; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public String getResiduo() { return residuo; }
    public void setResiduo(String residuo) { this.residuo = residuo; }

    public String getCreadoPor() { return creadoPor; }
    public void setCreadoPor(String creadoPor) { this.creadoPor = creadoPor; }

    public String getImagenBase64() { return imagenBase64; }
    public void setImagenBase64(String imagenBase64) { this.imagenBase64 = imagenBase64; }
    // @Exclude: Firestore no guarda este método como campo
    @Exclude
    public boolean tieneImagen() {
        return imagen != null && !imagen.isEmpty();
    }

    @Exclude
    public boolean tieneFoto() {
        return imagenBase64 != null && !imagenBase64.isEmpty();
    }

}