package com.msccs.hku.familycaregiver.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.msccs.hku.familycaregiver.Activity.SignedInActivity;
import com.msccs.hku.familycaregiver.Adapter.GroupsListPagerAdapter;
import com.msccs.hku.familycaregiver.Adapter.PollingPagerAdapter;
import com.msccs.hku.familycaregiver.R;

/**
 * Created by HoiKit on 25/12/2017.
 */

public class PollingTabHostFragment extends Fragment {

    private android.support.design.widget.TabLayout  mTabLayout;

    @Override
    public void onResume() {
        super.onResume();
        ((SignedInActivity)getActivity()).setActionBarTitle(getString(R.string.polling));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_polling_tab_host, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.activePolling)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.closedPolling)));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        final PagerAdapter adapter = new PollingPagerAdapter(getChildFragmentManager(),mTabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                ((SignedInActivity)getActivity()).onPollingTabSelected(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        try {
            //Call the method to update the fab button being show, this is handled in main activity
            ((SignedInActivity) getActivity()).onPollingTabSelected(mTabLayout.getSelectedTabPosition());
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    //Interface for
    public interface onPollingTabSelectedListener{
        void onPollingTabSelected(int position);
    };

}

