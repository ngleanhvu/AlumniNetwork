package com.finalterm.alumninetwork.service;

import com.finalterm.alumninetwork.dto.QuestionChoiceDto;
import com.finalterm.alumninetwork.dto.StatsSurveyDto;
import com.finalterm.alumninetwork.pojo.Survey;
import com.finalterm.alumninetwork.pojo.User;

import java.util.List;
import java.util.Map;

public interface SurveyService {
    List<Survey> getSurveys(Map<String, String> params);
    boolean saveSurvey(Survey survey);
    Survey getSurveyById(Integer surveyId);
    void deleteSurveyById(Integer surveyId);
    List<StatsSurveyDto> statUserSurveyChoices(Integer surveyId);
    void addUserSurveyChoice(User user, List<QuestionChoiceDto> questionChoiceDtos);
    long countSurveys();
}
