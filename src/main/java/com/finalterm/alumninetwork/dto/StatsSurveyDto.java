package com.finalterm.alumninetwork.dto;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.HashMap;
import java.util.Map;

public class StatsSurveyDto {
    private String content;
    private Map<String, Long> chocies = new HashMap<String, Long>();

    public Map<String, Long> getChocies() {
        return chocies;
    }

    public void setChocies(Map<String, Long> chocies) {
        this.chocies = chocies;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
