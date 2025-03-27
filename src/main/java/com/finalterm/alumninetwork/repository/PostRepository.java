package com.finalterm.alumninetwork.repository;

import com.finalterm.alumninetwork.pojo.Post;

import java.util.List;

public interface PostRepository {
    List<Post> getAll();
    Post saveOrUpdate(Post p);
    void delete(int id);
}
