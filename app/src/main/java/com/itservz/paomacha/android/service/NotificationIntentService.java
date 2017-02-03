package com.itservz.paomacha.android.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
        FirebaseDatabaseService.getInstance(null).getPaoNotification(this, pf.getLastNews(), true);
    }

    @Override
    public void onNewPao(Pao pao) {
        Log.d(TAG, "onNewPao");
        if (firstOneIsLatestNews) {
            pf.setLastNews(pao.createdOn);
            firstOneIsLatestNews = false;
        }
        Drawable d = getResources().getDrawable(R.mipmap.ic_launcher);
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("PAO MACHA")
                .setAutoCancel(true)
                .setLargeIcon(bitmap)
                .setContentText(pao.title)
                .setSmallIcon(R.drawable.ic_paomacha);

        //Set big text
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle(builder);
        style.setBigContentTitle(pao.title);
        style.bigText(pao.body.split("\n")[0]);

        Intent intent = new Intent(this, PaoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(this));

        Notification mNotificationObject = builder.build();
        //This is to keep the default settings of notification,
        mNotificationObject.defaults |= Notification.DEFAULT_SOUND;
        mNotificationObject.flags |= Notification.FLAG_AUTO_CANCEL;
        //This is to show the ticker text which appear at top.
        mNotificationObject.tickerText = pao.title ;

        final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, mNotificationObject);
    }
}