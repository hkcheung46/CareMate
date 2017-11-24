package com.msccs.hku.familycaregiver.Model;

import android.util.Log;

/**
 * Created by HoiKit on 11/11/2017.
 */

public class LocalContacts {

    private String contactsLocalDisplayName;
    private String contactsPhoneNumber;
    private String firebaseUID;

    public int hashCode(){
        return contactsPhoneNumber.hashCode();
    }

    public LocalContacts() {
    }

    public String getFirebaseUID() {
        return firebaseUID;
    }

    public void setFirebaseUID(String firebaseUID) {
        this.firebaseUID = firebaseUID;
    }

    public LocalContacts(String contactsLocalDisplayName, String contactsPhoneNumber, String firebaseUID) {

        this.contactsLocalDisplayName = contactsLocalDisplayName;
        this.contactsPhoneNumber = contactsPhoneNumber;
        this.firebaseUID = firebaseUID;
    }

    public LocalContacts(String userLocalDisplayName, String userPhoneNumber) {

        this.contactsLocalDisplayName = userLocalDisplayName;
        this.contactsPhoneNumber = userPhoneNumber;
    }

    public String getContactsLocalDisplayName() {

        return contactsLocalDisplayName;
    }

    public void setContactsLocalDisplayName(String contactsLocalDisplayName) {
        this.contactsLocalDisplayName = contactsLocalDisplayName;
    }

    public String getContactsPhoneNumber() {
        return contactsPhoneNumber;
    }

    public void setContactsPhoneNumber(String contactsPhoneNumber) {
        this.contactsPhoneNumber = contactsPhoneNumber;
    }

    @Override
    public String toString(){
        return this.getContactsLocalDisplayName();
    }

    public boolean equals(Object object) {
        if (object instanceof FirebaseUser) {
            String localContactsPhoneNo = this.getContactsPhoneNumber().replaceAll("\\s+","");
            String firebaseTelNo = ((FirebaseUser)object).getTelNum();
            return (localContactsPhoneNo.equals(firebaseTelNo)||("+852" +localContactsPhoneNo).equals(firebaseTelNo));
        }else if (object instanceof LocalContacts){
            return (this.getContactsPhoneNumber().equals(((LocalContacts) object).getContactsPhoneNumber()));
        }
        return super.equals(object);
    }

}
