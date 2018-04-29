package com.msccs.hku.familycaregiver.Activity;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.msccs.hku.familycaregiver.Model.ChatMessage;
import com.msccs.hku.familycaregiver.Model.LocalContacts;
import com.msccs.hku.familycaregiver.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class ChatRoomActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID="COM.MSCCS.HKU.FAMILYCAREGIVER.EXTRA_TASK_ID";
    public static final String EXTRA_GROUP_ID="COM.MSCCS.HKU.FAMILYCAREGIVER.EXTRA_GROUP_ID";

    private ListView mChatMsgListView;
    private FirebaseListAdapter mAdapter;
    private EditText mMessageTbx;
    private Button mSendBtn;

    private HashMap<String, LocalContacts> localContactsHashMap;
    private String mTaskId;
    private String mGroupId;
    private DatabaseReference chatMessagesRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        localContactsHashMap = getLocalContactsHashMap();
        mTaskId = getIntent().getStringExtra(EXTRA_TASK_ID);
        mGroupId = getIntent().getStringExtra(EXTRA_GROUP_ID);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.discussion);

        mChatMsgListView = (ListView) findViewById(R.id.chatMsgListView);
        mMessageTbx = (EditText) findViewById(R.id.messageTbx);
        mSendBtn = (Button) findViewById(R.id.sendButton);

        chatMessagesRef = FirebaseDatabase.getInstance().getReference("ChatRm").child(mGroupId).child(mTaskId);
        mAdapter = new FirebaseListAdapter<ChatMessage>(this,ChatMessage.class,R.layout.item_chat_message,chatMessagesRef) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.linearLayout);
                TextView messageTxtLbl = (TextView)v.findViewById(R.id.messageTextLbl);
                TextView messageNameLbl = (TextView)v.findViewById(R.id.messageNameLbl);
                TextView messageTimeLbl = (TextView)v.findViewById(R.id.messageTimeLbl);

                String senderPhoneNumber = model.getSenderPhoneNo();

                LocalContacts contact = localContactsHashMap.get(senderPhoneNumber);
                if (contact == null) {
                    contact = localContactsHashMap.get(senderPhoneNumber.substring(4));
                }

                if (senderPhoneNumber.equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
                    messageNameLbl.setText(getString(R.string.me));
                    linearLayout.setGravity(Gravity.END);
                } else if (contact != null) {
                    messageNameLbl.setText(contact.getContactsLocalDisplayName());
                    linearLayout.setGravity(Gravity.START);
                } else {
                    messageNameLbl.setText(senderPhoneNumber);
                    linearLayout.setGravity(Gravity.START);
                }

                messageTxtLbl.setText(model.getMessageText());

                SimpleDateFormat dateFormat = new SimpleDateFormat("(dd-MM-yyyy HH:mm:ss)");
                messageTimeLbl.setText(dateFormat.format(model.getMessageSendOutTime()));
            }
        };

        mChatMsgListView.setAdapter(mAdapter);

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtMessageToSend = mMessageTbx.getText().toString();
                String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String senderPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                ChatMessage newChatMessage = new ChatMessage(txtMessageToSend,senderUid,senderPhoneNumber);
                chatMessagesRef.push().setValue(newChatMessage);
                mMessageTbx.setText("");
            }
        });

        mMessageTbx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count!=0){
                    mSendBtn.setEnabled(true);
                }else{
                    mSendBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    //Performance much better if user Hashmap instead of Arraylist
    public HashMap<String, LocalContacts> getLocalContactsHashMap() {
        HashMap<String, LocalContacts> s = new HashMap<>();
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s+", "");
            s.put(phoneNumber, new LocalContacts(name, phoneNumber));
        }
        cursor.close();
        return s;
    }
}
