package com.finalterm.alumninetwork.service;

import com.finalterm.alumninetwork.dto.response.PostDTO;
import com.finalterm.alumninetwork.dto.response.PostDTOV1;
import com.finalterm.alumninetwork.pojo.EnumReaction;
import com.finalterm.alumninetwork.pojo.Post;
import com.finalterm.alumninetwork.pojo.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface PostService {
    List<PostDTO> getPosts();
    PostDTO saveOrUpdate(Map<String, String> params, List<MultipartFile> fileImages, User user);
    void delete(int id);
    List<PostDTO> getMyPosts(int userId);
    void lockComments(Post post);
    Post getPostById(int id);
    PostDTOV1 getPostByIdV1(int id);
    List<Object[]> statisticPosts(String timeType, int year);
    void toggleReaction(Post post, User user, EnumReaction type);
}
