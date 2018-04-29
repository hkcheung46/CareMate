package com.msccs.hku.familycaregiver.Model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by HoiKit on 05/10/2017.
 */

public class Group {
    private String elderName;
    private long birthday;


    public Group(){

    }

    public Group(String elderName, long birthday) {
        this.elderName = elderName;
        this.birthday = birthday;
    }

    public String getElderName() {
        return elderName;
    }

    public void setElderName(String elderName) {
        this.elderName = elderName;
    }

    public long getBirthday() {
        return birthday;
    }
    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }
}
