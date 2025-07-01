package com.grupo3.medrem.models;

public class Frecuencia {
    private int idFrecuencia;
    private String nombre;
    public Frecuencia(int idFrecuencia) {
        this.idFrecuencia = idFrecuencia;
    }
    @Override
    public String toString() {
        return nombre;
    }
    public int getIdFrecuencia() {
        return idFrecuencia;
    }

    public void setIdFrecuencia(int idFrecuencia) {
        this.idFrecuencia = idFrecuencia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
