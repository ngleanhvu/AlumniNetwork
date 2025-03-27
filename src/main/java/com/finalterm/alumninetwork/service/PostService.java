package com.finalterm.alumninetwork.service;

import com.finalterm.alumninetwork.pojo.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    List<Post> getPosts();
    Post saveOrUpdate(Post p, List<MultipartFile> fileImages);
    void delete(int id);
}
