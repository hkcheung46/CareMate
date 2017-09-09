package com.msccs.hku.familycaregiver;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.msccs.hku.familycaregiver.Fragment.MyTaskFragment;
import com.msccs.hku.familycaregiver.Fragment.ToBeAssignedTaskFragment;
import com.msccs.hku.familycaregiver.Fragment.AllTaskFragment;

/**
 * Created by HoiKit on 20/08/2017.
 */

public class ToDoListPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public ToDoListPagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.mNumOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MyTaskFragment tab1 = new MyTaskFragment();
                return tab1;
            case 1:
                ToBeAssignedTaskFragment tab2 = new ToBeAssignedTaskFragment();
                return tab2;
            case 2:
                AllTaskFragment tab3 = new AllTaskFragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return  mNumOfTabs;
    }
}
