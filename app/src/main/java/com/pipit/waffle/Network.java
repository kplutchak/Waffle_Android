package com.pipit.waffle;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.pipit.waffle.Objects.Choice;
import com.pipit.waffle.Objects.ClientData;
import com.pipit.waffle.Objects.Question;
import com.pipit.waffle.Objects.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric on 12/7/2014.
 */
public class Network {

    public static void getAllQuestions(final Context mcontext, final int numberOfQuestionsNeeded){
        /*This function currently populates clientData directly (bad practice)*/
        //TODO: make this block with a progress dialog and return questions directly, and let the caller populate the fields
        Log.d("ConnectToBackend", "starting getAllQuestions");
        final String url = "http://obscure-fjord-2523.herokuapp.com/api/questions/";
        ToolbarActivity ma = (ToolbarActivity) mcontext;

        ProgressBar mProgress = (ProgressBar) ma.findViewById(R.id.progress_bar);
        ProgressBar progressBar = new ProgressBar(mcontext);
        progressBar.setIndeterminate(true);
        ProgressDialog progressDialog = new ProgressDialog(mcontext);

        JsonArray result = new JsonArray();
        try{
            result = Ion.with(mcontext)
                    .load(url)
                    .progressBar(mProgress)
                    .progressHandler(new ProgressCallback() {
                        @Override
                        public void onProgress(long downloaded, long total) {
                            Log.d("ConnectToBackend", "" + downloaded + " / " + total);
                        }
                    })
                    .asJsonArray()/*
		.setCallback(new FutureCallback<JsonArray>() {
		   @Override
		    public void onCompleted(Exception e, JsonArray result) {
			   // this is called back onto the ui thread, no Activity.runOnUiThread or Handler.post necessary.
			   */
                    .get();
        }catch(Exception e){
            Log.d("ConnectToBackend", "getAllQuestions called with url " + url);
            if (e != null) {
                Toast.makeText(mcontext, "Error loading questions " + e.toString(), Toast.LENGTH_LONG).show();
                Log.d("ConnectToBackend", e.toString());
                return;
            }
        }
        if(false){}
        else{
            List<JsonObject> jsonlist = new ArrayList<JsonObject>();

            if (result != null) {
                for (int i = 0; i < result.size(); i++) {
                    jsonlist.add(result.get(i).getAsJsonObject());
                }
            }
            int k = 0;
            int jsonlistindex = 0;
            while(k < numberOfQuestionsNeeded && k < jsonlist.size()) {
                //Get text body, user of question
                jsonlistindex++;
                if (ClientData.getInstance().getIdsOfAnsweredQuestions().contains(jsonlist.get(k).get("id").getAsString())) {
                    String questionbody = jsonlist.get(k).get("text").getAsString();
                    String userID = jsonlist.get(k).get("user_id").getAsString();
                    User tempuser = new User(userID);
                    Question nq = new Question(questionbody, tempuser);

                    JsonArray answerJson = jsonlist.get(k).get("answers").getAsJsonArray();
                    List<JsonObject> answerJsonList = new ArrayList<JsonObject>();
                    for (int i = 0; i < answerJson.size(); i++) {
                        answerJsonList.add(answerJson.get(i).getAsJsonObject());
                    }

                    for (int j = 0; j < answerJsonList.size(); j++) {
                        String answerBody = answerJsonList.get(j).get("text").getAsString();
                        int questionIDinteger = answerJsonList.get(j).get("id").getAsInt();
                        String questionID = Integer.toString(questionIDinteger);
                        int answerVotes = answerJsonList.get(j).get("votes").getAsInt();
                        String picurl = answerJsonList.get(j).get("picture").getAsString();

                        Choice newans = new Choice();
                        newans.setAnswerBody(answerBody);
                        newans.setVotes(answerVotes);
                        newans.setQuestionID(questionID);
                        newans.setUrl(picurl);
                        nq.addChoice(newans);
                    }
                    if (nq.getChoices().size() == 2) {
                        ClientData.addQuestion(nq);
                        k++;
                    }
                }
                return;
            }
        }
    }

    public static void newQuestion(final Context mcontext, Question mquestion){
        JsonObject json = new JsonObject();
        json.addProperty("foo", "bar");

        Ion.with(mcontext)
                .load("http://obscure-fjord-2523.herokuapp.com/api/questions/")
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                    }
                });
    }

    public static void answerQuestion(final Context mcontext, Choice providedAnswer){
        JsonObject json = new JsonObject();
        json.addProperty("vote", "true");
        final String url = "http://obscure-fjord-2523.herokuapp.com/api/answers/"+providedAnswer.getQuestionID()+"/";
        Ion.with(mcontext)
                .load("PUT", url)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null){
                            Log.d("ConnectToBackend", "answerquestions called with "+url+" and has error" + e.toString());
                            if (result==null){
                                Log.d("ConnectToBackend", "answerQuestions returns result with NULL");
                            }
                        }
                        else{
                            Log.d("ConnectToBackend", "answerQuestion asked with url " + url + " : and result " + result.toString());
                        }

                    }
                });
    }

    public static void postQuestion(final Context mcontext, Question mquestion){
        JsonArray answerarray = new JsonArray();
        JsonObject answerjson = new JsonObject();
        JsonObject answerjson2 = new JsonObject();
        answerjson.addProperty("text", mquestion.getChoices().get(0).getAnswerBody());
        answerjson2.addProperty("text", mquestion.getChoices().get(1).getAnswerBody());
        answerarray.add(answerjson);
        answerarray.add(answerjson2);

        JsonObject json = new JsonObject();
        json.addProperty("text", mquestion.getQuestionBody());
        json.add("answers", answerarray);
        json.addProperty("user_id", "temp user id");
        final String url = "http://obscure-fjord-2523.herokuapp.com/api/questions/";
        Ion.with(mcontext)
                .load(url)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null){
                            Log.d("ConnectToBackend", "postQuestion called with "+url+" and has error" + e.toString());
                            if (result==null){
                                Log.d("ConnectToBackend", "postQuestion returns result with NULL");
                            }
                            Toast.makeText(mcontext, "Error submitting question " + e.toString(), Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(mcontext, "Sucessfully posted question", Toast.LENGTH_LONG).show();
                            Log.d("ConnectToBackend", "postQuestion asked with url " + url + " : and result " + result.toString());

                            //Switch to questions fragment
                         //   ToolbarActivity ma = (ToolbarActivity) mcontext;
                            /*
                            if(ma.getShowingFragmentID().equals(Constants.CREATE_QUESTION_FRAGMENT_ID))
                                ma.switchToFragment(Constants.QUESTION_ANSWER_FRAGMENT_ID);
                                */
                         //   ma.switchFragments();
                        }

                    }
                });
    }



}
