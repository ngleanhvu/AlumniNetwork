package com.finalterm.alumninetwork.service;

import com.finalterm.alumninetwork.pojo.LecturerInfo;

import java.util.List;
import java.util.Map;


public interface LecturerInfoService {
    List<LecturerInfo> getLecturerInfos(Map<String, String> params);
}
