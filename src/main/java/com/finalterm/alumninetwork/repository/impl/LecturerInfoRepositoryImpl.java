package com.finalterm.alumninetwork.repository.impl;

import com.finalterm.alumninetwork.pojo.LecturerInfo;
import com.finalterm.alumninetwork.repository.LecturerInfoRepository;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class LecturerInfoRepositoryImpl implements LecturerInfoRepository {
    @Autowired
    private LocalSessionFactoryBean sessionFactory;
    @Override
    public void saveLecturerInfo(LecturerInfo lecturerInfo) {
        Session session = sessionFactory.getObject().getCurrentSession();
        if (lecturerInfo == null) {
            session.persist(lecturerInfo);
        } else {
            session.merge(lecturerInfo);
        }
    }

    @Override
    public List<LecturerInfo> getLecturerInfos(Map<String, String> params) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<LecturerInfo> criteriaQuery = criteriaBuilder.createQuery(LecturerInfo.class);
        Root<LecturerInfo> root = criteriaQuery.from(LecturerInfo.class);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();
            String changedPassword = params.get("changedPassword");
            if (changedPassword != null && !changedPassword.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("changedPassword"), Boolean.parseBoolean(changedPassword)));
            }
            String expiredResetPasswordTime = params.get("expiredResetPasswordTime");
            if (expiredResetPasswordTime != null && !expiredResetPasswordTime.isEmpty()) {
                LocalDate date = LocalDate.parse(expiredResetPasswordTime);
                predicates.add(criteriaBuilder.equal(root.get("expiredResetPasswordTime"), java.sql.Date.valueOf(date)));
            }
            String username = params.get("username");
            if (username != null && !username.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("user").get("username"), username));
            }

            criteriaQuery.where(predicates.toArray(Predicate[]::new));
        }
        return session.createQuery(criteriaQuery).list();
    }


    @Override
    public LecturerInfo getLecturerInfoById(Integer id) {
        Session session = sessionFactory.getObject().getCurrentSession();
        Query query = session.createQuery("from LecturerInfo where id = :id");
        query.setParameter("id", id);
        return (LecturerInfo) query.uniqueResult();
    }
}
