package com.itservz.paomacha.android.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.itservz.paomacha.android.fragment.CentralFragment;
import com.itservz.paomacha.android.fragment.LeftFragment;
import com.itservz.paomacha.android.fragment.RightFragment;
import com.itservz.paomacha.android.model.Pao;

import java.util.List;

/**
 * Adapter for {@link ViewPager} that will populated from the collection of Fragments classes. Objects of that classes
 * will be instantiated on demand and used as a pages views.
 */
public class FragmentsClassesPagerAdapter extends FragmentPagerAdapter {
	private CentralFragment centralFragment = null;
	private Pao pao;

	public FragmentsClassesPagerAdapter(FragmentManager fragmentManager, Context context, List<Class<? extends Fragment>> pages, Pao pao) {
		super(fragmentManager);
		mPagesClasses = pages;
		mContext = context;
		this.pao = pao;
		Bundle bundle = new Bundle();
		bundle.putSerializable("paof", pao);
		centralFragment = new CentralFragment();
		centralFragment.setArguments(bundle);
	}

	private List<Class<? extends Fragment>> mPagesClasses;
	private Context mContext;

	@Override
	public Fragment getItem(int position) {
        if (position == 0) {
            return new LeftFragment();
        } else if (position == 1) {
			return centralFragment;
		} else if (position == 2) {
			Bundle bundle = new Bundle();
			bundle.putSerializable("originalNewsUrl", pao.originalNewsUrl);
			RightFragment rightFragment = new RightFragment();
			rightFragment.setArguments(bundle);
			return rightFragment;
		}
        return null;
        //return Fragment.instantiate(mContext, mPagesClasses.get(position).getName());
    }

	@Override
	public int getCount() {
		return mPagesClasses.size();
	}
}
