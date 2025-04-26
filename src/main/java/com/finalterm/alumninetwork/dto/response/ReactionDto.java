package com.finalterm.alumninetwork.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.finalterm.alumninetwork.pojo.EnumReaction;

public class ReactionDto {
    private String id;
    private int postId;
    private EnumReaction type;
    private int userId; //
    private String username;
    private long createdDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public EnumReaction getType() {
        return type;
    }

    public void setType(EnumReaction type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }
}
