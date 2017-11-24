package com.msccs.hku.familycaregiver.Fragment;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.msccs.hku.familycaregiver.Activity.SignedInActivity;
import com.msccs.hku.familycaregiver.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HoiKit on 27/08/2017.
 */

public class SettingsFragment extends Fragment {

    private TextInputEditText mDisplayNameTbx;
    private TextInputEditText mEmailTbx;

    //For email syntax checking
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;

    @Override
    public void onResume() {
        super.onResume();
        ((SignedInActivity) getActivity()).setActionBarTitle(getString(R.string.userSettings));
        ((SignedInActivity) getActivity()).fabChangeOnSettingsFragmentResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        mDisplayNameTbx = (TextInputEditText) v.findViewById(R.id.displayNameTbx);
        mEmailTbx = (TextInputEditText) v.findViewById(R.id.emailTbx);

        if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName() != null) {
            mDisplayNameTbx.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        }

        if (FirebaseAuth.getInstance().getCurrentUser().getEmail()!=null){
            mEmailTbx.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }
        return v;
    }

    //Interface for
    public interface OnSettingsFragmentResumeListener {
        void fabChangeOnSettingsFragmentResume();
    }

    ;

    private boolean isInputValid() {
        boolean isValid = true;
        if (mDisplayNameTbx.getText().toString().trim().equals("")) {
            isValid = false;
            mDisplayNameTbx.setError(getString(R.string.userDisplayNameCantEmpty));
        }

        if ((!isEmailSyntaxValid(mEmailTbx.getText().toString().trim())) && !mEmailTbx.getText().toString().trim().equals("")) {
            isValid = false;
            mEmailTbx.setError(getString(R.string.emailSyntaxInvalid));
        }
        return isValid;
    }

    public boolean isEmailSyntaxValid(String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void saveUserInfo() {
        if (isInputValid()) {
            String displayNameToBeUpdated = mDisplayNameTbx.getText().toString().trim();
            String contactEmailToBeUpdated = mEmailTbx.getText().toString().trim();
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(displayNameToBeUpdated).build();
            FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    ((SignedInActivity) getActivity()).showSnackbar(R.string.userInfoUpdateSuccessful);
                    (((SignedInActivity) getActivity())).updateUserDisplayInfo();
                }
            });
        }
    }

}
