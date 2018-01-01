package com.msccs.hku.familycaregiver.Model;

/**
 * Created by HoiKit on 26/12/2017.
 */

public class Polling {

    public final static String SINGLE_CHOICE_POLLING_MODE = "s";
    public final static String MULTIPLE_CHOICE_POLLING_MODE="m";
    public final static String ACTIVE_POLLING_STATUS="a";
    public final static String CLOSED_POLLING_STATUS="c";

    private String pollingQuestion;
    private String pollingMode;         //s-->Single choice only   m-->Multiple choice allowed
    private String belongToGroupId;
    private String status;              //a-->Active               c-->closed
    private int noOfOptions;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String option5;

    public Polling() {
    }

    public Polling(String pollingQuestion, String pollingMode, String belongToGroupId, String status, int noOfOptions, String option1, String option2, String option3, String option4, String option5) {
        this.pollingQuestion = pollingQuestion;
        this.pollingMode = pollingMode;
        this.belongToGroupId = belongToGroupId;
        this.status = status;
        this.noOfOptions = noOfOptions;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.option5 = option5;
    }

    public String getPollingQuestion() {
        return pollingQuestion;
    }

    public void setPollingQuestion(String pollingQuestion) {
        this.pollingQuestion = pollingQuestion;
    }

    public String getPollingMode() {
        return pollingMode;
    }

    public void setPollingMode(String pollingMode) {
        this.pollingMode = pollingMode;
    }

    public String getBelongToGroupId() {
        return belongToGroupId;
    }

    public void setBelongToGroupId(String belongToGroupId) {
        this.belongToGroupId = belongToGroupId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNoOfOptions() {
        return noOfOptions;
    }

    public void setNoOfOptions(int noOfOptions) {
        this.noOfOptions = noOfOptions;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public String getOption5() {
        return option5;
    }

    public void setOption5(String option5) {
        this.option5 = option5;
    }
}
