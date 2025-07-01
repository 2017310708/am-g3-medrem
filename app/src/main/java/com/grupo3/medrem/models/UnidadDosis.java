package com.grupo3.medrem.models;

import com.google.gson.annotations.SerializedName;

public class UnidadDosis {
    @SerializedName("id_unidad_dosis")
    private int idUnidadDosis;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("abreviatura")
    private String abreviatura;

    // Getters
    public int getIdUnidadDosis() { return idUnidadDosis; }
    public String getNombre() { return nombre; }
    public String getAbreviatura() { return abreviatura; }
}
