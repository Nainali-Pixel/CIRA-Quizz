package com.devst.ciraquizz.util;

import android.app.Activity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// Protege las pantallas de administración verificando que el usuario logueado
// tenga un UID que esté en la lista Constantes.UIDS_ADMIN.
public class AdminGuard {

    /**
     * Devuelve true si el usuario tiene permiso de admin.
     * Si devuelve false, la Activity ya fue cerrada; salir de onCreate inmediatamente.
     */
    public static boolean validar(Activity actividad) {
        if (!Constantes.EXIGIR_SESION_ADMIN) return true;

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        if (usuario == null) {
            Toast.makeText(actividad, "Debés iniciar sesión para acceder.", Toast.LENGTH_SHORT).show();
            actividad.finish();
            return false;
        }

        if (!Constantes.UIDS_ADMIN.contains(usuario.getUid())) {
            Toast.makeText(actividad, "No tenés permisos de administrador.", Toast.LENGTH_SHORT).show();
            actividad.finish();
            return false;
        }

        return true;
    }
}