//package com.finalterm.alumninetwork.service;
//
//import com.finalterm.alumninetwork.dto.StatsSurveyDto;
//import com.finalterm.alumninetwork.pojo.Choice;
//import com.finalterm.alumninetwork.pojo.Question;
//import com.finalterm.alumninetwork.pojo.Survey;
//import com.finalterm.alumninetwork.repository.SurveyRepository;
//import com.finalterm.alumninetwork.service.impl.SurveyServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class SurveyServiceImplTest {
//
//    @InjectMocks
//    private SurveyServiceImpl surveyServiceImpl;
//
//    @Mock
//    private SurveyRepository surveyRepository;
//
//    private List<Survey> surveys;
//
//    @BeforeEach
//    void setUp() {
//        surveys = new ArrayList<>();
//        Survey s1 = new Survey();
//        s1.setId(1);
//        s1.setTitle("title1");
//        s1.setDescription("description1");
//
//        Question question1 = new Question();
//        question1.setId(1);
//        question1.setContent("content1");
//
//        Choice choice1 = new Choice();
//        choice1.setId(1);
//        choice1.setContent("content-choice1");
//
//        Choice choice2 = new Choice();
//        choice1.setId(1);
//        choice1.setContent("content-choice2");
//
//        s1.getQuestions().add(question1);
//        question1.getChoices().addAll(Arrays.asList(choice1, choice2));
//        surveys.add(s1);
//
//        Survey s2 = new Survey();
//        s2.setId(2);
//        s2.setTitle("title2");
//        s2.setDescription("description2");
//
//        Question question2 = new Question();
//        question2.setId(2);
//        question2.setContent("content2");
//
//        Choice choice3 = new Choice();
//        choice1.setId(3);
//        choice1.setContent("content-choice3");
//
//        Choice choice4 = new Choice();
//        choice1.setId(4);
//        choice1.setContent("content-choice4");
//
//        s2.getQuestions().add(question2);
//        question2.getChoices().addAll(Arrays.asList(choice3, choice4));
//        surveys.add(s2);
//    }
//
//    @Test
//    @Tag("getSurvey")
//    void testGetSurveys_NoParameters() {
//        when(surveyRepository.getSurveys(null)).thenReturn(surveys);
//        assertEquals(surveys.size(), surveyServiceImpl.getSurveys(null).size());
//    }
//
//    @Test
//    @Tag("getSurvey")
//    void testGetSurveys_WithParameters() {
//        Map<String, String> map = new HashMap<>();
//        map.put("kw", "title1");
//        surveys.remove(0);
//        when(surveyRepository.getSurveys(map)).thenReturn(surveys);
//        assertEquals(surveys.size(), surveyServiceImpl.getSurveys(map).size());
//    }
//
//    @Test
//    @Tag("getSurvey")
//    void testGetSurveyById() {
//        Integer surveyId = 1;
//        when(surveyRepository.getSurveyById(surveyId)).thenReturn(surveys.get(0));
//        assertEquals(surveys.get(0).getTitle(), surveyServiceImpl.getSurveyById(surveyId).getTitle());
//    }
//
//}
