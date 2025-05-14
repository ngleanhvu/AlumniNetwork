package com.finalterm.alumninetwork.repository.impl;

import com.finalterm.alumninetwork.pojo.Event;
import com.finalterm.alumninetwork.repository.EventRepository;
import com.mysql.cj.xdevapi.SessionFactory;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
@Transactional
public class EventRepositoryImpl implements EventRepository {
    @Autowired
    private LocalSessionFactoryBean factoryBean;
    @Autowired
    private Environment env;

    @Override
    public List<Event> getAllEvent(Map<String, String> params) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        Root<Event> root = cq.from(Event.class);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<Predicate>();
            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                predicates.add(
                        cb.or(
                                cb.like(root.get("title"), String.format("%%%s%%", kw)),
                                cb.like(root.get("description"), String.format("%%%s%%", kw)),
                                cb.like(root.get("content"), String.format("%%%s%%", kw))
                        )
                );
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String startTime = params.get("startTime");
            if (startTime != null && !startTime.isEmpty()) {
                try {
                    Date startDate = sdf.parse(startTime);
                    predicates.add(cb.greaterThanOrEqualTo(root.get("startTime"), startDate));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            String endTime = params.get("endTime");
            if (endTime != null && !endTime.isEmpty()) {
                try {
                    Date endDate = sdf.parse(endTime);
                    predicates.add(cb.lessThanOrEqualTo(root.get("endTime"), endDate));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            cq.where(predicates.toArray(Predicate[]::new));
        }

        Query query = session.createQuery(cq);

        if (params != null) {
            int PAGE_SIZE = Integer.parseInt(Objects.requireNonNull(env.getProperty("PAGE_SIZE")));
            String pageStr = params.getOrDefault("page", "1");
            if (pageStr != null && !pageStr.trim().isEmpty()) {
                int p = Integer.parseInt(pageStr);
                int start = (p - 1) * PAGE_SIZE;
                query.setFirstResult(start);
                query.setMaxResults(PAGE_SIZE);
            }
        }

        return query.getResultList();
    }

    @Override
    @Transactional
    public Event saveEvent(Event e) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        if (e.getId() == null)
            session.persist(e);
        else
            session.merge(e);
        session.refresh(e);
        return e;
    }

    @Override
    public boolean deleteEventById(Integer id) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        Event event = session.get(Event.class, id);

        if (event != null)
            session.remove(event);
        else
            return false;
        return true;
    }

    @Override
    public Event getEventById(Integer eventId) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        Query query = session.createNamedQuery("Event.findById", Event.class);
        query.setParameter("id", eventId);
        return (Event) query.getSingleResult();
    }

    @Override
    public long countEvents() {
        Session session = this.factoryBean.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Event> root = cq.from(Event.class);
        cq.select(cb.count(root));
        return session.createQuery(cq).getSingleResult();
    }
}
