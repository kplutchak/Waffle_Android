package com.pipit.waffle.Objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric on 12/7/2014.
 */
public class Question {

    private String questionBody;
    private List<Choice> choices;
    private User asker;

    public Question(String qbody, User asker){
        setQuestionBody(qbody);
        choices = new ArrayList<Choice>();
        setAsker(asker);
    }

    public void addChoice(Choice ans){
        //ans.setQuestionID(questionID);
        this.choices.add(ans);
    }

    public void deleteAnswer(Choice ans){
        this.choices.remove(ans);
    }
    /**
     * @return the questionBody
     */
    public String getQuestionBody() {
        return questionBody;
    }

    /**
     * @param questionBody the questionBody to set
     */
    public void setQuestionBody(String questionBody) {
        this.questionBody = questionBody;
    }

    /**
     * @return the choiceA
     */


    /**
     * @return the asker
     */
    public User getAsker() {
        return asker;
    }

    /**
     * @param asker the asker to set
     */
    public void setAsker(User asker) {
        this.asker = asker;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public enum QuestionType {
        TEXT, PICTURE
    }

}