package com.itservz.paomacha.android.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.itservz.paomacha.android.PaoActivity;
import com.itservz.paomacha.android.R;
import com.itservz.paomacha.android.backend.FirebaseDatabaseService;
import com.itservz.paomacha.android.model.Pao;
import com.itservz.paomacha.android.preference.PrefManager;

/**
 * Created by Raju on 1/14/2017.
 */

public class NotificationIntentService extends IntentService implements FirebaseDatabaseService.PaoListener {

    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";
    private static final String TAG = "NotificationIntentSer";
    private PrefManager pf;

    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
    }

    public static Intent createIntentStartNotificationService(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static Intent createIntentDeleteNotification(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        pf = new PrefManager(this);
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                processStartNotification();
            }
            if (ACTION_DELETE.equals(action)) {
                processDeleteNotification(intent);
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void processDeleteNotification(Intent intent) {
        // Log something?
    }

    boolean firstOneIsLatestNews;

    private void processStartNotification() {
        Log.d(TAG, "processStartNotification");
        firstOneIsLatestNews = true;
        FirebaseDatabaseService.getInstance(null).getPaoNotification(this, pf.getLastNews());
    }

    @Override
    public void onNewPao(Pao pao) {
        Log.d(TAG, "onNewPao");
        if (firstOneIsLatestNews) {
            pf.setLastNews(pao.createdOn);
            firstOneIsLatestNews = false;
        }
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(pao.title)
                .setAutoCancel(true)
                //.setColor(getResources().getColor(R.color.accent))
                .setContentText(pao.body)
                .setSmallIcon(R.mipmap.ic_launcher);
        Intent intent = new Intent(this, PaoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(this));

        final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());
    }
}