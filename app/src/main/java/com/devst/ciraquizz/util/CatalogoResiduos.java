package com.devst.ciraquizz.util;

import com.devst.ciraquizz.model.Residuo;

import java.util.HashMap;
import java.util.Map;

// Lista de residuos que reconoce el escáner.
// La "clave" es el texto que lleva cada código QR (por ejemplo "CAJA_CEREAL").
public class CatalogoResiduos {

    private static final Map<String, Residuo> RESIDUOS = new HashMap<>();

    static {
        RESIDUOS.put("CAJA_CEREAL", new Residuo("Caja de cereal", Constantes.CARTON, "📦", "Aplánala antes de depositarla."));
        RESIDUOS.put("PAPEL_PERIODICO", new Residuo("Papel periódico", Constantes.CARTON, "📰", "Debe estar seco y limpio."));
        RESIDUOS.put("CAJA_ZAPATOS", new Residuo("Caja de zapatos", Constantes.CARTON, "📦", "Quita el papel de seda y aplánala."));
        RESIDUOS.put("LATA_ALUMINIO", new Residuo("Lata de aluminio", Constantes.ALUMINIO, "🥫", "Vacíala y enjuágala antes de reciclar."));
        RESIDUOS.put("LATA_ATUN", new Residuo("Lata de atún", Constantes.ALUMINIO, "🥫", "Enjuágala para quitar restos de comida."));
        RESIDUOS.put("LATA_REFRESCO", new Residuo("Lata de refresco", Constantes.ALUMINIO, "🥤", "Vacíala y aplástala para ahorrar espacio."));
        RESIDUOS.put("BOTELLA_REFRESCO", new Residuo("Botella de refresco", Constantes.PLASTICO, "🧴", "Vacíala, aplástala y ponle la tapa."));
        RESIDUOS.put("BOLSA_PLASTICO", new Residuo("Bolsa de plástico", Constantes.PLASTICO, "🛍️", "Junta varias bolsas dentro de una."));
        RESIDUOS.put("TAPA_BOTELLA", new Residuo("Tapa de botella", Constantes.PLASTICO, "🔘", "Va al contenedor de plástico."));
        RESIDUOS.put("CASCARA_PLATANO", new Residuo("Cáscara de plátano", Constantes.OTROS, "🍌", "Es orgánico: va al contenedor de otros."));
    }

    // Devuelve el residuo de ese código, o null si no existe
    public static Residuo buscar(String codigo) {
        if (codigo == null) return null;
        return RESIDUOS.get(codigo.trim().toUpperCase());
    }

    // Convierte la categoría en un texto bonito para mostrar
    public static String nombreContenedor(String categoria) {
        switch (categoria) {
            case Constantes.CARTON:   return "Cartón";
            case Constantes.ALUMINIO: return "Aluminio / Latas";
            case Constantes.PLASTICO: return "Plástico";
            default:                  return "Otros";
        }
    }
}