package com.msccs.hku.familycaregiver.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.msccs.hku.familycaregiver.Activity.ChatRoomActivity;
import com.msccs.hku.familycaregiver.Activity.PollingDetailActivity;
import com.msccs.hku.familycaregiver.Activity.SignedInActivity;
import com.msccs.hku.familycaregiver.Activity.TaskDetailActivity;
import com.msccs.hku.familycaregiver.NotificationHelper;

import java.util.Random;

/**
 * Created by HoiKit on 31/12/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    NotificationHelper helper;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        /**
        Log.d("fired","i am fired");
        Log.d("debug notification", "From: " + remoteMessage.getFrom());
        **/
        helper = new NotificationHelper(this);

        String title = remoteMessage.getData().get("title");
        String content = remoteMessage.getData().get("body");
        String notificationFrom = remoteMessage.getData().get("notification_from");


        if (remoteMessage.getData().size() > 0) {
            //Have to do the confirm what the notification is for before ask the NotificationHelper to create the push notification
            //Because they are accepting different number of parameters
            NotificationCompat.Builder builder =null;

            switch (notificationFrom){
                case "newTask":

                    String taskCreatorUid = remoteMessage.getData().get("creatorUid");
                    //Only need to send push notification to users inside the user group excluding the task creator who initiate the change

                    if (!taskCreatorUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        //get the task id of the new task, for redirect use to open the task detail page
                        String taskId = remoteMessage.getData().get("taskId");
                        String groupId = remoteMessage.getData().get("groupId");
                        Intent intent = new Intent(this, TaskDetailActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID,taskId);
                        intent.putExtra(TaskDetailActivity.EXTRA_GROUP_ID,groupId);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(TaskDetailActivity.class);
                        stackBuilder.addNextIntent(intent);
                        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
                        builder = helper.getChannelNotification(title, content,pendingIntent);
                        helper.getManager().notify(new Random().nextInt(),builder.build());
                    }
                    break;
                case "newPoll":
                    String pollCreatorUid = remoteMessage.getData().get("creatorUid");
                    //Only need to send push notification to users inside the user group excluding the poll creator who initiate the change
                    if (!pollCreatorUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        String pollingId = remoteMessage.getData().get("pollingId");
                        Intent intent = new Intent(this, PollingDetailActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra(PollingDetailActivity.EXTRA_POLLING_DETAIL_POLL_ID,pollingId);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(PollingDetailActivity.class);
                        stackBuilder.addNextIntent(intent);
                        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
                        builder = builder = helper.getChannelNotification(title, content,pendingIntent);
                        helper.getManager().notify(new Random().nextInt(),builder.build());
                    }
                    break;
                case "newInvitations":
                    Intent newInvitationIntent = new Intent(this, SignedInActivity.class);
                    newInvitationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    newInvitationIntent.putExtra(SignedInActivity.EXTRA_DEFAULT_OPEN_FRAGMENT,SignedInActivity.DEFAULT_OPEN_GROUP_FRAGMENT);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, newInvitationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder = builder = helper.getChannelNotification(title, content,pendingIntent);
                    helper.getManager().notify(new Random().nextInt(),builder.build());
                    break;
                case "newChatMsg":
                    String messageSenderUid = remoteMessage.getData().get("creatorUid");
                    //Only deliver message when the message received is not from the user himself
                    Log.e("messageSenderUid",messageSenderUid);
                    if (!messageSenderUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        String groupId = remoteMessage.getData().get("groupId");
                        String taskId = remoteMessage.getData().get("taskId");
                        Intent newChatMsgIntent = new Intent(this, ChatRoomActivity.class);
                        newChatMsgIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        newChatMsgIntent.putExtra(ChatRoomActivity.EXTRA_GROUP_ID,groupId);
                        newChatMsgIntent.putExtra(ChatRoomActivity.EXTRA_TASK_ID,taskId);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(ChatRoomActivity.class);
                        stackBuilder.addNextIntent(newChatMsgIntent);
                        PendingIntent pendingIntent1 = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
                        builder = builder = helper.getChannelNotification(title, content,pendingIntent1);
                        helper.getManager().notify(new Random().nextInt(),builder.build());
                    }
                    break;
                default:
                    builder = helper.getChannelNotification(title, content);
                    helper.getManager().notify(new Random().nextInt(),builder.build());
            }
        }
    }
}
