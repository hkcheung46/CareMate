package com.msccs.hku.familycaregiver.Model;

/**
 * Created by HoiKit on 26/12/2017.
 */

public class GroupPolling {
    private String groupId;
    private String pollingId;

    public GroupPolling() {
    }

    public GroupPolling(String groupId, String pollingId) {
        this.groupId = groupId;
        this.pollingId = pollingId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getPollingId() {
        return pollingId;
    }

    public void setPollingId(String pollingId) {
        this.pollingId = pollingId;
    }
}
