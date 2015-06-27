package com.octopusbeach.wearforgit.model;

/**
 * Created by hudson on 6/26/15.
 */
public class GitNotification {

    private String title;
    private String type;
    private String comment;
    private String repo;

    public GitNotification(String title, String type, String comment, String repo) {
        this.title = title;
        this.type = type;
        this.comment = comment;
        this.repo = repo;
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

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }
}
