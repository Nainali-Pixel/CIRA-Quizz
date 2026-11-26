package com.devst.ciraquizz.util;

import com.devst.ciraquizz.model.Pregunta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Preguntas iniciales: se suben a Firestore la primera vez que la colección está vacía
public class PreguntasEjemplo {

    private static final String SISTEMA = "sistema";
    private static final String TEXTO_IMAGEN = "¿En qué contenedor va este residuo?";
    private static final List<String> CONTENEDORES =
            Arrays.asList("Cartón", "Aluminio / Latas", "Plástico", "Otros");

    public static List<Pregunta> obtener() {
        List<Pregunta> lista = new ArrayList<>();

        // Preguntas con imagen (0=Cartón, 1=Aluminio, 2=Plástico, 3=Otros)
        lista.add(conImagen("🥫", "Lata de aluminio", 1));
        lista.add(conImagen("📦", "Caja de cereal", 0));
        lista.add(conImagen("🧴", "Botella de refresco", 2));
        lista.add(conImagen("🍌", "Cáscara de plátano", 3));
        lista.add(conImagen("📰", "Papel periódico", 0));
        lista.add(conImagen("🔘", "Tapa de botella", 2));
        lista.add(conImagen("🥤", "Lata de refresco", 1));
        lista.add(conImagen("🛍️", "Bolsa de plástico", 2));

        // Preguntas teóricas
        lista.add(teorica("¿Qué materiales se depositan en el contenedor de cartón?",
                Arrays.asList("Diarios y cajas", "Latas", "Botellas plásticas", "Restos de comida"),
                0, "Contenedor de cartón"));
        lista.add(teorica("¿Por qué conviene aplanar las cajas de cartón?",
                Arrays.asList("Para que ocupen menos espacio", "Porque así pesan más",
                        "Para que no se mojen", "No conviene aplanarlas"),
                0, "Aplanar cajas"));
        lista.add(teorica("¿Qué se debe hacer con una lata antes de reciclarla?",
                Arrays.asList("Vaciarla y enjuagarla", "Pintarla", "Llenarla de papel", "Nada"),
                0, "Preparar una lata"));
        lista.add(teorica("¿Un vaso de plumavit se puede reciclar como plástico?",
                Arrays.asList("Sí, siempre", "No, va a Otros", "Solo si está limpio", "Va a Cartón"),
                1, "Vaso de plumavit"));

        return lista;
    }

    private static Pregunta conImagen(String emoji, String residuo, int correcta) {
        return new Pregunta(TEXTO_IMAGEN, CONTENEDORES, correcta,
                Constantes.TIPO_IMAGEN, emoji, residuo, SISTEMA);
    }

    private static Pregunta teorica(String texto, List<String> opciones, int correcta, String residuo) {
        return new Pregunta(texto, opciones, correcta,
                Constantes.TIPO_TEORICA, "", residuo, SISTEMA);
    }
}