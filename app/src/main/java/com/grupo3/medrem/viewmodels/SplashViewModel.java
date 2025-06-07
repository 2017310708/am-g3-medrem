package com.grupo3.medrem.viewmodels;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.grupo3.medrem.R;

public class SplashViewModel extends AndroidViewModel {
    private final MutableLiveData<Boolean> _isSplashTimeFinished = new MutableLiveData<>(false);
    public LiveData<Boolean> isSplashTimeFinished = _isSplashTimeFinished;

    public SplashViewModel(@NonNull Application application) {
        super(application);
        long splashScreenDuration = application.getResources().getInteger(R.integer.splash_screen_duration_ms);

        new Handler(Looper.getMainLooper()).postDelayed(() ->
            _isSplashTimeFinished.setValue(true),
            splashScreenDuration
        );
    }
}