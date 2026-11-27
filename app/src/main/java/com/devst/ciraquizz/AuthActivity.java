package com.devst.ciraquizz;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AuthActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText email, password;
    private Button accion, cambiar;
    private boolean registro = false;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            abrirMenu();
            return;
        }
        mostrarFormulario();
    }

    private void mostrarFormulario() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 64, 48, 32);
        layout.setBackgroundColor(0xFFF1F8F2);
        layout.setFitsSystemWindows(true);

        TextView titulo = new TextView(this);
        titulo.setText("CIRA-Quizz\nAccede a tu cuenta");
        titulo.setTextSize(26);
        titulo.setTextColor(0xFF174B3A);
        layout.addView(titulo, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        email = campo("Correo electrónico");
        email.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        layout.addView(email);

        password = campo("Contraseña");
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(password);

        accion = new Button(this);
        accion.setText("Iniciar sesión");
        layout.addView(accion);

        cambiar = new Button(this);
        cambiar.setText("¿No tienes cuenta? Crear cuenta");
        layout.addView(cambiar);

        setContentView(layout);

        accion.setOnClickListener(v -> autenticar());
        cambiar.setOnClickListener(v -> {
            registro = !registro;
            titulo.setText(registro ? "CIRA-Quizz\nCrear cuenta" : "CIRA-Quizz\nAccede a tu cuenta");
            accion.setText(registro ? "Registrarme" : "Iniciar sesión");
            cambiar.setText(registro ? "Ya tengo una cuenta" : "¿No tienes cuenta? Crear cuenta");
        });
    }

    private EditText campo(String hint) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setSingleLine(true);
        e.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return e;
    }

    private void autenticar() {
        String mail = email.getText().toString().trim();
        String pass = password.getText().toString();
        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            email.setError("Ingresa un correo válido");
            return;
        }
        if (pass.length() < 6) {
            password.setError("Usa al menos 6 caracteres");
            return;
        }
        setBusy(true);
        if (registro) {
            auth.createUserWithEmailAndPassword(mail, pass)
                    .addOnSuccessListener(r -> abrirMenu())
                    .addOnFailureListener(this::error);
        } else {
            auth.signInWithEmailAndPassword(mail, pass)
                    .addOnSuccessListener(r -> abrirMenu())
                    .addOnFailureListener(this::error);
        }
    }

    private void setBusy(boolean busy) {
        email.setEnabled(!busy);
        password.setEnabled(!busy);
        accion.setEnabled(!busy);
        cambiar.setEnabled(!busy);
    }

    private void error(Exception e) {
        setBusy(false);
        Toast.makeText(this, "No se pudo autenticar: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }

    private void abrirMenu() {
        startActivity(new Intent(this, MenuActivity.class));
        finish();
    }
}
