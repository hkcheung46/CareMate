package com.msccs.hku.familycaregiver.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.msccs.hku.familycaregiver.R;

import java.util.Arrays;

/**
 * Created by HoiKit on 13/08/2017.
 */

//TO-DO: DECOMMISSION THIS CLASS SINCE WE NO LONGER NEED THE AUTH STATE CHANGE LISTENER

public class BaseActivity extends AppCompatActivity {

    // Choose an arbitrary request code value
    private static final int RC_SIGN_IN = 123;
    public static final String ANONYMOUS = "anonymous";

    protected String mUsername;
    protected String mUserPhone;
    protected Uri mUserPhotoUrl;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!=null){
                    //user is signed in
                    onSignedInInitialize(user.getDisplayName(),user.getPhoneNumber(),user.getPhotoUrl());
                }else{
                    //user is signed out
                    Bundle params = new Bundle();

                    // ## Set the default region code as HK (+852) when input phone number in UI
                    params.putString(AuthUI.EXTRA_DEFAULT_COUNTRY_CODE, "HK");
                    AuthUI.IdpConfig phoneConfigWithDefaultCountryAndNationalNumber =
                            new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER)
                                    .setParams(params)
                                    .build();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(
                                            Arrays.asList(
                                                    new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN,params);

                }
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthListener!=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthListener);
    }

    protected void onSignedInInitialize(String username,String userPhone,Uri userPhotoUrl){
        mUsername=username;
        mUserPhone =userPhone;
        mUserPhotoUrl=userPhotoUrl;
    }

    protected void onSignedOutCleanup(){
        mUsername = ANONYMOUS;
        mUserPhone ="";
        mUserPhotoUrl=null;
    }


    public void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(findViewById(android.R.id.content), errorMessageRes, Snackbar.LENGTH_LONG).show();
    }
}
