package com.finalterm.alumninetwork.dto.response;

import java.util.List;

public class FeedResponseDto {
    private List<PostDTO> posts;
    private Long nextCursorTime;

    public FeedResponseDto(List<PostDTO> posts, Long nextCursorTime) {
        this.posts = posts;
        this.nextCursorTime = nextCursorTime;
    }

    public List<PostDTO> getPosts() {
        return posts;
    }

    public void setPosts(List<PostDTO> posts) {
        this.posts = posts;
    }

    public Long getNextCursorTime() {
        return nextCursorTime;
    }

    public void setNextCursorTime(Long nextCursorTime) {
        this.nextCursorTime = nextCursorTime;
    }
}
