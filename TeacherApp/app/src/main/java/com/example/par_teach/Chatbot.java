package com.example.par_teach;

import java.util.List;

public class Chatbot {
    private List<Question> questions;

    public Chatbot(List<Question> questions) {
        this.questions = questions;
    }

    public Question getQuestionById(String id) {
        for (Question question : questions) {
            if (question.getId().equals(id)) {
                return question;
            }
            Question subQuestion = getSubQuestionById(question.getSubQuestions(), id);
            if (subQuestion != null) {
                return subQuestion;
            }
        }
        return null;
    }

    private Question getSubQuestionById(List<Question> subQuestions, String id) {
        for (Question subQuestion : subQuestions) {
            if (subQuestion.getId().equals(id)) {
                return subQuestion;
            }
            Question deeperSubQuestion = getSubQuestionById(subQuestion.getSubQuestions(), id);
            if (deeperSubQuestion != null) {
                return deeperSubQuestion;
            }
        }
        return null;
    }
}
