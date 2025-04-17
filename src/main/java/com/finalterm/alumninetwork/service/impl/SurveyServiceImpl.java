package com.finalterm.alumninetwork.service.impl;

import com.finalterm.alumninetwork.dto.StatsSurveyDto;
import com.finalterm.alumninetwork.pojo.Survey;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.repository.SurveyRepository;
import com.finalterm.alumninetwork.repository.UserRepository;
import com.finalterm.alumninetwork.service.SurveyService;
import com.finalterm.alumninetwork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Service
public class SurveyServiceImpl implements SurveyService {
    @Autowired
    private SurveyRepository surveyRepository;
    @Autowired
    private UserService userService;

    @Override
    public List<Survey> getSurveys(Map<String, String> params) {
        return this.surveyRepository.getSurveys(params);
    }

    @Override
    public boolean saveSurvey(Survey survey) {
        if (survey.getStartTime().after(survey.getEndTime())) {
            return false;
        }
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        User user = this.userService.getUserByUsername(principal.getName());
        survey.setUser(user);
        return this.surveyRepository.saveSurvey(survey);
    }

    @Override
    public Survey getSurveyById(Integer surveyId) {
        return this.surveyRepository.getSurveyById(surveyId);
    }

    @Override
    public void deleteSurveyById(Integer surveyId) {
        this.surveyRepository.deleteSurveyById(surveyId);
    }
    @Override
    public List<StatsSurveyDto> statUserSurveyChoices(Integer surveyId) {
        return this.surveyRepository.statsUserSurveyChoice(surveyId);
    }

}
