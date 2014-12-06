package com.pipit.waffle;

import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Kyle on 11/19/2014.
 */
public class AnsweringFragment extends Fragment implements SpringListener {

    private static double TENSION = 800;
    private static double DAMPER = 20; //friction

    private CardView mImageToAnimate;
    private CardView mImageToAnimate2;
    private SpringSystem mSpringSystem;
    private Spring mSpring;

    private VelocityTracker velocity = null;

    private boolean mMovedUp = false;
    private float mOrigY;
    private float mOrigX;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Outline

        View v = inflater.inflate(R.layout.answering_fragment, container, false);

        mImageToAnimate = (CardView) v.findViewById(R.id.card_view);
        mImageToAnimate2 = (CardView) v.findViewById(R.id.card_view2);
        // Set the CardViews' size and margins
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int width = size.x;
        int height = size.y;

        int margin = (int) (8 * getActivity().getResources().getDisplayMetrics().density);

        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        int dp_width = (int) ((width/displayMetrics.density)+0.5);

        CardView.LayoutParams card_params = (CardView.LayoutParams) mImageToAnimate.getLayoutParams();
        card_params.width = width - (2*margin);
        card_params.setMargins(margin, margin, margin, margin);
       // card_params.bottomMargin = margin;

        mImageToAnimate.setLayoutParams(card_params);

        CardView.LayoutParams card_params2 = (CardView.LayoutParams) mImageToAnimate2.getLayoutParams();
        card_params2.width = width - (2*margin);
        card_params2.setMargins(margin, 0, margin, margin);
        mImageToAnimate2.setLayoutParams(card_params2);



        mSpringSystem = SpringSystem.create();

        mSpring = mSpringSystem.createSpring();
        mSpring.addListener(this);

        SpringConfig config = new SpringConfig(TENSION, DAMPER);
        mSpring.setSpringConfig(config);

        Resources r = getResources();
        final float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, r.getDisplayMetrics());
        //mSpring.setEndValue(mImageToAnimate.getX());

         //CardView movement
        final View.OnTouchListener tl = new View.OnTouchListener() {
            public float offsetX;
            public float offsetY;
            private ArrayList<Float> last_velocities = new ArrayList<Float>(3);

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

                        last_velocities.add(0, 0.0f);
                        last_velocities.add(1, 0.0f);
                        last_velocities.add(2, 0.0f);

                        if(velocity == null) {
                            // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                            velocity = VelocityTracker.obtain();
                        }
                        else {
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
                        //WindowManager wm = (WindowManager) v.getSystemService(Context.WINDOW_SERVICE);
                        // Display display = wm.getDefaultDisplay();
                        //DisplayMetrics metrics = new DisplayMetrics();
                        // display.getMetrics(metrics);
                        //int width = metrics.widthPixels;
                        //int height = metrics.heightPixels;
                        if (!(newX < 5))
                            v.setX(newX);
                        //v.setY(newY);
                        break;
                    case MotionEvent.ACTION_UP:
                        // Button up

                        // Currently, the options for a "choice selection" are:
                        // 1) a velocity of 10,000 and at least 1/3 of the screen width in the positive
                        // x direction on ACTION_UP
                        // 2) at least 4/7 of the screen width in the positive x direction on
                        // ACTION_UP
                        // For case 2, we will (plan to - TODO) release the CardView at some set velocity
                        // For case 1, we will (plan to - TODO) release the CardView at some velocity near
                        // max_vel and slow it down as it approaches the right edge of the screen

                        Float max_vel = Collections.max(last_velocities);

                        Log.d("AnsweringFragment", "Max velocity: " + Float.toString(max_vel));

                        float xValue = v.getX();
                        // float yValue = v.getY();


                        if((xValue > ((float) width/3.0f)) && (max_vel > 12000))
                            Log.d("AnsweringFragment", "Selected - beyond threshold 1 and achieved sufficient velocity!");
                        if(xValue > ((4.0f/7.0f)*(float) width))
                            Log.d("AnsweringFragment", "Selected - beyond threshold 2!");

                        float dist = px - xValue;
                        TranslateAnimation anim = new TranslateAnimation(0, dist, 0, 0);
                        anim.setInterpolator(new DecelerateInterpolator(1.5f));
                        anim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                mImageToAnimate.setX(px);
                                //mSpring.setEndValue(100);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        //anim.setFillAfter(true);
                        anim.setFillEnabled(true);

                        // magical custom formula for creating appropriate slide speeds varying by travel distance
                        long dur = (long) ((dist/10) * (dist/10))/2;
                        if(dur > 500)
                            dur = 500;
                        if(dur < 200)
                            dur = 200;

                        anim.setDuration(dur);
                        mImageToAnimate.startAnimation(anim);
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

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int theAction = event.getAction();
                switch (theAction) {
                    case MotionEvent.ACTION_DOWN:
                        // Button down

                        mOrigX = v.getX();
                        mOrigY = v.getY();
                        offsetX = v.getX() - event.getRawX();
                        offsetY = v.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // Button moved

                        float newX = event.getRawX() + offsetX;
                        float newY = event.getRawY() + offsetY;
                        //WindowManager wm = (WindowManager) v.getSystemService(Context.WINDOW_SERVICE);
                        // Display display = wm.getDefaultDisplay();
                        //DisplayMetrics metrics = new DisplayMetrics();
                        // display.getMetrics(metrics);
                        //int width = metrics.widthPixels;
                        //int height = metrics.heightPixels;
                        if (!(newX < 5))
                            v.setX(newX);
                        //v.setY(newY);
                        break;
                    case MotionEvent.ACTION_UP:
                        // Button up





                        float xValue = v.getX();
                        float yValue = v.getY();

                        float dist = px - xValue;
                        TranslateAnimation anim = new TranslateAnimation(0, dist, 0, 0);
                        anim.setInterpolator(new DecelerateInterpolator(1.5f));
                        anim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                mImageToAnimate2.setX(px);
                                //mSpring.setEndValue(100);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        //anim.setFillAfter(true);
                        anim.setFillEnabled(true);
                        long dur = (long) ((dist/10) * (dist/10))/2;
                        if(dur > 500)
                            dur = 500;
                        if(dur < 200)
                            dur = 200;

                        anim.setDuration(dur);
                        mImageToAnimate2.startAnimation(anim);





                        break;
                    default:
                        break;
                }
                return true;
            }
        };

        mImageToAnimate.setOnTouchListener(tl);
        mImageToAnimate2.setOnTouchListener(tl2);



        return v;
    }


    @Override
    public void onSpringUpdate(Spring spring) {
        float value = (float) spring.getCurrentValue();

       mImageToAnimate.setX(value);
    }

    @Override
    public void onSpringAtRest(Spring spring) {

    }

    @Override
    public void onSpringActivate(Spring spring) {

    }

    @Override
    public void onSpringEndStateChange(Spring spring) {

    }

}
