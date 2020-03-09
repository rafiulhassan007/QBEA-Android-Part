package com.rafiulhassan.qrcodebasedevent.AllEvents;

/**
 * Created by Rafiul on 4/8/2019.
 */

public class EventModel {

    private String id;
    private String title;
    private String desc;
    private String type;
    private String status;
    private String code;

    public EventModel(String id, String title, String desc, String type, String status, String code) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.type = type;
        this.status = status;
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
