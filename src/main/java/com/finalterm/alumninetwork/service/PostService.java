package com.finalterm.alumninetwork.service;

import com.finalterm.alumninetwork.dto.response.PostDTO;
import com.finalterm.alumninetwork.pojo.Post;
import com.finalterm.alumninetwork.pojo.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PostService {
    List<PostDTO> getPosts();
    PostDTO saveOrUpdate(Map<String, String> params, List<MultipartFile> fileImages, User user);
    void delete(int id);
    List<PostDTO> getMyPosts(int userId, Date createdDate, int limit);
    void lockComments(Post post);
    Post getPostById(int id);
    List<Object[]> statisticPosts(String timeType, int year);
}
