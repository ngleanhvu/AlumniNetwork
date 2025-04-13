package com.finalterm.alumninetwork.repository.impl;

import com.finalterm.alumninetwork.pojo.Comment;
import com.finalterm.alumninetwork.pojo.Reaction;
import com.finalterm.alumninetwork.repository.ReactionRepository;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ReactionRepositoryImpl implements ReactionRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public Reaction addOrUpdateReaction(Reaction reaction) {
        Session session = sessionFactory.getObject().getCurrentSession();
        if (reaction.getId() == null) {
            session.persist(reaction);
        } else
            session.merge(reaction);

        session.refresh(reaction);
        return reaction;
    }

    @Override
    public List<Reaction> getTypeReactionsByPostId(int postId, String type) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Reaction> query = builder.createQuery(Reaction.class);
        Root<Reaction> root = query.from(Reaction.class);
        List<Predicate> predicates = new ArrayList<Predicate>();

        predicates.add(
                builder.and(
                        builder.equal(root.get("post").get("id"), postId),
                        builder.equal(root.get("type"),type))
        );
        query.where(predicates.toArray(Predicate[]::new));
        return session.createQuery(query).getResultList();
    }

    @Override
    public void deleteReaction(int postId, int userId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Reaction> query = builder.createQuery(Reaction.class);
        Root<Reaction> root = query.from(Reaction.class);

        query.where(
                builder.and(
                        builder.equal(root.get("post").get("id"), postId),
                        builder.equal(root.get("user").get("id"), userId)
                )
        );

        try {
            Reaction reaction = session.createQuery(query).getSingleResult();
            session.remove(reaction);
        } catch (NoResultException ex) {

        }
    }

    @Override
    public long countTotalByPostId(int postId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Reaction> root = query.from(Reaction.class);

        query.select(builder.count(root));
        query.where(builder.equal(root.get("post").get("id"), postId));

        return session.createQuery(query).getSingleResult();
    }

    @Override
    public long countByPostIdAndType(int postId, String type) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Reaction> root = query.from(Reaction.class);

        query.select(builder.count(root));
        query.where(builder.and(
                builder.equal(root.get("post").get("id"), postId),
                builder.equal(root.get("type"), type)
        ));

        return session.createQuery(query).getSingleResult();
    }

    @Override
    public Optional<Reaction> existsReaction(int postId, int userId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Reaction> query = builder.createQuery(Reaction.class);
        Root<Reaction> root = query.from(Reaction.class);

        query.where(
                builder.and(
                        builder.equal(root.get("post").get("id"), postId),
                        builder.equal(root.get("user").get("id"), userId)
                )
        );
        List<Reaction> result = session.createQuery(query).getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }
}
