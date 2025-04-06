package com.finalterm.alumninetwork.repository.impl;

import com.finalterm.alumninetwork.pojo.Event;
import com.finalterm.alumninetwork.repository.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class EventRepositoryImpl implements EventRepository {
    @Autowired
    private LocalSessionFactoryBean factoryBean;

    @Override
    public List<Event> getAll() {
        Session session = this.factoryBean.getObject().getCurrentSession();
        Query query = session.createQuery("FROM Event", Event.class);

        return query.getResultList();
    }

    @Override
    public Event saveOrUpdate(Event e) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        if (e.getId() == null)
            session.persist(e);
        else
            session.merge(e);
        session.refresh(e);
        return e;
    }

    @Override
    public void delete(int id) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        Event event = session.get(Event.class, id);

        if (event != null)
            session.remove(event);
        else
            throw new EntityNotFoundException();
    }
}
