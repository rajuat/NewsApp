package com.itservz.paomacha.android.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itservz.paomacha.android.R;
import com.itservz.paomacha.android.preference.PrefManager;

/**
 * Created by Raju on 1/14/2017.
 */

public class AppRater {
    private final static String APP_TITLE = "PAO MACHA";
    private final static String APP_PNAME = "com.itservz.paomacha.android";


    private final static int DAYS_UNTIL_PROMPT = 4;
    private final static int LAUNCHES_UNTIL_PROMPT = 12;
    private PrefManager pf;
    private Activity mContext;

    public AppRater(Activity activity) {
        this.mContext = activity;
        pf = new PrefManager(activity);
    }

    public void appLaunched() {
        if (pf.isDontShowAgain()) {
            return;
        }

        long launch_count = pf.getLaunchCount() + 1;
        pf.setLaunchCount(launch_count);

        Long date_firstLaunch = pf.getDateFirstLaunch();
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            pf.setDateFirstLaunch(date_firstLaunch);
        }
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog();
            }
        }

    }

    private void showRateDialog() {
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
                pf.setDontShowAgain(true);
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
                reset();
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
                pf.setDontShowAgain(true);
                dialog.dismiss();
            }
        });
        ll.addView(b3);

        dialog.setContentView(ll);
        dialog.show();

    }

    private void reset() {
        pf.setDateFirstLaunch(0);
        pf.setDontShowAgain(false);
        pf.setLaunchCount(0);
    }
}