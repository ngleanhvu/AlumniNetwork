package com.finalterm.alumninetwork.service.impl;

import com.finalterm.alumninetwork.dto.QuestionChoiceDto;
import com.finalterm.alumninetwork.dto.StatsSurveyDto;
import com.finalterm.alumninetwork.pojo.*;
import com.finalterm.alumninetwork.repository.SurveyRepository;
import com.finalterm.alumninetwork.repository.UserRepository;
import com.finalterm.alumninetwork.service.SurveyService;
import com.finalterm.alumninetwork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public void addUserSurveyChoice(User user, List<QuestionChoiceDto> questionChoiceDtos) {
        List<Integer> questionIds = questionChoiceDtos.stream().map(QuestionChoiceDto::getQuestionId).toList();
        List<Integer> choiceIds = questionChoiceDtos.stream().map(QuestionChoiceDto::getChoiceId).toList();

        List<Question> questions = surveyRepository.getQuestionByIds(questionIds);
        List<Choice> choices = surveyRepository.getChoiceByIds(choiceIds);

        List<UserSurveyChoice> userSurveyChoices = new ArrayList<>();

        int length = questions.size();

        for (int i = 0; i < length; i++) {
            Question question = questions.get(i);
            Choice choice = choices.get(i);
            UserSurveyChoice userSurveyChoice = new UserSurveyChoice();
            userSurveyChoice.setQuestion(question);
            userSurveyChoice.setChoice(choice);
            userSurveyChoice.setUser(user);
            userSurveyChoices.add(userSurveyChoice);
        }

        this.surveyRepository.addUserSurveyChoice(userSurveyChoices);
    }

    @Override
    public long countSurveys() {
        return this.surveyRepository.countSurveys();
    }

}
