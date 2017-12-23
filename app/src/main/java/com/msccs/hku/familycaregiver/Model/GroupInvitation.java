package com.msccs.hku.familycaregiver.Model;

/**
 * Created by HoiKit on 26/11/2017.
 */

public class GroupInvitation {

    public String recipientUid;
    public String groupId;
    public String groupName;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getRecipientUid() {
        return recipientUid;
    }

    public void setRecipientUid(String recipientUid) {
        this.recipientUid = recipientUid;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public GroupInvitation() {

    }

    public GroupInvitation(String recipientUid, String groupId, String groupName) {

        this.recipientUid = recipientUid;
        this.groupId = groupId;
        this.groupName = groupName;
    }
}
