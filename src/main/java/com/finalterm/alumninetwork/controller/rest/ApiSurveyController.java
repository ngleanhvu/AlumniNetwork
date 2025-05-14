package com.finalterm.alumninetwork.controller.rest;

import com.finalterm.alumninetwork.dto.QuestionChoiceDto;
import com.finalterm.alumninetwork.dto.StatsSurveyDto;
import com.finalterm.alumninetwork.dto.response.ChoiceDto;
import com.finalterm.alumninetwork.dto.response.QuestionDto;
import com.finalterm.alumninetwork.dto.response.SurveyDto;
import com.finalterm.alumninetwork.pojo.Question;
import com.finalterm.alumninetwork.pojo.Survey;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.pojo.UserSurveyChoice;
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
        return ResponseEntity.ok(surveyService.statUserSurveyChoices(surveyId));
    }

    @GetMapping
    public ResponseEntity<List<SurveyDto>> getSurveys(@RequestParam Map<String, String> params) {
        List<Survey> surveys = surveyService.getSurveys(params);
        List<SurveyDto> surveyDtos = surveys.stream()
                .map(survey -> {
                    SurveyDto surveyDto = new SurveyDto();
                    surveyDto.setId(survey.getId());
                    surveyDto.setTitle(survey.getTitle());
                    surveyDto.setDescription(survey.getDescription());
                    surveyDto.setStatus(survey.getStatus());
                    surveyDto.setEndTime(survey.getEndTime());
                    surveyDto.setStartTime(survey.getStartTime());
                    return surveyDto;
                })
                .toList();
        return ResponseEntity.ok(surveyDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<QuestionDto>> getSurvey(@PathVariable Integer id,
                                                       @RequestParam Map<String, String> params) {
        List<Question> questions = this.surveyService.getSurveyQuestions(id, null);
        List<QuestionDto> questionDtos = questions.stream()
                .map(question -> {
                    QuestionDto questionDto = new QuestionDto();
                    questionDto.setId(question.getId());
                    questionDto.setContent(question.getContent());
                    List<ChoiceDto> choiceDtos = question.getChoices()
                            .stream()
                            .map(choice -> {
                                ChoiceDto choiceDto = new ChoiceDto();
                                choiceDto.setId(choice.getId());
                                choiceDto.setContent(choice.getContent());
                                return choiceDto;
                            })
                            .toList();
                    questionDto.setChoiceDtos(choiceDtos);
                    return questionDto;
                })
                .toList();
        return ResponseEntity.ok(questionDtos);
    }

    @PostMapping("/answer-survey")
    @ResponseStatus(HttpStatus.OK)
    public void answerSurvey(@RequestBody List<QuestionChoiceDto> questionChoiceDtos) {
        User user = userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        this.surveyService.addUserSurveyChoice(user, questionChoiceDtos);
    }

    @GetMapping("/answer-survey/{id}")
    public ResponseEntity<List<QuestionChoiceDto>> getAnswerSurvey(@PathVariable Integer id) {
        User user = userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        List<UserSurveyChoice> userSurveyChoices = this.surveyService.getUserSurveyChoices(id, user);
        List<QuestionChoiceDto> questionChoiceDtos = userSurveyChoices.stream()
                .map(q -> {
                    QuestionChoiceDto questionChoiceDto = new QuestionChoiceDto();
                    questionChoiceDto.setChoiceId(q.getChoice().getId());
                    questionChoiceDto.setQuestionId(q.getQuestion().getId());
                    return questionChoiceDto;
                })
                .toList();
        return ResponseEntity.ok(questionChoiceDtos);
    }
}
