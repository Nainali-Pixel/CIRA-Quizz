package com.devst.ciraquizz;

import com.devst.ciraquizz.model.Pregunta;
import com.devst.ciraquizz.model.Residuo;
import com.devst.ciraquizz.util.CatalogoResiduos;
import com.devst.ciraquizz.util.Constantes;
import com.devst.ciraquizz.util.FechaUtil;
import com.devst.ciraquizz.util.ImagenUtil;
import com.devst.ciraquizz.util.PreguntasEjemplo;

import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Pruebas unitarias locales de utilidades y modelos de CIRA-Quizz.
 */
public class ExampleUnitTest {

    @Test
    public void catalogoResiduos_buscaCorrectamente() {
        Residuo r = CatalogoResiduos.buscar("  lata_aluminio ");
        assertNotNull(r);
        assertEquals(Constantes.ALUMINIO, r.getCategoria());
        assertNull(CatalogoResiduos.buscar(null));
        assertNull(CatalogoResiduos.buscar("QR_INEXISTENTE"));
        assertEquals("Otros", CatalogoResiduos.nombreContenedor(null));
        assertFalse(CatalogoResiduos.obtenerTodos().isEmpty());
    }

    @Test
    public void preguntasEjemplo_sonValidasParaElQuiz() {
        List<Pregunta> lista = PreguntasEjemplo.obtener();
        assertTrue(lista.size() >= 10);
        for (Pregunta p : lista) {
            assertNotNull(p.getTexto());
            assertNotNull(p.getOpciones());
            assertEquals(4, p.getOpciones().size());
            assertTrue(p.getCorrecta() >= 0 && p.getCorrecta() < 4);
        }
    }

    @Test
    public void fechaYImagenUtil_manejanNulosSinFallas() {
        assertEquals("--/--/----", FechaUtil.formatear(null));
        assertEquals(0L, FechaUtil.milisegundos(null));
        Date ahora = new Date(1_700_000_000_000L);
        assertEquals(1_700_000_000_000L, FechaUtil.milisegundos(ahora));
        assertNull(ImagenUtil.base64ABitmap(null));
        assertNull(ImagenUtil.base64ABitmap(""));
    }
}
