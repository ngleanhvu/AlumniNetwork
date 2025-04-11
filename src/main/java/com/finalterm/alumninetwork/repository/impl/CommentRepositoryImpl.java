package com.finalterm.alumninetwork.repository.impl;

import com.finalterm.alumninetwork.pojo.Comment;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.repository.CommentRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class CommentRepositoryImpl implements CommentRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public Comment saveOrUpdate(Comment comment) {
        Session session = sessionFactory.getObject().getCurrentSession();
        if (comment.getId() == null) {
            session.persist(comment);
        } else
            session.merge(comment);

        session.refresh(comment);
        return comment;

    }

    @Override
    public Comment getCommentById(int id) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        return s.get(Comment.class, id);
    }

    @Override
    public List<Comment> getRootCommentsByPostId(int id) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Comment> query = builder.createQuery(Comment.class);
        Root<Comment> root = query.from(Comment.class);

        List<Predicate> predicates = new ArrayList<Predicate>();

        predicates.add(
                builder.and(
                        builder.equal(root.get("post").get("id"), id),
                        builder.isNull(root.get("parentCommentId")))
        );

        query.where(predicates.toArray(Predicate[]::new));
        query.orderBy(builder.desc(root.get("createdAt")));

        return session.createQuery(query).getResultList();
    }

    @Override
    public List<Comment> getCommentsByParentCommentId(int parent_id) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Comment> query = builder.createQuery(Comment.class);
        Root<Comment> root = query.from(Comment.class);

        query.where(builder.equal(root.get("parentCommentId").get("id"), parent_id));
        query.orderBy(builder.desc(root.get("createdAt")));

        return session.createQuery(query).getResultList();
    }

    @Override
    public void deleteComment(int id) {
        Session session = sessionFactory.getObject().getCurrentSession();

        Comment comment = session.get(Comment.class, id);
        if (comment != null) {
            //deleteChildComments(comment, session);
            session.remove(comment);
        }
    }

    private void deleteChildComments(Comment parentComment, Session session) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Comment> query = builder.createQuery(Comment.class);
        Root<Comment> root = query.from(Comment.class);

        query.select(root).where(builder.equal(root.get("parentCommentId"), parentComment.getId()));
        List<Comment> childs = session.createQuery(query).getResultList();

        for (Comment child : childs) {
            deleteChildComments(child, session);
            session.remove(child);
        }

    }
}
