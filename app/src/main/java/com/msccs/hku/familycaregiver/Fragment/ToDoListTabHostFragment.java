package com.msccs.hku.familycaregiver.Fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ListFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.msccs.hku.familycaregiver.Activity.SignedInActivity;
import com.msccs.hku.familycaregiver.R;
import com.msccs.hku.familycaregiver.Adapter.ToDoListPagerAdapter;

public class ToDoListTabHostFragment extends ListFragment {

    private android.support.design.widget.TabLayout  mTabLayout;

    public ToDoListTabHostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_to_do_list_tab_host, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((SignedInActivity)getActivity()).setActionBarTitle(getString(R.string.toDoList));
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

        mTabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.joinedTakeUp)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.waitingForAttention)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.allTasks)));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        final PagerAdapter adapter = new ToDoListPagerAdapter(getChildFragmentManager(),mTabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                try{
                    //Call the method to update the fab button being show, this is handled in main activity
                    ((SignedInActivity)getActivity()).onToDoListTabSelected(tab.getPosition());
                }catch(ClassCastException e){
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

        try {
            //Call the method to update the fab button being show, this is handled in main activity
            ((SignedInActivity) getActivity()).onToDoListTabSelected(mTabLayout.getSelectedTabPosition());
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }


    //Interface for
    public interface onToDoListTabSelectedListener{
        void onToDoListTabSelected(int position);
    };




}
