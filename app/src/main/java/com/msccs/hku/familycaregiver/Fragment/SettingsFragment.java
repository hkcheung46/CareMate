package com.msccs.hku.familycaregiver.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.msccs.hku.familycaregiver.Activity.InviteGroupMemberActivityForNewGp;
import com.msccs.hku.familycaregiver.Activity.SignedInActivity;
import com.msccs.hku.familycaregiver.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

/**
 * Created by HoiKit on 27/08/2017.
 */

public class SettingsFragment extends Fragment {

    private ImageView mUserPhotoImgView;
    private static final int RC_PHOTO_PICKER = 2;
    private Bitmap mGroupPhoto;

    @Override
    public void onResume() {
        super.onResume();
        ((SignedInActivity) getActivity()).setActionBarTitle(getString(R.string.userSettings));
        ((SignedInActivity) getActivity()).fabChangeOnSettingsFragmentResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        mUserPhotoImgView = (ImageView) v.findViewById(R.id.userPhotoImgView);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //StorageReference storageReference = storage.getReference();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://family-care-giver.appspot.com");
        final StorageReference pathReference = storageReference.child("userPhoto/"+currentUserUid);

        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(SettingsFragment.this).using(new FirebaseImageLoader()).load(pathReference).asBitmap().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).centerCrop().into(new BitmapImageViewTarget(mUserPhotoImgView){
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        mUserPhotoImgView.setImageDrawable(circularBitmapDrawable);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // File not found

                Glide.with(SettingsFragment.this).load(R.drawable.ic_account_circle_white_48dp).asBitmap().into(new BitmapImageViewTarget(mUserPhotoImgView){
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        mUserPhotoImgView.setImageDrawable(circularBitmapDrawable);
                        mUserPhotoImgView.setBackground(getResources().getDrawable(R.drawable.round_grey_oval));
                    }
                });
            }
        });

        mUserPhotoImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, ""), RC_PHOTO_PICKER);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                mGroupPhoto = BitmapFactory.decodeStream(imageStream);

                Glide.with(SettingsFragment.this).load(bitmapToByte(mGroupPhoto)).asBitmap().centerCrop().into(new BitmapImageViewTarget(mUserPhotoImgView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        mUserPhotoImgView.setImageDrawable(circularBitmapDrawable);
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    private byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    //--For resizing image
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    //Interface for
    public interface OnSettingsFragmentResumeListener {
        void fabChangeOnSettingsFragmentResume();
    }

    public void saveUserInfo() {
        //Save elderly photo thumbnail
        if (mGroupPhoto != null) {
            String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference thumbnailRef = firebaseStorage.getReference().child("userPhoto").child(currentUserUid);
            thumbnailRef.putFile(bitmapToUriConverter(mGroupPhoto));
        }
    }

    public Uri bitmapToUriConverter(Bitmap mBitmap) {
        Uri uri = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            //Bitmap newBitmap = Bitmap.createScaledBitmap(mBitmap, 200, 200, true);
            File file = new File(getActivity().getFilesDir(), "Image" + new Random().nextInt() + ".jpeg");
            FileOutputStream out = getActivity().openFileOutput(file.getName(), Context.MODE_PRIVATE);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //get absolute path
            String realPath = file.getAbsolutePath();
            File f = new File(realPath);
            uri = Uri.fromFile(f);

        } catch (Exception e) {
            Log.e("Error message", e.getMessage());
        }
        return uri;
    }

}
