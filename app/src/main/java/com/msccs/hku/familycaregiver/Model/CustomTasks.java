package com.msccs.hku.familycaregiver.Model;

import java.util.Date;

/**
 * Created by HoiKit on 03/12/2017.
 */

public class CustomTasks {

    private String taskName;
    private String status;              //Assigned (A) or PENDING (N) or Completed (C)
    private String taskType;             //Casual Task (c), Important Task (i), Reminder (r)
    private Date taskStartDate;         //Task start Date (From philip: Date and time also stay here)
    private Date taskEndDate;           //Task End Date
    private boolean isAllDayEvent;      //Is it a whole day event?
    private Date createDate;            //Task creation date
    private String taskDescription;
    private String belongToGroupId;

    /**
     I assume assignee should be saved in somewhere else?
     **/


    public boolean isAllDayEvent() {
        return isAllDayEvent;
    }

    public CustomTasks(String taskName, String status, String taskType, Date taskStartDate, Date taskEndDate, boolean isAllDayEvent, Date createDate, String taskDescription, String belongToGroupId) {
        this.taskName = taskName;
        this.status = status;
        this.taskType = taskType;
        this.taskStartDate = taskStartDate;
        this.taskEndDate = taskEndDate;
        this.isAllDayEvent = isAllDayEvent;
        this.createDate = createDate;
        this.taskDescription = taskDescription;
        this.belongToGroupId = belongToGroupId;
    }

    public void setAllDayEvent(boolean allDayEvent) {
        isAllDayEvent = allDayEvent;
    }

    public CustomTasks() {
    }

    public String getTaskName() {

        return taskName;
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

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public Date getTaskStartDate() {
        return taskStartDate;
    }

    public void setTaskStartDate(Date taskStartDate) {
        this.taskStartDate = taskStartDate;
    }


    public Date getTaskEndDate() {
        return taskEndDate;
    }

    public void setTaskEndDate(Date taskEndDate) {
        this.taskEndDate = taskEndDate;
    }


    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
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
