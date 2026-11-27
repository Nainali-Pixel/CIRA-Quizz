package com.devst.ciraquizz.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

// Pasa la foto que elige el admin a texto Base64 para guardarla dentro de la pregunta en Firestore.
// Elegí esto en vez de Firebase Storage porque el proyecto no tiene esa dependencia.
// La reduzco a máx. 400 px y JPEG calidad 75 para que pese pocos KB (Firestore limita cada documento a 1 MB).
public class ImagenUtil {

    private static final int MAX_LADO = 400;

    // Lee la imagen elegida en la galería y la devuelve como Base64
    public static String uriABase64(Context contexto, Uri uri) throws IOException {
        // 1) Solo leo el tamaño para no cargar una foto gigante completa en memoria
        BitmapFactory.Options medida = new BitmapFactory.Options();
        medida.inJustDecodeBounds = true;
        try (InputStream in = contexto.getContentResolver().openInputStream(uri)) {
            BitmapFactory.decodeStream(in, null, medida);
        }
        int lado = Math.max(medida.outWidth, medida.outHeight);
        int muestra = 1;
        while (lado / muestra > MAX_LADO * 2) muestra *= 2;

        // 2) Cargo la imagen ya reducida
        BitmapFactory.Options opciones = new BitmapFactory.Options();
        opciones.inSampleSize = muestra;
        Bitmap bitmap;
        try (InputStream in = contexto.getContentResolver().openInputStream(uri)) {
            bitmap = BitmapFactory.decodeStream(in, null, opciones);
        }
        if (bitmap == null) throw new IOException("No se pudo leer la imagen");

        // 3) Ajusto el tamaño final y comprimo
        float escala = Math.min(1f, (float) MAX_LADO / Math.max(bitmap.getWidth(), bitmap.getHeight()));
        if (escala < 1f) {
            bitmap = Bitmap.createScaledBitmap(bitmap,
                    Math.round(bitmap.getWidth() * escala),
                    Math.round(bitmap.getHeight() * escala), true);
        }
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, salida);
        return Base64.encodeToString(salida.toByteArray(), Base64.NO_WRAP);
    }

    // Convierte el Base64 guardado en Firestore otra vez en imagen (o null si está dañado)
    public static Bitmap base64ABitmap(String base64) {
        try {
            byte[] datos = Base64.decode(base64, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(datos, 0, datos.length);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}