package com.pipit.waffle;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
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
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.RoundedTransformationBuilder;
import com.pipit.waffle.Objects.Choice;
import com.pipit.waffle.Objects.ClientData;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Kyle on 11/19/2014.
 */
public class AnsweringFragment extends Fragment  {

    private static final String TAG = "AnsweringFragment";
    // CardViews -  there are two CardViews for top, two for bottom. The two swap when a selection
    // occurs
    private QuestionSet setOne;
    private QuestionSet setTwo;

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

    private int image_height_stored;
    private int image_height_stored_landscape;

    private VelocityTracker velocity = null;

    private LinearLayout card_holder_layout;
    private CustomScrollView answering_scrollview;
    private TouchableFrameLayout answering_frame;
    private LinearLayout main_view;

    private boolean animating_bottom_bar = false;

    private ListView listview;
    private boolean mAlreadyAtTop;
    private SlidingUpPanelLayout mLayout;

    private RelativeLayout top_comments_bar;
    private LinearLayout entire_drag_view;

    private boolean offsetIsAlreadyOne = true;

    private boolean isExpanded = false;

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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.answering_fragment, container, false);
        card_holder_layout = (LinearLayout) v.findViewById(R.id.card_holder_layout);

        //Code for bottom bar
        createBottomBar(v);

        // adjust the size of the LinearLayout that is contained within the ScrollView
        final TypedArray styledAttributes = getActivity().getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        int mActionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }

        // Height of the screen minus the Toolbar and Status Bar
        final int true_height = point.y - mActionBarSize - getStatusBarHeight();

        LinearLayout.LayoutParams card_holder_lp = (LinearLayout.LayoutParams) card_holder_layout.getLayoutParams();
        card_holder_lp.height = true_height;
        card_holder_layout.setLayoutParams(card_holder_lp);
        // end adjustments

        // Retrieve the CardViews
        CardView cardViewTop1 = (CardView) v.findViewById(R.id.card_view);
        CardView cardViewBot1 = (CardView) v.findViewById(R.id.card_view2);
        CardView cardViewTop2 = (CardView) v.findViewById(R.id.card_view_extra);
        CardView cardViewBot2 = (CardView) v.findViewById(R.id.card_view2_extra);

        // Set the CardViews' size and margins
        //Display display = getActivity().getWindowManager().getDefaultDisplay();
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

        // Create the two QuestionSets, each containing two card views, and question data
        setOne = new QuestionSet(cardViewTop1, cardViewBot1);
        setTwo = new QuestionSet(cardViewTop2, cardViewBot2);

        // TODO: make sure the two images appear at the same time. If they don't , don't reveal the one that has already loaded until the second image has finished loading
        transformation_rounded_image = new RoundedTransformationBuilder()
                .cornerRadiusDp(4).borderColor(getResources().getColor(R.color.black_tint_light)).borderWidthDp(1)
                .oval(false)
                .build();

        //Set Progress Bars
        ProgressBar pb_cvtop1 = (ProgressBar) cardViewTop1.findViewById(R.id.progress_bar_cvtop1);
        ProgressBar pb_cvbot1 = (ProgressBar) cardViewBot1.findViewById(R.id.progress_bar_cvbot1);
        setOne.setPb_cvbot(pb_cvbot1);
        setOne.setPb_cvtop(pb_cvtop1);

        ProgressBar pb_cvtop2 = (ProgressBar) cardViewTop1.findViewById(R.id.progress_bar_cvtop2);
        ProgressBar pb_cvbot2 = (ProgressBar) cardViewBot1.findViewById(R.id.progress_bar_cvbot2);
        setTwo.setPb_cvbot(pb_cvbot2);
        setTwo.setPb_cvtop(pb_cvtop2);

        CardView.LayoutParams cvtop_image_params = new CardView.LayoutParams(cardViewTop1.getLayoutParams());
        cvtop_image_params.width = card_params.width - (2 * margin_images);
        if (savedInstanceState != null && getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            cvtop_image_params.height = image_height_stored;

        cvtop_image_params.setMargins(margin_images, margin_images, margin_images, margin_images);
        setOne.getImageViewTop().setLayoutParams(cvtop_image_params);
        setTwo.getImageViewTop().setLayoutParams(cvtop_image_params);

        CardView.LayoutParams cvbot1_image_params = new CardView.LayoutParams(cardViewBot1.getLayoutParams());
        cvbot1_image_params.width = card_params.width - (2 * margin_images);
        if (savedInstanceState != null && getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            cvbot1_image_params.height = image_height_stored;

        if (savedInstanceState != null && getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                && image_height_stored_landscape != 0)
            cvbot1_image_params.height = image_height_stored_landscape;

        cvbot1_image_params.setMargins(margin_images, margin_images, margin_images, margin_images);
        setOne.getImageViewBot().setLayoutParams(cvbot1_image_params);
        setTwo.getImageViewBot().setLayoutParams(cvbot1_image_params);

        setOne.getImageViewTop().setScaleType(ImageView.ScaleType.CENTER_CROP);
        setOne.getImageViewBot().setScaleType(ImageView.ScaleType.CENTER_CROP);
        setTwo.getImageViewTop().setScaleType(ImageView.ScaleType.CENTER_CROP);
        setTwo.getImageViewBot().setScaleType(ImageView.ScaleType.CENTER_CROP);

        setOne.applyImageviewsToCards();
        setTwo.applyImageviewsToCards();

        //Set textViews
        setOne.getTextViewBot().setLayoutParams(cvbot1_image_params);
        setTwo.getTextViewBot().setLayoutParams(cvbot1_image_params);

        setOne.applyTextviewsToCards();
        setTwo.applyTextviewsToCards();
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
                        // Currently, the options for a "choice selection" are:
                        // 1) a velocity of 12,000 and at least 1/2 of the screen width in the positive
                        // x direction on ACTION_UP
                        // 2) at least 51/100 of the screen width in the positive x direction on
                        Float max_vel = Collections.max(last_velocities);

                        float xValue = v.getX();
                        float halfway = (float) frame_width / 2.0f;

                        float dist;
                        if (((xValue > halfway && (max_vel > 12000))) ||
                                (xValue > ((51.0f / 100.0f) * (float) frame_width))) {
                            Log.d("AnsweringFragment", "Selected! Released at " + Float.toString(xValue) + " pixels with a " +
                                    "velocity of " + Float.toString(max_vel) + " pixels per second.");
                            dist = ending_pos - xValue;
                            selected = true;
                            setTwo.getCardViewTop().setX(ending_pos_left);
                            setTwo.getCardViewBot().setX(ending_pos);
                        } else {
                            dist = starting_pos - xValue;
                        }

                        TranslateAnimation anim_in = new TranslateAnimation(0, starting_pos - ending_pos_left, 0, 0);
                        anim_in.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                setTwo.getCardViewTop().setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                setTwo.getCardViewTop().setX(starting_pos);
                                setTwo.getCardViewTop().setEnabled(true);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });

                        anim_in.setInterpolator(new DecelerateInterpolator(1.5f));
                        TranslateAnimation anim_in_right = new TranslateAnimation(0, starting_pos - ending_pos, 0, 0);

                        anim_in_right.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                setTwo.getCardViewBot().setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                setTwo.getCardViewBot().setX(starting_pos);
                                setTwo.getCardViewBot().setEnabled(true);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });

                        anim_in_right.setInterpolator(new DecelerateInterpolator(1.5f));

                        TranslateAnimation anim_other = new TranslateAnimation(0, ending_pos_left - setOne.getCardViewBot().getX(), 0, 0);
                        anim_other.setInterpolator(new DecelerateInterpolator(1.5f));

                        if (selected)
                            anim_tcard1 = ObjectAnimator.ofFloat(setOne.getCardViewTop(), "translationX", xValue - starting_pos, ending_pos - starting_pos);
                        else
                            anim_tcard1 = ObjectAnimator.ofFloat(setOne.getCardViewTop(), "translationX", xValue - starting_pos, 0);

                        final float deltaX = xValue - starting_pos;

                        anim_tcard1.setInterpolator(new DecelerateInterpolator(1.5f));
                        anim_tcard1.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                setOne.getCardViewTop().setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (selected) {
                                    setOne.getCardViewTop().setX(ending_pos);
                                }
                                setOne.getCardViewTop().setEnabled(true);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                float frac = anim_tcard1.getAnimatedFraction();
                                setOne.getCardViewTop().setTranslationX((1 - frac) * deltaX);
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                            }
                        });

                        anim_in.setFillEnabled(true);
                        anim_in_right.setFillEnabled(true);
                        anim_other.setFillEnabled(true);

                        long dur = AnsweringFragmentHelpers.calculateSlideSpeed(dist);
                        anim_tcard1.setDuration(dur);
                        anim_other.setDuration(dur);
                        anim_in.setDuration(dur);
                        anim_in_right.setDuration(dur);

                        anim_other.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                setOne.getCardViewBot().setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                setOne.getCardViewBot().setX(ending_pos_left);
                                setOne.getCardViewBot().setEnabled(true);
                                setOne.resetQuestionData();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });
                        anim_other.setStartOffset(AnsweringFragmentHelpers.ANIM_STARTING_OFFSET);
                        anim_in.setStartOffset(dur + AnsweringFragmentHelpers.ANIM_STARTING_OFFSET);
                        anim_in_right.setStartOffset(dur + AnsweringFragmentHelpers.ANIM_STARTING_OFFSET);
                        anim_tcard1.start();

                        if (selected) {
                            synchronized(questionLock){
                        if (setOne.getQuestion() == null){
                                    submitQuestionOne(null);
                            }else {
                                submitQuestionOne(setOne.getQuestion().getChoices().get(0));
                            }
                            if(setTwo.getQuestion() != null && setTwo.getQuestion().getChoices().size()>=2) {
                                setTwo.setBitmapTop();
                                setTwo.setBitmapBot();
                                setTwo.setTextBot();
                                setTwo.setTextTop();
                                //setTwo.getImageViewTop().setImageBitmap(setTwo.getQuestion().getChoices().get(0).get_image());
                                //setTwo.getImageViewBot().setImageBitmap(setTwo.getQuestion().getChoices().get(1).get_image());
                                Log.d("AnsweringFragment", "Set cv_top2 (~476) with current question " + setTwo.getQuestion().getChoices().get(0).getAnswerBody());
                                Log.d("AnsweringFragment", "Set cv_bot2 (~477) with current question " + setTwo.getQuestion().getChoices().get(1).getAnswerBody());
                            }

                            if (anim_bcard1 != null)
                                anim_bcard1.cancel();

                            setOne.getCardViewBot().startAnimation(anim_other);
                            setOne.showingStatus = QuestionSet.ShowingStatus.STANDBY;

                            setTwo.getCardViewTop().startAnimation(anim_in);
                            setTwo.getCardViewBot().startAnimation(anim_in_right);
                            setTwo.showingStatus = QuestionSet.ShowingStatus.ACTIVE;
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
                        // Remember the last 3 velocities
                        last_velocities.set(2, last_velocities.get(1));
                        last_velocities.set(1, last_velocities.get(0));
                        last_velocities.set(0, current_vel);

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
                            setTwo.getCardViewBot().setX(ending_pos_left);
                            setTwo.getCardViewTop().setX(ending_pos);
                        } else {
                            dist = starting_pos - xValue;
                        }


                        TranslateAnimation anim_in = new TranslateAnimation(0, starting_pos - ending_pos_left, 0, 0);
                        anim_in.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                setTwo.getCardViewBot().setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                setTwo.getCardViewBot().setX(starting_pos);
                                setTwo.getCardViewBot().setEnabled(true);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });

                        anim_in.setInterpolator(new DecelerateInterpolator(1.5f));

                        TranslateAnimation anim_in_right = new TranslateAnimation(0, starting_pos - ending_pos, 0, 0);

                        anim_in_right.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                setTwo.getCardViewTop().setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                setTwo.getCardViewTop().setX(starting_pos);
                                setTwo.getCardViewTop().setEnabled(true);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });

                        anim_in_right.setInterpolator(new DecelerateInterpolator(1.5f));

                        TranslateAnimation anim_other = new TranslateAnimation(0, ending_pos_left - setOne.getCardViewTop().getX(), 0, 0);
                        anim_other.setInterpolator(new DecelerateInterpolator(1.5f));

                        if (selected)
                            anim_bcard1 = ObjectAnimator.ofFloat(setOne.getCardViewBot(), "translationX", xValue - starting_pos, ending_pos - starting_pos);
                        else
                            anim_bcard1 = ObjectAnimator.ofFloat(setOne.getCardViewBot(), "translationX", xValue - starting_pos, 0);

                        final float deltaX = xValue - starting_pos;

                        anim_bcard1.setInterpolator(new DecelerateInterpolator(1.5f));
                        anim_bcard1.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                setOne.getCardViewBot().setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (selected) {
                                    setOne.getCardViewBot().setX(ending_pos);
                                }
                                setOne.getCardViewBot().setEnabled(true);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                float frac = anim_bcard1.getAnimatedFraction();
                                setOne.getCardViewBot().setTranslationX((1 - frac) * deltaX);

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) { }
                        });

                        anim_in.setFillEnabled(true);
                        anim_in_right.setFillEnabled(true);
                        anim_other.setFillEnabled(true);

                        long dur = AnsweringFragmentHelpers.calculateSlideSpeed(dist);
                        anim_bcard1.setDuration(dur);
                        anim_other.setDuration(dur);
                        anim_in.setDuration(dur);
                        anim_in_right.setDuration(dur);
                        anim_other.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                setOne.getCardViewBot().setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                setOne.getCardViewTop().setX(ending_pos_left);
                                setOne.getCardViewTop().setEnabled(true);
                                setOne.resetQuestionData();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });
                        anim_other.setStartOffset(AnsweringFragmentHelpers.ANIM_STARTING_OFFSET);
                        anim_in.setStartOffset(dur + AnsweringFragmentHelpers.ANIM_STARTING_OFFSET);
                        anim_in_right.setStartOffset(dur + AnsweringFragmentHelpers.ANIM_STARTING_OFFSET);
                        anim_bcard1.start();
                        if (selected) {
                            synchronized(AnsweringFragment.this.questionLock) {
                                if (setOne.getQuestion() == null){
                                    submitQuestionOne(null);
                                }else {
                                    submitQuestionOne(setOne.getQuestion().getChoices().get(1));
                                }
                                if (setTwo.getQuestion() != null && setTwo.getQuestion().getChoices().size() >= 2) {
                                    setTwo.setBitmapTop();
                                    setTwo.setBitmapBot();
                                    setTwo.setTextBot();
                                    setTwo.setTextTop();
                                    //setTwo.getImageViewTop().setImageBitmap(setTwo.getQuestion().getChoices().get(0).get_image());
                                    //setTwo.getImageViewBot().setImageBitmap(setTwo.getQuestion().getChoices().get(1).get_image());
                                    Log.d("AnsweringFragment", "Set cv_top2 (~722) with current question " + setTwo.getQuestion().getChoices().get(0).getAnswerBody());
                                    Log.d("AnsweringFragment", "Set cv_bot2 (~722) with current question " + setTwo.getQuestion().getChoices().get(1).getAnswerBody());
                                }
                                if (anim_tcard1 != null)
                                    anim_tcard1.cancel();
                                setOne.getCardViewTop().startAnimation(anim_other);
                                setOne.showingStatus = QuestionSet.ShowingStatus.STANDBY;

                                setTwo.getCardViewBot().startAnimation(anim_in);
                                setTwo.getCardViewTop().startAnimation(anim_in_right);
                                setTwo.showingStatus = QuestionSet.ShowingStatus.ACTIVE;
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
                        velocity.addMovement(event);
                        velocity.computeCurrentVelocity(1000);
                        float current_vel = VelocityTrackerCompat.getXVelocity(velocity,
                                pointerId);

                        last_velocities.set(2, last_velocities.get(1));
                        last_velocities.set(1, last_velocities.get(0));
                        last_velocities.set(0, current_vel);

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
                        float xValue = v.getX();
                        float halfway = (float) frame_width / 2.0f;
                        float dist;
                        if (((xValue > halfway && (max_vel > 12000))) ||
                                (xValue > ((51.0f / 100.0f) * (float) frame_width))) {
                            dist = ending_pos - xValue;
                            selected = true;
                            setOne.getCardViewTop().setX(ending_pos_left);
                            setOne.getCardViewBot().setX(ending_pos);
                        } else {
                            dist = starting_pos - xValue;
                        }

                        TranslateAnimation anim_in = new TranslateAnimation(0, starting_pos - ending_pos_left, 0, 0);
                        anim_in.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                setOne.getCardViewTop().setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                setOne.getCardViewTop().setX(starting_pos);
                                setOne.getCardViewTop().setEnabled(true);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });

                        anim_in.setInterpolator(new DecelerateInterpolator(1.5f));

                        TranslateAnimation anim_in_right = new TranslateAnimation(0, starting_pos - ending_pos, 0, 0);

                        anim_in_right.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                setOne.getCardViewBot().setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                setOne.getCardViewBot().setX(starting_pos);
                                setOne.getCardViewBot().setEnabled(true);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });

                        anim_in_right.setInterpolator(new DecelerateInterpolator(1.5f));

                        TranslateAnimation anim_other = new TranslateAnimation(0, ending_pos_left - setTwo.getCardViewBot().getX(), 0, 0);
                        anim_other.setInterpolator(new DecelerateInterpolator(1.5f));

                        if (selected)
                            anim_tcard2 = ObjectAnimator.ofFloat(setTwo.getCardViewTop(), "translationX", xValue, ending_pos);
                        else
                            anim_tcard2 = ObjectAnimator.ofFloat(setTwo.getCardViewTop(), "translationX", xValue, starting_pos);

                        final float deltaX = xValue - starting_pos;

                        anim_tcard2.setInterpolator(new DecelerateInterpolator(1.5f));
                        anim_tcard2.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                setTwo.getCardViewTop().setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (selected) {
                                    setTwo.getCardViewTop().setX(ending_pos);
                                }
                                setTwo.getCardViewTop().setEnabled(true);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                float frac = anim_tcard2.getAnimatedFraction();
                                setTwo.getCardViewTop().setTranslationX(((1 - frac) * deltaX) + starting_pos);
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {}
                        });

                        anim_in.setFillEnabled(true);
                        anim_in_right.setFillEnabled(true);
                        anim_other.setFillEnabled(true);

                        long dur = AnsweringFragmentHelpers.calculateSlideSpeed(dist);
                        anim_tcard2.setDuration(dur);
                        anim_other.setDuration(dur);
                        anim_in.setDuration(dur);
                        anim_in_right.setDuration(dur);
                        anim_other.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                setTwo.getCardViewBot().setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                setTwo.getCardViewBot().setX(ending_pos_left);
                                setTwo.getCardViewBot().setEnabled(true);
                                //Reset the previous image
                                setTwo.resetQuestionData();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });
                        anim_other.setStartOffset(AnsweringFragmentHelpers.ANIM_STARTING_OFFSET);
                        anim_in.setStartOffset(dur + AnsweringFragmentHelpers.ANIM_STARTING_OFFSET);
                        anim_in_right.setStartOffset(dur + AnsweringFragmentHelpers.ANIM_STARTING_OFFSET);
                        anim_tcard2.start();
                        synchronized(questionLock) {
                            if (selected) {
                                if (setTwo.getQuestion() == null){
                                    submitQuestionTwo(null);
                                }else {
                                    submitQuestionTwo(setTwo.getQuestion().getChoices().get(0));
                                }
                                if (setOne.getQuestion() != null && setOne.getQuestion().getChoices().size() >= 2) {
                                    setOne.setBitmapTop();
                                    setOne.setBitmapBot();
                                    setOne.setTextBot();
                                    setOne.setTextTop();
                                    //setOne.getImageViewTop().setImageBitmap(setOne.getQuestion().getChoices().get(0).get_image());
                                    //setOne.getImageViewBot().setImageBitmap(setOne.getQuestion().getChoices().get(1).get_image());
                                    Log.d("AnsweringFragment", "Set cv_top1 (~968) with current question " + setOne.getQuestion().getChoices().get(0).getAnswerBody());
                                    Log.d("AnsweringFragment", "Set cv_bot1 (~969) with current question " + setOne.getQuestion().getChoices().get(1).getAnswerBody());
                                }
                                if (anim_bcard2 != null)
                                    anim_bcard2.cancel();
                                setTwo.getCardViewBot().startAnimation(anim_other);
                                setTwo.showingStatus = QuestionSet.ShowingStatus.STANDBY;

                                setOne.getCardViewTop().startAnimation(anim_in);
                                setOne.getCardViewBot().startAnimation(anim_in_right);
                                setOne.showingStatus = QuestionSet.ShowingStatus.ACTIVE;
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
                        last_velocities.clear();
                        selected = false;

                        last_velocities.add(0, 0.0f);
                        last_velocities.add(1, 0.0f);
                        last_velocities.add(2, 0.0f);

                        if (velocity == null) {
                            velocity = VelocityTracker.obtain();
                        } else {
                            velocity.clear();
                        }

                        mOrigX = v.getX();
                        mOrigY = v.getY();
                        offsetX = v.getX() - event.getRawX();
                        offsetY = v.getY() - event.getRawY();

                        velocity.addMovement(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        velocity.addMovement(event);
                        velocity.computeCurrentVelocity(1000);
                        float current_vel = VelocityTrackerCompat.getXVelocity(velocity,
                                pointerId);
                        last_velocities.set(2, last_velocities.get(1));
                        last_velocities.set(1, last_velocities.get(0));
                        last_velocities.set(0, current_vel);

                        float newX = event.getRawX() + offsetX;
                        float newY = event.getRawY() + offsetY;

                        if (!(newX < starting_pos - 10))
                            v.setX(newX);
                        break;
                    case MotionEvent.ACTION_UP:
                        Float max_vel = Collections.max(last_velocities);

                        float xValue = v.getX();
                        float halfway = (float) frame_width / 2.0f;
                        float dist;
                        if (((xValue > halfway && (max_vel > 12000))) ||
                                (xValue > ((51.0f / 100.0f) * (float) frame_width))) {
                            dist = ending_pos - xValue;
                            selected = true;
                            setOne.getCardViewBot().setX(ending_pos_left);
                            setOne.getCardViewTop().setX(ending_pos);
                        } else {
                            dist = starting_pos - xValue;
                        }

                        TranslateAnimation anim_in = new TranslateAnimation(0, starting_pos - ending_pos_left, 0, 0);
                        anim_in.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                setOne.getCardViewBot().setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                setOne.getCardViewBot().setX(starting_pos);
                                setOne.getCardViewBot().setEnabled(true);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });

                        anim_in.setInterpolator(new DecelerateInterpolator(1.5f));

                        TranslateAnimation anim_in_right = new TranslateAnimation(0, starting_pos - ending_pos, 0, 0);

                        anim_in_right.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                setOne.getCardViewTop().setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                setOne.getCardViewTop().setX(starting_pos);
                                setOne.getCardViewTop().setEnabled(true);
                            }
                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });

                        anim_in_right.setInterpolator(new DecelerateInterpolator(1.5f));

                        TranslateAnimation anim_other = new TranslateAnimation(0, ending_pos_left - setTwo.getCardViewTop().getX(), 0, 0);
                        anim_other.setInterpolator(new DecelerateInterpolator(1.5f));

                        if (selected)
                            anim_bcard2 = ObjectAnimator.ofFloat(setTwo.getCardViewBot(), "translationX", xValue, ending_pos);
                        else
                            anim_bcard2 = ObjectAnimator.ofFloat(setTwo.getCardViewBot(), "translationX", xValue, starting_pos);
                        final float deltaX = xValue - starting_pos;

                        anim_bcard2.setInterpolator(new DecelerateInterpolator(1.5f));
                        anim_bcard2.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                setTwo.getCardViewBot().setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (selected) {
                                    setTwo.getCardViewBot().setX(ending_pos);
                                }
                                setTwo.getCardViewBot().setEnabled(true);
                            }
                            @Override
                            public void onAnimationCancel(Animator animation) {
                                float frac = anim_bcard2.getAnimatedFraction();
                                setTwo.getCardViewBot().setTranslationX(((1 - frac) * deltaX) + starting_pos);
                            }
                            @Override
                            public void onAnimationRepeat(Animator animation) {}
                        });

                        anim_in.setFillEnabled(true);
                        anim_in_right.setFillEnabled(true);
                        anim_other.setFillEnabled(true);

                        long dur = AnsweringFragmentHelpers.calculateSlideSpeed(dist);
                        anim_bcard2.setDuration(dur);
                        anim_other.setDuration(dur);
                        anim_in.setDuration(dur);
                        anim_in_right.setDuration(dur);
                        anim_other.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                setTwo.getCardViewTop().setEnabled(false);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                setTwo.getCardViewTop().setX(ending_pos_left);
                                setTwo.getCardViewTop().setEnabled(true);
                                setTwo.resetQuestionData();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        anim_other.setStartOffset(AnsweringFragmentHelpers.ANIM_STARTING_OFFSET);
                        anim_in.setStartOffset(dur + AnsweringFragmentHelpers.ANIM_STARTING_OFFSET);
                        anim_in_right.setStartOffset(dur + AnsweringFragmentHelpers.ANIM_STARTING_OFFSET);
                        anim_bcard2.start();
                        synchronized(questionLock) {
                            if (selected) {
                                if (setTwo.getQuestion() == null) {
                                    submitQuestionTwo(null);
                                } else {
                                    if (setTwo.getQuestion().getChoices().size() > 1) {
                                        submitQuestionTwo(setTwo.getQuestion().getChoices().get(1));
                                        if (setOne.getQuestion() != null && setOne.getQuestion().getChoices().size() >= 2) {
                                            setOne.setBitmapTop();
                                            setOne.setBitmapBot();
                                            setOne.setTextBot();
                                            setOne.setTextTop();
                                            //setOne.getImageViewTop().setImageBitmap(setOne.getQuestion().getChoices().get(0).get_image());
                                            //setOne.getImageViewBot().setImageBitmap(setOne.getQuestion().getChoices().get(1).get_image());
                                            Log.d("AnsweringFragment", "Set cv_top1 (~1210) with current question " + setOne.getQuestion().getChoices().get(0).getAnswerBody());
                                            Log.d("AnsweringFragment", "Set cv_bot1 (~1211) with current question " + setOne.getQuestion().getChoices().get(1).getAnswerBody());
                                        }
                                        if (anim_tcard2 != null)
                                            anim_tcard2.cancel();
                                        setTwo.getCardViewTop().startAnimation(anim_other);
                                        setTwo.showingStatus = QuestionSet.ShowingStatus.STANDBY;

                                        setOne.getCardViewBot().startAnimation(anim_in);
                                        setOne.getCardViewTop().startAnimation(anim_in_right);
                                        setOne.showingStatus = QuestionSet.ShowingStatus.ACTIVE;
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
    public boolean notifyOfReadyQuestion() {
        synchronized (questionLock) {
            Log.d("AnsweringFragment", "notifyOfReadyQuestion setOneQuestion:"+setOne.getQuestion()+" imageViewTop:" + setOne.getImageViewTop().getDrawable());
            Log.d("AnsweringFragment", "notifyOfReadyQuestion setTwoQuestion:"+setOne.getQuestion()+" imageViewTop:" + setOne.getImageViewTop().getDrawable());
            if (setOne.getQuestion() == null && setOne.getImageViewTop().getDrawable() == null && setOne.getImageViewBot().getDrawable() == null) {
                setOne.setQuestion(ClientData.pollReadyQuestions());
                Log.d("AnsweringFragment", "card one to be set" + setOne.getQuestion());
                if (setOne.getQuestion() == null) {
                    return false;
                } else {
                    Log.d("AnsweringFragment", "setting Card 1 " + setOne.getQuestion().getQuestionBody());
                    if (setOne.getQuestion().getChoices().size() == 2) {
                        Choice c1 = setOne.getQuestion().getChoices().get(0);
                        Choice c2 = setOne.getQuestion().getChoices().get(1);
                        if (c1.imageState == Choice.LoadState.IMAGE_READY && c1.get_image() != null) {
                            final Bitmap b = c1.get_image();
                            final String text = c1.getAnswerBody();
                            setCardImageWithAnimation(b, setOne.getCardViewTop(), setOne.getImageViewTop(), setOne.getPb_cvtop(), setOne.getTextViewTop(), text);
                        }
                        if (c2.imageState == Choice.LoadState.IMAGE_READY && c2.get_image() != null) {
                            final Bitmap b = c2.get_image();
                            final String text = c2.getAnswerBody();
                            setCardImageWithAnimation(b, setOne.getCardViewBot(), setOne.getImageViewBot(), setOne.getPb_cvbot(), setOne.getTextViewBot(), text);
                        }
                        return true;
                        }
                    }
                }
            if (setTwo.getQuestion() == null && setTwo.getImageViewTop().getDrawable() == null && setTwo.getImageViewBot().getDrawable() == null) {
                setTwo.setQuestion(ClientData.pollReadyQuestions());
                Log.d("AnsweringFragment", "card two to be set" + setTwo.getQuestion());
                if (setTwo.getQuestion() == null) {
                    return false;
                } else {
                    Log.d("AnsweringFragment", "setting Card 2 " + setTwo.getQuestion().getQuestionBody());
                    if (setTwo.getQuestion().getChoices().size() == 2) {
                        Choice c1 = setTwo.getQuestion().getChoices().get(0);
                        Choice c2 = setTwo.getQuestion().getChoices().get(1);
                        if (c1.imageState == Choice.LoadState.IMAGE_READY && c1.get_image() != null) {
                            Bitmap b = c1.get_image();
                            final String text = c1.getAnswerBody();
                            setCardImageWithAnimation(b, setTwo.getCardViewTop(), setTwo.getImageViewTop(), setTwo.getPb_cvtop(), setTwo.getTextViewTop(), text);
                            Log.d("AnsweringFragment", "Set cv_top2 image (in notifyOfReady) " + c1.getAnswerBody());
                        }
                        if (c2.imageState == Choice.LoadState.IMAGE_READY && c2.get_image() != null) {
                            Bitmap b = c2.get_image();
                            final String text = c2.getAnswerBody();
                            setCardImageWithAnimation(b, setTwo.getCardViewBot(), setTwo.getImageViewBot(), setTwo.getPb_cvbot(), setTwo.getTextViewBot(), text);
                            Log.d("AnsweringFragment", "Set cv_bot2 image (in notifyOfReady) " + c2.getAnswerBody());
                        }
                    }
                    return true;
                }
            }
            }
            return false; //No questions needed
    }

    private boolean setCardImageWithAnimation(final Bitmap b,final CardView cv, final ImageView iv, final ProgressBar pb, final AutoResizeTextView tv, final String txt ){
        if (iv.getDrawable()!=null){
            return false;
        }
        Animation fade_in = AnimationUtils.loadAnimation(cv.getContext(), R.anim.fade_in);
        fade_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (pb!=null) {
                    pb.setVisibility(View.INVISIBLE);
                }
                iv.setVisibility(View.INVISIBLE);
                iv.setImageBitmap(b);
                tv.setText(txt);
                Log.d("AnsweringFragment", "Set an image (OnNotifyReady) in setCardImagewithAnimation");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        iv.startAnimation(fade_in);
        return true;
    }

    /**
     * Call this when cards leave screen
     * Submits the answer to the choice, if applicable, and prepares the next question.
     * Requests more from server if we are running low on questions.
     * @param ans - The chosen answer - Null if none chosen but card was swiped away anyway
     */
    public void submitQuestionOne(Choice ans){
        synchronized(questionLock) {
            if (ans != null) {
                //Todo: Submit result
            }

        setOne.setQuestion(ClientData.pollReadyQuestions()); //Remember this will return null if none exist
        }
    }

    public void submitQuestionTwo(Choice ans){
        synchronized(questionLock) {
            if (ans != null) {
                //Todo: Submit result
            }
           // setTwo.resetQuestionData();
            setTwo.setQuestion(ClientData.pollReadyQuestions());

        }
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

    private void createBottomBar(View v){
        // Bottom bar code
        mLayout = (SlidingUpPanelLayout) v.findViewById(R.id.sliding_layout);

        top_comments_bar = (RelativeLayout) v.findViewById(R.id.top_comments_bar);
        entire_drag_view = (LinearLayout) v.findViewById(R.id.dragView);

        final TextView t = (TextView) v.findViewById(R.id.name);
        t.setText("Mila Kunis asked...");

        final TextView num_comments_tv = (TextView) v.findViewById(R.id.num_comments);
        final ImageView comments_icon = (ImageView) v.findViewById(R.id.comments_icon);
        final ImageView comments_profile = (ImageView) v.findViewById(R.id.comments_profile);
        final TextView expanded_tv = (TextView) v.findViewById(R.id.expanded_textview);

        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        mLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);

                int image_alpha = (int) ((1-(slideOffset*2))*255);
                if(image_alpha < 0)
                    image_alpha = 0;

                t.setAlpha(1-(slideOffset*2));
                num_comments_tv.setAlpha(1-(slideOffset*2));
                comments_icon.setImageAlpha(image_alpha);
                comments_profile.setImageAlpha(image_alpha);
                expanded_tv.setAlpha((slideOffset*2)-1);
            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.i(TAG, "onPanelExpanded");
                isExpanded = true;
                mLayout.setDragView(top_comments_bar);
            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed");
                isExpanded = false;
            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.i(TAG, "onPanelHidden");
            }
        });

        // Set the bar's contents appropriately, depending on the state of the bottom bar
        if(isExpanded)
        {
            t.setAlpha(0);
            num_comments_tv.setAlpha(0);
            comments_icon.setImageAlpha(0);
            comments_profile.setImageAlpha(0);
            expanded_tv.setAlpha(1);
        }
        else
        {
            t.setAlpha(1);
            num_comments_tv.setAlpha(1f);
            comments_icon.setImageAlpha(255);
            comments_profile.setImageAlpha(255);
            expanded_tv.setAlpha(0);
        }

        // Set the hidden comments
        listview = (ListView) v.findViewById(R.id.comments_listview);
        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
                "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
                "Android", "iPhone", "WindowsMobile" };

        final ArrayList<String> list = new ArrayList<String>();
        for(int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }

        listview.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }

            int mPosition=0;
            int mOffset=0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // if(!canScrollUp(view))
                // Log.d(TAG, "We're at the bottom of the comments ListView first.");
            }
        });

        CommentsArrayAdapter adapter = new CommentsArrayAdapter(v.getContext(), values);
        listview.setAdapter(adapter);

        // end bottom bar code
    }

    // TODO: devices that have no status bar/different status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


}