package com.msccs.hku.familycaregiver.tempStructure;

import com.msccs.hku.familycaregiver.Model.Polling;

/**
 * Created by HoiKit on 26/12/2017.
 */

public class IdPolling {

    private String pollingId;
    private Polling polling;

    public IdPolling() {
    }

    public IdPolling(String pollingId, Polling polling) {
        this.pollingId = pollingId;
        this.polling = polling;
    }

    public String getPollingId() {
        return pollingId;
    }

    public void setPollingId(String pollingId) {
        this.pollingId = pollingId;
    }

    public Polling getPolling() {
        return polling;
    }

    public void setPolling(Polling polling) {
        this.polling = polling;
    }


}
