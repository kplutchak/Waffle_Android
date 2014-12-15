package com.pipit.waffle.Objects;

import android.content.Context;
import android.util.Log;

import com.pipit.waffle.Network;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Eric on 12/14/2014.
 */
public class ClientData {
    //Singleton class to hold all data
    private static ClientData clientdata = new ClientData();
    private static List<Question> questions;
    private static List<Question> answeredQuestions;

    private ClientData(){
        //Initialize
        questions = new ArrayList<Question>();
        answeredQuestions = new ArrayList<Question>();
        Log.d("ClientData", "ClientData() - initializing questions arrays");
    }

    public static ClientData getInstance(){
        if (clientdata == null){
            clientdata = new ClientData();
        }
        return clientdata;
    }

    public static void addQuestion(Question q){
        questions.add(q);
        Log.d("ClientData", "addQuestion(q) added a question with text: '" + q.getQuestionBody() + "' and " + q.getChoices().get(0).getQuestionID() + " userID  - question array size is now" + Integer.toString(questions.size()));
        return;
    }

    public static void setAllQuestions(){
        return;
    }

    public static Question getNextUnansweredQuestion(Context mcontext){

        Log.d("ClientData", "getNextUnAnsweredQuestion: questions.size() = " +Integer.toString(questions.size()));
        Question q = new Question("", Self.getUser());
        Choice a = new Choice();
        a.setAnswerBody("developer dun goofed");
        q.addChoice(a);
        q.addChoice(a);

        if (questions.size()<1){
            Network.getAllQuestions(mcontext);
        }

        Log.d("ClientData", "getNextUnAnsweredQuestion: questions.size() = " +Integer.toString(questions.size()));

        Random rand = new Random();
        if (questions.size() > 0){
            int randomNum = rand.nextInt(questions.size());

            q = questions.get(randomNum);
            questions.remove(randomNum);
            answeredQuestions.add(q);
            Log.d("ClientData", "Retrieved a question with body" + q.getQuestionBody());
        }
        return q;
    }

}
