package com.finalterm.alumninetwork.service;

import com.finalterm.alumninetwork.dto.PostDTO;
import com.finalterm.alumninetwork.pojo.Post;
import com.finalterm.alumninetwork.pojo.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    List<Post> getPosts();
    Post saveOrUpdate(String content, List<MultipartFile> fileImages, User user);
    void delete(int id);
    List<PostDTO> getMyPosts(int userId);
    void lockComments(Post post);
}
