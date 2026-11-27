package com.devst.ciraquizz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.devst.ciraquizz.util.Constantes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// Pantalla 1 - Menú principal de CIRA-Quizz
public class MenuActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        FirebaseUser usuario = auth.getCurrentUser();
        if (usuario == null) {
            irLogin();
            return;
        }

        setContentView(R.layout.activity_menu);

        findViewById(R.id.cardEscanearQr).setOnClickListener(v ->
                startActivity(new Intent(this, EscanearQrActivity.class)));

        findViewById(R.id.cardQuiz).setOnClickListener(v ->
                startActivity(new Intent(this, QuizActivity.class)));

        findViewById(R.id.cardHistorial).setOnClickListener(v ->
                startActivity(new Intent(this, HistorialActivity.class)));

        findViewById(R.id.cardMateriales).setOnClickListener(v ->
                startActivity(new Intent(this, MaterialesActivity.class)));

        findViewById(R.id.btnOpinion).setOnClickListener(v -> {
            Intent intent = new Intent(this, EnviarComentarioActivity.class);
            intent.putExtra(EnviarComentarioActivity.EXTRA_ORIGEN, Constantes.ORIGEN_MENU);
            startActivity(intent);
        });

        findViewById(R.id.btnSobreNosotros).setOnClickListener(v -> mostrarSobreNosotros());

        findViewById(R.id.btnAdministracion).setOnClickListener(v ->
                startActivity(new Intent(this, AdminPanelActivity.class)));

        findViewById(R.id.btnCerrarSesion).setOnClickListener(v -> confirmarCerrarSesion(usuario));
    }

    private void mostrarSobreNosotros() {
        LinearLayout contenido = new LinearLayout(this);
        contenido.setOrientation(LinearLayout.VERTICAL);
        int pad = Math.round(20 * getResources().getDisplayMetrics().density);
        contenido.setPadding(pad, pad, pad, 0);

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.logo_cira);
        logo.setAdjustViewBounds(true);
        int logoSize = Math.round(96 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams lpLogo = new LinearLayout.LayoutParams(logoSize, logoSize);
        lpLogo.gravity = android.view.Gravity.CENTER_HORIZONTAL;
        lpLogo.bottomMargin = Math.round(12 * getResources().getDisplayMetrics().density);
        contenido.addView(logo, lpLogo);

        TextView texto = new TextView(this);
        texto.setText("CIRA-Quizz es una aplicación educativa sobre reciclaje y correcta clasificación de residuos.\n\n"
                + "Escanea los códigos QR de los contenedores, pon a prueba tus conocimientos en el quiz, "
                + "lleva tus registros de reciclaje y envíanos tus sugerencias.");
        texto.setTextSize(14f);
        contenido.addView(texto);

        new AlertDialog.Builder(this)
                .setTitle("Sobre CIRA-Quizz")
                .setView(contenido)
                .setPositiveButton("Entendido", null)
                .show();
    }

    private void confirmarCerrarSesion(FirebaseUser usuario) {
        String correo = usuario.getEmail() != null ? usuario.getEmail() : "tu cuenta";
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("Sesión actual: " + correo + "\n\n¿Deseas salir de tu cuenta?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Cerrar sesión", (d, w) -> {
                    auth.signOut();
                    irLogin();
                })
                .show();
    }

    private void irLogin() {
        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }
}
