package com.itservz.paomacha.android.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

/**
 * Adapter for {@link ViewPager} that will populated from the collection of Fragments classes. Objects of that classes
 * will be instantiated on demand and used as a pages views.
 */
public class FragmentsClassesPagerAdapter extends FragmentPagerAdapter {

	public FragmentsClassesPagerAdapter(FragmentManager fragmentManager, Context context,
			List<Class<? extends Fragment>> pages) {
		super(fragmentManager);
		mPagesClasses = pages;
		mContext = context;
	}

	private List<Class<? extends Fragment>> mPagesClasses;
	private Context mContext;

	@Override
	public Fragment getItem(int position) {
		return Fragment.instantiate(mContext, mPagesClasses.get(position).getName());
	}

	@Override
	public int getCount() {
		return mPagesClasses.size();
	}
}
