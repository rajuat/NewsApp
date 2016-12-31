package com.itservz.paomacha.android.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.itservz.paomacha.android.PaoActivity;
import com.itservz.paomacha.android.R;


/**
 * Fragment to manage the central page of the 5 pages application navigation (top, center, bottom, left, right).
 */
public class CentralFragment extends Fragment {
    static final String TAG = "CentralFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_central, container, false);
        final PaoActivity paoActivity = (PaoActivity) getActivity();
        fragmentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppBarLayout appBarLayout = (AppBarLayout) paoActivity.findViewById(R.id.appbar);
                Toolbar toolbarBottom = (Toolbar) paoActivity.findViewById(R.id.toolbarBottom);
                if (paoActivity.FULLSCREEN) {
                    Log.d(TAG, "fragmentView.setOnClickListener show");
                    showAppBar(appBarLayout);
                    showAppBar(toolbarBottom);
                    paoActivity.FULLSCREEN = false;
                } else {
                    Log.d(TAG, "fragmentView.setOnClickListener hide");
                    hideAppBar(appBarLayout);
                    hideToolBar(toolbarBottom);
                    paoActivity.FULLSCREEN = true;
                }
                //toolbar.animate().translationY(0).alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator());
                //toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
            }
        });
        return fragmentView;
    }

    private static final float APPBAR_ELEVATION = 14f;

    private void hideAppBar(final View appBar) {
        appBar.animate().translationY(-appBar.getHeight()).setInterpolator(new LinearInterpolator()).setDuration(500);

    }

    private void hideToolBar(final View appBar) {
        appBar.animate().translationY(appBar.getHeight()).setInterpolator(new LinearInterpolator()).setDuration(500);

    }

    public void showAppBar(final View appBar) {
        appBar.animate().translationY(0).setInterpolator(new LinearInterpolator()).setDuration(500).setListener(new AnimatorListenerAdapter() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onAnimationEnd(Animator animation) {
                appBar.setElevation(APPBAR_ELEVATION);
            }
        });
    }
}
