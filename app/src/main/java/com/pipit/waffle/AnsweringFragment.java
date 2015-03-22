package com.pipit.waffle;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.makeramen.RoundedTransformationBuilder;
import com.pipit.waffle.Objects.Choice;
import com.pipit.waffle.Objects.ClientData;
import com.pipit.waffle.Objects.Question;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Kyle on 11/19/2014.
 */
public class AnsweringFragment extends Fragment  {

    private static double TENSION = 800;
    private static double DAMPER = 20; //friction
    // CardViews -  there are two CardViews for top, two for bottom. The two swap when a selection
    // occurs

    private CardView cardViewTop1;
    private CardView cardViewBot1;
    private CardView cardViewTop2;
    private CardView cardViewBot2;

    private Question currentQuestion;
    private Question nextQuestion;
    private Object questionLock = new Object();

    private ImageView plus_one;

    private Transformation transformation_rounded_image;

    // ObjectAnimators that control the selection movement of each of the four cards. We use ObjectAnimator
    // in this case instead of TranslateAnimation so we can control the objects themselves rather than the
    // views
    private ObjectAnimator anim_bcard1;
    private ObjectAnimator anim_tcard1;
    private ObjectAnimator anim_bcard2;
    private ObjectAnimator anim_tcard2;

    private float mOrigX;
    private float mOrigY;

    private ImageView imageView_cv_top1;
    private ImageView imageView_cv_bot1;
    private ImageView imageView_cv_top2;
    private ImageView imageView_cv_bot2;
    private int image_height_stored;
    private int image_height_stored_landscape;

    private VelocityTracker velocity = null;

    private ProgressBar pb_cvtop1;
    private ProgressBar pb_cvbot1;

    private boolean tcard1_lock = false;
    private boolean tcard2_lock = false;
    private boolean bcard1_lock = false;
    private boolean bcard2_lock = false;

    private LinearLayout card_holder_layout;


    // TODO: when a Choice is selected, remove it from the mapping
    // TODO: when a Choice is being brought in via getNext...(), add it to the mapping using it's
    // answerID
    // TODO: pass the mappings between orientation changes (make sure they are not lost)

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt("height_portrait", image_height_stored);

        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((ToolbarActivity) getActivity()).current_fragment_id = Constants.ANSWERING_FRAGMENT_ID;

        ClientData.setAnsweringFragment(this);
        if(savedInstanceState != null) {
            image_height_stored = savedInstanceState.getInt("height_portrait");
            image_height_stored_landscape = savedInstanceState.getInt("height_landscape");
        }
        // get the next four unanswered questions and set their mappings
        View v = null;

        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.answering_fragment, container, false);

        card_holder_layout = (LinearLayout) v.findViewById(R.id.card_holder_layout);

        // Retrieve the CardViews
        cardViewTop1 = (CardView) v.findViewById(R.id.card_view);
        cardViewBot1 = (CardView) v.findViewById(R.id.card_view2);
        cardViewTop2 = (CardView) v.findViewById(R.id.card_view_extra);
        cardViewBot2 = (CardView) v.findViewById(R.id.card_view2_extra);

        // Set the CardViews' size and margins
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int width = size.x;

        int margin = (int) (8 * getActivity().getResources().getDisplayMetrics().density);
        int margin_images = (int) (5 * getActivity().getResources().getDisplayMetrics().density);
        int margin_left = (int) (1000 * getActivity().getResources().getDisplayMetrics().density);
        final int frame_width = (int) (2000 * getActivity().getResources().getDisplayMetrics().density);

        CardView.LayoutParams card_params = (CardView.LayoutParams) cardViewTop1.getLayoutParams();
        card_params.width = width - (2 * margin);

        final int starting_pos = margin_left - (card_params.width / 2);
        final int ending_pos = margin_left + (card_params.width / 2) + 24;
        final float ending_pos_left = margin_left - ((3.0f / 2.0f) * (float) card_params.width) - 24;

        // TODO: Correct all margins with 1/2 value for top/bottom (ie center divide)
        card_params.setMargins(margin_left - (card_params.width / 2), margin, margin, margin / 2);

        cardViewTop1.setLayoutParams(card_params);

        CardView.LayoutParams card_params2 = (CardView.LayoutParams) cardViewBot1.getLayoutParams();
        card_params2.width = width - (2 * margin);
        card_params2.setMargins(margin_left - (card_params.width / 2), margin / 2, margin, margin);

        cardViewBot1.setLayoutParams(card_params2);

        CardView.LayoutParams card_params3 = (CardView.LayoutParams) cardViewTop2.getLayoutParams();
        card_params3.width = width - (2 * margin);
        card_params3.setMargins(0, margin, margin, margin);

        cardViewTop2.setLayoutParams(card_params3);

        CardView.LayoutParams card_params4 = (CardView.LayoutParams) cardViewBot2.getLayoutParams();
        card_params4.width = width - (2 * margin);
        card_params4.setMargins(0, 0, margin, margin);

        cardViewBot2.setLayoutParams(card_params4);

        // TODO: make sure the two images appear at the same time. If they don't , don't reveal the one that has already loaded until the second image has finished loading

        transformation_rounded_image = new RoundedTransformationBuilder()
                .cornerRadiusDp(4).borderColor(getResources().getColor(R.color.black_tint_light)).borderWidthDp(1)
                .oval(false)
                .build();

        pb_cvtop1 = (ProgressBar) cardViewTop1.findViewById(R.id.progress_bar_cvtop1);
        pb_cvbot1 = (ProgressBar) cardViewBot1.findViewById(R.id.progress_bar_cvbot1);

        imageView_cv_top1 = new ImageView(cardViewTop1.getContext());
        imageView_cv_bot1 = new ImageView(cardViewBot1.getContext());
        imageView_cv_top2 = new ImageView(cardViewTop2.getContext());
        imageView_cv_bot2 = new ImageView(cardViewBot2.getContext());

        CardView.LayoutParams cvtop1_image_params = new CardView.LayoutParams(cardViewTop1.getLayoutParams());
        cvtop1_image_params.width = card_params.width - (2 * margin_images);
        if (savedInstanceState != null && getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            cvtop1_image_params.height = image_height_stored;


        cvtop1_image_params.setMargins(margin_images, margin_images, margin_images, margin_images);
        imageView_cv_top1.setLayoutParams(cvtop1_image_params);
        imageView_cv_top2.setLayoutParams(cvtop1_image_params);

        CardView.LayoutParams cvbot1_image_params = new CardView.LayoutParams(cardViewBot1.getLayoutParams());
        cvbot1_image_params.width = card_params.width - (2 * margin_images);
        if (savedInstanceState != null && getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            cvbot1_image_params.height = image_height_stored;

        if (savedInstanceState != null && getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                && image_height_stored_landscape != 0)
            cvbot1_image_params.height = image_height_stored_landscape;

        cvbot1_image_params.setMargins(margin_images, margin_images, margin_images, margin_images);
        imageView_cv_bot1.setLayoutParams(cvbot1_image_params);
        imageView_cv_bot2.setLayoutParams(cvbot1_image_params);


        imageView_cv_top1.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView_cv_bot1.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView_cv_top2.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView_cv_bot2.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // add the new ImageViews to their parent layouts
        cardViewTop1.addView(imageView_cv_top1);
        cardViewBot1.addView(imageView_cv_bot1);
        cardViewTop2.addView(imageView_cv_top2);
        cardViewBot2.addView(imageView_cv_bot2);

        // CardView movement and touch behavior
        final View.OnTouchListener tl = new View.OnTouchListener() {
            public float offsetX;
            public float offsetY;
            private ArrayList<Float> last_velocities = new ArrayList<Float>(3);
            private boolean selected = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int theAction = event.getAction();

                int index = event.getActionIndex();
                int action = event.getActionMasked();
                int pointerId = event.getPointerId(index);

                switch (theAction) {
                    case MotionEvent.ACTION_DOWN:
                        // Button down



                        last_velocities.clear();

                        selected = false;

                        last_velocities.add(0, 0.0f);
                        last_velocities.add(1, 0.0f);
                        last_velocities.add(2, 0.0f);

                        if (velocity == null) {
                            // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                            velocity = VelocityTracker.obtain();
                        } else {
                            // Reset the velocity tracker back to its initial state.
                            velocity.clear();
                        }

                        mOrigX = v.getX();
                        mOrigY = v.getY();
                        offsetX = v.getX() - event.getRawX();
                        offsetY = v.getY() - event.getRawY();

                        velocity.addMovement(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // Button moved
                        velocity.addMovement(event);
                        velocity.computeCurrentVelocity(1000);
                        float current_vel = VelocityTrackerCompat.getXVelocity(velocity,
                                pointerId);
                       // Log.d("AnsweringFragment", "Velocity: " + current_vel);

                        // Remember the last 3 velocities
                        last_velocities.set(2, last_velocities.get(1));
                        last_velocities.set(1, last_velocities.get(0));
                        last_velocities.set(0, current_vel);

                        //Log.d("AnsweringFragment", Float.toString(x_velocity));
                        float newX = event.getRawX() + offsetX;
                        float newY = event.getRawY() + offsetY;

                        if (!(newX < starting_pos - 10))
                            v.setX(newX);

                        break;
                    case MotionEvent.ACTION_UP:
                        // Button up

                        // Currently, the options for a "choice selection" are:
                        // 1) a velocity of 12,000 and at least 1/2 of the screen width in the positive
                        // x direction on ACTION_UP
                        // 2) at least 51/100 of the screen width in the positive x direction on
                        // ACTION_UP




                        Float max_vel = Collections.max(last_velocities);

                       // Log.d("AnsweringFragment", "Max velocity: " + Float.toString(max_vel));

                        float xValue = v.getX();

                        float halfway = (float) frame_width / 2.0f;

                        float dist;
                        if (((xValue > halfway && (max_vel > 12000))) ||
                                (xValue > ((51.0f / 100.0f) * (float) frame_width))) {
                            Log.d("AnsweringFragment", "Selected! Released at " + Float.toString(xValue) + " pixels with a " +
                                    "velocity of " + Float.toString(max_vel) + " pixels per second.");
                            dist = ending_pos - xValue;
                            selected = true;
                            cardViewTop2.setX(ending_pos_left);
                            cardViewBot2.setX(ending_pos);
                        } else {
                            dist = starting_pos - xValue;
                        }

                        TranslateAnimation anim_in = new TranslateAnimation(0, starting_pos - ending_pos_left, 0, 0);
                        anim_in.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                cardViewTop2.setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                cardViewTop2.setX(starting_pos);
                                cardViewTop2.setEnabled(true);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        anim_in.setInterpolator(new DecelerateInterpolator(1.5f));

                        TranslateAnimation anim_in_right = new TranslateAnimation(0, starting_pos - ending_pos, 0, 0);

                        anim_in_right.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                cardViewBot2.setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                cardViewBot2.setX(starting_pos);
                                cardViewBot2.setEnabled(true);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        anim_in_right.setInterpolator(new DecelerateInterpolator(1.5f));

                        TranslateAnimation anim_other = new TranslateAnimation(0, ending_pos_left - cardViewBot1.getX(), 0, 0);
                        anim_other.setInterpolator(new DecelerateInterpolator(1.5f));

                        if (selected)
                            anim_tcard1 = ObjectAnimator.ofFloat(cardViewTop1, "translationX", xValue - starting_pos, ending_pos - starting_pos);
                        else
                            anim_tcard1 = ObjectAnimator.ofFloat(cardViewTop1, "translationX", xValue - starting_pos, 0);

                        final float deltaX = xValue - starting_pos;

                        anim_tcard1.setInterpolator(new DecelerateInterpolator(1.5f));
                        anim_tcard1.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                cardViewTop1.setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (selected) {
                                    cardViewTop1.setX(ending_pos);
                                }
                                cardViewTop1.setEnabled(true);

                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                float frac = anim_tcard1.getAnimatedFraction();

                                cardViewTop1.setTranslationX((1 - frac) * deltaX);

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });

                        anim_in.setFillEnabled(true);
                        anim_in_right.setFillEnabled(true);
                        anim_other.setFillEnabled(true);

                        // magical custom formula for creating appropriate slide speeds varying by travel distance
                        long dur = (long) ((dist / 10) * (dist / 10)) / 2;
                        if (dur > 500)
                            dur = 500;
                        if (dur < 300)
                            dur = 300;

                        anim_tcard1.setDuration(dur);
                        anim_other.setDuration(dur);
                        anim_in.setDuration(dur);
                        anim_in_right.setDuration(dur);

                        anim_other.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                cardViewBot1.setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                cardViewBot1.setX(ending_pos_left);
                                cardViewBot1.setEnabled(true);
                            }


                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        anim_other.setStartOffset(200);
                        anim_in.setStartOffset(dur + 200);
                        anim_in_right.setStartOffset(dur + 200);
                        anim_tcard1.start();


                        if (selected) {
                            /*Picasso.with(cardViewTop1.getContext()).load(ClientData.getNextUnansweredQuestion(getActivity()).getChoices().get(0).getUrl())
                                    .fit().centerCrop()
                                    .transform(transformation_rounded_image).into(imageView_cv_top2, new com.squareup.picasso.Callback() {

                                @Override
                                public void onSuccess() {
                                    pb_cvtop1.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onError() {
                                    pb_cvtop1.setVisibility(View.VISIBLE);
                                }
                            });
                            */
                            synchronized(questionLock){
                            if (currentQuestion == null){
                                    submitCurrentQuestion(null);
                            }else {
                                submitCurrentQuestion(currentQuestion.getChoices().get(0));
                            }
                            if(currentQuestion != null && currentQuestion.getChoices().size()>=2) {
                                imageView_cv_top2.setImageBitmap(currentQuestion.getChoices().get(0).get_image());
                                imageView_cv_bot2.setImageBitmap(currentQuestion.getChoices().get(1).get_image());
                                Log.d("AnsweringFragment", "Set cv_top2 (~476) with current question " + currentQuestion.getChoices().get(0).getAnswerBody());
                                Log.d("AnsweringFragment", "Set cv_bot2 (~477) with current question " + currentQuestion.getChoices().get(1).getAnswerBody());


                            }

                            if (anim_bcard1 != null)
                                anim_bcard1.cancel();
                            cardViewBot1.startAnimation(anim_other);
                            cardViewTop2.startAnimation(anim_in);
                            cardViewBot2.startAnimation(anim_in_right);
                            }
                        }



                        break;
                    default:
                        break;
                }
                return true;
            }
        };

        final View.OnTouchListener tl2 = new View.OnTouchListener() {
            public float offsetX;
            public float offsetY;
            private ArrayList<Float> last_velocities = new ArrayList<Float>(3);
            private boolean selected = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int theAction = event.getAction();

                int index = event.getActionIndex();
                int action = event.getActionMasked();
                int pointerId = event.getPointerId(index);

                switch (theAction) {
                    case MotionEvent.ACTION_DOWN:
                        // Button down

                        last_velocities.clear();

                        selected = false;

                        last_velocities.add(0, 0.0f);
                        last_velocities.add(1, 0.0f);
                        last_velocities.add(2, 0.0f);

                        if (velocity == null) {
                            // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                            velocity = VelocityTracker.obtain();
                        } else {
                            // Reset the velocity tracker back to its initial state.
                            velocity.clear();
                        }

                        mOrigX = v.getX();
                        mOrigY = v.getY();
                        offsetX = v.getX() - event.getRawX();
                        offsetY = v.getY() - event.getRawY();

                        velocity.addMovement(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // Button moved
                        velocity.addMovement(event);
                        velocity.computeCurrentVelocity(1000);
                        float current_vel = VelocityTrackerCompat.getXVelocity(velocity,
                                pointerId);
                       // Log.d("AnsweringFragment", "Velocity: " + current_vel);

                        // Remember the last 3 velocities
                        last_velocities.set(2, last_velocities.get(1));
                        last_velocities.set(1, last_velocities.get(0));
                        last_velocities.set(0, current_vel);

                        //Log.d("AnsweringFragment", Float.toString(x_velocity));
                        float newX = event.getRawX() + offsetX;
                        float newY = event.getRawY() + offsetY;
                        if (!(newX < starting_pos - 10))
                            v.setX(newX);
                        break;
                    case MotionEvent.ACTION_UP:
                        // Button up
                        // Currently, the options for a "choice selection" are:
                        // 1) a velocity of 10,000 and at least 1/2 of the screen width in the positive
                        // x direction on ACTION_UP
                        // 2) at least 50/51 of the screen width in the positive x direction on
                        // ACTION_UP

                        Float max_vel = Collections.max(last_velocities);

                       //Log.d("AnsweringFragment", "Max velocity: " + Float.toString(max_vel));

                        float xValue = v.getX();

                        float halfway = (float) frame_width / 2.0f;

                        float dist;
                        if (((xValue > halfway && (max_vel > 12000))) ||
                                (xValue > ((51.0f / 100.0f) * (float) frame_width))) {
                            Log.d("AnsweringFragment", "Selected! Released at " + Float.toString(xValue) + " pixels with a " +
                                    "velocity of " + Float.toString(max_vel) + " pixels per second.");
                            dist = ending_pos - xValue;
                            selected = true;
                            cardViewBot2.setX(ending_pos_left);
                            cardViewTop2.setX(ending_pos);
                        } else {
                            dist = starting_pos - xValue;
                        }


                        TranslateAnimation anim_in = new TranslateAnimation(0, starting_pos - ending_pos_left, 0, 0);
                        anim_in.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                cardViewBot2.setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                cardViewBot2.setX(starting_pos);
                                cardViewBot2.setEnabled(true);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        anim_in.setInterpolator(new DecelerateInterpolator(1.5f));

                        TranslateAnimation anim_in_right = new TranslateAnimation(0, starting_pos - ending_pos, 0, 0);

                        anim_in_right.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                cardViewTop2.setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                cardViewTop2.setX(starting_pos);
                                cardViewTop2.setEnabled(true);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        anim_in_right.setInterpolator(new DecelerateInterpolator(1.5f));

                        TranslateAnimation anim_other = new TranslateAnimation(0, ending_pos_left - cardViewTop1.getX(), 0, 0);
                        anim_other.setInterpolator(new DecelerateInterpolator(1.5f));

                        if (selected)
                            anim_bcard1 = ObjectAnimator.ofFloat(cardViewBot1, "translationX", xValue - starting_pos, ending_pos - starting_pos);
                        else
                            anim_bcard1 = ObjectAnimator.ofFloat(cardViewBot1, "translationX", xValue - starting_pos, 0);

                        final float deltaX = xValue - starting_pos;

                        anim_bcard1.setInterpolator(new DecelerateInterpolator(1.5f));
                        anim_bcard1.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                cardViewBot1.setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (selected) {
                                    cardViewBot1.setX(ending_pos);

                                }

                                cardViewBot1.setEnabled(true);

                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                float frac = anim_bcard1.getAnimatedFraction();

                                cardViewBot1.setTranslationX((1 - frac) * deltaX);

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });

                        anim_in.setFillEnabled(true);
                        anim_in_right.setFillEnabled(true);
                        anim_other.setFillEnabled(true);

                        // magical custom formula for creating appropriate slide speeds varying by travel distance
                        long dur = (long) ((dist / 10) * (dist / 10)) / 2;
                        if (dur > 500)
                            dur = 500;
                        if (dur < 300)
                            dur = 300;

                        anim_bcard1.setDuration(dur);
                        anim_other.setDuration(dur);
                        anim_in.setDuration(dur);
                        anim_in_right.setDuration(dur);
                        anim_other.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                cardViewTop1.setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                cardViewTop1.setX(ending_pos_left);
                                cardViewTop1.setEnabled(true);
                            }


                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        anim_other.setStartOffset(200);
                        anim_in.setStartOffset(dur + 200);
                        anim_in_right.setStartOffset(dur + 200);
                        anim_bcard1.start();
                        if (selected) {
                            synchronized(AnsweringFragment.this.questionLock) {
                                if (currentQuestion == null){
                                    submitCurrentQuestion(null);
                                }else {
                                    submitCurrentQuestion(currentQuestion.getChoices().get(1));
                                }
                                if (currentQuestion != null && currentQuestion.getChoices().size() >= 2) {
                                    imageView_cv_top2.setImageBitmap(currentQuestion.getChoices().get(0).get_image());
                                    imageView_cv_bot2.setImageBitmap(currentQuestion.getChoices().get(1).get_image());
                                    Log.d("AnsweringFragment", "Set cv_top2 (~722) with current question " + currentQuestion.getChoices().get(0).getAnswerBody());
                                    Log.d("AnsweringFragment", "Set cv_bot2 (~722) with current question " + currentQuestion.getChoices().get(1).getAnswerBody());
                                }
                                if (anim_tcard1 != null)
                                    anim_tcard1.cancel();
                                cardViewTop1.startAnimation(anim_other);
                                cardViewBot2.startAnimation(anim_in);
                                cardViewTop2.startAnimation(anim_in_right);
                            }
                        }

                        break;
                    default:
                        break;
                }
                return true;
            }


        };

        final View.OnTouchListener tl3 = new View.OnTouchListener() {
            public float offsetX;
            public float offsetY;
            private ArrayList<Float> last_velocities = new ArrayList<Float>(3);
            private boolean selected = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int theAction = event.getAction();

                int index = event.getActionIndex();
                int action = event.getActionMasked();
                int pointerId = event.getPointerId(index);

                switch (theAction) {
                    case MotionEvent.ACTION_DOWN:
                        // Button down

                        last_velocities.clear();

                        selected = false;

                        last_velocities.add(0, 0.0f);
                        last_velocities.add(1, 0.0f);
                        last_velocities.add(2, 0.0f);

                        if (velocity == null) {
                            // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                            velocity = VelocityTracker.obtain();
                        } else {
                            // Reset the velocity tracker back to its initial state.
                            velocity.clear();
                        }

                        mOrigX = v.getX();
                        mOrigY = v.getY();
                        offsetX = v.getX() - event.getRawX();
                        offsetY = v.getY() - event.getRawY();


                        velocity.addMovement(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // Button moved
                        velocity.addMovement(event);
                        velocity.computeCurrentVelocity(1000);
                        float current_vel = VelocityTrackerCompat.getXVelocity(velocity,
                                pointerId);
                        //Log.d("AnsweringFragment", "Velocity: " + current_vel);

                        // Remember the last 3 velocities
                        last_velocities.set(2, last_velocities.get(1));
                        last_velocities.set(1, last_velocities.get(0));
                        last_velocities.set(0, current_vel);


                        //Log.d("AnsweringFragment", Float.toString(x_velocity));
                        float newX = event.getRawX() + offsetX;
                        float newY = event.getRawY() + offsetY;

                        if (!(newX < starting_pos - 10))
                            v.setX(newX);

                        break;
                    case MotionEvent.ACTION_UP:
                        // Button up

                        // Currently, the options for a "choice selection" are:
                        // 1) a velocity of 10,000 and at least 1/2 of the screen width in the positive
                        // x direction on ACTION_UP
                        // 2) at least 50/51 of the screen width in the positive x direction on
                        // ACTION_UP

                        Float max_vel = Collections.max(last_velocities);

                        Log.d("AnsweringFragment", "Max velocity: " + Float.toString(max_vel));

                        float xValue = v.getX();

                        float halfway = (float) frame_width / 2.0f;

                        float dist;
                        if (((xValue > halfway && (max_vel > 12000))) ||
                                (xValue > ((51.0f / 100.0f) * (float) frame_width))) {
                            Log.d("AnsweringFragment", "Selected! Released at " + Float.toString(xValue) + " pixels with a " +
                                    "velocity of " + Float.toString(max_vel) + " pixels per second.");
                            dist = ending_pos - xValue;
                            selected = true;
                            cardViewTop1.setX(ending_pos_left);
                            cardViewBot1.setX(ending_pos);
                        } else {
                            dist = starting_pos - xValue;

                        }

                        TranslateAnimation anim_in = new TranslateAnimation(0, starting_pos - ending_pos_left, 0, 0);
                        anim_in.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                cardViewTop1.setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                cardViewTop1.setX(starting_pos);
                                cardViewTop1.setEnabled(true);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        anim_in.setInterpolator(new DecelerateInterpolator(1.5f));

                        TranslateAnimation anim_in_right = new TranslateAnimation(0, starting_pos - ending_pos, 0, 0);

                        anim_in_right.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                cardViewBot1.setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                cardViewBot1.setX(starting_pos);
                                cardViewBot1.setEnabled(true);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        anim_in_right.setInterpolator(new DecelerateInterpolator(1.5f));

                        TranslateAnimation anim_other = new TranslateAnimation(0, ending_pos_left - cardViewBot2.getX(), 0, 0);
                        anim_other.setInterpolator(new DecelerateInterpolator(1.5f));

                        if (selected)
                            anim_tcard2 = ObjectAnimator.ofFloat(cardViewTop2, "translationX", xValue, ending_pos);
                        else
                            anim_tcard2 = ObjectAnimator.ofFloat(cardViewTop2, "translationX", xValue, starting_pos);

                        final float deltaX = xValue - starting_pos;

                        anim_tcard2.setInterpolator(new DecelerateInterpolator(1.5f));
                        anim_tcard2.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                cardViewTop2.setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (selected) {
                                    cardViewTop2.setX(ending_pos);

                                }

                                cardViewTop2.setEnabled(true);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                float frac = anim_tcard2.getAnimatedFraction();

                                cardViewTop2.setTranslationX(((1 - frac) * deltaX) + starting_pos);
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });

                        anim_in.setFillEnabled(true);
                        anim_in_right.setFillEnabled(true);
                        anim_other.setFillEnabled(true);

                        // magical custom formula for creating appropriate slide speeds varying by travel distance
                        long dur = (long) ((dist / 10) * (dist / 10)) / 2;
                        if (dur > 500)
                            dur = 500;
                        if (dur < 300)
                            dur = 300;

                        anim_tcard2.setDuration(dur);
                        anim_other.setDuration(dur);
                        anim_in.setDuration(dur);
                        anim_in_right.setDuration(dur);
                        anim_other.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                cardViewBot2.setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                cardViewBot2.setX(ending_pos_left);
                                cardViewBot2.setEnabled(true);
                            }


                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        anim_other.setStartOffset(200);
                        anim_in.setStartOffset(dur + 200);
                        anim_in_right.setStartOffset(dur + 200);
                        anim_tcard2.start();
                        synchronized(questionLock) {
                            if (selected) {
                                if (currentQuestion == null){
                                    submitCurrentQuestion(null);
                                }else {
                                    submitCurrentQuestion(currentQuestion.getChoices().get(0));
                                }
                                if (currentQuestion != null && currentQuestion.getChoices().size() >= 2) {
                                    imageView_cv_top1.setImageBitmap(currentQuestion.getChoices().get(0).get_image());
                                    imageView_cv_bot1.setImageBitmap(currentQuestion.getChoices().get(1).get_image());
                                    Log.d("AnsweringFragment", "Set cv_top1 (~968) with current question " + currentQuestion.getChoices().get(0).getAnswerBody());
                                    Log.d("AnsweringFragment", "Set cv_bot1 (~969) with current question " + currentQuestion.getChoices().get(1).getAnswerBody());
                                }
                                if (anim_bcard2 != null)
                                    anim_bcard2.cancel();
                                cardViewBot2.startAnimation(anim_other);
                                cardViewTop1.startAnimation(anim_in);
                                cardViewBot1.startAnimation(anim_in_right);
                            }
                        }

                        break;
                    default:
                        break;
                }
                return true;
            }
        };

        final View.OnTouchListener tl4 = new View.OnTouchListener() {
            public float offsetX;
            public float offsetY;
            private ArrayList<Float> last_velocities = new ArrayList<Float>(3);
            private boolean selected = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int theAction = event.getAction();

                int index = event.getActionIndex();
                int action = event.getActionMasked();
                int pointerId = event.getPointerId(index);

                switch (theAction) {
                    case MotionEvent.ACTION_DOWN:
                        // Button down

                        last_velocities.clear();

                        selected = false;

                        last_velocities.add(0, 0.0f);
                        last_velocities.add(1, 0.0f);
                        last_velocities.add(2, 0.0f);

                        if (velocity == null) {
                            // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                            velocity = VelocityTracker.obtain();
                        } else {
                            // Reset the velocity tracker back to its initial state.
                            velocity.clear();
                        }

                        mOrigX = v.getX();
                        mOrigY = v.getY();
                        offsetX = v.getX() - event.getRawX();
                        offsetY = v.getY() - event.getRawY();

                        velocity.addMovement(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // Button moved
                        velocity.addMovement(event);
                        velocity.computeCurrentVelocity(1000);
                        float current_vel = VelocityTrackerCompat.getXVelocity(velocity,
                                pointerId);
                        Log.d("AnsweringFragment", "Velocity: " + current_vel);

                        // Remember the last 3 velocities
                        last_velocities.set(2, last_velocities.get(1));
                        last_velocities.set(1, last_velocities.get(0));
                        last_velocities.set(0, current_vel);


                        //Log.d("AnsweringFragment", Float.toString(x_velocity));
                        float newX = event.getRawX() + offsetX;
                        float newY = event.getRawY() + offsetY;

                        if (!(newX < starting_pos - 10))
                            v.setX(newX);
                        break;
                    case MotionEvent.ACTION_UP:
                        // Button up

                        // Currently, the options for a "choice selection" are:
                        // 1) a velocity of 10,000 and at least 1/2 of the screen width in the positive
                        // x direction on ACTION_UP
                        // 2) at least 50/51 of the screen width in the positive x direction on
                        // ACTION_UP

                        Float max_vel = Collections.max(last_velocities);

                       // Log.d("AnsweringFragment", "Max velocity: " + Float.toString(max_vel));

                        float xValue = v.getX();

                        float halfway = (float) frame_width / 2.0f;

                        float dist;
                        if (((xValue > halfway && (max_vel > 12000))) ||
                                (xValue > ((51.0f / 100.0f) * (float) frame_width))) {
                            Log.d("AnsweringFragment", "Selected! Released at " + Float.toString(xValue) + " pixels with a " +
                                    "velocity of " + Float.toString(max_vel) + " pixels per second.");
                            dist = ending_pos - xValue;
                            selected = true;
                            cardViewBot1.setX(ending_pos_left);
                            cardViewTop1.setX(ending_pos);
                        } else {
                            dist = starting_pos - xValue;
                        }

                        TranslateAnimation anim_in = new TranslateAnimation(0, starting_pos - ending_pos_left, 0, 0);
                        anim_in.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                cardViewBot1.setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                cardViewBot1.setX(starting_pos);
                                cardViewBot1.setEnabled(true);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        anim_in.setInterpolator(new DecelerateInterpolator(1.5f));

                        TranslateAnimation anim_in_right = new TranslateAnimation(0, starting_pos - ending_pos, 0, 0);

                        anim_in_right.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                cardViewTop1.setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                cardViewTop1.setX(starting_pos);
                                cardViewTop1.setEnabled(true);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        anim_in_right.setInterpolator(new DecelerateInterpolator(1.5f));

                        TranslateAnimation anim_other = new TranslateAnimation(0, ending_pos_left - cardViewTop2.getX(), 0, 0);
                        anim_other.setInterpolator(new DecelerateInterpolator(1.5f));

                        if (selected)
                            anim_bcard2 = ObjectAnimator.ofFloat(cardViewBot2, "translationX", xValue, ending_pos);
                        else
                            anim_bcard2 = ObjectAnimator.ofFloat(cardViewBot2, "translationX", xValue, starting_pos);

                        final float deltaX = xValue - starting_pos;

                        anim_bcard2.setInterpolator(new DecelerateInterpolator(1.5f));
                        anim_bcard2.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                cardViewBot2.setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (selected) {
                                    cardViewBot2.setX(ending_pos);

                                }

                                cardViewBot2.setEnabled(true);

                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                float frac = anim_bcard2.getAnimatedFraction();

                                cardViewBot2.setTranslationX(((1 - frac) * deltaX) + starting_pos);
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });

                        anim_in.setFillEnabled(true);
                        anim_in_right.setFillEnabled(true);
                        anim_other.setFillEnabled(true);

                        // magical custom formula for creating appropriate slide speeds varying by travel distance
                        long dur = (long) ((dist / 10) * (dist / 10)) / 2;
                        if (dur > 500)
                            dur = 500;
                        if (dur < 300)
                            dur = 300;

                        anim_bcard2.setDuration(dur);
                        anim_other.setDuration(dur);
                        anim_in.setDuration(dur);
                        anim_in_right.setDuration(dur);
                        anim_other.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                cardViewTop2.setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                cardViewTop2.setX(ending_pos_left);
                                cardViewTop2.setEnabled(true);
                            }


                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        anim_other.setStartOffset(200);
                        anim_in.setStartOffset(dur + 200);
                        anim_in_right.setStartOffset(dur + 200);
                        anim_bcard2.start();
                        synchronized(questionLock) {
                            if (selected) {
                                if (currentQuestion == null) {
                                    submitCurrentQuestion(null);
                                } else {
                                    if (currentQuestion.getChoices().size() > 1) {
                                        submitCurrentQuestion(currentQuestion.getChoices().get(1));
                                        if (currentQuestion != null && currentQuestion.getChoices().size() >= 2) {
                                            imageView_cv_top1.setImageBitmap(currentQuestion.getChoices().get(0).get_image());
                                            imageView_cv_bot1.setImageBitmap(currentQuestion.getChoices().get(1).get_image());
                                            Log.d("AnsweringFragment", "Set cv_top1 (~1210) with current question " + currentQuestion.getChoices().get(0).getAnswerBody());
                                            Log.d("AnsweringFragment", "Set cv_bot1 (~1211) with current question " + currentQuestion.getChoices().get(1).getAnswerBody());
                                        }
                                        if (anim_tcard2 != null)
                                            anim_tcard2.cancel();
                                        cardViewTop2.startAnimation(anim_other);
                                        cardViewBot1.startAnimation(anim_in);
                                        cardViewTop1.startAnimation(anim_in_right);
                                    }
                                }
                            }
                        }

                        break;
                    default:
                        break;
                }
                return true;
            }


        };

        cardViewTop1.setOnTouchListener(tl);
        cardViewBot1.setOnTouchListener(tl2);
        cardViewTop2.setOnTouchListener(tl3);
        cardViewBot2.setOnTouchListener(tl4);

        return v;
    }

    /**
     * Notifys answeringfragment that a question is ready to be taken from the ready question queue
     * If a queston is needed by fragment, this function will load the question
     * @param
     * @return true if a question was taken from readyQuestions queue, false otherwise
     */
    public boolean notifyOfReadyQuestion(){
        synchronized (questionLock){
        if (this.currentQuestion==null){
            this.currentQuestion = ClientData.pollReadyQuestions();
            if (this.currentQuestion == null){
                return false;
            }else{
                if (this.currentQuestion.getChoices().size()==2){
                    Choice c1 = this.currentQuestion.getChoices().get(0);
                    Choice c2 = this.currentQuestion.getChoices().get(1);
                    if (c1.imageState== Choice.LoadState.IMAGE_READY && c1.get_image()!=null){
                        final Bitmap b = c1.get_image();
                        final String choiceBodyForLog_c1 = c1.getAnswerBody();
                        // TODO: fade out spinner
                        Animation fade_in = AnimationUtils.loadAnimation(cardViewTop1.getContext(), R.anim.fade_in);
                        fade_in.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                pb_cvtop1.setVisibility(View.INVISIBLE);
                                imageView_cv_top1.setVisibility(View.INVISIBLE);
                                imageView_cv_top1.setImageBitmap(b);
                                Log.d("AnsweringFragment", "Set cv_top1 image! (OnNotifyReady) " + choiceBodyForLog_c1);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                imageView_cv_top1.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        imageView_cv_top1.startAnimation(fade_in);
                    }
                    if (c2.imageState== Choice.LoadState.IMAGE_READY && c2.get_image()!=null){
                        final Bitmap b = c2.get_image();
                        final String choiceBodyForLog_c2 = c2.getAnswerBody();
                        // TODO: fade out spinner
                        Animation fade_in = AnimationUtils.loadAnimation(cardViewBot1.getContext(), R.anim.fade_in);
                        fade_in.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                pb_cvbot1.setVisibility(View.INVISIBLE);
                                imageView_cv_bot1.setVisibility(View.INVISIBLE);
                                imageView_cv_bot1.setImageBitmap(b);
                                Log.d("AnsweringFragment", "Set cv_bot1 image!(OnNotifyReady) " + choiceBodyForLog_c2);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                imageView_cv_bot1.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        imageView_cv_bot1.startAnimation(fade_in);
                    }
                }
                return true;
            }
        }
        if (this.nextQuestion==null) {
            this.nextQuestion = ClientData.pollReadyQuestions();
            if (this.nextQuestion == null) {
                return false;
            } else {
                if (this.nextQuestion.getChoices().size() == 2) {
                    Choice c1 = this.nextQuestion.getChoices().get(0);
                    Choice c2 = this.nextQuestion.getChoices().get(1);
                    if (c1.imageState == Choice.LoadState.IMAGE_READY && c1.get_image() != null) {
                        Bitmap b = c1.get_image();
                        imageView_cv_top2.setImageBitmap(b);
                        Log.d("AnsweringFragment", "Set cv_top2 image (in notifyOfReady) " + c1.getAnswerBody());
                    }
                    if (c2.imageState == Choice.LoadState.IMAGE_READY && c2.get_image() != null) {
                        Bitmap b = c2.get_image();
                        imageView_cv_bot2.setImageBitmap(b);
                        Log.d("AnsweringFragment", "Set cv_bot2 image (in notifyOfReady) " + c2.getAnswerBody());

                    }
                }
                return true;
            }
        }
        }
        return false; //No questions needed
    }

    /**
     * Call this when cards leave screen
     * Submits the answer to the choice, if applicable, and prepares the next question.
     * Requests more from server if we are running low on questions.
     * @param ans - The chosen answer - Null if none chosen but card was swiped away anyway
     */
    public void submitCurrentQuestion(Choice ans){
        synchronized(questionLock) {
            if (ans != null) {
                //Todo: Submit result
            }
            currentQuestion = nextQuestion;
            nextQuestion = ClientData.pollReadyQuestions(); //Remember this will return null if none exist
        }
    }

    /**
     * Call this when a new card needs to be populated with Question data
     * @return Question if one is ready (meaning fully loaded), and NULL if no question is ready
     */
    public synchronized Question getCurrentQuestion(){
        return currentQuestion;
    }


    public static int randInt(int min, int max) {

            // NOTE: Usually this should be a field rather than a method
            // variable so that it is not re-seeded every call.
            Random rand = new Random();

            // nextInt is normally exclusive of the top value,
            // so add 1 to make it inclusive
            int randomNum = rand.nextInt((max - min) + 1) + min;

            return randomNum;
        }


    /**
     * We need this setter to translate between the information the animator
     * produces (a new "PathPoint" describing the current animated location)
     * and the information that the button requires (an xy location). The
     * setter will be called by the ObjectAnimator given the 'buttonLoc'
     * property string.
     */
    public void setButtonLoc(PathPoint newLoc) {
        plus_one.setTranslationX(newLoc.mX);
        plus_one.setTranslationY(newLoc.mY);
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {

        Animator animator = null;

        if(enter)
            animator = ObjectAnimator.ofFloat(this, "translationX", 0, 0);
        if (animator != null) {
            animator.setDuration(getResources().getInteger(R.integer.transition_time));
        }
        return animator;
    }

    public void disableClicks() {
        if(card_holder_layout != null)
        {
            disable(card_holder_layout);
        }
    }

    public static void disable(ViewGroup layout) {
        layout.setEnabled(false);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup) {
                disable((ViewGroup) child);
            } else {
                child.setEnabled(false);
            }
        }
    }

}