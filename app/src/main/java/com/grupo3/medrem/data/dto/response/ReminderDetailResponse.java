package com.grupo3.medrem.data.dto.response;

import com.google.gson.annotations.SerializedName;
import com.grupo3.medrem.models.User;
import com.grupo3.medrem.models.Medicamento;
import com.grupo3.medrem.models.Frecuencia;

import java.util.List;

public class ReminderDetailResponse {
    @SerializedName("idRecordatorio")
    private int idRecordatorio;

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

    @SerializedName("recordatorios")
    private List<DiaRecordatorioResponse> diasRecordatorio;

    public ReminderDetailResponse() {
    }

    public int getIdRecordatorio() {
        return idRecordatorio;
    }

    public void setIdRecordatorio(int idRecordatorio) {
        this.idRecordatorio = idRecordatorio;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
    }

    public Frecuencia getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(Frecuencia frecuencia) {
        this.frecuencia = frecuencia;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public List<DiaRecordatorioResponse> getDiasRecordatorio() {
        return diasRecordatorio;
    }

    public void setDiasRecordatorio(List<DiaRecordatorioResponse> diasRecordatorio) {
        this.diasRecordatorio = diasRecordatorio;
    }

    public static class DiaRecordatorioResponse {
        @SerializedName("idDiaRecordatorio")
        private int idDiaRecordatorio;

        @SerializedName("dia")
        private int dia;

        public DiaRecordatorioResponse() {
        }

        public int getIdDiaRecordatorio() {
            return idDiaRecordatorio;
        }

        public void setIdDiaRecordatorio(int idDiaRecordatorio) {
            this.idDiaRecordatorio = idDiaRecordatorio;
        }

        public int getDia() {
            return dia;
        }

        public void setDia(int dia) {
            this.dia = dia;
        }
    }
}
