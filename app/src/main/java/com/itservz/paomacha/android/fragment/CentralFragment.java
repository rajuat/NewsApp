package com.itservz.paomacha.android.fragment;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itservz.paomacha.android.PaoActivity;
import com.itservz.paomacha.android.R;
import com.itservz.paomacha.android.view.ActionBarToggler;


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
                View toolbarBottom = paoActivity.findViewById(R.id.toolbarBottom);
                if (paoActivity.FULLSCREEN) {
                    Log.d(TAG, "fragmentView.setOnClickListener show");
                    ActionBarToggler.showAppBar(appBarLayout);
                    ActionBarToggler.showAppBar(toolbarBottom);
                    paoActivity.FULLSCREEN = false;
                } else {
                    Log.d(TAG, "fragmentView.setOnClickListener hide");
                    ActionBarToggler.hideAppBar(appBarLayout);
                    ActionBarToggler.hideToolBar(toolbarBottom);
                    paoActivity.FULLSCREEN = true;
                }

            }
        });
        return fragmentView;
    }


}
