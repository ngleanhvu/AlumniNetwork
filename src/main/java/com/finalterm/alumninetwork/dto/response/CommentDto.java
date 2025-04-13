package com.finalterm.alumninetwork.dto.response;

import java.util.Date;

public class CommentDto {
        private int id;
        private String content;
        private Date createdAt;
        private String username;
        private int parentCommentId; // Chỉ lưu ID, không lưu đối tượng
        private int countReplies;
        // getters and setters

    public CommentDto() {

    }
    public CommentDto(String content, Date createdAt, String username, int parentCommentId, int countReplies, int id) {
        this.content = content;
        this.createdAt = createdAt;
        this.username = username;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getParentCommentId() {
            return parentCommentId;
        }

        public void setParentCommentId(int parentCommentId) {
            this.parentCommentId = parentCommentId;
        }

    public int getCountReplies() {
        return countReplies;
    }

    public void setCountReplies(int countReplies) {
        this.countReplies = countReplies;
    }
}


