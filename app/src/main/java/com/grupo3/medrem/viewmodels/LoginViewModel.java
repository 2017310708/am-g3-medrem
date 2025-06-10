package com.grupo3.medrem.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.grupo3.medrem.models.LoginResult;
import com.grupo3.medrem.repositories.UserRepository;

public class LoginViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    public LoginViewModel() {
        userRepository = new UserRepository();
    }

    public void login(String correo, String password) {
        userRepository.login(correo, password, new UserRepository.LoginCallback() {
            @Override
            public void onSuccess(String message) {
                loginResult.postValue(new LoginResult(true, message));
            }

            @Override
            public void onError(String message) {
                loginResult.postValue(new LoginResult(false, message));
            }
        });
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }
}