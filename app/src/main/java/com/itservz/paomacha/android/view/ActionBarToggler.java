package com.itservz.paomacha.android.view;

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
        appBar.animate().translationY(-appBar.getHeight()).setInterpolator(new LinearInterpolator()).setDuration(300);
        //toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
    }

    public static void hideBottomBar(final View appBar, final View fab) {
        appBar.animate().translationY(appBar.getHeight()).setInterpolator(new LinearInterpolator()).setDuration(300);
        if (fab != null)
            fab.animate().translationY(appBar.getHeight()).setInterpolator(new LinearInterpolator()).setDuration(300);
    }

    public static void showAppBar(final View appBar) {
        //toolbar.animate().translationY(0).alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator());
        appBar.animate().translationY(0).setInterpolator(new LinearInterpolator()).setDuration(300).setListener(new AnimatorListenerAdapter() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onAnimationEnd(Animator animation) {
                appBar.setElevation(APPBAR_ELEVATION);
            }
        });
    }
}
