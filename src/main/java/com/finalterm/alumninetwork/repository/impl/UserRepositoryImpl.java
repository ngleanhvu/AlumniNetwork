package com.finalterm.alumninetwork.repository.impl;

import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.pojo.UserRole;
import com.finalterm.alumninetwork.repository.UserRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.geo.Circle;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {
    @Autowired
    private LocalSessionFactoryBean factoryBean;
    @Autowired
    private Environment env;

    @Override
    public List<User> getAllAdmin() {
        Session session = this.factoryBean.getObject().getCurrentSession();
        Query query = session.createQuery("FROM User WHERE role = :role", User.class);
        query.setParameter("role", UserRole.ROLE_ADMIN);
        return query.getResultList();
    }

    @Override
    public void saveUser(User user) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        if (user.getId() == null) {
            session.persist(user);
        } else {
            session.merge(user);
        }
    }

    @Override
    public User getUserByUsername(String username) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        Query query = session.createNamedQuery("User.findByUsername", User.class);
        query.setParameter("username", username);
        return (User) query.getSingleResult();
    }

    @Override
    public void saveAllUser(List<User> users) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        for (User user : users) {
            session.merge(user);
        }
    }

    @Override
    public List<User> getUsers(Map<String, String> params) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<Predicate>();
            String keyword = params.get("kw");
            if (!keyword.isEmpty()) {
                predicates.add(
                        builder.or(
                                builder.like(root.get("username"), String.format("%%%s%%", keyword)),
                                builder.like(root.get("email"), String.format("%%%s%%", keyword)),
                                builder.like(root.get("phone"), String.format("%%%s%%", keyword)),
                                builder.like(root.get("fullName"), String.format("%%%s%%", keyword))
                        )
                );
            }
            query.where(predicates.toArray(Predicate[]::new));
        }

        Query q = session.createQuery(query);

        if (params != null) {
            int PAGE_SIZE = Integer.parseInt(Objects.requireNonNull(env.getProperty("PAGE_SIZE")));
            String page = params.get("page") == null ? "1" : params.get("page");
            if (page != null && !page.isEmpty()) {
                int p = Integer.parseInt(page);
                int start = (p - 1) * PAGE_SIZE;

                q.setFirstResult(start);
                q.setMaxResults(PAGE_SIZE);
            }
        }

        return q.getResultList();
    }



    @Override
    public void deleteUser(User user) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        user.setActive(false);
        session.merge(user);
    }

    @Override
    public User getUserByEmail(String email) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.where(builder.equal(root.get("email"), email));
        Query q = session.createQuery(query);
        return (User) q.getSingleResult();
    }

    @Override
    public User getUserById(Integer userId) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        Query query = session.createNamedQuery("User.findById", User.class);
        query.setParameter("id", userId);
        return (User) query.getSingleResult();
    }

    @Override
    public List<User> getAllUserExactAdmin() {
        Session session = this.factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.where(builder.not(
                builder.equal(root.get("role"), UserRole.ROLE_ADMIN)
        ));
        Query q = session.createQuery(query);
        return q.getResultList();
    }

    @Override
    public List<User> getUserByIds(List<Integer> userIds) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.select(root).where(root.get("id").in(userIds));
        return session.createQuery(query).getResultList();
    }

    @Override
    public long countUsers() {
        Session session = this.factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<User> root = query.from(User.class);
        query.select(builder.count(root));
        return session.createQuery(query).getSingleResult();
    }

}
