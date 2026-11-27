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
    // Desde dónde se escribió un comentario (campo "origen" en "comentarios")
    public static final String ORIGEN_QUIZ = "QUIZ";
    public static final String ORIGEN_MENU = "MENU";

    // ── Control de administradores ──────────────────────────────────────────────
    // Poné en true para activar la protección de pantallas admin.
    // Luego agregá los UIDs de Firebase Auth de cada admin en la lista de abajo.
    //
    // ¿Cómo obtengo mi UID?
    //   Firebase Console → Authentication → Users → columna "User UID"
    public static final boolean EXIGIR_SESION_ADMIN = true;

    // UIDs de Firebase Auth que tienen acceso admin.
    // Reemplazá "PEGAR_UID_AQUI" por el UID real del admin (podés agregar más separados por comas).
    public static final java.util.Set<String> UIDS_ADMIN =
            new java.util.HashSet<>(java.util.Arrays.asList(
                    "N2JFyZBpWCOfKAJf9t1SRkYSJ313"
            ));
}