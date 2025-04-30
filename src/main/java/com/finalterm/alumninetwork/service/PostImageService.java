package com.finalterm.alumninetwork.service;

import com.finalterm.alumninetwork.pojo.PostImage;

import java.util.List;

public interface PostImageService {
    List<PostImage> getPostImagesByPostId(int postId);
    PostImage saveOrUpdate(PostImage postImage);
}
