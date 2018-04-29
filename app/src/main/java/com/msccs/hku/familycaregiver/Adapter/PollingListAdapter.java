package com.msccs.hku.familycaregiver.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.msccs.hku.familycaregiver.Model.Polling;
import com.msccs.hku.familycaregiver.R;
import com.msccs.hku.familycaregiver.tempStructure.IdPolling;

import java.util.ArrayList;

/**
 * Created by HoiKit on 26/12/2017.
 */

public class PollingListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<IdPolling> idPollingList;

    public PollingListAdapter(Context context, ArrayList<IdPolling> idPollingList) {
        this.context = context;
        this.idPollingList = idPollingList;
    }

    @Override
    public int getCount() {
        return idPollingList.size();
    }

    @Override
    public Object getItem(int position) {
        return idPollingList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.polling_list_item, parent, false);
        }

        TextView pollingQuestionLbl = (TextView) convertView.findViewById(R.id.pollingQuestionLbl);
        final ImageView groupImageView = (ImageView) convertView.findViewById(R.id.groupImgView);

        pollingQuestionLbl.setText(idPollingList.get(position).getPolling().getPollingQuestion());

        FirebaseStorage storage = FirebaseStorage.getInstance();
        String fileName = getPolling(position).getBelongToGroupId();
        //StorageReference storageReference = storage.getReference();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://family-care-giver.appspot.com");
        final StorageReference pathReference = storageReference.child("groupThumbNail/" + fileName);

        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).using(new FirebaseImageLoader()).load(pathReference).asBitmap().centerCrop().into(new BitmapImageViewTarget(groupImageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        groupImageView.setImageDrawable(circularBitmapDrawable);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // File not found
                Glide.with(context).load(R.drawable.ic_account_circle_white_24dp).asBitmap().centerCrop().into(new BitmapImageViewTarget(groupImageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        groupImageView.setImageDrawable(circularBitmapDrawable);
                        groupImageView.setBackground(context.getResources().getDrawable(R.drawable.round_grey_oval));
                    }
                });
            }
        });

        return convertView;
    }

    //insert two method here for getting the CustomTask and Task Id directly
    public String getPollingId(int position) {
        return idPollingList.get(position).getPollingId();
    }

    public Polling getPolling(int position) {
        return idPollingList.get(position).getPolling();
    }

    public void updateData(ArrayList<IdPolling> list) {
        this.idPollingList = list;
        notifyDataSetChanged();
    }

    public void removeData() {
        this.idPollingList.clear();
        notifyDataSetChanged();
    }
}
