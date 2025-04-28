package com.finalterm.alumninetwork.dto.response;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class PostDTOV1 {
    private Integer id;
    private String title;
    private Boolean blockedComment = false;
    private Boolean active = true;
    private Date createdAt;
    List<String> imgUrls;
    Map<String, Integer> statsReaction;
    int totalComments;

    public PostDTOV1() {}

    public PostDTOV1(Integer id, String title, Boolean blockedComment, Boolean active, Date createdAt, List<String> imgUrls) {
        this.id = id;
        this.title = title;
        this.blockedComment = blockedComment;
        this.active = active;
        this.createdAt = createdAt;
        this.imgUrls = imgUrls;
    }

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
}
