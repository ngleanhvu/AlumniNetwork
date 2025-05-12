package com.finalterm.alumninetwork.repository;

import com.finalterm.alumninetwork.pojo.Post;
import com.finalterm.alumninetwork.pojo.User;
import com.google.api.client.util.DateTime;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface PostRepository {
    List<Post> getAll();
    Post saveOrUpdate(Post p);
    List<Post> getPostPaginate(int userId, Date cursorTime, int limit);
    List<Post> getPostByPostIds(List<Integer> postIds);
    void delete(int id);
    Post getPostById(int id);
    List<Object[]> statisticPosts(String timeType, int year);
}
