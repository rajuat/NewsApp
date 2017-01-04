package com.itservz.paomacha.android;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageButton;

import com.itservz.paomacha.android.event.EventBus;
import com.itservz.paomacha.android.event.PageChangedEvent;
import com.itservz.paomacha.android.fragment.CentralCompositeFragment;
import com.itservz.paomacha.android.preference.PrefManager;
import com.itservz.paomacha.android.view.VerticalPager;
import com.itservz.paomacha.utils.Share;
import com.squareup.otto.Subscribe;

public class PaoActivity extends AppCompatActivity {

    static final String TAG = "PaoActivity";
    /**
     * Start page index. 0 - top page, 1 - central page, 2 - bottom page.
     */
    private static final int CENTRAL_PAGE_INDEX = 1;
    public boolean FULLSCREEN;
    private VerticalPager mVerticalPager;


    boolean refreshed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        setContentView(R.layout.activity_pao);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarTop);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        toolbar.setTitleTextColor(getResources().getColor(R.color.primary_dark));

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.activity_main_vertical_pager, new CentralCompositeFragment());
        fragmentTransaction.add(R.id.activity_main_vertical_pager, new CentralCompositeFragment());
        fragmentTransaction.add(R.id.activity_main_vertical_pager, new CentralCompositeFragment());
        fragmentTransaction.add(R.id.activity_main_vertical_pager, new CentralCompositeFragment());
        fragmentTransaction.add(R.id.activity_main_vertical_pager, new CentralCompositeFragment());
        fragmentTransaction.commit();

        findViews();

        ImageButton post = (ImageButton) findViewById(R.id.fab);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaoActivity.this, PostActivity.class);
                startActivity(intent);
            }
        });


    }



    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_pao, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh) {
            //toggle
            if(refreshed){
                item.setIcon(getResources().getDrawable(R.drawable.ic_refresh_black_24dp));
                refreshed = false;
            } else {
                item.setIcon(getResources().getDrawable(R.drawable.ic_arrow_upward_black_24dp));
                refreshed = true;
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void findViews() {
        mVerticalPager = (VerticalPager) findViewById(R.id.activity_main_vertical_pager);
        snapPageWhenLayoutIsReady(mVerticalPager, CENTRAL_PAGE_INDEX);
    }

    private void snapPageWhenLayoutIsReady(final View pageView, final int page) {
        /*
		 * VerticalPager is not fully initialized at the moment, so we want to snap to the central page only when it
		 * layout and measure all its pages.
		 */
        pageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                mVerticalPager.snapToPage(page, VerticalPager.PAGE_SNAP_DURATION_INSTANT);

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                    // recommended removeOnGlobalLayoutListener method is available since API 16 only
                    pageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                else
                    removeGlobalOnLayoutListenerForJellyBean(pageView);
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            private void removeGlobalOnLayoutListenerForJellyBean(final View pageView) {
                pageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        EventBus.getInstance().unregister(this);
        super.onPause();
    }

    @Subscribe
    public void onLocationChanged(PageChangedEvent event) {
        mVerticalPager.setPagingEnabled(event.hasVerticalNeighbors());
    }
}
