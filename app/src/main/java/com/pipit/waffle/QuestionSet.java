package com.pipit.waffle;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.pipit.waffle.Objects.Question;

/**
 * Created by Eric on 4/7/2015.
 *
 * Each questionSet (there will be two total), represents a displayed question in AnsweringFragment
 * In the answeringFragment, one questionSet will be active at a time, displaying both of its cards
 * loaded with choices, while the other cardSet is on standby.
 */

public class QuestionSet {

    private CardView cardViewTop;
    private CardView cardViewBot;

    private Question _question;

    private ImageView imageViewTop;
    private ImageView imageViewBot;

    private AutoResizeTextView textViewTop;
    private AutoResizeTextView textViewBot;

    private ProgressBar pb_cvtop;
    private ProgressBar pb_cvbot;

    private ObjectAnimator anim_top;
    private ObjectAnimator anim_bot;

    public LoadedStatus loadedStatus;
    public ShowingStatus showingStatus;

    public QuestionSet(CardView top, CardView bot ){
        loadedStatus = LoadedStatus.NOT_READY;
        cardViewTop = top;
        cardViewBot = bot;
        imageViewTop = new ImageView(cardViewTop.getContext());
        imageViewBot = new ImageView(cardViewBot.getContext());
        textViewTop = new AutoResizeTextView(cardViewTop.getContext());
        textViewBot = new AutoResizeTextView(cardViewBot.getContext());
    }

    public void resetQuestionData(){
        //_question = null;
        imageViewTop.setImageDrawable(null);
        imageViewTop.destroyDrawingCache();
        imageViewBot.setImageDrawable(null);
        imageViewBot.destroyDrawingCache();
        loadedStatus = LoadedStatus.NOT_READY;
        if (pb_cvbot !=null && pb_cvtop !=null){
            pb_cvbot.setVisibility(View.VISIBLE);
            pb_cvtop.setVisibility(View.VISIBLE);
        }
        textViewTop.setText("");
        textViewBot.setText("");
    }

    public CardView getCardViewTop() {
        return cardViewTop;
    }

    public void setCardViewTop(CardView cardViewTop) {
        this.cardViewTop = cardViewTop;
    }

    public CardView getCardViewBot() {
        return cardViewBot;
    }

    public void setCardViewBot(CardView cardViewBot) {
        this.cardViewBot = cardViewBot;
    }

    public Question getQuestion() {
        return _question;
    }

    public void setQuestion(Question _question) {
        this._question = _question;
    }

    public ImageView getImageViewTop() {
        return imageViewTop;
    }

    public void setImageViewTop(ImageView imageViewTop) {
        this.imageViewTop = imageViewTop;
    }

    public AutoResizeTextView getTextViewTop(){ return textViewTop; }

    public AutoResizeTextView getTextViewBot(){ return textViewBot; }

    public void setTextViewTop(AutoResizeTextView textViewTop){
        this.textViewTop = textViewTop;
    }

    public void setTextViewBot(AutoResizeTextView textViewBot){
        this.textViewBot = textViewBot;
    }
    
    public void setBitmapTop(){
        try {
            setBitmapTop(_question.getChoices().get(0).get_image());
        } catch (Exception e) {
            //Todo: set default image
        }
    }

    public void setBitmapTop(Bitmap b){
        if(pb_cvtop!=null){
            pb_cvtop.setVisibility(View.INVISIBLE);
        }
        imageViewTop.setImageBitmap(b);
    }

    public void setBitmapBot(){
        try {
            setBitmapBot(_question.getChoices().get(1).get_image());
        }catch(Exception e){
            //Todo: set default
        }
    }

    public void setBitmapBot(Bitmap b){
        if(pb_cvbot!=null){
            pb_cvbot.setVisibility(View.INVISIBLE);
        }
        imageViewBot.setImageBitmap(b);
    }

    public void setTextTop(){
        try {
            setTextTop(_question.getChoices().get(0).getAnswerBody());
        }catch(Exception e){
            //Todo: set default
        }
    }

    public void setTextTop(String text){
        textViewTop.setText(text);
    }

    public void setTextBot(){
        try {
            setTextBot(_question.getChoices().get(1).getAnswerBody());
        }catch(Exception e){
            //Todo: set default
        }
    }

    public void setTextBot(String text){
        textViewBot.setText(text);
    }


    public ImageView getImageViewBot() {
        return imageViewBot;
    }

    public void setImageViewBot(ImageView imageViewBot) {
        this.imageViewBot = imageViewBot;
    }

    public ProgressBar getPb_cvtop() {
        return pb_cvtop;
    }

    public void setPb_cvtop(ProgressBar pb_cvtop) {
        this.pb_cvtop = pb_cvtop;
    }

    public ProgressBar getPb_cvbot() {
        return pb_cvbot;
    }

    public void setPb_cvbot(ProgressBar pb_cvbot) {
        this.pb_cvbot = pb_cvbot;
    }

    public ObjectAnimator getAnim_top() {
        return anim_top;
    }

    public void setAnim_top(ObjectAnimator anim_top) {
        this.anim_top = anim_top;
    }

    public ObjectAnimator getAnim_bot() {
        return anim_bot;
    }

    public void setAnim_bot(ObjectAnimator anim_bot) {
        this.anim_bot = anim_bot;
    }

    public String getText(int i){
        if (_question!=null) {
            if (_question.getChoices().size() == Constants.NUMBER_OF_CHOICES_PER_QUESTION
                    && i < Constants.NUMBER_OF_CHOICES_PER_QUESTION ) {
                return _question.getChoices().get(i).getAnswerBody();
            }
        }
        return "";
    }


    public enum LoadedStatus {
        READY, NOT_READY
    }

    public enum ShowingStatus {
        ACTIVE, STANDBY
    }

    /**
     * Called when imageViews are ready to be attached to cardViews
     */
    public void applyImageviewsToCards(){
        // add the new ImageViews to their parent layouts
        cardViewTop.addView(imageViewTop);
        cardViewBot.addView(imageViewBot);
    }

    public void applyTextviewsToCards(){
        //textViewTop.setElevation(1);
        //textViewBot.setElevation(1);
        textViewTop.bringToFront();
        textViewBot.bringToFront();
        cardViewTop.addView(textViewTop);
        cardViewBot.addView(textViewBot);
    }


}

