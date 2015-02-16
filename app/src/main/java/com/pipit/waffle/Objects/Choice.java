package com.pipit.waffle.Objects;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

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

    public Choice(){
        //Todo: Make the constructor require fields, use a 'hasImage' flag for best practice, instead of checking url length at runtime
        setVotes(0);
        imageState = LoadState.NOT_LOADED;
        answerID = UUID.randomUUID().toString();
    }

    public boolean loadURLintoBitmap(String imageUri){
        imageLoader.loadImage(imageUri, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                imageState = LoadState.LOADING;
            }
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                _image = loadedImage;
                imageState = LoadState.IMAGE_READY;
                Log.d("Choice", "Image Loading Completed");
                ClientData.getAnsweringFragment().setImageViewBitmap(_image, answerID);
            }
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                imageState = LoadState.FAILED;
            }
        });
        return true;
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
