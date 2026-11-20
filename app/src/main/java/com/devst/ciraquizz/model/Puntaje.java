package com.devst.ciraquizz.model;

//Representa un puntaje, tiene su id, nombre, puntaje y fecha
public class Puntaje {
    private final long id;
    private final String nombre;
    private final int puntaje;
    private final String fecha;

    public Puntaje(long id, String nombre, int puntaje, String fecha) {
        this.id = id;
        this.nombre = nombre;
        this.puntaje = puntaje;
        this.fecha = fecha;
    }

    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public int getPuntaje() { return puntaje; }
    public String getFecha() { return fecha; }
}