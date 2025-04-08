package com.finalterm.alumninetwork.repository;

import com.finalterm.alumninetwork.pojo.LecturerInfo;

import java.util.List;
import java.util.Map;

public interface LecturerInfoRepository {
    void saveLecturerInfo(LecturerInfo lecturerInfo);
    List<LecturerInfo> getLecturerInfos(Map<String, String> params);
    LecturerInfo getLecturerInfoById(Integer id);
}
