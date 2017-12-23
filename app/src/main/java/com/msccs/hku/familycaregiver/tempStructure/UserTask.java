package com.msccs.hku.familycaregiver.tempStructure;

/**
 * Created by HoiKit on 13/12/2017.
 */

public class UserTask {

    private String uid;
    private String taskId;

    public UserTask() {
    }

    public UserTask(String uid, String taskId) {

        this.uid = uid;
        this.taskId = taskId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

}
