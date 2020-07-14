package com.app.crease.data.model;

import android.net.Uri;

public class Posts {
    private String postID = "";
    private String userName = "";
    private String postTitle = "";
    private String postContent = "";
    private String uri;

    public Posts(){

    }

    public Posts(String postID, String userName, String uri, String postTitle, String postContent){
        this.postID = postID;
        this.userName=userName;
        this.uri = uri;
        this.postContent = postContent;
        this.postTitle = postTitle;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }
}
