package com.finalterm.alumninetwork.repository.impl;

import com.finalterm.alumninetwork.pojo.PostImage;
import com.finalterm.alumninetwork.repository.PostImageRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Repository
@Transactional
public class PostImageRepositoryImpl implements PostImageRepository {

    @Autowired
    private LocalSessionFactoryBean factoryBean;

    @Override
    public PostImage saveOrUpdate(PostImage postImage) {
        Session session = this.factoryBean.getObject().getCurrentSession();

        if (postImage.getId() == null)
            session.persist(postImage);

        session.refresh(postImage);
        return postImage;
    }

}
