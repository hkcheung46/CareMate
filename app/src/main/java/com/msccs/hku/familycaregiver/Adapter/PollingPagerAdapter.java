package com.msccs.hku.familycaregiver.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.msccs.hku.familycaregiver.Fragment.ActivePollingListFragment;
import com.msccs.hku.familycaregiver.Fragment.ClosedPollingListFragment;

/**
 * Created by HoiKit on 20/08/2017.
 */

public class PollingPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public PollingPagerAdapter(FragmentManager fm, int mNumOfTabs) {
        super(fm);
        this.mNumOfTabs = mNumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ActivePollingListFragment tab1 = new ActivePollingListFragment();
                return tab1;
            case 1:
                ClosedPollingListFragment tab2 = new ClosedPollingListFragment();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
