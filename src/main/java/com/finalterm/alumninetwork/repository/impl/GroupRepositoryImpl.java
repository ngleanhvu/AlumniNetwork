package com.finalterm.alumninetwork.repository.impl;

import com.finalterm.alumninetwork.pojo.GroupNetwork;
import com.finalterm.alumninetwork.pojo.GroupNetworkUser;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.repository.GroupRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
@Transactional
public class GroupRepositoryImpl implements GroupRepository {
    @Autowired
    private Environment env;
    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public void saveGroup(GroupNetwork groupNetwork) {
        Session session = sessionFactory.getObject().getCurrentSession();
        if (groupNetwork.getId() == null) {
            session.persist(groupNetwork);
        } else {
            session.merge(groupNetwork);
        }
    }

    @Override
    public List<GroupNetwork> getGroups(Map<String, String> params) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<GroupNetwork> query = builder.createQuery(GroupNetwork.class);
        Root<GroupNetwork> root = query.from(GroupNetwork.class);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();
            String groupName = params.get("groupName");
            if (!groupName.isEmpty()) {
                predicates.add(builder.like(root.get("groupName"), String.format("%%%s%%", groupName)));
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
    public GroupNetwork getGroupById(Integer groupId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        Query query = session.createNamedQuery("GroupNetwork.findById", GroupNetwork.class);
        query.setParameter("id", groupId);
        return (GroupNetwork) query.getSingleResult();
    }

    @Override
    public boolean deleteGroup(Integer groupId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.remove(getGroupById(groupId));
        return true;
    }

    @Override
    public List<User> getAllUserGroupsById(List<Integer> groupIds) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<GroupNetworkUser> cq = cb.createQuery(GroupNetworkUser.class);
        Root<GroupNetworkUser> root = cq.from(GroupNetworkUser.class);
        Predicate predicate = root.get("groupNetwork").get("id").in(groupIds);
        cq.where(predicate);
        List<GroupNetworkUser> groupNetworkUsers = session.createQuery(cq).getResultList();
        Set<User> users = new HashSet<>();
        for (GroupNetworkUser groupNetworkUser : groupNetworkUsers) {
            users.add(groupNetworkUser.getUser());
        }
        return new ArrayList<>(users);
    }
}
