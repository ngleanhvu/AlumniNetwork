package com.finalterm.alumninetwork.dto.response;

import com.finalterm.alumninetwork.pojo.User;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class PostDTO {
    private Integer id;
    private String title;
    private String content;
    private Boolean blockedComment;
    private Boolean active = true;
    private Date createdAt;
    private UserWithPostDto user;
    List<String> imgUrls;
    Map<String, Integer> statsReaction;
    int totalComments;

    public PostDTO() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getBlockedComment() {
        return blockedComment;
    }

    public void setBlockedComment(Boolean blockedComment) {
        this.blockedComment = blockedComment;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    public List<String> getImgUrls() {
        return imgUrls;
    }

    public void setImgUrls(List<String> imgUrls) {
        this.imgUrls = imgUrls;
    }

    public Map<String, Integer> getStatsReaction() {
        return statsReaction;
    }

    public void setStatsReaction(Map<String, Integer> statsReaction) {
        this.statsReaction = statsReaction;
    }

    public int getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(int totalComments) {
        this.totalComments = totalComments;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}