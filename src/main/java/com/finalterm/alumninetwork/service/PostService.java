package com.finalterm.alumninetwork.service;

import com.finalterm.alumninetwork.dto.response.FeedResponseDto;
import com.finalterm.alumninetwork.dto.response.PostDTO;
import com.finalterm.alumninetwork.pojo.EnumReaction;
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
    void lockOrUnlockComments(Post post);
//    PostDTO getPostByIdToCache(int postId);
    Post getPostById(int postId);
    List<Object[]> statisticPosts(String timeType, int year);
    void toggleReaction(Post post, User user, EnumReaction type);
    FeedResponseDto loadGlobalFeed(Date createdDate, int limit);

}
