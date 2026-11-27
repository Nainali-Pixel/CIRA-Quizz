package com.devst.ciraquizz.model;

import com.google.firebase.firestore.DocumentId;
import java.util.HashMap;
import java.util.Map;

public class RegistroReciclaje {
    @DocumentId private String id;
    private String nombre, categoria, detalle, ownerUid;
    private long cantidad, actualizado;

    public RegistroReciclaje() {}
    public RegistroReciclaje(String nombre, String categoria, long cantidad, String detalle, String ownerUid, long actualizado) {
        this.nombre=nombre; this.categoria=categoria; this.cantidad=cantidad;
        this.detalle=detalle; this.ownerUid=ownerUid; this.actualizado=actualizado;
    }
    public String getId(){return id;} public void setId(String id){this.id=id;}
    public String getNombre(){return nombre;} public String getCategoria(){return categoria;}
    public long getCantidad(){return cantidad;} public String getDetalle(){return detalle;}
    public String getOwnerUid(){return ownerUid;} public long getActualizado(){return actualizado;}
    public Map<String,Object> toMap() {
        Map<String,Object> m=new HashMap<>();
        m.put("nombre",nombre); m.put("categoria",categoria); m.put("cantidad",cantidad);
        m.put("detalle",detalle); m.put("ownerUid",ownerUid); m.put("actualizado",actualizado);
        return m;
    }
}
