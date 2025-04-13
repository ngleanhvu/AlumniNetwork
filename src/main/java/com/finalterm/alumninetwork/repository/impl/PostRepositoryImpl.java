package com.finalterm.alumninetwork.repository.impl;

import com.finalterm.alumninetwork.pojo.Post;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
@Transactional
public class PostRepositoryImpl implements PostRepository {
    @Autowired
    private LocalSessionFactoryBean factoryBean;

    @Override
    public Post saveOrUpdate(Post p) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        if (p.getId() == null)
            session.persist(p);
        else
            session.merge(p);
        session.refresh(p);
        return p;
    }

    @Override
    public Post getPostById(int id) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        return session.get(Post.class, id);
    }

    @Override
    public void delete(int id) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        Post p = session.get(Post.class, id);

        if (p != null)
            session.remove(p);
        else
            throw new EntityNotFoundException();
    }

    @Override
    public List<Post> getAll() {
        Session session = this.factoryBean.getObject().getCurrentSession();
//        CriteriaBuilder builder = session.getCriteriaBuilder();
//        CriteriaQuery<Post> query = builder.createQuery(Post.class);
//        Root root = query.from(Post.class);
//        query.select(root);
        Query query = session.createQuery("FROM Post", Post.class);
        return query.getResultList();
    }

    @Override
    public List<Post> getMyPost(int userId) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Post> criteriaQuery = criteriaBuilder.createQuery(Post.class);
        Root root = criteriaQuery.from(Post.class);

        Predicate userPredicate = criteriaBuilder.equal(root.get("user").get("id"), userId);
        criteriaQuery.where(userPredicate);

        Query query = session.createQuery(criteriaQuery);
        return query.getResultList();
    }

    @Override
    public List<Object[]> statisticPosts(String timeType, int year) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = builder.createQuery(Object[].class);
        Root<Post> root = query.from(Post.class);

        // timeType: "MONTH" hoặc "QUARTER"
        Expression<Integer> timeExpression = builder.function(timeType, Integer.class, root.get("createdAt"));
        Expression<Long> countExpression = builder.count(root);

        query.multiselect(timeExpression, countExpression);
        query.where(
                builder.equal(
                        builder.function("YEAR", Integer.class, root.get("createdAt")),
                        year
                )
        );
        query.groupBy(timeExpression);
        query.orderBy(builder.asc(timeExpression));

        return session.createQuery(query).getResultList();
    }
}
