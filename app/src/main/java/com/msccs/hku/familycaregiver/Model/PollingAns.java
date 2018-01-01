package com.msccs.hku.familycaregiver.Model;

/**
 * Created by HoiKit on 27/12/2017.
 */

public class PollingAns {

    private boolean option1Chosen;
    private boolean option2Chosen;
    private boolean option3Chosen;
    private boolean option4Chosen;
    private boolean option5Chosen;

    public PollingAns() {
    }

    public PollingAns(boolean option1Chosen, boolean option2Chosen, boolean option3Chosen, boolean option4Chosen, boolean option5Chosen) {
        this.option1Chosen = option1Chosen;
        this.option2Chosen = option2Chosen;
        this.option3Chosen = option3Chosen;
        this.option4Chosen = option4Chosen;
        this.option5Chosen = option5Chosen;
    }

    public boolean isOption1Chosen() {
        return option1Chosen;
    }

    public void setOption1Chosen(boolean option1Chosen) {
        this.option1Chosen = option1Chosen;
    }

    public boolean isOption2Chosen() {
        return option2Chosen;
    }

    public void setOption2Chosen(boolean option2Chosen) {
        this.option2Chosen = option2Chosen;
    }

    public boolean isOption3Chosen() {
        return option3Chosen;
    }

    public void setOption3Chosen(boolean option3Chosen) {
        this.option3Chosen = option3Chosen;
    }

    public boolean isOption4Chosen() {
        return option4Chosen;
    }

    public void setOption4Chosen(boolean option4Chosen) {
        this.option4Chosen = option4Chosen;
    }

    public boolean isOption5Chosen() {
        return option5Chosen;
    }

    public void setOption5Chosen(boolean option5Chosen) {
        this.option5Chosen = option5Chosen;
    }
}
