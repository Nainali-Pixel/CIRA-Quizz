package com.devst.ciraquizz;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.devst.ciraquizz.model.RegistroReciclaje;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;

public class RegistrosActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String uid;
    private LinearLayout lista;
    private ListenerRegistration listener;
    private final String COLECCION="registros_reciclaje";

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        uid=FirebaseAuth.getInstance().getUid();
        if(uid==null){finish();return;}
        db=FirebaseFirestore.getInstance();
        LinearLayout root=new LinearLayout(this); root.setOrientation(1); root.setPadding(28,24,28,24);
        root.setBackgroundColor(0xFFF4F7F5);
        TextView title=new TextView(this); title.setText("Mis registros de reciclaje"); title.setTextSize(23); title.setTextColor(0xFF174B3A);
        root.addView(title);
        TextView info=new TextView(this); info.setText("Cada cuenta solo puede consultar y administrar sus propios registros.");
        root.addView(info);
        Button nuevo=new Button(this); nuevo.setText("＋ Agregar registro"); root.addView(nuevo);
        ScrollView scroll=new ScrollView(this); lista=new LinearLayout(this); lista.setOrientation(1); scroll.addView(lista);
        root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        Button volver=new Button(this); volver.setText("Volver al menú"); root.addView(volver);
        setContentView(root);
        nuevo.setOnClickListener(v->formulario(null));
        volver.setOnClickListener(v->finish());
    }

    @Override protected void onStart(){super.onStart();
        listener=db.collection(COLECCION).whereEqualTo("ownerUid",uid)
                .addSnapshotListener((snap,error)->{
                    if(error!=null){aviso("Error al consultar: "+error.getLocalizedMessage());return;}
                    lista.removeAllViews();
                    if(snap==null || snap.isEmpty()){TextView vacio=new TextView(this);vacio.setText("Aún no tienes registros. Agrega el primero.");lista.addView(vacio);return;}
                    List<RegistroReciclaje> registros=new ArrayList<>();
                    for(DocumentSnapshot doc:snap.getDocuments()){
                        RegistroReciclaje r=doc.toObject(RegistroReciclaje.class);
                        if(r!=null){r.setId(doc.getId());registros.add(r);}
                    }
                    Collections.sort(registros,(a,b)->Long.compare(b.getActualizado(),a.getActualizado()));
                    for(RegistroReciclaje r:registros) tarjeta(r);
                });
    }
    @Override protected void onStop(){super.onStop();if(listener!=null){listener.remove();listener=null;}}

    private void tarjeta(RegistroReciclaje r){
        LinearLayout card=new LinearLayout(this);card.setOrientation(1);card.setPadding(18,14,18,14);
        card.setBackgroundColor(0xFFFFFFFF);
        TextView t=new TextView(this);t.setText(r.getNombre()+" · "+r.getCategoria());t.setTextSize(18);t.setTextColor(0xFF174B3A);
        TextView d=new TextView(this);d.setText("Cantidad: "+r.getCantidad()+"\nDetalle: "+(r.getDetalle()==null?"—":r.getDetalle()));
        card.addView(t);card.addView(d);
        LinearLayout acciones=new LinearLayout(this);acciones.setOrientation(0);
        Button editar=new Button(this);editar.setText("Editar"); Button borrar=new Button(this);borrar.setText("Eliminar");
        acciones.addView(editar,new LinearLayout.LayoutParams(0,-2,1));acciones.addView(borrar,new LinearLayout.LayoutParams(0,-2,1));
        card.addView(acciones);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,12,0,4);lista.addView(card,lp);
        editar.setOnClickListener(v->formulario(r));
        borrar.setOnClickListener(v->new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Eliminar registro").setMessage("¿Deseas eliminar “"+r.getNombre()+"”?")
                .setNegativeButton("Cancelar",null).setPositiveButton("Eliminar",(a,b)->eliminar(r)).show());
    }

    private void formulario(RegistroReciclaje actual){
        LinearLayout box=new LinearLayout(this);box.setPadding(24,8,24,0);box.setOrientation(1);
        EditText nombre=edit("Residuo / material",box);
        Spinner categoria=new Spinner(this);
        String[] cats={"Cartón","Aluminio","Plástico","Vidrio","Otros"};
        categoria.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,cats));box.addView(categoria);
        EditText cantidad=edit("Cantidad (número entero)",box);cantidad.setInputType(InputType.TYPE_CLASS_NUMBER);
        EditText detalle=edit("Detalle (opcional)",box);
        if(actual!=null){nombre.setText(actual.getNombre());cantidad.setText(String.valueOf(actual.getCantidad()));detalle.setText(actual.getDetalle());
            for(int i=0;i<cats.length;i++)if(cats[i].equals(actual.getCategoria()))categoria.setSelection(i);}
        androidx.appcompat.app.AlertDialog dialog=new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(actual==null?"Nuevo registro":"Editar registro").setView(box)
                .setNegativeButton("Cancelar",null).setPositiveButton("Guardar",null).create();
        dialog.setOnShowListener(x->dialog.getButton(-1).setOnClickListener(v->{
            String n=nombre.getText().toString().trim(), q=cantidad.getText().toString().trim();
            if(n.isEmpty()){nombre.setError("Campo obligatorio");return;}
            if(q.isEmpty()||Long.parseLong(q)<=0){cantidad.setError("Ingresa una cantidad mayor que cero");return;}
            RegistroReciclaje r=new RegistroReciclaje(n,categoria.getSelectedItem().toString(),Long.parseLong(q),
                    detalle.getText().toString().trim(),uid,System.currentTimeMillis());
            guardar(r,actual,dialog);
        }));
        dialog.show();
    }
    private EditText edit(String hint,LinearLayout box){EditText e=new EditText(this);e.setHint(hint);box.addView(e);return e;}
    private void guardar(RegistroReciclaje r,RegistroReciclaje anterior,androidx.appcompat.app.AlertDialog dialog){
        DocumentReference ref=anterior==null?db.collection(COLECCION).document():db.collection(COLECCION).document(anterior.getId());
        ref.set(r.toMap()).addOnSuccessListener(v->{dialog.dismiss();aviso(anterior==null?"Registro creado":"Registro actualizado");})
                .addOnFailureListener(e->aviso("No se pudo guardar: "+e.getLocalizedMessage()));
    }
    private void eliminar(RegistroReciclaje r){
        db.collection(COLECCION).document(r.getId()).delete()
                .addOnSuccessListener(v->aviso("Registro eliminado"))
                .addOnFailureListener(e->aviso("No se pudo eliminar: "+e.getLocalizedMessage()));
    }
    private void aviso(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
}
