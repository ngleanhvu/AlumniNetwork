package com.finalterm.alumninetwork.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class QuestionChoiceDto {
    @NotEmpty
    @NotNull
    private Integer questionId;

    @NotNull
    @NotEmpty
    private Integer choiceId;

    public QuestionChoiceDto() {}

    public QuestionChoiceDto(Integer questionId, Integer choiceId) {
        this.questionId = questionId;
        this.choiceId = choiceId;
    }

    public Integer getChoiceId() {
        return choiceId;
    }

    public void setChoiceId(Integer choiceId) {
        this.choiceId = choiceId;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }
}
