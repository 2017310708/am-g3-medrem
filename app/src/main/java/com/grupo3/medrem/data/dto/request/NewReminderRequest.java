package com.grupo3.medrem.data.dto.request;

import com.google.gson.annotations.SerializedName;
import com.grupo3.medrem.models.Frecuencia;
import com.grupo3.medrem.models.Medicamento;
import com.grupo3.medrem.models.User;

public class NewReminderRequest {

    @SerializedName("usuario")
    private User usuario;

    @SerializedName("medicamento")
    private Medicamento medicamento;

    @SerializedName("frecuencia")
    private Frecuencia frecuencia;

    @SerializedName("fechaInicio")
    private String fechaInicio;

    @SerializedName("fechaFin")
    private String fechaFin;

    @SerializedName("hora")
    private String hora;

    @SerializedName("notas")
    private String notas;

    public NewReminderRequest() {}

    public NewReminderRequest(User usuario, Medicamento medicamento, Frecuencia frecuencia,
                              String fechaInicio, String fechaFin, String hora, String notas) {
        this.usuario = usuario;
        this.medicamento = medicamento;
        this.frecuencia = frecuencia;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.hora = hora;
        this.notas = notas;
    }

    public User getUser() { return usuario; }
    public void setUser(User usuario) { this.usuario = usuario; }

    public Medicamento getMedicamento() { return medicamento; }
    public void setMedicamento(Medicamento medicamento) { this.medicamento = medicamento; }

    public Frecuencia getFrecuencia() { return frecuencia; }
    public void setFrecuencia(Frecuencia frecuencia) { this.frecuencia = frecuencia; }

    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}