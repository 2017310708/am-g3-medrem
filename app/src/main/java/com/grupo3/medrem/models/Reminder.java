package com.grupo3.medrem.models;

public class Reminder {
    private int idReminder;
    private int idUsuario;
    private int idMedicamento;
    private int idFrecuencia;
    private String fechaInicio;
    private String fechaFin;
    private String hora;
    private String notas;

    public Reminder() {
    }

    public Reminder(int idReminder, int idUsuario, int idMedicamento, int idFrecuencia, String fechaInicio, String fechaFin, String hora, String notas) {
        this.idReminder = idReminder;
        this.idUsuario = idUsuario;
        this.idMedicamento = idMedicamento;
        this.idFrecuencia = idFrecuencia;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.hora = hora;
        this.notas = notas;
    }

    public int getIdReminder() {
        return idReminder;
    }

    public void setIdReminder(int idReminder) {
        this.idReminder = idReminder;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdMedicamento() {
        return idMedicamento;
    }

    public void setIdMedicamento(int idMedicamento) {
        this.idMedicamento = idMedicamento;
    }

    public int getIdFrecuencia() {
        return idFrecuencia;
    }

    public void setIdFrecuencia(int idFrecuencia) {
        this.idFrecuencia = idFrecuencia;
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
}