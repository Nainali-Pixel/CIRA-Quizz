package com.devst.ciraquizz.util;

// Nombres que usa toda la app (categorías de reciclaje y colecciones de Firestore)
public class Constantes {

    // Categorías de reciclaje
    public static final String CARTON = "CARTON";
    public static final String ALUMINIO = "ALUMINIO";
    public static final String PLASTICO = "PLASTICO";
    public static final String OTROS = "OTROS";

    // Colecciones de Firestore (todo el equipo debe usar estos nombres)
    public static final String COL_PREGUNTAS   = "preguntas";
    public static final String COL_PUNTAJES    = "puntajes";
    public static final String COL_COMENTARIOS = "comentarios";
    public static final String COL_USUARIOS    = "usuarios";

    // Tipos de pregunta
    public static final String TIPO_IMAGEN  = "IMAGEN";
    public static final String TIPO_TEORICA = "TEORICA";
}