package com.finalterm.alumninetwork.repository.impl;

import com.finalterm.alumninetwork.pojo.Comment;
import com.finalterm.alumninetwork.pojo.Post;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.repository.CommentRepository;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
    public int countTotalCommentsByPostId(int id) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Comment> root = query.from(Comment.class);

        query.where(builder.equal(root.get("post").get("id"), id));
        query.select(builder.count(root));

        Long result = session.createQuery(query).uniqueResult();
        return (result != null) ? result.intValue() : 0;
    }

    @Override
    public Comment getCommentById(int id) {
        Session s = this.sessionFactory.getObject().getCurrentSession();
        return s.get(Comment.class, id);
    }

    @Override
    public List<Comment> getPaginateComment(int postId, Date createdAt, int limit, Integer parentCommentId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Comment> query = builder.createQuery(Comment.class);
        Root<Comment> root = query.from(Comment.class);

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (parentCommentId != null)
            //Neu la Comment con
            predicates.add(builder.equal(root.get("parentCommentId").get("id"), parentCommentId));
        else
            predicates.add(builder.isNull(root.get("parentCommentId")));

        predicates.add(builder.equal(root.get("post").get("id"), postId));
        predicates.add(builder.lessThan(root.get("createdAt"), createdAt));

        query.where(predicates.toArray(Predicate[]::new));
        query.orderBy(builder.desc(root.get("createdAt")));

        return session.createQuery(query).setMaxResults(limit).getResultList();
    }

    @Override
    public List<Comment> getCommentsByList(List<Integer> commentIds) {
        if (commentIds == null || commentIds.isEmpty()) {
            return Collections.emptyList();
        }

        Session session = this.sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Comment> criteriaQuery = criteriaBuilder.createQuery(Comment.class);
        Root<Comment> root = criteriaQuery.from(Comment.class);

        criteriaQuery.select(root).where(root.get("id").in(commentIds));

        return session.createQuery(criteriaQuery).getResultList();
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
