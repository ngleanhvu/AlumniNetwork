package com.finalterm.alumninetwork.repository.impl;

import com.finalterm.alumninetwork.pojo.Comment;
import com.finalterm.alumninetwork.pojo.EnumReaction;
import com.finalterm.alumninetwork.pojo.Reaction;
import com.finalterm.alumninetwork.repository.ReactionRepository;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
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

import java.util.*;

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
    public List<Reaction> getTypeReactionsByPostId(int postId, String type, int page) {
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

        query.where(predicates.toArray(Predicate[]::new)).orderBy(builder.desc(root.get("createdDate")));

        int start = (page - 1) * 6;
        int end = start + 6;

        Query query2 = session.createQuery(query);
        query2.setFirstResult(start);
        query2.setMaxResults(end);

        return query2.getResultList();
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
    public Integer countTotalByPostId(int postId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Reaction> root = query.from(Reaction.class);

        query.select(builder.count(root));
        query.where(builder.equal(root.get("post").get("id"), postId));

        return session.createQuery(query).getSingleResult().intValue();
    }

    @Override
    public Integer countByPostIdAndType(int postId, String type) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Reaction> root = query.from(Reaction.class);

        query.select(builder.count(root));
        query.where(builder.and(
                builder.equal(root.get("post").get("id"), postId),
                builder.equal(root.get("type"), type)
        ));

        return session.createQuery(query).getSingleResult().intValue();
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

    @Override
    public Map<String, Integer> statsReactionByPostId(int postId) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("LIKE", countByPostIdAndType(postId, EnumReaction.LIKE.name()));
        stats.put("LOVE", countByPostIdAndType(postId, EnumReaction.LOVE.name()));
        stats.put("HAHA", countByPostIdAndType(postId, EnumReaction.HAHA.name()));
        stats.put("WOW", countByPostIdAndType(postId, EnumReaction.WOW.name()));
        stats.put("SAD", countByPostIdAndType(postId, EnumReaction.SAD.name()));
        stats.put("TOTAL", countTotalByPostId(postId));
        return stats;
    }

    @Override
    public List<Reaction> getReactionsByPostId(int postId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        Query query = session.createNamedQuery("Reaction.findByPostId");
        query.setParameter("postId", postId);
        return query.getResultList();
    }

    @Override
    public void deleteReactionById(int reactionId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        Reaction reaction = session.get(Reaction.class, reactionId);
        session.remove(reaction);
    }

    @Override
    public Reaction getReactionById(int reactionId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        Query query = session.createNamedQuery("Reaction.findById", Reaction.class);
        query.setParameter("id", reactionId);
        return (Reaction) query.getSingleResult();
    }

    @Override
    public Reaction getReactionByPostIdAndUserId(int postId, int userId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Reaction> query = builder.createQuery(Reaction.class);
        Root<Reaction> root = query.from(Reaction.class);

        query.select(root).where(
                builder.and(
                        builder.equal(root.get("post").get("id"), postId),
                        builder.equal(root.get("user").get("id"), userId)
                )
        );

        TypedQuery<Reaction> typedQuery = session.createQuery(query);
        List<Reaction> results = typedQuery.getResultList();

        return results.isEmpty() ? null : results.get(0);
    }

}
