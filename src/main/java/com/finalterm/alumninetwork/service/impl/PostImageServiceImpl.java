package com.finalterm.alumninetwork.service.impl;

import com.finalterm.alumninetwork.pojo.PostImage;
import com.finalterm.alumninetwork.repository.PostImageRepository;
import com.finalterm.alumninetwork.service.PostImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostImageServiceImpl implements PostImageService {

    @Autowired
    private PostImageRepository postImageRepository;

    @Override
    public List<PostImage> getPostImagesByPostId(int postId) {
        return postImageRepository.getPostImagesByPostId(postId);
    }

    @Override
    public PostImage saveOrUpdate(PostImage image) {
        return this.postImageRepository.saveOrUpdate(image);
    }
}
