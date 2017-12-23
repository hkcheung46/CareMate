package com.msccs.hku.familycaregiver.tempStructure;

/**
 * Created by HoiKit on 03/12/2017.
 */

public class InGroup {

    private String groupId;
    private String groupName;

    public InGroup() {
    }


    public InGroup(String groupId, String groupName) {
        this.groupId = groupId;
        this.groupName = groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {

        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return this.groupName;
    }
}
