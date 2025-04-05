package com.finalterm.alumninetwork.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.finalterm.alumninetwork.pojo.Post;
import com.finalterm.alumninetwork.pojo.PostImage;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.repository.PostImageRepository;
import com.finalterm.alumninetwork.repository.PostRepository;
import com.finalterm.alumninetwork.repository.UserRepository;
import com.finalterm.alumninetwork.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostImageRepository postImageRepository;

    @Autowired
    private Cloudinary cloudinary;


    @Override
    @Transactional
    public List<Post> getPosts() {
        return this.postRepository.getAll();
    }

    @Transactional
    @Override
    public Post saveOrUpdate(String content, List<MultipartFile> fileImages, User user) {
        Post p = new Post();
        p.setTitle(content);
        p.setUser(user);
        p.setCreatedAt(new Date());
        this.postRepository.saveOrUpdate(p);

        // -> Upload cloudinary
        if (fileImages != null && !fileImages.isEmpty()) {
            for (MultipartFile file : fileImages) {
                if (!file.isEmpty()) {
                    try {
                        Map res = this.cloudinary.uploader().upload(file.getBytes(),
                                ObjectUtils.asMap("resource_type", "auto"));

                        PostImage postImage = new PostImage();
                        postImage.setUrl(res.get("url").toString());
                        postImage.setPost(p);

                        this.postImageRepository.saveOrUpdate(postImage);
                    } catch (IOException e) {
                        throw new RuntimeException("Lỗi khi upload ảnh lên Cloudinary", e);
                    }
                }
            }
        }
        return p;
    }

    @Transactional
    @Override
    public void delete(int id) {
        this.postRepository.delete(id);
    }

    @Transactional
    @Override
    public void lockComments(Post post) {
        post.setBlockedComment(true);
        this.postRepository.saveOrUpdate(post);
    }

    @Override
    public List<Post> getMyPosts(int userId) {
        return this.postRepository.getMyPost(userId);
    }
}
