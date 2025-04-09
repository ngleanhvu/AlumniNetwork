package com.finalterm.alumninetwork.controller.admin;

import com.finalterm.alumninetwork.pojo.Survey;
import com.finalterm.alumninetwork.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @GetMapping("/surveys/admin")
    public String manageSurvey(Model model,
                               @ModelAttribute("kw") String kw,
                               @ModelAttribute("startDate") String startDate,
                               @ModelAttribute("endDate") String endDate) {
//        Map<String, String> params = new HashMap<>();
//        params.put("kw", kw);
//        params.put("startDate",startDate);
//        params.put("endDate",endDate);
        model.addAttribute("surveys", this.surveyService.getSurveys(null));
        return "surveys";
    }

    @GetMapping("/surveys/admin/add")
    public String saveSurveyForm(Model model) {
        model.addAttribute("survey", new Survey());
        return "surveys-form";
    }

    @PostMapping("/surveys/admin/add")
    public String saveSurvey(@ModelAttribute("survey") Survey survey,
                             RedirectAttributes redirectAttrs) {
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
        model.addAttribute("survey", this.surveyService.getSurveyById(surveyId));
        return "surveys-form";
    }

    @PostMapping("/surveys/admin/delete/{id}")
    public String deleteSurvey(@PathVariable("id") Integer surveyId,
                               RedirectAttributes redirectAttrs) {
        this.surveyService.deleteSurveyById(surveyId);
        redirectAttrs.addAttribute("msg", "Xóa thành công");
        return "surveys";
    }
}
