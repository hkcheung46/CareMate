package com.msccs.hku.familycaregiver.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.msccs.hku.familycaregiver.R;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthUIActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    @BindView(R.id.root)
    View mRootView;

    @BindView(R.id.signIn)
    View mSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_ui);
        ButterKnife.bind(this);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser()!=null) {
            //If user is already login go to the main menu
            startSignedInActivity(null);
            finish();
            return;
        }
    }

    private void startSignedInActivity(IdpResponse response){
        Intent intent = new Intent(AuthUIActivity.this,SignedInActivity.class);
        startActivity(intent);
    }

    @SuppressLint("RestrictedApi")
    //Suppress the error from startActivityForResu
    //As mentioned in below link, should be a bug for android studio
    //https://stackoverflow.com/questions/45308159/basefragmentactivityapi16-startactivityforresultintent-int-bundle-throwing-e
    private void startLoginActivity(){
        Bundle params = new Bundle();
        AuthUI.IdpConfig phoneConfigWithDefaultCountryAndNationalNumber = new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).setParams(params).build();
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Arrays.asList(
                                        new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()))
                        .build(),
                RC_SIGN_IN,params);
    }

    @OnClick(R.id.signIn)
    public void signIn(View view){
        Log.d("signin","signIn");
        startLoginActivity();
    }


    //This function is to handle the call back from Auth UI sign in action
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
            return;
        }
        showSnackbar(R.string.unknown_response);
    }

    //The detail sign in call back handling logic
    @MainThread
    private void handleSignInResponse(int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        // Successfully signed in
        if (resultCode == RESULT_OK) {
            startSignedInActivity(response);
            finish();
            return;
        } else {
            // Sign in failed
            if (response == null) {
                // User pressed back button
                showSnackbar(R.string.sign_in_cancelled);
                return;
            }

            //電話認證應該唔會HIT中NO NETWORK, 不過放住先
            if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                showSnackbar(R.string.no_internet_connection);
                return;
            }

            if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                showSnackbar(R.string.unknown_error);
                return;
            }
        }
        showSnackbar(R.string.unknown_sign_in_response);
    }

    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }
}
