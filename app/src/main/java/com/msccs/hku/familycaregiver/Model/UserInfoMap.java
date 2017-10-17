package com.msccs.hku.familycaregiver.Model;

import android.util.Log;

/**
 * Created by HoiKit on 02/09/2017.
 */

public class UserInfoMap {

    private String UID;
    private String telNum;
    private String displayName;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public UserInfoMap(String UID, String telNum, String displayName) {
        this.UID = UID;
        this.telNum = telNum;
        this.displayName = displayName;

    }

    public UserInfoMap() {
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getTelNum() {
        return telNum;
    }

    public void setTelNum(String telNum) {
        this.telNum = telNum;
    }


    //This equal operation is for arraylist to remove duplication and inner join like operation
    public boolean equals(Object object){
        //Handle the case it only map with phone number
        if (object instanceof String){
            return (this.getTelNum().equals(((String)object).toString()) || ((this.getTelNum()).equals(("+852"+(((String)object))))));
        }else{
            //Handle the case it only map with UserInfoMap itself
            return (this.telNum.equals(((UserInfoMap)object).getTelNum()) && this.UID.equals(((UserInfoMap)object).getUID()));
        }
    }

    @Override
    public String toString() {
        return this.displayName;
    }
}
