package com.grupo3.medrem.data.dto.request;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("correo")
    private String correo;
    
    @SerializedName("password")
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String correo, String password) {
        this.correo = correo;
        this.password = password;
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
} 