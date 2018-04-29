package com.msccs.hku.familycaregiver.Model;

/**
 * Created by hoiki on 4/1/2018.
 */

public class UploadInfo {

    private String name;

    public UploadInfo() {
    }

    public String getName() {

        return name;
    }

    public UploadInfo(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private  String url;
}
