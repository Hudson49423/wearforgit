package com.octopusbeach.wearforgit.model;

/**
 * Created by hudson on 6/26/15.
 */
public class GitNotification {

    private String title;
    private String type;
    private String comment;

    public GitNotification(String title, String type, String comment) {
        this.title = title;
        this.type = type;
        this.comment = comment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
