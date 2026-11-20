package com.devst.ciraquizz.model;

//Representa una pregunta del quizz, tiene su texto, opciones y su imagen si es que tiene
public class Pregunta {
    private final String texto;
    private final String[] opciones;
    private final int correcta;    // posición de la opción correcta: 0, 1, 2 o 3
    private final int imagen;      // 0 significa "pregunta sin imagen"

    public Pregunta(String texto, String[] opciones, int correcta, int imagen) {
        this.texto = texto;
        this.opciones = opciones;
        this.correcta = correcta;
        this.imagen = imagen;
    }

    public String getTexto() { return texto; }
    public String[] getOpciones() { return opciones; }
    public int getCorrecta() { return correcta; }
    public int getImagen() { return imagen; }
    public boolean tieneImagen() { return imagen != 0; }
}