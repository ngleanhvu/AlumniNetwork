package com.finalterm.alumninetwork.repository;

import com.finalterm.alumninetwork.pojo.Post;
import com.finalterm.alumninetwork.pojo.User;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface PostRepository {
    List<Post> getAll();
    Post saveOrUpdate(Post p);
    List<Post> getPostsByUserId(int userId);
    void delete(int id);
    List<Integer> getMyPostIds(int userId, Date createdDate, int limit);
    Post getPostById(int id);
    List<Object[]> statisticPosts(String timeType, int year);
}
