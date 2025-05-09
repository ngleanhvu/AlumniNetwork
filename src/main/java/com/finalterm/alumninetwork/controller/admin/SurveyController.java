package com.finalterm.alumninetwork.controller.admin;

import com.finalterm.alumninetwork.pojo.Choice;
import com.finalterm.alumninetwork.pojo.Question;
import com.finalterm.alumninetwork.pojo.Survey;
import com.finalterm.alumninetwork.service.SurveyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
public class SurveyController {

    @Autowired
    private SurveyService surveyService;
    @Autowired
    private Environment env;

    @GetMapping("/surveys/admin")
    public String manageSurvey(Model model,
                               @ModelAttribute("kw") String kw,
                               @ModelAttribute("startDate") String startDate,
                               @ModelAttribute("endDate") String endDate,
                               @RequestParam(name = "page", defaultValue = "1", required = false) int page) {
        Map<String, String> params = new HashMap<>();
        params.put("kw", kw);
        params.put("startDate",startDate);
        params.put("endDate",endDate);
        params.put("page", String.valueOf(page));
        model.addAttribute("surveys", this.surveyService.getSurveys(params));
        long totalEvents = this.surveyService.countSurveys();
        int totalPages = (int) Math.ceil((double) totalEvents / Integer.parseInt(
                Objects.requireNonNull(env.getProperty("PAGE_SIZE"))));
        model.addAttribute("kw", kw);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", totalPages != 0 ? totalPages : 1);
        return "surveys";
    }

    @GetMapping("/surveys/admin/add")
    public String saveSurveyForm(Model model) {
        model.addAttribute("survey", new Survey());
        return "surveys-form";
    }

    @PostMapping("/surveys/admin/add")
    public String saveSurvey(@Valid @ModelAttribute("survey") Survey survey,
                             RedirectAttributes redirectAttrs,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "surveys-form";
        }
        for (Question question : survey.getQuestions()) {
            question.setSurvey(survey);
            for (Choice choice : question.getChoices()) {
                choice.setQuestion(question);
            }
        }
        boolean check = this.surveyService.saveSurvey(survey);
        if (check) {
            return "redirect:/surveys/admin";
        } else {
            return "surveys-form";
        }
    }

    @GetMapping("/surveys/admin/update/{id}")
    public String updateSurveyForm(@PathVariable("id") Integer surveyId,
                                   Model model) {
        Survey survey = this.surveyService.getSurveyById(surveyId);
        model.addAttribute("survey", survey);
        List<Question> questions = survey.getQuestions();
        model.addAttribute("questions", questions);
        return "surveys-form-update";
    }

//    @GetMapping("/surveys/admin/update/{id}")
//    public String updateSurveyForm(@PathVariable("id") Integer surveyId,
//                                   Model model) {
//        model.addAttribute("survey", this.surveyService.getSurveyById(surveyId));
//        return "surveys-form";
//    }

    @PostMapping("/surveys/admin/delete/{id}")
    public String deleteSurvey(@PathVariable("id") Integer surveyId,
                               RedirectAttributes redirectAttrs) {
        this.surveyService.deleteSurveyById(surveyId);
        redirectAttrs.addAttribute("msg", "Xóa thành công");
        return "redirect:/surveys/admin";
    }
}
