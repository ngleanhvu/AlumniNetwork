package com.finalterm.alumninetwork.dto.response;

import com.finalterm.alumninetwork.pojo.EnumReaction;

public class ReactionDto {
    private int id;
    private int postId;
    private int userId;
    private EnumReaction type;
    private String fullName; //


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public EnumReaction getType() {
        return type;
    }

    public void setType(EnumReaction type) {
        this.type = type;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName ) {
        this.fullName = fullName;
    }
}
