package com.finalterm.alumninetwork.repository;

import com.finalterm.alumninetwork.dto.StatsSurveyDto;
import com.finalterm.alumninetwork.pojo.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface SurveyRepository {
    List<Survey> getSurveys(Map<String, String> params);
    boolean saveSurvey(Survey survey);
    Survey getSurveyById(Integer surveyId);
    void deleteSurveyById(Integer surveyId);
    List<StatsSurveyDto> statsUserSurveyChoice(Integer surveyId);
    void addUserSurveyChoice(List<UserSurveyChoice> userSurveyChoices);
    List<Question> getQuestionByIds(List<Integer> questionIds);
    List<Choice> getChoiceByIds(List<Integer> choiceIds);
    long countSurveys();
    List<Question> getQuestionsBySurveyId(Integer surveyId, Map<String, String> params);
    List<UserSurveyChoice> getUserSurveyChoicesBySurveyIdAndUserId(Integer surveyId, User user);
}
