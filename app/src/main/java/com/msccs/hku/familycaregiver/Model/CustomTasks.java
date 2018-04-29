package com.msccs.hku.familycaregiver.Model;

/**
 * Created by HoiKit on 03/12/2017.
 */

public class CustomTasks {

    private String taskName;
    private String status;              //Assigned (A) or PENDING (N) or Completed (C)
    private String importance;             //Casual (c), Important Task (i)
    private double taskStartDate;         //Task start Date (From philip: Date and time also stay here)
    private double taskEndDate;           //Task End Date
    private String taskEventType;           //E = event     T = task
    //private boolean isAllDayEvent;      //Is it a whole day event?
    private long createDate;            //Task creation date
    private String taskDescription;
    private String belongToGroupId;
    private String creatorUid;

    /**
     I assume assignee should be saved in somewhere else?
     **/



    public String getCreatorUid() {
        return creatorUid;
    }

    public void setCreatorUid(String creatorUid) {
        this.creatorUid = creatorUid;
    }

    public CustomTasks(String taskName, String status, String importance, double taskStartDate, double taskEndDate, long createDate, String taskDescription, String belongToGroupId, String creatorUid, String taskEventType) {
        this.taskName = taskName;
        this.status = status;
        this.importance = importance;
        this.taskStartDate = taskStartDate;
        this.taskEndDate = taskEndDate;
        this.createDate = createDate;
        this.taskDescription = taskDescription;
        this.belongToGroupId = belongToGroupId;
        this.creatorUid = creatorUid;
        this.taskEventType = taskEventType;

    }

    public CustomTasks() {
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskEventType() {
        return taskEventType;
    }

    public void setTaskEventType(String taskEventType) {
        this.taskEventType = taskEventType;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public double getTaskStartDate() {
        return taskStartDate;
    }

    public void setTaskStartDate(double taskStartDate) {
        this.taskStartDate = taskStartDate;
    }

    public double getTaskEndDate() {
        return taskEndDate;
    }

    public void setTaskEndDate(double taskEndDate) {
        this.taskEndDate = taskEndDate;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getBelongToGroupId() {
        return belongToGroupId;
    }

    public void setBelongToGroupId(String belongToGroupId) {
        this.belongToGroupId = belongToGroupId;
    }
}
