package com.msccs.hku.familycaregiver.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.msccs.hku.familycaregiver.Activity.SignedInActivity;
import com.msccs.hku.familycaregiver.Adapter.GroupsListPagerAdapter;
import com.msccs.hku.familycaregiver.R;

/**
 * Created by HoiKit on 20/08/2017.
 */

public class GroupsTabHostFragment extends Fragment {

    private android.support.design.widget.TabLayout  mTabLayout;

    public GroupsTabHostFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        ((SignedInActivity)getActivity()).setActionBarTitle(getString(R.string.groups));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_tab_host, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.groups)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.invitations)));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        final PagerAdapter adapter = new GroupsListPagerAdapter(getChildFragmentManager(),mTabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                ((SignedInActivity)getActivity()).onGroupTabSelected(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Bundle bundle = getArguments();
        if (bundle!=null){
            String defaultSelectedTab = bundle.getString("defaultTab");
            if (defaultSelectedTab!=null && defaultSelectedTab.equals("i")){
                viewPager.setCurrentItem(1);
            }
        }

        try {
            //Call the method to update the fab button being show, this is handled in main activity
            ((SignedInActivity) getActivity()).onGroupTabSelected(mTabLayout.getSelectedTabPosition());
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    //Interface for
    public interface onGroupsTabSelectedListener{
        void onGroupTabSelected(int position);
    };


}
