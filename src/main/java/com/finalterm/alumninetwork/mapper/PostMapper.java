package com.finalterm.alumninetwork.mapper;

import com.finalterm.alumninetwork.dto.PostDTO;
import com.finalterm.alumninetwork.pojo.Post;
import com.finalterm.alumninetwork.pojo.PostImage;

import java.util.List;
import java.util.stream.Collectors;

public class PostMapper {
    public static PostDTO toPostDTO(Post post) {
        PostDTO postDTO = new PostDTO();
        postDTO.setId(post.getId());
        postDTO.setTitle(post.getTitle());
        postDTO.setUserId(post.getUser() != null ? post.getUser().getId() : null);
        postDTO.setCreatedAt(post.getCreatedAt());

        if (post.getImages() != null) {
            List<String> urls = post.getImages().stream()
                    .map(PostImage::getUrl)
                    .collect(Collectors.toList());
            postDTO.setImgUrls(urls);
        }
        return postDTO;
    }
}

