package com.example.parent_application;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private String id;
    private String text;
    private String answer;
    private List<Question> subQuestions;

    public Question(String id, String text, String answer) {
        this.id = id;
        this.text = text;
        this.answer = answer;
        this.subQuestions = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getAnswer() {
        return answer;
    }

    public List<Question> getSubQuestions() {
        return subQuestions;
    }

    public void addSubQuestion(Question subQuestion) {
        this.subQuestions.add(subQuestion);
    }
}
