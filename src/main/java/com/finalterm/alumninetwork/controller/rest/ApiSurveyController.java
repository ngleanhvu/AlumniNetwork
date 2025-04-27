package com.finalterm.alumninetwork.controller.rest;

import com.finalterm.alumninetwork.dto.QuestionChoiceDto;
import com.finalterm.alumninetwork.dto.StatsSurveyDto;
import com.finalterm.alumninetwork.pojo.Survey;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.service.SurveyService;
import com.finalterm.alumninetwork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/surveys")
@CrossOrigin
public class ApiSurveyController {
    @Autowired
    private SurveyService surveyService;
    @Autowired
    private UserService userService;

//    @DeleteMapping("/{id}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void deleteSurvey(@PathVariable("id") Integer surveyId) {
//        this.surveyService.deleteSurveyById(surveyId);
//    }

    @GetMapping("/stats/{surveyId}")
    public ResponseEntity<List<StatsSurveyDto>> statUserSurveyChoices(@PathVariable Integer surveyId) {
        return ResponseEntity
                .ok(this.surveyService.statUserSurveyChoices(surveyId));
    }

    @GetMapping
    public ResponseEntity<List<Survey>> getSurveys(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(this.surveyService.getSurveys(params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Survey> getSurvey(@PathVariable Integer id) {
        return ResponseEntity.ok(this.surveyService.getSurveyById(id));
    }

    @PostMapping("/answer-survey")
    @ResponseStatus(HttpStatus.OK)
    public void answerSurvey(@RequestBody List<QuestionChoiceDto> questionChoiceDtos) {
        User user = userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        this.surveyService.addUserSurveyChoice(user, questionChoiceDtos);
    }
}
