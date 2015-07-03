package com.octopusbeach.wearforgit.model;

/**
 * Created by hudson on 6/26/15.
 */
public class GitNotification {

    private String title;
    private String type;
    private String comment;
    private String repo;
    private String user;

    public GitNotification(String title, String type, String comment, String repo, String user) {
        this.title = title;
        this.type = type;
        this.comment = comment;
        this.repo = repo;
        this.user = user;
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

    public String getUser() {
        return user;
    }

}
