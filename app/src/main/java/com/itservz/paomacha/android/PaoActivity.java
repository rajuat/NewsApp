package com.itservz.paomacha.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import com.itservz.paomacha.android.backend.FirebaseDatabaseService;
import com.itservz.paomacha.android.event.EventBus;
import com.itservz.paomacha.android.event.PageChangedEvent;
import com.itservz.paomacha.android.fragment.CentralCompositeFragment;
import com.itservz.paomacha.android.model.Pao;
import com.itservz.paomacha.android.preference.PrefManager;
import com.itservz.paomacha.android.service.NotificationEventReceiver;
import com.itservz.paomacha.android.utils.AppRater;
import com.itservz.paomacha.android.utils.GpsHelper;
import com.itservz.paomacha.android.view.VerticalPager;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class PaoActivity extends AppCompatActivity implements FirebaseDatabaseService.PaoListener {
    public boolean FULLSCREEN;
    public static String CATEGORY_TAG = "category_TAG";
    static final String TAG = "PaoActivity";
    private VerticalPager mVerticalPager;
    private FragmentManager fragmentManager;
    private Toolbar toolbar;
    private List<String> fragmentTags = null;
    private ArrayList<String> categoriesFromDB = new ArrayList<>();
    private static final int CENTRAL_PAGE_INDEX = 0;
    private boolean showAllNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_pao);
        FirebaseDatabaseService.getCategories(categoriesFromDB);
        fragmentTags = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.toolbarTop);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        toolbar.setTitleTextColor(getResources().getColor(R.color.primary_dark));

        mVerticalPager = (VerticalPager) findViewById(R.id.activity_main_vertical_pager);
        fragmentManager = getSupportFragmentManager();

        ImageButton post = (ImageButton) findViewById(R.id.fab);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaoActivity.this, PostActivity.class);
                intent.putStringArrayListExtra(CATEGORY_TAG, categoriesFromDB);
                startActivity(intent);
            }
        });
        showAllNews = true;
        //gps
        GpsHelper.turnGPSOn(getApplicationContext());
        if (new PrefManager(this).isNotificationEnabled()) {
            NotificationEventReceiver.setupAlarm(getApplicationContext());
        }
        AppRater.appLaunched(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        toolbar.setTitle(getTitle());
        if (showAllNews) {
            FirebaseDatabaseService.getInstance(null).getPaoLatest(this);
            FirebaseDatabaseService.getInstance(null).getUserPaoLatest(this);
        }
        EventBus.getInstance().register(this);
    }

    static final String NOTIFICATION_ID = "notified_pao";
    @Override
    public void onNewPao(Pao pao) {
        Log.d(TAG, "onNewPao");
        if (pao == null || pao.uuid == null || pao.title == null || pao.body == null)
            return;
        addNewPao(pao);
    }

    void addNewPao(Pao pao) {
        if (fragmentTags.contains(pao.uuid)) {
            Log.d(TAG, "duplicates");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("pao", pao);
        CentralCompositeFragment centralCompositeFragment = new CentralCompositeFragment();
        centralCompositeFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.activity_main_vertical_pager, centralCompositeFragment, pao.uuid);
        fragmentTransaction.commit();
        fragmentTags.add(pao.uuid);
        snapPageWhenLayoutIsReady();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_pao, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
        switch (requestCode) {
            case (RETURN_FROM_SETTING): {
                if (resultCode == Activity.RESULT_OK) {
                    //clean the fragments
                    List<String> tempTags = new ArrayList<>();
                    for (String tag : fragmentTags) {
                        Fragment fragment = fragmentManager.findFragmentByTag(tag);
                        if (fragment != null) {
                            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                            tempTags.add(tag);
                        }
                    }
                    fragmentTags.removeAll(tempTags);

                    PrefManager prefManager = new PrefManager(this);

                    String category = data.getStringExtra(SettingActivity.CATEGORY);
                    if (category.equals(getResources().getString(R.string.category_allnews))) {
                        showAllNews = true;
                    } else if (category.equals(getResources().getString(R.string.category_trending))) {
                        FirebaseDatabaseService.getInstance("").getTrendingPao(this);
                        showAllNews = false;
                    } else if (category.equals(getResources().getString(R.string.category_fromuser))) {
                        FirebaseDatabaseService.getInstance("").getUserPaoLatest(this);
                        showAllNews = false;
                    } else if (category.equals(getResources().getString(R.string.category_bookmarks))) {
                        FirebaseDatabaseService.getInstance("").getUserTags(this, prefManager.getBookmark());
                        showAllNews = false;
                    } else if (category.equals(getResources().getString(R.string.category_dislikes))) {
                        FirebaseDatabaseService.getInstance("").getUserTags(this, prefManager.getDislike());
                        showAllNews = false;
                    } else if (category.equals(getResources().getString(R.string.category_likes))) {
                        FirebaseDatabaseService.getInstance("").getUserTags(this, prefManager.getLike());
                        showAllNews = false;
                    } else {
                        FirebaseDatabaseService.getInstance("").getPaoForCategory(this, category);
                        showAllNews = false;
                    }
                    setTitle(category);
                }
                break;
            }
        }
    }

    private static final int RETURN_FROM_SETTING = 1;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Log.d(TAG, "to settings");
                    Intent intent = new Intent(PaoActivity.this, SettingActivity.class);
                    intent.putStringArrayListExtra(CATEGORY_TAG, categoriesFromDB);
                    startActivityForResult(intent, RETURN_FROM_SETTING);
                    return true;
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    private void snapPageWhenLayoutIsReady() {
        /*
         * VerticalPager is not fully initialized at the moment, so we want to snap to the central page only when it
		 * layout and measure all its pages.
		 */
        mVerticalPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                mVerticalPager.snapToPage(CENTRAL_PAGE_INDEX, VerticalPager.PAGE_SNAP_DURATION_INSTANT);

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                    // recommended removeOnGlobalLayoutListener method is available since API 16 only
                    mVerticalPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                else
                    removeGlobalOnLayoutListenerForJellyBean(mVerticalPager);
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            private void removeGlobalOnLayoutListenerForJellyBean(final View pageView) {
                pageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
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

    private boolean doubleBackToExitPressedOnce;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
