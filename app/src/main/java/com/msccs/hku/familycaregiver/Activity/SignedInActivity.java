package com.msccs.hku.familycaregiver.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.msccs.hku.familycaregiver.Fragment.GroupsTabHostFragment;
import com.msccs.hku.familycaregiver.Fragment.SettingsFragment;
import com.msccs.hku.familycaregiver.R;
import com.msccs.hku.familycaregiver.Fragment.ToDoListTabHostFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//This is the class controlling the main page after login
//The individual behaviour of each function is handled inside each fragment

public class SignedInActivity extends AppCompatActivity implements ToDoListTabHostFragment.onToDoListTabSelectedListener, GroupsTabHostFragment.onGroupsTabSelectedListener, SettingsFragment.OnSettingsFragmentResumeListener {


    private TextView mLoginUserPhoneTxtView;
    private TextView mLoginUserNameTxtView;
    private ImageView mLoginUserPhotoImgView;
    private DatabaseReference mDatabase;

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 222;

    @BindView(R.id.root)
    View mRootView;

    @BindView(R.id.id_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.id_nv_menu)
    NavigationView mNavigationView;

    @BindView(R.id.add_new_group_fab)
    FloatingActionButton mGroupAddFab;

    @BindView(R.id.add_new_task_fab)
    FloatingActionButton mNewTaskFab;

    @OnClick(R.id.add_new_task_fab)
    public void onAddNewTaskFABClick(View v){
        Intent intent = new Intent(SignedInActivity.this,CreateNewTaskActivity.class);
        intent.putExtra(CreateNewTaskActivity.EXTRA_CREATE_NEW_TASK_MODE,"g");
        startActivity(intent);
    }

    @BindView(R.id.save_settings_fab)
    FloatingActionButton mSaveSettingsFab;

    @OnClick(R.id.save_settings_fab)
    public void onSaveSettingsFABClick(View v) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof SettingsFragment) {
            ((SettingsFragment) currentFragment).saveUserInfo();
        }
    }

    @OnClick(R.id.add_new_group_fab)
    public void onAddNewGroupFABClick(View v) {
        Intent intent = new Intent(SignedInActivity.this, NewGroupActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_in);
        ButterKnife.bind(this);

        //Action Bar handling
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        //ActionBarToggle
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.setDrawerIndicatorEnabled(true);

        //Navigation Item onClick Handling
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            //Last Checked Item
            private MenuItem mPreMenuItem;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //Hotfix, default check item handling
                if (mPreMenuItem == null) {
                    mPreMenuItem = mNavigationView.getMenu().getItem(0);
                }

                //For highlight selected item, and un-select previous selected item
                if (mPreMenuItem != null) {
                    mPreMenuItem.setChecked(false);
                }

                //Handle appearance change in nav menu
                if (menuItem.getItemId() != R.id.sign_out_menu) {
                    menuItem.setChecked(true);
                    mPreMenuItem = menuItem;
                }

                FragmentTransaction transaction;

                switch (menuItem.getItemId()) {

                    case R.id.toDoListNavItem:
                        mDrawerLayout.closeDrawers();
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, new ToDoListTabHostFragment(), null);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        return true;

                    case R.id.groupsNavItem:
                        mDrawerLayout.closeDrawers();
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, new GroupsTabHostFragment(), null);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        return true;

                    case R.id.settingsNavItem:
                        mDrawerLayout.closeDrawers();
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, new SettingsFragment(), null);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        return true;

                    case R.id.sign_out_menu:
                        mDrawerLayout.closeDrawers();
                        AuthUI.getInstance()
                                .signOut(SignedInActivity.this)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(SignedInActivity.this, AuthUIActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            showSnackbar(R.string.sign_out_failed);
                                        }
                                    }
                                });
                        return true;
                }
                return false;
            }
        });

        //Navigation drawer Item linkage
        View header = mNavigationView.getHeaderView(0);
        mLoginUserPhoneTxtView = (TextView) header.findViewById(R.id.loginUserPhone);
        mLoginUserPhotoImgView = (ImageView) header.findViewById(R.id.loginUserPhoto);

        updateUserDisplayInfo();

        //Default entry page will be the to do list
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new ToDoListTabHostFragment(), null);
        transaction.commit();


        //Ask for reading local contact permission
        if (ContextCompat.checkSelfPermission(SignedInActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            Log.d("testing", "i am fired");
            //Handle the case in which the users have replied not grant the read local contacts to the application, show the rationale and ask him to grant
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignedInActivity.this, Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(SignedInActivity.this, "Please go to app settings to grant the READ CONTACTS PERMISSION", Toast.LENGTH_LONG).show();
            } else {
                //If user's have never answer grant the permission or not, display the dialog and ask for the requested permission
                String[] perReqArray = {Manifest.permission.READ_CONTACTS};
                ActivityCompat.requestPermissions(SignedInActivity.this, perReqArray, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
    }


    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void animateFab(char mode, int position) {
        //classify the mode according to the fragment inflated in main_content
        //mode: t -->task -->always show add_task floating action button
        //mode: g -->groups & invitation -->shows add group floating action button , maybe other button later
        //mode: s -->settings -->show save floating action button
        switch (mode) {
            case 't':
                mGroupAddFab.hide();
                mSaveSettingsFab.hide();
                mNewTaskFab.show();
                break;
            case 'g':
                switch (position) {
                    case 0:
                        //groups tab -->show groups fab
                        mNewTaskFab.hide();
                        mSaveSettingsFab.hide();
                        mGroupAddFab.show();
                        break;
                    case 1:
                        //pending invitation tab --> hide all fab
                        mNewTaskFab.hide();
                        mSaveSettingsFab.hide();
                        mGroupAddFab.hide();
                        break;
                }
                break;
            case 's':
                mGroupAddFab.hide();
                mNewTaskFab.hide();
                mSaveSettingsFab.show();
                break;
            default:
                break;
        }
    }

    //Below method are for implement the fab changes method for fragment
    @Override
    public void onToDoListTabSelected(int position) {
        //This part should call the animate Fab, which makes the final decision on which FAB to show
        animateFab('t', position);
    }

    @Override
    public void onGroupTabSelected(int position) {
        animateFab('g', position);
    }

    @Override
    public void fabChangeOnSettingsFragmentResume() {
        animateFab('s', 0);
    }

    public void updateUserDisplayInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mLoginUserPhoneTxtView.setText(user.getPhoneNumber());
        Glide.with(mLoginUserPhotoImgView.getContext()).load(user.getPhotoUrl()).into(mLoginUserPhotoImgView);
    }

    @MainThread
    public void showSnackbar(@StringRes int messageRes) {
        Snackbar.make(mRootView, messageRes, Snackbar.LENGTH_LONG).show();
    }

}
