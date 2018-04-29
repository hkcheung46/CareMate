package com.msccs.hku.familycaregiver.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.msccs.hku.familycaregiver.Model.UploadInfo;
import com.msccs.hku.familycaregiver.R;

public class AttachmentListActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "COM.MSCCS.HKU.FAMILYCAREGIVER.EXTRA_TASK_ID";
    private FirebaseListAdapter firebaseListAdapter;
    private FloatingActionButton uploadFAB;
    private String mTaskId;
    private ListView mAttachmentListView;
    private CoordinatorLayout mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.attachment);

        uploadFAB = (FloatingActionButton) findViewById(R.id.uploadAttchmentFAB);
        mAttachmentListView = (ListView) findViewById(R.id.attachment_listView);
        mRootView = (CoordinatorLayout) findViewById(R.id.root_view);

        //Get the task information from the intent
        mTaskId = getIntent().getStringExtra(EXTRA_TASK_ID);

        final DatabaseReference attachmentRef = FirebaseDatabase.getInstance().getReference().child("Attachment").child(mTaskId);

        firebaseListAdapter = new FirebaseListAdapter<UploadInfo>(this, UploadInfo.class, R.layout.attachment_list_item, attachmentRef) {
            @Override
            protected void populateView(View v, final UploadInfo uploadInfo, final int position) {

                //Link the view inside a list item
                TextView fileNameLbl = (TextView) v.findViewById(R.id.attachmentNameLbl);
                ImageView deleteBtn = (ImageView) v.findViewById(R.id.deleteBtn);
                TextView fileTypeLbl = (TextView) v.findViewById(R.id.fileTypeLbl);
                ImageView renameBtn = (ImageView) v.findViewById(R.id.renameBtn);

                //get the file extension from the file name
                String name = ((UploadInfo) firebaseListAdapter.getItem(position)).getName();
                String extension = name.substring(name.lastIndexOf(".") + 1, name.length());
                extension = extension.toLowerCase();
                if (extension.equals("") || extension == null) {
                    extension = "unknown";
                }

                //Update the appearance
                fileNameLbl.setText(uploadInfo.getName());
                switch (extension) {
                    case "jpg":
                        fileTypeLbl.setText("IMG");
                        ((GradientDrawable) fileTypeLbl.getBackground()).setColor(getResources().getColor(R.color.brown));
                        break;
                    case "png":
                        fileTypeLbl.setText("IMG");
                        ((GradientDrawable) fileTypeLbl.getBackground()).setColor(getResources().getColor(R.color.brown));
                        break;
                    case "gif":
                        fileTypeLbl.setText("IMG");
                        ((GradientDrawable) fileTypeLbl.getBackground()).setColor(getResources().getColor(R.color.brown));
                        break;
                    case "pdf":
                        fileTypeLbl.setText("PDF");
                        ((GradientDrawable) fileTypeLbl.getBackground()).setColor(getResources().getColor(R.color.red));
                        break;
                    case "xlsx":
                        fileTypeLbl.setText("XLS");
                        ((GradientDrawable) fileTypeLbl.getBackground()).setColor(getResources().getColor(R.color.green));
                        break;
                    case "xls":
                        fileTypeLbl.setText("XLS");
                        ((GradientDrawable) fileTypeLbl.getBackground()).setColor(getResources().getColor(R.color.green));
                        break;
                    case "docx":
                        fileTypeLbl.setText("DOC");
                        ((GradientDrawable) fileTypeLbl.getBackground()).setColor(getResources().getColor(R.color.blue));
                        break;
                    case "doc":
                        fileTypeLbl.setText("DOC");
                        ((GradientDrawable) fileTypeLbl.getBackground()).setColor(getResources().getColor(R.color.blue));
                        break;
                    case "ppt":
                        fileTypeLbl.setText("PPT");
                        ((GradientDrawable) fileTypeLbl.getBackground()).setColor(getResources().getColor(R.color.red));
                        break;
                    case "pptx":
                        fileTypeLbl.setText("PPT");
                        ((GradientDrawable) fileTypeLbl.getBackground()).setColor(getResources().getColor(R.color.red));
                        break;
                    default:
                        fileTypeLbl.setText("?");
                        ((GradientDrawable) fileTypeLbl.getBackground()).setColor(getResources().getColor(R.color.grey));
                        break;
                }

                renameBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AttachmentListActivity.this);
                        LinearLayout layout = new LinearLayout(AttachmentListActivity.this);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(20, 0, 30, 0);

                        final EditText input = new EditText(AttachmentListActivity.this);
                        layout.addView(input, params);
                        builder.setMessage(R.string.newFileName);
                        builder.setView(layout);

                        // Set up the buttons
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String oldName = ((UploadInfo) firebaseListAdapter.getItem(position)).getName();
                                String oldExtension = oldName.substring(oldName.lastIndexOf(".") + 1, oldName.length());

                                if (input.getText() != null && !input.getText().toString().trim().equals("")) {
                                    //The new name save in the database should be equal to the new file name+ "." + the old file extension
                                    final String newName = input.getText().toString() + "." + oldExtension;
                                    final DatabaseReference assignmentRef = FirebaseDatabase.getInstance().getReference("Attachment").child(mTaskId);
                                    Query query = assignmentRef.orderByChild("name").equalTo(newName);
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (!dataSnapshot.exists()) {
                                                //the new file already exist
                                                String key = firebaseListAdapter.getRef(position).getKey();
                                                assignmentRef.child(key).child("name").setValue(newName);
                                            } else {
                                                // if file or the same name is found, stop this action
                                                Snackbar.make(mRootView, getString(R.string.duplicateName), Snackbar.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }else{
                                    Snackbar.make(mRootView,getString(R.string.pleaseCheckInput),Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }
                });


                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AttachmentListActivity.this).setMessage(R.string.confirmDeleteAttachment);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Have to delete both the reference in database and the actual file
                                FirebaseStorage.getInstance().getReferenceFromUrl(uploadInfo.getUrl()).delete();
                                firebaseListAdapter.getRef(position).setValue(null);
                            }
                        });

                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });

            }
        };

        mAttachmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = ((UploadInfo) firebaseListAdapter.getItem(position)).getName();

                String extension = name.substring(name.lastIndexOf(".") + 1, name.length());
                if (extension.equals("") || extension != null) {
                    extension = "unknown";
                }
                /**Debug
                 Toast.makeText(AttachmentListActivity.this, extension, Toast.LENGTH_SHORT).show();
                 **/

                String url = ((UploadInfo) firebaseListAdapter.getItem(position)).getUrl();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });


        mAttachmentListView.setAdapter(firebaseListAdapter);

        uploadFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttachmentListActivity.this, UploadTaskAttachmentActivity.class);
                intent.putExtra(UploadTaskAttachmentActivity.EXTRA_TASK_ID, mTaskId);
                startActivity(intent);
            }
        });
    }

}
