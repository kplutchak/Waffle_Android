package com.pipit.waffle.Objects;

/**
 * Created by Eric on 12/13/2014.
 */
public class Choice {


    private String answerBody;
    private String answerID;
    private String questionID;
    private String url;
    private int votes;

    public Choice(){
        setVotes(0);
    }

    public String getQuestionID() {
        return this.questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public String getAnswerID() {
        return answerID;
    }

    public void setAnswerID(String answerID) {
        this.answerID = answerID;
    }

    public String getAnswerBody() {
        return answerBody;
    }

    public void setAnswerBody(String answerBody) {
        this.answerBody = answerBody;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
