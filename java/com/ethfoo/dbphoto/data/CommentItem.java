package com.ethfoo.dbphoto.data;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/5/26.
 */
public class CommentItem implements Serializable{
    private String userImageUrl;
    private String userName;
    private String usrId;
    private String content;
    private String createdTime;

    public CommentItem(String userImageUrl, String name, String content){
        this.userImageUrl = userImageUrl;
        this.userName = name;
        this.content = content;
    }

    public CommentItem(String userImageUrl, String name, String content, String time){
        this.userImageUrl = userImageUrl;
        this.userName = name;
        this.content = content;
        this.createdTime = time;
    }

    public String getUsrId() {
        return usrId;
    }

    public void setUsrId(String usrId) {
        this.usrId = usrId;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public String getUserName() {
        return userName;
    }
}
