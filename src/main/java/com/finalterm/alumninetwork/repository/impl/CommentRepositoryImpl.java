package com.finalterm.alumninetwork.repository.impl;

import com.finalterm.alumninetwork.pojo.Comment;
import com.finalterm.alumninetwork.repository.CommentRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class CommentRepositoryImpl implements CommentRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public void addComment(Comment comment) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.persist(comment);
    }

    @Override
    public List<Comment> getCommentsByPostId() {
        return List.of();
    }


}
