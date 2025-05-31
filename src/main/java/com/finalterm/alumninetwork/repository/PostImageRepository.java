
package com.finalterm.alumninetwork.repository;

import com.finalterm.alumninetwork.pojo.PostImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostImageRepository {
    PostImage saveOrUpdate(PostImage postImages);
    List<PostImage> getPostImagesByPostId(int postId);
}

