package com.finalterm.alumninetwork.repository;

import com.finalterm.alumninetwork.pojo.Survey;

import java.util.List;
import java.util.Map;

public interface SurveyRepository {
    List<Survey> getSurveys(Map<String, String> params);
    boolean saveSurvey(Survey survey);
    Survey getSurveyById(Integer surveyId);
    void deleteSurveyById(Integer surveyId);
}
