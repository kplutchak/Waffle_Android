package com.pipit.waffle.Objects;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.UUID;

/**
 * Created by Eric on 12/13/2014.
 */
public class Choice {


    private String answerBody;
    private String answerID;
    private String questionID;
    private String url;
    private Bitmap _image;
    private int votes;
    public LoadState imageState;
    static ImageLoader imageLoader = ImageLoader.getInstance();

    public Choice(String answerID){
        //Todo: Make the constructor require fields, use a 'hasImage' flag for best practice, instead of checking url length at runtime
        setVotes(0);
        imageState = LoadState.NOT_LOADED;
    }

    public Choice(){
        //Todo: Make the constructor require fields, use a 'hasImage' flag for best practice, instead of checking url length at runtime
        answerID = UUID.randomUUID().toString();
        setVotes(0);
        imageState = LoadState.NOT_LOADED;
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

    public Bitmap get_image() {
        return _image;
    }

    public void set_image(Bitmap _image) {
        this._image = _image;
    }

    public enum LoadState{
        NO_IMAGE, NOT_LOADED, LOADING, IMAGE_READY, FAILED
    }
}
