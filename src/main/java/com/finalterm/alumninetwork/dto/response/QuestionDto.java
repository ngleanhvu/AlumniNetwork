package com.finalterm.alumninetwork.dto.response;

import java.util.ArrayList;
import java.util.List;

public class QuestionDto {
    private int id;
    private String content;
    private List<ChoiceDto> choiceDtos = new ArrayList<ChoiceDto>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ChoiceDto> getChoiceDtos() {
        return choiceDtos;
    }

    public void setChoiceDtos(List<ChoiceDto> choiceDtos) {
        this.choiceDtos = choiceDtos;
    }
}
