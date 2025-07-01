package com.grupo3.medrem.models;

public class Medicamento {

    private int idMedicamento;
    private String nombre;
    private int dosis_cantidad;
    private UnidadDosis unidadDosis;

    @Override
    public String toString() {
        String abreviatura = unidadDosis != null ? unidadDosis.getAbreviatura() : "";
        return nombre + " " + dosis_cantidad + " " + abreviatura;
    }
    public Medicamento(int idMedicamento) {
        this.idMedicamento = idMedicamento;
    }

    public int getIdMedicamento() {
        return idMedicamento;
    }

    public void setIdMedicamento(int idMedicamento) {
        this.idMedicamento = idMedicamento;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getDosis_cantidad() {
        return dosis_cantidad;
    }

    public void setDosis_cantidad(int dosis_cantidad) {
        this.dosis_cantidad = dosis_cantidad;
    }

    public UnidadDosis getUnidadDosis() {
        return unidadDosis;
    }

    public void setUnidadDosis(UnidadDosis unidadDosis) {
        this.unidadDosis = unidadDosis;
    }
}
