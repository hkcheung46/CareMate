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

import com.msccs.hku.familycaregiver.Activity.GroupDetailActivity;
import com.msccs.hku.familycaregiver.Adapter.GroupDetailPagerAdapter;
import com.msccs.hku.familycaregiver.R;

/**
 * Created by HoiKit on 03/12/2017.
 */

public class GroupDetailTabHostFragment extends Fragment {

    private android.support.design.widget.TabLayout  mTabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_dtl_tab_host, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String groupId = getArguments().getString("groupId");

        mTabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.tasks).toUpperCase()));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.groupMember).toUpperCase()));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        //Have to rewrite another PagerAdapter

        final PagerAdapter adapter = new GroupDetailPagerAdapter(getChildFragmentManager(),mTabLayout.getTabCount(),groupId);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                try{
                    ((GroupDetailActivity)getActivity()).onGroupDetailTabSelected(tab.getPosition());
                }catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    //Interface for
    public interface onGroupDetailTabSelectedListener{
        void onGroupDetailTabSelected(int position);
    };

}
