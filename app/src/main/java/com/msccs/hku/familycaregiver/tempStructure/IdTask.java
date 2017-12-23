package com.msccs.hku.familycaregiver.tempStructure;

import com.msccs.hku.familycaregiver.Model.CustomTasks;

/**
 * Created by HoiKit on 19/12/2017.
 */

public class IdTask {

    private String taskId;
    private CustomTasks customTask;

    //Remarks: this is just a class of temp structure for storing the array {TaskId, task}

    public IdTask(String taskId, CustomTasks task) {
        this.taskId = taskId;
        this.customTask = task;
    }

    public IdTask() {

    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public CustomTasks getCustomTask() {
        return customTask;
    }

    public void setCustomTask(CustomTasks customTask) {
        this.customTask=customTask;
    }
}
