package com.finalterm.alumninetwork.repository.impl;

import com.finalterm.alumninetwork.pojo.Post;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.repository.PostRepository;
import com.google.api.client.util.DateTime;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
@Transactional
public class PostRepositoryImpl implements PostRepository {
    @Autowired
    private LocalSessionFactoryBean factoryBean;

    @Autowired
    private Environment env;

    @Override
    public Post saveOrUpdate(Post p) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        if (p.getId() == null)
            session.persist(p);
        else
            p = (Post) session.merge(p);
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
    public Integer countPosts() {
        Session session = this.factoryBean.getObject().getCurrentSession();
        Query query = session.createNamedQuery("Post.count");
        Long count = (Long) query.getSingleResult();
        return count.intValue();
    }

    @Override
    public List<Post> getAll(Map<String, String> params) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Post> query = builder.createQuery(Post.class);
        Root<Post> root = query.from(Post.class);

        Join<Post, User> userJoin = root.join("user", JoinType.INNER);

        if (params != null && !params.isEmpty()) {
            List<Predicate> predicates = new ArrayList<>();
            String key = params.get("kw");
            if (key != null && !key.isEmpty()) {
                predicates.add(
                        builder.or(
                                builder.like(root.get("content"), String.format("%%%s%%", key)),
                                builder.like(root.get("title"), String.format("%%%s%%", key)),
                                builder.like(userJoin.get("fullName"), String.format("%%%s%%", key))
                        )
                );
                query.where(predicates.toArray(Predicate[]::new));
            }
        }
        query.orderBy(builder.desc(root.get("createdAt")));

        Query q = session.createQuery(query);
        if (params != null && !params.isEmpty()) {

            int PAGE_SIZE = params.get("limit") == null ?
                    Integer.parseInt(Objects.requireNonNull(env.getProperty("PAGE_SIZE"))) :
                    Integer.parseInt(params.get("limit"));

            String page = params.get("page") == null ? "1" : params.get("page");

            int p = Integer.parseInt(page);
            int start = (p -1) * PAGE_SIZE;

            q.setFirstResult(start);
            q.setMaxResults(PAGE_SIZE);

        }

        return q.getResultList();
    }

    @Override
    public List<Post> getPostByPostIds(List<Integer> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return Collections.emptyList();
        }

        Session session = this.factoryBean.getObject().getCurrentSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Post> criteriaQuery = criteriaBuilder.createQuery(Post.class);
        Root<Post> root = criteriaQuery.from(Post.class);

        root.fetch("images", JoinType.LEFT); // fetch images
        criteriaQuery.select(root).where(root.get("id").in(postIds)).distinct(true);

        return session.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public List<Post> getPostPaginate(int userId, Date cursorTime, int limit) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Post> criteriaQuery = criteriaBuilder.createQuery(Post.class);
        Root<Post> root = criteriaQuery.from(Post.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));
        predicates.add(criteriaBuilder.lessThan(root.get("createdAt"), cursorTime));

        criteriaQuery.select(root)
                .where(predicates.toArray(Predicate[]::new))
                .orderBy(criteriaBuilder.desc(root.get("createdAt")));

        Query query = session.createQuery(criteriaQuery);
        query.setMaxResults(limit);

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
