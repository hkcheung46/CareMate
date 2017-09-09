package com.msccs.hku.familycaregiver;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.msccs.hku.familycaregiver.Fragment.AllGroupListFragment;
import com.msccs.hku.familycaregiver.Fragment.InvitationListFragment;

/**
 * Created by HoiKit on 20/08/2017.
 */

public class GroupsListPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public GroupsListPagerAdapter(FragmentManager fm, int mNumOfTabs) {
        super(fm);
        this.mNumOfTabs = mNumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                AllGroupListFragment tab1 = new AllGroupListFragment();
                return tab1;
            case 1:
                InvitationListFragment tab2 = new InvitationListFragment();
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
