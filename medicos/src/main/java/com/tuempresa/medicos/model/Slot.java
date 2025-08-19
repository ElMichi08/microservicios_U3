package com.tuempresa.medicos.model;

public class Slot {
    private Long id;
    private Long medicoId;
    private String inicio; // ISO-8601
    private String fin;    // ISO-8601
    private String estado; // LIBRE | RESERVADO | EXTRA
    private String creadoPor; // SISTEMA | EXTRA

    public Slot(){}
    public Slot(Long id, Long medicoId, String inicio, String fin, String estado, String creadoPor){
        this.id=id; this.medicoId=medicoId; this.inicio=inicio; this.fin=fin; this.estado=estado; this.creadoPor=creadoPor;
    }
    // getters/setters
    public Long getId(){ return id; } public void setId(Long id){ this.id=id; }
    public Long getMedicoId(){ return medicoId; } public void setMedicoId(Long m){ this.medicoId=m; }
    public String getInicio(){ return inicio; } public void setInicio(String i){ this.inicio=i; }
    public String getFin(){ return fin; } public void setFin(String f){ this.fin=f; }
    public String getEstado(){ return estado; } public void setEstado(String e){ this.estado=e; }
    public String getCreadoPor(){ return creadoPor; } public void setCreadoPor(String c){ this.creadoPor=c; }
}
