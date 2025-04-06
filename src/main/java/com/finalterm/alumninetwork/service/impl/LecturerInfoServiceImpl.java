package com.finalterm.alumninetwork.service.impl;

import com.finalterm.alumninetwork.pojo.LecturerInfo;
import com.finalterm.alumninetwork.repository.LecturerInfoRepository;
import com.finalterm.alumninetwork.service.LecturerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LecturerInfoServiceImpl implements LecturerInfoService {

    @Autowired
    private LecturerInfoRepository lecturerInfoRepository;

    @Override
    public List<LecturerInfo> getLecturerInfos(Map<String, String> params) {
        return lecturerInfoRepository.getLecturerInfos(params);
    }
}
