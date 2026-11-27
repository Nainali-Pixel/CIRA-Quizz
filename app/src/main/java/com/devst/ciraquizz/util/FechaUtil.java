package com.devst.ciraquizz.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// Convierte la fecha de Firestore al formato que muestran los mockups (22/11/2026).
// Uso java.util.Date porque es lo que ya usa model/Puntaje (Firestore la guarda como Timestamp).
public class FechaUtil {

    public static String formatear(Date fecha) {
        if (fecha == null) return "--/--/----";
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fecha);
    }

    // Para ordenar listas: una fecha que falta cuenta como la más antigua
    public static long milisegundos(Date fecha) {
        return fecha == null ? 0L : fecha.getTime();
    }
}
