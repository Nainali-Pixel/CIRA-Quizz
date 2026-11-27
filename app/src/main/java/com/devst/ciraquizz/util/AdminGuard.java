package com.devst.ciraquizz.util;

import android.app.Activity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

// Protege las pantallas de administración: si no hay sesión iniciada, se cierran.
// OJO: AuthActivity deja que CUALQUIERA cree una cuenta, así que "hay sesión" NO significa "es admin".
// PENDIENTE (equipo): definir quién es admin (por ejemplo un campo en la colección "usuarios" o una
// lista de correos) y validarlo aquí antes de poner Constantes.EXIGIR_SESION_ADMIN = true.
public class AdminGuard {

    // Devuelve true si se puede seguir; si devuelve false la Activity ya se cerró y hay que salir de onCreate
    public static boolean validar(Activity actividad) {
        if (!Constantes.EXIGIR_SESION_ADMIN) return true;
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(actividad, "Debes iniciar sesión como administrador", Toast.LENGTH_SHORT).show();
            actividad.finish();
            return false;
        }
        return true;
    }
}