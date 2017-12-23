package com.msccs.hku.familycaregiver.databases;
import java.util.Date;

public class EventObjects {

    private int id;

    private String message;

    private Date date;

    private Date end;

    public EventObjects(String message, Date date, Date end) {
        this.message = message;
        this.date = date;
        this.end = end;
    }

    public EventObjects(int id, String message, Date date, Date end) {
        this.date = date;
        this.message = message;
        this.id = id;
        this.end = end;
    }

    public Date getEnd(){
        return end;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }
}