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

    public String getType() {
        return type;
    }

    public String getComment() {
        return comment;
    }

    public String getRepo() {
        return repo;
    }

}
