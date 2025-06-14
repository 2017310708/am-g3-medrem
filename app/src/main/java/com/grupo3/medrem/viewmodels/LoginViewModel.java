package com.grupo3.medrem.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.grupo3.medrem.models.User;
import com.grupo3.medrem.repositories.UserRepository;
import com.grupo3.medrem.utils.ValidationUtils;

public class LoginViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<LoginState> loginState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public LoginViewModel() {
        userRepository = new UserRepository();
    }

    public void login(String correo, String password) {
        if (!validateInput(correo, password)) {
            return;
        }

        isLoading.setValue(true);
        userRepository.login(correo, password, new UserRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                isLoading.postValue(false);
                loginState.postValue(new LoginState(true, null, user));
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                loginState.postValue(new LoginState(false, message, null));
            }
        });
    }

    private boolean validateInput(String correo, String password) {
        if (correo == null || correo.isEmpty()) {
            loginState.setValue(new LoginState(false, "El correo es obligatorio", null));
            return false;
        }

        if (!ValidationUtils.isValidEmail(correo)) {
            loginState.setValue(new LoginState(false, "El formato del correo es inválido", null));
            return false;
        }

        if (password == null || password.isEmpty()) {
            loginState.setValue(new LoginState(false, "La contraseña es obligatoria", null));
            return false;
        }

        return true;
    }

    public LiveData<LoginState> getLoginState() {
        return loginState;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public static class LoginState {
        private final boolean success;
        private final String message;
        private final User user;

        public LoginState(boolean success, String message, User user) {
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