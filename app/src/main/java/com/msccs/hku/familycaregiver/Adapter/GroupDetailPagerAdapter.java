package com.msccs.hku.familycaregiver.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.msccs.hku.familycaregiver.Fragment.GroupMemberListFragment;
import com.msccs.hku.familycaregiver.Fragment.GroupTaskListFragment;

/**
 * Created by HoiKit on 04/12/2017.
 */

public class GroupDetailPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    String mGroupId;

    public GroupDetailPagerAdapter(FragmentManager fm, int numOfTabs, String groupId) {
        super(fm);
        this.mNumOfTabs = numOfTabs;
        this.mGroupId = groupId;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Bundle args1 = new Bundle();
                args1.putString("groupId",mGroupId);
                GroupTaskListFragment tab1 = new GroupTaskListFragment();
                tab1.setArguments(args1);
                return tab1;
            case 1:
                Bundle args2 = new Bundle();
                args2.putString("groupId",mGroupId);
                GroupMemberListFragment tab2 = new GroupMemberListFragment();
                tab2.setArguments(args2);
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
