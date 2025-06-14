package com.grupo3.medrem.data.mappers;

import com.grupo3.medrem.data.dto.response.UserResponse;
import com.grupo3.medrem.models.User;

public class UserMapper {
    
    public static User fromUserResponse(UserResponse userResponse) {
        if (userResponse == null) {
            return null;
        }
        
        User user = new User();
        user.setId(userResponse.getIdUsuario());
        user.setNombre(userResponse.getNombre());
        user.setApellidoPaterno(userResponse.getApellidoPaterno());
        user.setApellidoMaterno(userResponse.getApellidoMaterno());
        user.setCorreo(userResponse.getCorreo());
        user.setTelefono(userResponse.getTelefono());
        user.setFechaNacimiento(userResponse.getFechaNacimiento());
        user.setToken(userResponse.getToken());
        
        return user;
    }
} 