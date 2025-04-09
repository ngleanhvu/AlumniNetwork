package com.finalterm.alumninetwork.formatter;

import com.finalterm.alumninetwork.pojo.Survey;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

public class SurveyFormatter implements Formatter<Survey> {
    @Override
    public Survey parse(String surveyId, Locale locale) throws ParseException {
        Survey survey = new Survey();
        survey.setId(Integer.parseInt(surveyId));
        return survey;
    }

    @Override
    public String print(Survey survey, Locale locale) {
        return String.valueOf(survey.getId());
    }
}
