package com.grupo3.medrem.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.grupo3.medrem.models.User;
import com.grupo3.medrem.repositories.UserRepository;
import com.grupo3.medrem.utils.ValidationUtils;

public class RegisterViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<RegisterState> registerState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public RegisterViewModel() {
        userRepository = new UserRepository();
    }

    public void register(String nombre, String apellidoPaterno, String apellidoMaterno,
                        String correo, String password, String telefono, String fechaNacimiento) {
        
        if (!validateInput(nombre, apellidoPaterno, apellidoMaterno, correo, password, telefono, fechaNacimiento)) {
            return;
        }

        // Formatear 'fono
        String formattedPhone = ValidationUtils.formatPhone(telefono);
        
        isLoading.setValue(true);
        userRepository.register(nombre, apellidoPaterno, apellidoMaterno, correo, password, formattedPhone, fechaNacimiento, new UserRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                isLoading.postValue(false);
                registerState.postValue(new RegisterState(true, null, user));
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                registerState.postValue(new RegisterState(false, message, null));
            }
        });
    }

    private boolean validateInput(String nombre, String apellidoPaterno, String apellidoMaterno, String correo, String password, String telefono, String fechaNacimiento) {
        
        if (!ValidationUtils.isValidName(nombre)) {
            registerState.setValue(new RegisterState(false, "El nombre debe tener al menos 3 caracteres", null));
            return false;
        }
        
        if (!ValidationUtils.isValidName(apellidoPaterno)) {
            registerState.setValue(new RegisterState(false, "El apellido paterno debe tener al menos 3 caracteres", null));
            return false;
        }
        
        if (!ValidationUtils.isValidName(apellidoMaterno)) {
            registerState.setValue(new RegisterState(false, "El apellido materno debe tener al menos 3 caracteres", null));
            return false;
        }
        
        if (!ValidationUtils.isValidEmail(correo)) {
            registerState.setValue(new RegisterState(false, "El formato del correo es inválido", null));
            return false;
        }
        
        if (!ValidationUtils.isValidPassword(password)) {
            registerState.setValue(new RegisterState(false, "La contraseña debe tener al menos 8 caracteres", null));
            return false;
        }
        
        if (telefono != null && !telefono.isEmpty() && !ValidationUtils.isValidPhone(telefono)) {
            registerState.setValue(new RegisterState(false, "El teléfono debe tener entre 7 y 9 dígitos", null));
            return false;
        }
        
        if (!ValidationUtils.isValidBirthDate(fechaNacimiento)) {
            registerState.setValue(new RegisterState(false, "La fecha de nacimiento no es válida", null));
            return false;
        }
        
        return true;
    }

    public LiveData<RegisterState> getRegisterState() {
        return registerState;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public static class RegisterState {
        private final boolean success;
        private final String message;
        private final User user;

        public RegisterState(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public User getUser() {
            return user;
        }
    }
} 