package com.itservz.paomacha.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Raju on 12/31/2016.
 */

public class ActionBarToggler {

    private static final float APPBAR_ELEVATION = 14f;

    public static void hideAppBar(final View appBar) {
        appBar.animate().translationY(-appBar.getHeight()).setInterpolator(new LinearInterpolator()).setDuration(500);

    }

    public static void hideToolBar(final View appBar) {
        appBar.animate().translationY(appBar.getHeight()).setInterpolator(new LinearInterpolator()).setDuration(500);

    }

    public static void showAppBar(final View appBar) {
        appBar.animate().translationY(0).setInterpolator(new LinearInterpolator()).setDuration(500).setListener(new AnimatorListenerAdapter() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onAnimationEnd(Animator animation) {
                appBar.setElevation(APPBAR_ELEVATION);
            }
        });
    }
}
