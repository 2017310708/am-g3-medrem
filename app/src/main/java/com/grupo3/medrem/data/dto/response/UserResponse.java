package com.grupo3.medrem.data.dto.response;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("id_usuario")
    private int idUsuario;
    
    @SerializedName("nombre")
    private String nombre;
    
    @SerializedName("apellidoPaterno")
    private String apellidoPaterno;
    
    @SerializedName("apellidoMaterno")
    private String apellidoMaterno;
    
    @SerializedName("correo")
    private String correo;
    
    @SerializedName("telefono")
    private String telefono;
    
    @SerializedName("fechaNacimiento")
    private String fechaNacimiento;
    
    @SerializedName("token")
    private String token;

    public UserResponse() {
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}