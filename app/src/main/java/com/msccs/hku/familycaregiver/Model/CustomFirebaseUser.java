package com.msccs.hku.familycaregiver.Model;

/**
 * Created by HoiKit on 02/09/2017.
 */

public class CustomFirebaseUser {

    //Assume in the whole application, the display name will be using the locally stored name
    //So in here, no need to store the user display name setup by user

    private String UID;
    private String telNum;

    public CustomFirebaseUser(String UID, String telNum) {
        this.UID = UID;
        this.telNum = telNum;
    }

    public CustomFirebaseUser() {
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

    public boolean equals(Object object){
        if (object instanceof LocalContacts){
            String localContactsPhoneNo = ((LocalContacts)(object)).getContactsPhoneNumber().replaceAll("\\s+","");
            String firebaseTelNo = this.getTelNum();
            return (localContactsPhoneNo.equals(firebaseTelNo)||("+852" +localContactsPhoneNo).equals(firebaseTelNo));
        }else if (object instanceof CustomFirebaseUser){
            return (this.getUID().equals(((CustomFirebaseUser) object).getUID()));
        }
        return super.equals(object);
    }

}
