package com.itservz.paomacha.android.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.itservz.paomacha.android.fragment.CentralFragment;
import com.itservz.paomacha.android.fragment.LeftFragment;
import com.itservz.paomacha.android.fragment.RightFragment;

import java.util.List;

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
        if (position == 0) {
            return new LeftFragment();
        } else if (position == 1) {
            return new CentralFragment();
        } else if (position == 2) {
            return new RightFragment();
        }
        return null;
        //return Fragment.instantiate(mContext, mPagesClasses.get(position).getName());
    }

	@Override
	public int getCount() {
		return mPagesClasses.size();
	}
}
