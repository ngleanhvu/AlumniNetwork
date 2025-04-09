package com.finalterm.alumninetwork.service;

import com.finalterm.alumninetwork.pojo.Survey;

import java.util.List;
import java.util.Map;

public interface SurveyService {
    List<Survey> getSurveys(Map<String, String> params);
    boolean saveSurvey(Survey survey);
    Survey getSurveyById(Integer surveyId);
    void deleteSurveyById(Integer surveyId);
}
