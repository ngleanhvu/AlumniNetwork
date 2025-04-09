package com.finalterm.alumninetwork.controller.rest;

import com.finalterm.alumninetwork.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/surveys")
public class ApiSurveyController {
    @Autowired
    private SurveyService surveyService;

//    @DeleteMapping("/{id}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void deleteSurvey(@PathVariable("id") Integer surveyId) {
//        this.surveyService.deleteSurveyById(surveyId);
//    }


}
