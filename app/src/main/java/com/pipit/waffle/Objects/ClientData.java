package com.pipit.waffle.Objects;

import android.content.Context;
import android.util.Log;

import com.pipit.waffle.AnsweringFragment;
import com.pipit.waffle.AskingFragment;
import com.pipit.waffle.Network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Eric on 12/14/2014.
 */
public class ClientData {
    //Singleton class to hold all data

    public static final int MAXIMUM_QUEUED_QUESTIONS = 5;
    public static final int MAXIMUM_IMAGE_LOADED_QUESTIONS = 3;

    private static ClientData clientdata = new ClientData();
    private static ConcurrentLinkedQueue<Question> questions;
    private static ConcurrentLinkedQueue<Question> questions_being_loaded;
    //List of available questions
    private static ConcurrentLinkedQueue<Question> readyQuestions;

    private static List<String> idsOfAnsweredQuestions;
    private static List<String> idsOfAskedQuestions;
    private static AnsweringFragment answeringFragment;

     /* Data about self */


    private static AskingFragment askingFragment;
    // maps Choice answerID to an integer that specifies the ImageView that the _image
    // from Choice should be loaded into
    public HashMap<String, Integer> card_image_map;


    private ClientData(){
        //Initialize
        questions = new ConcurrentLinkedQueue<Question>();
        readyQuestions = new ConcurrentLinkedQueue<Question>();
        questions_being_loaded = new ConcurrentLinkedQueue<Question>();
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
        //If we take out a loaded question, start loading another one
        if (readyQuestions.size() + questions_being_loaded.size() <= MAXIMUM_IMAGE_LOADED_QUESTIONS){
            q.beginImageLoading();
            questions_being_loaded.add(q);
        }else{
            questions.add(q);
        }
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
    public static AskingFragment getAskingFragment() {
        return askingFragment;
    }
    public static void setAskingFragment(AskingFragment askingFragment) {
        ClientData.askingFragment = askingFragment;
    }

    public static boolean moveQuestionToReady(Question q){
        //TODO: Check for race conditions
        Question k = q;
        if (q.state != Question.QuestionState.LOADED || q.getChoices().size()!=2){
            return false;
        }
        readyQuestions.add(q);
        if (questions_being_loaded.contains(q)){
            questions_being_loaded.remove();
        }
        answeringFragment.notifyOfReadyQuestion(); //Offer new question if needed.
        return true;
    }


    public static boolean checkAndUpdateQuestionStatus(Question q){
        //TODO: Currently only supports questions with 2 choices exactly
        if (q.getChoices().size()!=2){
            return false;
        }
        for (int i = 0; i < q.getChoices().size() ; i++){
            List<Choice> choices_for_debugger = q.getChoices();
            if (!(q.getChoices().get(i).imageState==Choice.LoadState.IMAGE_READY ||q.getChoices().get(i).imageState==Choice.LoadState.NO_IMAGE)){
                return false;
            }
        }
        q.state = Question.QuestionState.LOADED;
        ClientData.moveQuestionToReady(q);
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

    public synchronized static Question pollReadyQuestions(){
        //If we take out a loaded question, start loading another one
        if (readyQuestions.size()<= MAXIMUM_IMAGE_LOADED_QUESTIONS){
            Question q = pollNewQuestions();
            if (q!=null){
                q.beginImageLoading();
                questions_being_loaded.add(q);
            }
        }
        return readyQuestions.poll();
    }

    public synchronized static Question pollNewQuestions(){
        Log.d("ClientData", "pollNewQuestions - ClientData.questions.size() = " + ClientData.questions.size());
        if (ClientData.questions.size() <= 1) {
            ClientData.getNextUnansweredQuestion(answeringFragment.getActivity());
        }
        return questions.poll();
    }

    /**
     * Func 1 - Just pull all questions and parse the package (dont load images)
     * Func 2 - Get a question (Called in the AskingFragment screen) - Pull a question from the queue
     * Func 3 - Loading is done when specified
     */
    //public static Question getQuestion

}
