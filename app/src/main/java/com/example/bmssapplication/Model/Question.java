package com.example.bmssapplication.Model;

public class Question {

    String qID,qQuestion,qAnswer;

    boolean showAnswer=false;

    public Question() {
    }

    public Question(String qID, String qQuestion, String qAnswer) {
        this.qID = qID;
        this.qQuestion = qQuestion;
        this.qAnswer = qAnswer;
        this.showAnswer = false;
    }

    public String getqID() {
        return qID;
    }

    public void setqID(String qID) {
        this.qID = qID;
    }

    public String getqQuestion() {
        return qQuestion;
    }

    public void setqQuestion(String qQuestion) {
        this.qQuestion = qQuestion;
    }

    public String getqAnswer() {
        return qAnswer;
    }

    public void setqAnswer(String qAnswer) {
        this.qAnswer = qAnswer;
    }

    public boolean isShowAnswer() {
        return showAnswer;
    }

    public void setShowAnswer(boolean showAnswer) {
        this.showAnswer = showAnswer;
    }
}
