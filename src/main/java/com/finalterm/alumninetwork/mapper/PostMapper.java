package com.finalterm.alumninetwork.mapper;

import com.finalterm.alumninetwork.dto.response.PostDTO;
import com.finalterm.alumninetwork.pojo.Post;
import com.finalterm.alumninetwork.pojo.PostImage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PostMapper {
    public static PostDTO toPostDTO(Post post, int totalComments, Map<String, Integer> statsReactions, List<String> imgUrls) {
        PostDTO postDTO = new PostDTO();
        postDTO.setId(post.getId());
        postDTO.setContent(post.getContent());
        postDTO.setTitle(post.getTitle());
        postDTO.setUser(UserWithPostMapper.toUserWithPostDto(post.getUser()));
        postDTO.setCreatedAt(post.getCreatedAt());
        postDTO.setImgUrls(imgUrls);
        postDTO.setTotalComments(totalComments);
        postDTO.setStatsReaction(statsReactions);
        return postDTO;
    }
}

