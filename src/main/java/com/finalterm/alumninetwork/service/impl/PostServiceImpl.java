package com.finalterm.alumninetwork.service.impl;

import com.finalterm.alumninetwork.pojo.Post;
import com.finalterm.alumninetwork.repository.PostRepository;
import com.finalterm.alumninetwork.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostRepository postRepository;

    @Override
    @Transactional
    public List<Post> getPosts() {
        return this.postRepository.getAll();
    }
    @Transactional
    @Override
    public Post saveOrUpdate(Post p, List<MultipartFile> fileImages) {

        // -> Upload cloudinary
        return this.postRepository.saveOrUpdate(p);
    }

    @Transactional
    @Override
    public void delete(int id) {
        this.postRepository.delete(id);
    }
}
