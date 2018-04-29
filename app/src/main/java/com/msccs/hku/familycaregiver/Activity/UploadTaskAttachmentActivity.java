package com.msccs.hku.familycaregiver.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.msccs.hku.familycaregiver.Adapter.UploadListAdapter;
import com.msccs.hku.familycaregiver.Model.CustomFirebaseUser;
import com.msccs.hku.familycaregiver.Model.UploadInfo;
import com.msccs.hku.familycaregiver.R;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class UploadTaskAttachmentActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID="COM.MSCCS.HKU.FAMILYCAREGIVER.EXTRA_TASK_ID";

    private static final int RESULT_LOAD_IMAGE = 1;
    private Button mSelectBtn;
    private RecyclerView mUploadList;
    private List<String> fileNameList;
    private List<String> fileDoneList;
    private UploadListAdapter uploadListAdapter;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private String mTaskId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_task_attachment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.uploadAttachment);

        //Get the task information from the intent
        mTaskId = getIntent().getStringExtra(EXTRA_TASK_ID);

        mStorage = FirebaseStorage.getInstance().getReference().child("Attachment");
        mDatabase = FirebaseDatabase.getInstance().getReference("Attachment").child(mTaskId);

        mSelectBtn = (Button) findViewById(R.id.selectButton);
        mUploadList = (RecyclerView) findViewById(R.id.uploadList);

        fileNameList = new ArrayList<>();
        fileDoneList = new ArrayList<>();

        uploadListAdapter = new UploadListAdapter(fileNameList, fileDoneList);

        mUploadList.setLayoutManager(new LinearLayoutManager(this));
        mUploadList.setHasFixedSize(true);
        mUploadList.setAdapter(uploadListAdapter);

        mSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select File"), RESULT_LOAD_IMAGE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK){
            if(data.getClipData() != null){
                fileNameList.clear();
                fileDoneList.clear();
                uploadListAdapter.notifyDataSetChanged();
                int totalItemsSelected = data.getClipData().getItemCount();
                for(int i = 0; i < totalItemsSelected; i++){
                    Uri fileUri = data.getClipData().getItemAt(i).getUri();
                    final String fileName = getFileName(fileUri);
                    fileNameList.add(fileName);
                    fileDoneList.add("uploading");
                    uploadListAdapter.notifyDataSetChanged();

                    //This line control the path in firebase the attachment is uploaded to
                    StorageReference fileToUpload = mStorage.child(mTaskId).child(fileName);
                    final int finalI = i;
                    fileToUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            fileDoneList.remove(finalI);
                            fileDoneList.add(finalI, "done");
                            uploadListAdapter.notifyDataSetChanged();

                            Query query = mDatabase.orderByChild("name").equalTo(fileName);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                            //If the file is new, no same name file is found, then create a new path
                                            String key = mDatabase.push().getKey();
                                            String name = taskSnapshot.getMetadata().getName();
                                            String url = taskSnapshot.getDownloadUrl().toString();
                                            mDatabase.child(key).setValue(new UploadInfo(name,url));
                                    }else{
                                        // if file or the same name is found, then overwrite it
                                        for (DataSnapshot uploadInfoSnapshot:dataSnapshot.getChildren()) {
                                            String key = uploadInfoSnapshot.getKey();
                                            String name = taskSnapshot.getMetadata().getName();
                                            String url = taskSnapshot.getDownloadUrl().toString();
                                            mDatabase.child(key).setValue(new UploadInfo(name, url));
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }

                //Toast.makeText(MainActivity.this, "Selected Multiple Files", Toast.LENGTH_SHORT).show();

            } else if (data.getData() != null){
                fileNameList.clear();
                fileDoneList.clear();
                uploadListAdapter.notifyDataSetChanged();
                int totalItemsSelected = 1;
                for(int i = 0; i < totalItemsSelected; i++) {
                    Uri fileUri = data.getData();
                    final String fileName = getFileName(fileUri);
                    fileNameList.add(fileName);
                    fileDoneList.add("uploading");
                    uploadListAdapter.notifyDataSetChanged();

                    //This line control the path in firebase the attachment is uploaded to
                    StorageReference fileToUpload = mStorage.child(mTaskId).child(fileName);
                    final int finalI = i;
                    fileToUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            fileDoneList.remove(finalI);
                            fileDoneList.add(finalI, "done");
                            uploadListAdapter.notifyDataSetChanged();

                            Query query = mDatabase.orderByChild("name").equalTo(fileName);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        //If the file is new, no same name file is found, then create a new path
                                        String key = mDatabase.push().getKey();
                                        String name = taskSnapshot.getMetadata().getName();
                                        String url = taskSnapshot.getDownloadUrl().toString();
                                        mDatabase.child(key).setValue(new UploadInfo(name,url));
                                    }else{
                                        // if file or the same name is found, then overwrite it
                                        for (DataSnapshot uploadInfoSnapshot:dataSnapshot.getChildren()) {
                                            String key = uploadInfoSnapshot.getKey();
                                            String name = taskSnapshot.getMetadata().getName();
                                            String url = taskSnapshot.getDownloadUrl().toString();
                                            mDatabase.child(key).setValue(new UploadInfo(name, url));
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            }
        }

    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

}
