package com.finalterm.alumninetwork.repository.impl;

import com.finalterm.alumninetwork.pojo.Post;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
        Root<Post> root = criteriaQuery.from(Post.class);

        Predicate userPredicate = criteriaBuilder.equal(root.get("user").get("id"), userId);
        criteriaQuery.where(userPredicate);

        Query query = session.createQuery(criteriaQuery);
        return query.getResultList();
    }
}
