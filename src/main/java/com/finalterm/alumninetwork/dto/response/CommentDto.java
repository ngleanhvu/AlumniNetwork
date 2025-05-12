package com.finalterm.alumninetwork.dto.response;

import com.finalterm.alumninetwork.mapper.UserWithPostMapper;

import java.util.Date;

public class CommentDto {
        private Integer id;
        private String content;
        private Date createdAt;
        private UserWithPostDto user;
        private Integer parentCommentId; // Chỉ lưu ID, không lưu đối tượng
        private int countReplies;
        private int postOwnerId;
        // getters and setters

    public int getPostOwnerId() {
        return postOwnerId;
    }

    public void setPostOwnerId(int postOwnerId) {
        this.postOwnerId = postOwnerId;
    }

    public CommentDto() {

    }
    public CommentDto(String content, Date createdAt, UserWithPostDto user, int parentCommentId, int countReplies, int id) {
        this.content = content;
        this.createdAt = createdAt;
        this.user = user;
        this.parentCommentId = parentCommentId;
        this.countReplies = countReplies;
        this.id = id;
    }

    public int getId() {
            return id;
        }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }


    public UserWithPostDto getUser() {
        return user;
    }

    public void setUser(UserWithPostDto user) {
        this.user = user;
    }


    public Integer getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Integer parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public int getCountReplies() {
        return countReplies;
    }

    public void setCountReplies(int countReplies) {
        this.countReplies = countReplies;
    }
}


