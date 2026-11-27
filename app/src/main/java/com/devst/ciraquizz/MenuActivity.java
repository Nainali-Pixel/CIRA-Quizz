package com.devst.ciraquizz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth=FirebaseAuth.getInstance();
        if(auth.getCurrentUser()==null){irLogin();return;}
        setContentView(R.layout.activity_menu);
        findViewById(R.id.cardEscanearQr).setOnClickListener(v->startActivity(new Intent(this,EscanearQrActivity.class)));
        findViewById(R.id.cardQuiz).setOnClickListener(v->startActivity(new Intent(this,QuizActivity.class)));
        findViewById(R.id.cardHistorial).setOnClickListener(v->abrirRegistros());
        findViewById(R.id.btnAdministracion).setOnClickListener(v->abrirRegistros());
        findViewById(R.id.cardMateriales).setOnClickListener(v->avisar("Materiales"));
        findViewById(R.id.btnOpinion).setOnClickListener(v->avisar("Opinión / Sugerencias"));
        findViewById(R.id.btnSobreNosotros).setOnClickListener(v->avisar("Sobre nosotros"));
        android.view.ViewGroup content=findViewById(android.R.id.content);
        LinearLayout contenedor=(LinearLayout)content.getChildAt(0);
        Button salir=new Button(this);salir.setText("Cerrar sesión ("+auth.getCurrentUser().getEmail()+")");
        contenedor.addView(salir);
        salir.setOnClickListener(v->{auth.signOut();irLogin();});
    }
    private void abrirRegistros(){startActivity(new Intent(this,RegistrosActivity.class));}
    private void irLogin(){startActivity(new Intent(this,AuthActivity.class));finish();}
    private void avisar(String s){Toast.makeText(this,"Próximamente: "+s,Toast.LENGTH_SHORT).show();}
}
