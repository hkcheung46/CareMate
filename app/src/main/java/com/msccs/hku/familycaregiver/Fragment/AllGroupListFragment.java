package com.msccs.hku.familycaregiver.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Movie;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.msccs.hku.familycaregiver.Activity.GroupDetailActivity;
import com.msccs.hku.familycaregiver.tempStructure.InGroup;
import com.msccs.hku.familycaregiver.R;

/**
 * Created by HoiKit on 20/08/2017.
 */

public class AllGroupListFragment extends ListFragment {


    FirebaseListAdapter<InGroup> mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference inGroupRef = database.getReference("inGroup").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mAdapter = new FirebaseListAdapter<InGroup>(getActivity(), InGroup.class, R.layout.group_list_item, inGroupRef) {
            @Override
            protected void populateView(View v, InGroup model, int position) {
                TextView groupNameLbl = (TextView) v.findViewById(R.id.groupNameLbl);
                final ImageView groupImageView = (ImageView) v.findViewById(R.id.groupImgView);
                groupNameLbl.setText(model.getGroupName());

                FirebaseStorage storage = FirebaseStorage.getInstance();
                String fileName = model.getGroupId();
                //StorageReference storageReference = storage.getReference();
                StorageReference storageReference = storage.getReferenceFromUrl("gs://family-care-giver.appspot.com");
                final StorageReference pathReference = storageReference.child("groupThumbNail/"+fileName);

                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(AllGroupListFragment.this).using(new FirebaseImageLoader()).load(pathReference).asBitmap().into(new BitmapImageViewTarget(groupImageView){
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                groupImageView.setImageDrawable(circularBitmapDrawable);
                            }


                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // File not found
                        Glide.with(AllGroupListFragment.this).load(R.drawable.ic_assignment_black_24dp).asBitmap().into(new BitmapImageViewTarget(groupImageView){
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                groupImageView.setImageDrawable(circularBitmapDrawable);
                            }
                        });
                    }
                });;


            }
        };
        this.setListAdapter(mAdapter);

        final ListView groupListView = this.getListView();
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), GroupDetailActivity.class);
                intent.putExtra(GroupDetailActivity.EXTRA_GROUP_NAME, ((InGroup) (getListAdapter().getItem(position))).getGroupName());
                intent.putExtra(GroupDetailActivity.EXTRA_GROUP_ID, ((InGroup) (getListAdapter().getItem(position))).getGroupId());
                startActivity(intent);
            }
        });
    }


}
