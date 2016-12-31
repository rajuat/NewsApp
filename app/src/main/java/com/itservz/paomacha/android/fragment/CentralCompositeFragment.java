package com.itservz.paomacha.android.fragment;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itservz.paomacha.android.ActionBarToggler;
import com.itservz.paomacha.android.PaoActivity;
import com.itservz.paomacha.android.R;
import com.itservz.paomacha.android.adapter.FragmentsClassesPagerAdapter;
import com.itservz.paomacha.android.event.EventBus;
import com.itservz.paomacha.android.event.PageChangedEvent;

import java.util.ArrayList;


/**
 * Fragment to manage the horizontal pages (left, central, right) of the 5 pages application navigation (top, center,
 * bottom, left, right).
 */
public class CentralCompositeFragment extends Fragment {
    static final String TAG = "CentralCompositeFrag";
    private ViewPager mHorizontalPager;
	private int mCentralPageIndex = 0;
    private int leftFrag = 0;
    private int rightFrag = 2;
    private OnPageChangeListener mPagerChangeListener = new OnPageChangeListener() {
        PaoActivity paoActivity = (PaoActivity) getActivity();

        @Override
		public void onPageSelected(int position) {
			EventBus.getInstance().post(new PageChangedEvent(mCentralPageIndex == position));
            if (position == leftFrag) {
                Log.d(TAG, "hide bars");
                AppBarLayout appBarLayout = (AppBarLayout) paoActivity.findViewById(R.id.appbar);
                Toolbar toolbarBottom = (Toolbar) paoActivity.findViewById(R.id.toolbarBottom);
                ActionBarToggler.hideAppBar(appBarLayout);
                ActionBarToggler.hideToolBar(toolbarBottom);
                paoActivity.FULLSCREEN = true;
            }
            if (position == rightFrag) {
                //getActivity().getWindow().requestFeature(Window.FEATURE_PROGRESS);
            }
        }

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_composite_central, container, false);
		findViews(fragmentView);
		return fragmentView;
	}

	private void findViews(View fragmentView) {
		mHorizontalPager = (ViewPager) fragmentView.findViewById(R.id.fragment_composite_central_pager);
		initViews();
	}

	private void initViews() {
		populateHozizontalPager();
		mHorizontalPager.setCurrentItem(mCentralPageIndex);
		mHorizontalPager.setOnPageChangeListener(mPagerChangeListener);
	}

	private void populateHozizontalPager() {
		ArrayList<Class<? extends Fragment>> pages = new ArrayList<Class<? extends Fragment>>();
		pages.add(LeftFragment.class);
		pages.add(CentralFragment.class);
		pages.add(RightFragment.class);
		mCentralPageIndex = pages.indexOf(CentralFragment.class);
		mHorizontalPager.setAdapter(new FragmentsClassesPagerAdapter(getChildFragmentManager(), getActivity(), pages));
	}
}
