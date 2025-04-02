package com.finalterm.alumninetwork.service.impl;

import com.finalterm.alumninetwork.pojo.LecturerInfo;
import com.finalterm.alumninetwork.repository.LecturerInfoRepository;
import com.finalterm.alumninetwork.repository.UserRepository;
import com.finalterm.alumninetwork.service.LockUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LockUserServiceImpl implements LockUserService {

    @Autowired
    private LecturerInfoRepository lecturerInfoRepository;

    @Scheduled(fixedRate = 3600000)
    @Override
    public void lockUsers() {
        Map<String, String> params = new HashMap<>();
        params.put("changedPassword", "false");
        List<LecturerInfo> lecturerInfos = lecturerInfoRepository.getLecturerInfos(params);
        for (LecturerInfo lecturerInfo : lecturerInfos) {

        }
    }
}
