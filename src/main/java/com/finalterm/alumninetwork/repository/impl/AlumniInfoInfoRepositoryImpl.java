package com.finalterm.alumninetwork.repository.impl;

import com.finalterm.alumninetwork.pojo.AlumniInfo;
import com.finalterm.alumninetwork.repository.AlumniInfoRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AlumniInfoInfoRepositoryImpl implements AlumniInfoRepository {
    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public void addAlumniInfo(AlumniInfo alumniInfo) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.persist(alumniInfo);
    }
}
