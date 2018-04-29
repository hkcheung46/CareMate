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
import com.msccs.hku.familycaregiver.Model.CustomTasks;
import com.msccs.hku.familycaregiver.R;
import com.msccs.hku.familycaregiver.tempStructure.IdTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by HoiKit on 20/12/2017.
 */

public class ToDoListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<IdTask> idTaskList;

    public ToDoListAdapter(Context context, ArrayList<IdTask> idTaskList) {
        this.context = context;
        this.idTaskList = idTaskList;
    }

    public ToDoListAdapter() {

    }

    @Override
    public int getCount() {
        return idTaskList.size();
    }

    @Override
    public Object getItem(int position) {
        return idTaskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.task_list_item, parent, false);
        }

        final TextView taskNameLbl = (TextView) convertView.findViewById(R.id.taskNameLbl);
        final TextView taskStartDateLbl = (TextView) convertView.findViewById(R.id.taskStartDateLbl);
        final TextView taskEndDateLbl = (TextView) convertView.findViewById(R.id.taskEndDateLbl);
        final ImageView groupImageView = (ImageView) convertView.findViewById(R.id.groupImgView);
        TextView dateTimeHyphenLbl = (TextView) convertView.findViewById(R.id.dateTimeHyphenLbl);

        CustomTasks currentTask = getCustomTask(position);

        taskNameLbl.setText(currentTask.getTaskName());

        if (currentTask.getTaskEventType().equals("E")){
            taskStartDateLbl.setText(new SimpleDateFormat("MM-dd-yyyy").format(currentTask.getTaskStartDate()));
            taskEndDateLbl.setText(new SimpleDateFormat("MM-dd-yyyy").format(currentTask.getTaskEndDate()));
        }else{
            taskStartDateLbl.setText(new SimpleDateFormat("MM-dd-yyyy hh:mm").format(currentTask.getTaskStartDate()));
            dateTimeHyphenLbl.setVisibility(View.GONE);
            taskEndDateLbl.setVisibility(View.GONE);
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        String fileName = getCustomTask(position).getBelongToGroupId();
        //StorageReference storageReference = storage.getReference();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://family-care-giver.appspot.com");
        final StorageReference pathReference = storageReference.child("groupThumbNail/"+fileName);

        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).using(new FirebaseImageLoader()).load(pathReference).asBitmap().centerCrop()
                        .into(new BitmapImageViewTarget(groupImageView){
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

                Glide.with(context).load(R.drawable.ic_account_circle_white_24dp).asBitmap().centerCrop().into(new BitmapImageViewTarget(groupImageView){
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
    public String getCustomTaskId(int position) {
        return idTaskList.get(position).getTaskId();
    }

    public CustomTasks getCustomTask(int position) {
        return idTaskList.get(position).getCustomTask();
    }

    public void updateData(ArrayList<IdTask> list){
        this.idTaskList=list;
        notifyDataSetChanged();
    }

    public void removeData() {
        this.idTaskList.clear();
        notifyDataSetChanged();
    }


}
