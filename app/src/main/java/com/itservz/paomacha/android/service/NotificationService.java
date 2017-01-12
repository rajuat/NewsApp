package com.itservz.paomacha.android.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.itservz.paomacha.android.PaoActivity;
import com.itservz.paomacha.android.R;
import com.itservz.paomacha.android.backend.FirebaseDatabaseService;
import com.itservz.paomacha.android.model.Pao;
import com.itservz.paomacha.android.preference.PrefManager;

/**
 * Created by raju.athokpam on 12-01-2017.
 */

public class NotificationService extends Service implements FirebaseDatabaseService.PaoListener {
    private NotificationManager mNM;
    private static final String NOTIFICATION_ID = "notified_pao";
    private int NOTIFICATION = 0;

    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (new PrefManager(this).isNotificationEnabled()) {
            FirebaseDatabaseService.getInstance(null).getPaoLatest(this);
            FirebaseDatabaseService.getInstance(null).getUserPaoLatest(this);
        }
        //showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mNM.cancel(NOTIFICATION);
        Toast.makeText(this, "Notification service is stop.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showNotification(Pao pao) {
        Intent notificationIntent = new Intent(this, PaoActivity.class);
        notificationIntent.putExtra(NOTIFICATION_ID, pao.uuid);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification noti = new Notification.Builder(this)
                .setContentTitle(pao.title)
                .setContentText(pao.body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .build();
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        mNM.notify(-(int) pao.createdOn, noti);
    }

    @Override
    public void onNewPao(Pao pao) {
        showNotification(pao);
    }
}
