package com.devst.ciraquizz.model;

// Representa un residuo: su nombre, la categoría de reciclaje,
// un emoji que lo dibuja y un consejo para depositarlo bien
public class Residuo {
    private String nombre;
    private String categoria;
    private String emoji;
    private String consejo;

    // Constructor vacío (lo pide Firestore si algún día se guarda en la nube)
    public Residuo() {}

    public Residuo(String nombre, String categoria, String emoji, String consejo) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.emoji = emoji;
        this.consejo = consejo;
    }

    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public String getEmoji() { return emoji; }
    public String getConsejo() { return consejo; }
}