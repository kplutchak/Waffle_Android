package com.pipit.waffle;

import android.animation.ObjectAnimator;
import android.support.v7.widget.CardView;
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

    private ProgressBar pb_cvtop1;
    private ProgressBar pb_cvbot1;

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
    }

    public void resetQuestionData(){
        _question = null;
        imageViewTop.setImageBitmap(null);
        imageViewTop.destroyDrawingCache();
        imageViewBot.setImageBitmap(null);
        imageViewBot.destroyDrawingCache();
        loadedStatus = LoadedStatus.NOT_READY;
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

    public ImageView getImageViewBot() {
        return imageViewBot;
    }

    public void setImageViewBot(ImageView imageViewBot) {
        this.imageViewBot = imageViewBot;
    }

    public ProgressBar getPb_cvtop1() {
        return pb_cvtop1;
    }

    public void setPb_cvtop1(ProgressBar pb_cvtop1) {
        this.pb_cvtop1 = pb_cvtop1;
    }

    public ProgressBar getPb_cvbot1() {
        return pb_cvbot1;
    }

    public void setPb_cvbot1(ProgressBar pb_cvbot1) {
        this.pb_cvbot1 = pb_cvbot1;
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


}

