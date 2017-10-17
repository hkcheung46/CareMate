package com.msccs.hku.familycaregiver.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.msccs.hku.familycaregiver.Fragment.GroupsTabHostFragment;
import com.msccs.hku.familycaregiver.Fragment.SettingsFragment;
import com.msccs.hku.familycaregiver.Model.UserInfoMap;
import com.msccs.hku.familycaregiver.R;
import com.msccs.hku.familycaregiver.Fragment.ToDoListTabHostFragment;

public class MainActivity extends BaseActivity implements ToDoListTabHostFragment.onToDoListTabSelectedListener, GroupsTabHostFragment.onGroupsTabSelectedListener, SettingsFragment.OnSettingsFragmentResumeListener {

    private TextView mLoginUserPhoneTxtView;
    private TextView mLoginUserNameTxtView;
    private ImageView mLoginUserPhotoImgView;
    private NavigationView mNavigationView;
    private FloatingActionButton mGroupAddFab;
    private FloatingActionButton mNewTaskFab;
    private FloatingActionButton mSaveSettingsFab;
    private DatabaseReference mDatabase;


    @Override
    protected void onResume() {
        super.onResume();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            //假如USER是第一次LOGIN..佢既USER DISPLAY INFO會是沒有DISPLAY NAME的 (因為只是ENABLE了PHONE NUMBER AUTHENTICATION), 要迫USER填番
            if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName() == null) {
                View userDisplayNameDialog = getLayoutInflater().inflate(R.layout.dialog_user_display_name, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setView(userDisplayNameDialog);
                final EditText userInput = (EditText) userDisplayNameDialog.findViewById(R.id.editTextDialogDisplayNameInput);

                // Set Dialog Message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
                                        String displayNameToBeUpdated = userInput.getText().toString().trim();
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(displayNameToBeUpdated).build();
                                        FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                showSnackbar(R.string.userInfoUpdateSuccessful);
                                                updateUserDisplayInfo();
                                                mDatabase.child("users").push().setValue( new UserInfoMap(FirebaseAuth.getInstance().getCurrentUser().getUid(),FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
                                            }
                                        });
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Action Bar handling
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        final DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.setDrawerIndicatorEnabled(true);

        mNavigationView = (NavigationView) findViewById(R.id.id_nv_menu);
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
                        FirebaseAuth fAuth = FirebaseAuth.getInstance();
                        fAuth.signOut();
                        return true;
                }
                return false;
            }
        });

        //Navigation drawer Item linkage
        View header = mNavigationView.getHeaderView(0);
        mLoginUserPhoneTxtView = (TextView) header.findViewById(R.id.loginUserPhone);
        mLoginUserNameTxtView = (TextView) header.findViewById(R.id.loginUserName);
        mLoginUserPhotoImgView = (ImageView) header.findViewById(R.id.loginUserPhoto);

        //FAB linkage
        mGroupAddFab = (FloatingActionButton) findViewById(R.id.add_new_group_fab);
        mNewTaskFab = (FloatingActionButton) findViewById(R.id.add_new_task_fab);
        mSaveSettingsFab = (FloatingActionButton) findViewById(R.id.save_settings_fab);

        //Handle the Add Group Button Fab
        mGroupAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewGroupActivity.class);
                startActivity(intent);
            }
        });

        //Default entry page will be the to do list
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new ToDoListTabHostFragment(), null);
        transaction.commit();

        //Handle the Save Settings FAB
        mSaveSettingsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (currentFragment instanceof SettingsFragment) {
                    ((SettingsFragment) currentFragment).saveUserInfo();
                }
            }

        });
    }

    @Override
    protected void onSignedInInitialize(String username, String userPhone, Uri userPhotoUri) {
        super.onSignedInInitialize(username, userPhone, userPhotoUri);
        mLoginUserNameTxtView.setText(username);
        mLoginUserPhoneTxtView.setText(userPhone);
        //Glide.with(mLoginUserPhotoImgView.getContext()).load(userPhotoUri).into(mLoginUserPhotoImgView);
    }

    @Override
    protected void onSignedOutCleanup() {
        super.onSignedOutCleanup();
        mLoginUserNameTxtView.setText(ANONYMOUS);
        mLoginUserPhoneTxtView.setText("");
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
        mLoginUserNameTxtView.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        mLoginUserPhoneTxtView.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
    }
}
