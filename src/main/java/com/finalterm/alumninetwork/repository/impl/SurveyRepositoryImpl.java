package com.finalterm.alumninetwork.repository.impl;

import com.finalterm.alumninetwork.pojo.Survey;
import com.finalterm.alumninetwork.repository.SurveyRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.expression.ParseException;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Repository
@Transactional
public class SurveyRepositoryImpl implements SurveyRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactory;
    @Autowired
    private Environment env;

    @Override
    public List<Survey> getSurveys(Map<String, String> params) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Survey> query = builder.createQuery(Survey.class);
        Root<Survey> root = query.from(Survey.class);
        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();

            String keyword = params.get("kw");
            if (keyword != null && !keyword.trim().isEmpty()) {
                predicates.add(
                        builder.or(
                                builder.like(root.get("title"), "%" + keyword + "%"),
                                builder.like(root.get("description"), "%" + keyword + "%")
                        )
                );
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String startDate = params.get("startDate");
            if (startDate != null && !startDate.trim().isEmpty()) {
                try {
                    Date start = sdf.parse(startDate);
                    predicates.add(builder.greaterThanOrEqualTo(root.get("startTime"), start));
                } catch (ParseException | java.text.ParseException e) {
                    e.printStackTrace();
                }
            }
            String endDate = params.get("endDate");
            if (endDate != null && !endDate.trim().isEmpty()) {
                try {
                    Date end = sdf.parse(endDate);
                    predicates.add(builder.lessThanOrEqualTo(root.get("endTime"), end));
                } catch (ParseException | java.text.ParseException e) {
                    e.printStackTrace();
                }
            }
            if (!predicates.isEmpty()) {
                query.where(predicates.toArray(new Predicate[0]));
            }
        }

        Query q = session.createQuery(query);
        if (params != null) {
            try {
                int PAGE_SIZE = Integer.parseInt(Objects.requireNonNull(env.getProperty("PAGE_SIZE")));
                String pageStr = params.getOrDefault("page", "1");
                if (pageStr != null && !pageStr.trim().isEmpty()) {
                    int p = Integer.parseInt(pageStr);
                    int start = (p - 1) * PAGE_SIZE;
                    q.setFirstResult(start);
                    q.setMaxResults(PAGE_SIZE);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return q.getResultList();
    }

    @Override
    public boolean saveSurvey(Survey survey) {
        Session session = sessionFactory.getObject().getCurrentSession();
        if (survey.getId() == null) {
            session.persist(survey);
        } else {
            session.merge(survey);
        }
        return true;
    }

    @Override
    public Survey getSurveyById(Integer surveyId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        Query query = session.createQuery("from Survey where id = :id");
        query.setParameter("id", surveyId);
        return (Survey) query.getSingleResult();
    }

    @Override
    public void deleteSurveyById(Integer surveyId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        Survey survey = getSurveyById(surveyId);
        session.remove(survey);
    }
}
