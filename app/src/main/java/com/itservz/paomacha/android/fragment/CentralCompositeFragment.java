package com.itservz.paomacha.android.fragment;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itservz.paomacha.android.PaoActivity;
import com.itservz.paomacha.android.R;
import com.itservz.paomacha.android.adapter.FragmentsClassesPagerAdapter;
import com.itservz.paomacha.android.event.EventBus;
import com.itservz.paomacha.android.event.PageChangedEvent;
import com.itservz.paomacha.android.model.Pao;
import com.itservz.paomacha.android.view.ActionBarToggler;

import java.util.ArrayList;


/**
 * Fragment to manage the horizontal pages (left, central, right) of the 5 pages application navigation (top, center,
 * bottom, left, right).
 */
public class CentralCompositeFragment extends Fragment {
    static final String TAG = "CentralCompositeFrag";
	private PaoActivity paoActivity;
	private View fragmentView;
    private ViewPager mHorizontalPager;
	private int mCentralPageIndex = 0;
    private int leftFrag = 0;
    private int rightFrag = 2;

	private OnPageChangeListener mPagerChangeListener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int position) {
			EventBus.getInstance().post(new PageChangedEvent(mCentralPageIndex == position));
            if (position == rightFrag || position == leftFrag) {
                Log.d(TAG, "hide bars");
                AppBarLayout appBarLayout = (AppBarLayout) paoActivity.findViewById(R.id.appbar);
                View toolbarBottom = fragmentView.findViewById(R.id.toolbarBottom);
				View fab = paoActivity.findViewById(R.id.fab);
                ActionBarToggler.hideAppBar(appBarLayout);
                ActionBarToggler.hideBottomBar(toolbarBottom, fab);
                paoActivity.FULLSCREEN = true;
            }
        }

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

		@Override
		public void onPageScrollStateChanged(int state) {}
	};
	private Pao pao;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		pao = (Pao) this.getArguments().getSerializable("pao");
		paoActivity = (PaoActivity) getActivity();
		fragmentView = inflater.inflate(R.layout.fragment_composite_central, container, false);
		findViews();
		return fragmentView;
	}

	private void findViews() {
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
		mHorizontalPager.setAdapter(new FragmentsClassesPagerAdapter(getChildFragmentManager(), getActivity(), pages, pao));
	}
}
