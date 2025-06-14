package com.grupo3.medrem.data.dto.request;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class RegisterRequest {
    @SerializedName("nombre")
    private String nombre;
    
    @SerializedName("apellidoPaterno")
    private String apellidoPaterno;
    
    @SerializedName("apellidoMaterno")
    private String apellidoMaterno;
    
    @SerializedName("correo")
    private String correo;
    
    @SerializedName("password")
    private String password;
    
    @SerializedName("telefono")
    private String telefono;
    
    @SerializedName("fechaNacimiento")
    private String fechaNacimiento;

    public RegisterRequest() {
    }

    public RegisterRequest(String nombre, String apellidoPaterno, String apellidoMaterno, String correo, String password, String telefono, String fechaNacimiento) {
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.correo = correo;
        this.password = password;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getapellidoPaterno() {
        return apellidoPaterno;
    }

    public void setapellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
} 