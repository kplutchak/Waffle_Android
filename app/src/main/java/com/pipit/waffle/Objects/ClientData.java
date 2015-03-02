package com.pipit.waffle.Objects;

import android.content.Context;
import android.util.Log;

import com.pipit.waffle.AnsweringFragment;
import com.pipit.waffle.Network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Eric on 12/14/2014.
 */
public class ClientData {
    //Singleton class to hold all data

    public static final int MAXIMUM_QUEUED_QUESTIONS = 5;

    private static ClientData clientdata = new ClientData();
    public static LinkedList<Question> questions;
    //List of available questions
    public static Queue<Question> readyQuestions;

    private static List<String> idsOfAnsweredQuestions;
    private static AnsweringFragment answeringFragment;
    // maps Choice answerID to an integer that specifies the ImageView that the _image
    // from Choice should be loaded into
    public HashMap<String, Integer> card_image_map;


    private ClientData(){
        //Initialize
        questions = new LinkedList<Question>();
        readyQuestions = new LinkedList<Question>();
        idsOfAnsweredQuestions = new ArrayList<String>();

        card_image_map = new HashMap<String, Integer>();
        Log.d("ClientData", "ClientData() - initializing questions arrays");
    }

    public static ClientData getInstance(){
        if (clientdata == null){
            clientdata = new ClientData();
        }
        return clientdata;
    }

    public static void addQuestion(Question q){
        /*
            When a question gets added, it contains a url to the associated image, but not the image itself. We have to
            start the synchronous download of that image here.
         */
        //Todo: Check that question contains at least one option.

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
            Network.getAllQuestions(mcontext, numberQuestionsToPull());
            q.state = Question.QuestionState.NOT_LOADED;
            q.generateAndSetID();
            Choice _a = new Choice();
            Choice _b = new Choice();
            q.addChoice(_a);
            q.addChoice(_b);
            idsOfAnsweredQuestions.add(q.getId());
            return q;
        }

        Log.d("ClientData", "getNextUnAnsweredQuestion: questions.size() = " +Integer.toString(questions.size()));

//        Random rand = new Random();
        if (questions.size() > 0){
            //TODO: WHAT WILL WE DO WHEN WE STILL HAVE NO QUESTIONS READY!? WE ARE FUCKED? or will getAllQuestions block via Ion? We'll see
            //TODO: Implement some way of randomizing questions
//            int randomNum = rand.nextInt(questions.size());
//            q = questions.get(randomNum);
//            questions.remove(randomNum);

            q = questions.remove();
            idsOfAnsweredQuestions.add(q.getId());
            Log.d("ClientData", "Retrieved a question with body" + q.getQuestionBody());
        }
        return q;
    }

    public static int numberQuestionsToPull(){
        int spaceInQueue = MAXIMUM_QUEUED_QUESTIONS - questions.size();
        if (spaceInQueue<0){
            return 0;
        }
        else return spaceInQueue;
    }

    public static List<String> getIdsOfAnsweredQuestions() {
        return idsOfAnsweredQuestions;
    }

    public static AnsweringFragment getAnsweringFragment() {
        return answeringFragment;
    }

    public static void setAnsweringFragment(AnsweringFragment answeringFragment) {
        ClientData.answeringFragment = answeringFragment;
    }

    public static boolean moveQuestionToReady(final Question q){
        //TODO: Check for race conditions

        if (q.state != Question.QuestionState.LOADED || q.getChoices().size()!=2 || !questions.contains(q)){
            return false;
        }
        readyQuestions.add(q);
        questions.remove(q);
        answeringFragment.notifyOfReadyQuestion(); //Offer new question if needed.
        return true;
    }

    /**
     * Checks for FAILED questions in the questions queue and removes any that are found.
     * @return True if one is found and removed
     */
    public static boolean tipoffSecretPolice(){
        boolean iGotOne = false;
        for (Question q : questions){
            if (q.state == Question.QuestionState.FAILED){
                iGotOne = questions.remove(q);
            }
        }
        return iGotOne;
    }

}
