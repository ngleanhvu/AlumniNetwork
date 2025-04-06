package com.finalterm.alumninetwork.repository.impl;

import com.finalterm.alumninetwork.pojo.Event;
import com.finalterm.alumninetwork.pojo.LecturerInfo;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.pojo.UserRole;
import com.finalterm.alumninetwork.repository.UserRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {
    @Autowired
    private LocalSessionFactoryBean factoryBean;

    @Override
    public List<User> getAllAdmin() {
        Session session = this.factoryBean.getObject().getCurrentSession();
        Query query = session.createQuery("FROM User WHERE role = :role", User.class);
        query.setParameter("role", UserRole.ROLE_ADMIN);
        return query.getResultList();
    }

    @Override
    public void addUser(User user) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        session.persist(user);
    }

    @Override
    public User getUserByUsername(String username) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        Query query = session.createQuery("FROM User WHERE username = :username", User.class);
        query.setParameter("username", username);
        return (User) query.getSingleResult();
    }

    @Override
    public User getUserById(int id) {
        Session s = this.factoryBean.getObject().getCurrentSession();
        return s.get(User.class, id);
    }
    @Override
    public void saveAllUser(List<User> users) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        for (User user : users) {
            session.merge(user);
        }
    }

}
