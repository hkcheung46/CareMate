package com.msccs.hku.familycaregiver.tempStructure;

/**
 * Created by HoiKit on 17/12/2017.
 */

public class GroupTask {

    private String groupId;
    private String taskId;

    public GroupTask() {
    }

    public GroupTask(String groupId, String taskId) {
        this.groupId = groupId;

        this.taskId = taskId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
