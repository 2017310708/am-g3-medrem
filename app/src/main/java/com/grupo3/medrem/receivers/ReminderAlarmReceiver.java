package com.grupo3.medrem.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.grupo3.medrem.R;
import com.grupo3.medrem.activities.DashboardActivity;

public class ReminderAlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "medrem_channel_id";
    private static final String CHANNEL_NAME = "Recordatorios de Medicamentos";
    private static final String CHANNEL_DESC = "Notificaciones para tomar tus medicamentos a tiempo";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ReminderAlarmReceiver", "onReceive() llamado!");

        String medicamento = intent.getStringExtra("medicamento");
        String dosis = intent.getStringExtra("dosis");
        String notas = intent.getStringExtra("notas");

        if (medicamento == null) medicamento = "Medicamento";
        if (dosis == null) dosis = "";
        if (notas == null) notas = "";

        String contentText = medicamento;
        if (!dosis.isEmpty()) contentText += " - " + dosis;
        if (!notas.isEmpty()) contentText += " (" + notas + ")";

        createNotificationChannel(context);

        // es para abrir la app al tocar la notificación
        Intent activityIntent = new Intent(context, DashboardActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent contentPendingIntent = PendingIntent.getActivity(
                context,
                0,
                activityIntent,
                flags
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_bell)
                .setContentTitle("¡Hora de tu medicamento!")
                .setContentText(contentText)
                .setContentIntent(contentPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            int notificationId = (int) System.currentTimeMillis();
            notificationManager.notify(notificationId, builder.build());
            Log.d("ReminderAlarmReceiver", "Notificación lanzada: " + notificationId);
        } else {
            Log.e("ReminderAlarmReceiver", "NotificationManager es null");
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager == null) {
                Log.e("ReminderAlarmReceiver", "NotificationManager es null al crear canal");
                return;
            }

            NotificationChannel existingChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (existingChannel == null) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription(CHANNEL_DESC);
                notificationManager.createNotificationChannel(channel);
                Log.d("ReminderAlarmReceiver", "Canal de notificación creado");
            } else {
                Log.d("ReminderAlarmReceiver", "ℹCanal ya existe");
            }
        }
    }
}
