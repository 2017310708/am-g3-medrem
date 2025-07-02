package com.grupo3.medrem.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.grupo3.medrem.data.dto.request.NewReminderRequest;
import com.grupo3.medrem.data.dto.response.ReminderDetailResponse;
import com.grupo3.medrem.models.Reminder;
import com.grupo3.medrem.repositories.ReminderRepository;

import java.util.List;

public class ReminderViewModel extends ViewModel {
    private final ReminderRepository reminderRepository;
    private final MutableLiveData<ReminderState> reminderState = new MutableLiveData<>();
    private final MutableLiveData<RemindersListState> remindersListState = new MutableLiveData<>();

    public ReminderViewModel() { reminderRepository = new ReminderRepository(); }

    public void saveReminder(NewReminderRequest request) {
        reminderRepository.newReminder(request, new ReminderRepository.AuthCallback() {

            @Override
            public void onSuccess(Reminder reminder) {
                reminderState.postValue(new ReminderState(true, null, reminder));
            }
            @Override
            public void onError(String message) {
                reminderState.postValue(new ReminderState(false, message, null));
            }
        });
    }

    public void loadRemindersByUser(int idUsuario) {
        reminderRepository.getRemindersByUser(idUsuario, new ReminderRepository.LoadRemindersCallback() {
            @Override
            public void onSuccess(List<ReminderDetailResponse> reminders) {
                remindersListState.postValue(new RemindersListState(true, null, reminders));
            }

            @Override
            public void onError(String message) {
                remindersListState.postValue(new RemindersListState(false, message, null));
            }
        });
    }

    public MutableLiveData<RemindersListState> getRemindersListState() {
        return remindersListState;
    }

    public static class ReminderState {
        private final boolean success;
        private final String message;
        private final Reminder reminder;

        public ReminderState(boolean success, String message, Reminder reminder) {
            this.success = success;
            this.message = message;
            this.reminder = reminder;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public Reminder getReminder() {
            return reminder;
        }
    }

    public static class RemindersListState {
        private final boolean success;
        private final String message;
        private final List<ReminderDetailResponse> reminders;

        public RemindersListState(boolean success, String message, List<ReminderDetailResponse> reminders) {
            this.success = success;
            this.message = message;
            this.reminders = reminders;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public List<ReminderDetailResponse> getReminders() {
            return reminders;
        }
    }
}