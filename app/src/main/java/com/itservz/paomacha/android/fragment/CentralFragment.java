package com.itservz.paomacha.android.fragment;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.itservz.paomacha.android.PaoActivity;
import com.itservz.paomacha.android.R;
import com.itservz.paomacha.android.preference.PrefManager;
import com.itservz.paomacha.android.view.ActionBarToggler;
import com.itservz.paomacha.utils.Share;


/**
 * Fragment to manage the central page of the 5 pages application navigation (top, center, bottom, left, right).
 */
public class CentralFragment extends Fragment {
    static final String TAG = "CentralFragment";
    private PrefManager prefManager;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private PaoActivity paoActivity = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_central, container, false);
        paoActivity = (PaoActivity) getActivity();
        fragmentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppBarLayout appBarLayout = (AppBarLayout) paoActivity.findViewById(R.id.appbar);
                View toolbarBottom = fragmentView.findViewById(R.id.toolbarBottom);
                View fab = paoActivity.findViewById(R.id.fab);
                if (paoActivity.FULLSCREEN) {
                    Log.d(TAG, "fragmentView.setOnClickListener show");
                    ActionBarToggler.showAppBar(appBarLayout);
                    ActionBarToggler.showAppBar(toolbarBottom);
                    ActionBarToggler.showAppBar(fab);
                    paoActivity.FULLSCREEN = false;
                } else {
                    Log.d(TAG, "fragmentView.setOnClickListener hide");
                    ActionBarToggler.hideAppBar(appBarLayout);
                    ActionBarToggler.hideBottomBar(toolbarBottom, fab);
                    paoActivity.FULLSCREEN = true;
                }

            }
        });

        prefManager = new PrefManager(paoActivity);
        final ImageButton bookmark = (ImageButton) fragmentView.findViewById(R.id.bookmark);
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(prefManager.hasBookmark("newsid")){
                    bookmark.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark_black_24dp));
                    prefManager.removeBookmark("newsid");
                } else {
                    bookmark.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark_border_black_24dp));
                    prefManager.addBookmark("newsid");
                }
            }
        });

        final View likeContainer = fragmentView.findViewById(R.id.like_container);
        final ImageButton like = (ImageButton) fragmentView.findViewById(R.id.like);
        final TextView likeCount = (TextView) fragmentView.findViewById(R.id.like_count);
        likeContainer.setOnClickListener(new View.OnClickListener() {
            int count = Integer.parseInt(likeCount.getText().toString());
            @Override
            public void onClick(View view) {
                if(prefManager.hasLike("newsid")){
                    like.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_up_pdark_24dp));
                    count ++;
                    likeCount.setText(""+count);
                    prefManager.removeLike("newsid");
                } else {
                    like.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_up_p_24dp));
                    count --;
                    likeCount.setText(""+count);
                    prefManager.addLike("newsid");
                }
            }
        });

        final View dislikeContainer = fragmentView.findViewById(R.id.dislike_container);
        final ImageButton dislike = (ImageButton) fragmentView.findViewById(R.id.dislike);
        final TextView dislikeCount = (TextView) fragmentView.findViewById(R.id.dislike_count);
        dislikeContainer.setOnClickListener(new View.OnClickListener() {
            int count = Integer.parseInt(dislikeCount.getText().toString());
            @Override
            public void onClick(View view) {
                if(prefManager.hasDislike("newsid")){
                    dislike.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_down_pdark_24dp));
                    count ++;
                    dislikeCount.setText(""+count);
                    prefManager.removeDislike("newsid");
                } else {
                    dislike.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_down_p_24dp));
                    count --;
                    dislikeCount.setText(""+count);
                    prefManager.addDislike("newsid");
                }
            }
        });

        ImageButton share = (ImageButton) fragmentView.findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(paoActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(paoActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user *asynchronously* -- don't block this thread waiting for the user's response! After the user sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(paoActivity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an app-defined int constant. The callback method gets the result of the request.
                    }
                } else {
                    new Share(paoActivity).getScreenShot().store("abc.jpg").shareImage("abc.jpg");
                }
            }
        });

        return fragmentView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new Share(paoActivity).getScreenShot().store("abc.jpg").shareImage("abc.jpg");
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other permissions this app might request
        }
    }


}
