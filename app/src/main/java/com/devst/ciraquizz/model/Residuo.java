package com.devst.ciraquizz.model;


//Representa una basura, tiene su nombre, categoria de reciclaje y su imagen
public class Residuo {
    private final String nombre;
    private final String categoria;
    private final int imagen;      // aquí va R.drawable.nombre_de_la_imagen

    public Residuo(String nombre, String categoria, int imagen) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.imagen = imagen;
    }

    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public int getImagen() { return imagen; }
}