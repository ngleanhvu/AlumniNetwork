package com.finalterm.alumninetwork.repository.impl;

import com.finalterm.alumninetwork.pojo.LecturerInfo;
import com.finalterm.alumninetwork.repository.LecturerInfoRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class LecturerInfoRepositoryImpl implements LecturerInfoRepository {
    @Autowired
    private LocalSessionFactoryBean sessionFactory;
    @Override
    public void addLecturerInfo(LecturerInfo lecturerInfo) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.persist(lecturerInfo);
    }
}
