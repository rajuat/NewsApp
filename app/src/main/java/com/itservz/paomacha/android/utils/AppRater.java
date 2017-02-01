package com.itservz.paomacha.android.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itservz.paomacha.android.R;

/**
 * Created by Raju on 1/14/2017.
 */

public class AppRater {
    private final static String APP_TITLE = "PAO MACHA";
    private final static String APP_PNAME = "com.itservz.paomacha.android";


    private final static int DAYS_UNTIL_PROMPT = 5;
    private final static int LAUNCHES_UNTIL_PROMPT = 12;
    private static SharedPreferences prefs;

    public static void appLaunched(Activity activity) {
        //activity.getApplicationContext();
        prefs = activity.getSharedPreferences("apprater", 0);
        if (prefs.getBoolean("dontshowagain", false)) {
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }
        editor.commit();
        //showRateDialog(activity);
        // Wait at least n days before opening dialog
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(activity);
            }
        }

    }

    public static void showRateDialog(final Context mContext) {
        final SharedPreferences.Editor editor = prefs.edit();
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle("Rate " + APP_TITLE);

        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.VERTICAL);

        ScreenSizeScaler sss = new ScreenSizeScaler(mContext.getResources());
        int padding = sss.getdpAspixel(16);
        TextView tv = new TextView(mContext);
        tv.setTextColor(mContext.getResources().getColor(R.color.primary_dark));
        tv.setText("If you enjoy using " + APP_TITLE + ", please take a moment to rate it. Thanks for your support!");
        tv.setPadding(padding, padding, padding, padding);
        ll.addView(tv);

        Button b1 = new Button(mContext);
        b1.setText("Rate " + APP_TITLE);
        b1.setPadding(padding, padding, padding, padding);
        b1.setBackgroundColor(mContext.getResources().getColor(R.color.primary_dark));
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                }
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                dialog.dismiss();
            }
        });
        ll.addView(b1);

        Button b2 = new Button(mContext);
        b2.setText("Remind me later");
        b2.setPadding(padding, padding, padding, padding);
        b2.setBackgroundColor(mContext.getResources().getColor(R.color.primary));
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editor != null) {
                    editor.clear();
                }
                dialog.dismiss();
            }
        });
        ll.addView(b2);

        Button b3 = new Button(mContext);
        b3.setText("No, thanks");
        b3.setPadding(padding, padding, padding, padding);
        b3.setBackgroundColor(mContext.getResources().getColor(R.color.accent));
        b3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);

                }
                dialog.dismiss();
            }
        });
        ll.addView(b3);

        dialog.setContentView(ll);
        dialog.show();
        editor.commit();
    }
}