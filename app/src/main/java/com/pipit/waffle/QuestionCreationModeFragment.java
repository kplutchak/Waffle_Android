package com.pipit.waffle;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.TransitionDrawable;
import android.media.Image;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.github.siyamed.shapeimageview.path.parser.SvgToPath;

/**
 * Created by Kyle on 11/19/2014.
 */
public class QuestionCreationModeFragment extends Fragment {


    public class ReverseInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float paramFloat) {
            return Math.abs(paramFloat -1f);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.question_creation_mode_fragment, container, false);



        //Outline(s)


        final ImageView text_shadow = (ImageView) v.findViewById(R.id.text_shadow);
        final ImageView mic_shadow = (ImageView) v.findViewById(R.id.voice_shadow);
        final ImageView camera_shadow = (ImageView) v.findViewById(R.id.camera_shadow);

        final FrameLayout voice_frame = (FrameLayout) v.findViewById(R.id.voice_frame);
        final FrameLayout camera_frame = (FrameLayout) v.findViewById(R.id.camera_frame);
        final FrameLayout text_frame = (FrameLayout) v.findViewById(R.id.text_frame);


        text_shadow.setAlpha(0.2f);
        final Animation anim = AnimationUtils.loadAnimation(v.getContext(), R.anim.scale_up);
        final Animation anim_reverse = AnimationUtils.loadAnimation(v.getContext(), R.anim.scale_circle_reverse);
        final Animation scale_frame = AnimationUtils.loadAnimation(v.getContext(), R.anim.scale_frame);

        final Animation expand_box = AnimationUtils.loadAnimation(v.getContext(), R.anim.scale_from_corner);

        expand_box.setFillAfter(true);

        final ImageView box = (ImageView) v.findViewById(R.id.rectangle_expanding);

        scale_frame.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                box.setVisibility(View.VISIBLE);
                box.startAnimation(expand_box);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        scale_frame.setFillAfter(true);
        anim_reverse.setFillAfter(true);
        anim.setFillAfter(true); // Needed to keep the result of the animation

       // anim_reverse.setInterpolator(new ReverseInterpolator());

        text_shadow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    TransitionDrawable transition = (TransitionDrawable) text_shadow.getDrawable();
                    transition.startTransition(500);
                    text_shadow.startAnimation(anim);

                    return true;
                }
                if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    text_shadow.startAnimation(anim_reverse);
                    TransitionDrawable transition = (TransitionDrawable) text_shadow.getDrawable();
                    transition.reverseTransition(500);
                    return true;
                }
                return true;
            }
        });


        camera_shadow.setAlpha(0.2f);

        final ImageView text_hex = (ImageView) v.findViewById(R.id.answer_camera);

        // anim_reverse.setInterpolator(new ReverseInterpolator());

        camera_shadow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    TransitionDrawable transition = (TransitionDrawable) camera_shadow.getDrawable();
                    transition.startTransition(500);
                    camera_shadow.startAnimation(anim);

                    return true;
                }
                if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    camera_shadow.startAnimation(anim_reverse);
                    TransitionDrawable transition = (TransitionDrawable) camera_shadow.getDrawable();
                    transition.reverseTransition(500);
                    TranslateAnimation ta = new TranslateAnimation(0, -25-camera_frame.getX(), 0, -25-camera_frame.getY());
                    ta.setDuration(500);
                    ta.setFillAfter(true);
                    ta.setInterpolator(new DecelerateInterpolator());



                    text_hex.startAnimation(scale_frame);

                    AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
                    anim.setDuration(500);
                    anim.setFillAfter(true);

                    voice_frame.startAnimation(anim);
                    text_frame.startAnimation(anim);




                    //ScaleAnimation sa = new ScaleAnimation();

                    camera_frame.startAnimation(ta);

                    return true;
                }
                return true;
            }
        });


        mic_shadow.setAlpha(0.2f);


        // anim_reverse.setInterpolator(new ReverseInterpolator());

        mic_shadow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    TransitionDrawable transition = (TransitionDrawable) mic_shadow.getDrawable();
                    transition.startTransition(500);
                    mic_shadow.startAnimation(anim);

                    return true;
                }
                if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    mic_shadow.startAnimation(anim_reverse);
                    TransitionDrawable transition = (TransitionDrawable) mic_shadow.getDrawable();
                    transition.reverseTransition(500);
                    return true;
                }
                return true;
            }
        });



       // Button tb = (Button) v.findViewById(R.id.test_button);
       // tb.bringToFront();



/*
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.stay);

            }
        });

*/

        /*

        Button fab2 = (Button) v.findViewById(R.id.answer_camera);

        ViewOutlineProvider viewOutlineProvider2 = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Or read size directly from the view's width/height
                int size = getResources().getDimensionPixelSize(R.dimen.button_answering_size);
                outline.setOval(0, 0, size, size);
                outline.setConvexPath();
            }
        };
        fab2.setOutlineProvider(viewOutlineProvider);

        fab2.setClipToOutline(true);

        Button fab3 = (Button) v.findViewById(R.id.answer_voice);

        ViewOutlineProvider viewOutlineProvider3 = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Or read size directly from the view's width/height
                int size = getResources().getDimensionPixelSize(R.dimen.button_answering_size);
                outline.setOval(0, 0, size, size);
            }
        };
        fab3.setOutlineProvider(viewOutlineProvider);

        fab3.setClipToOutline(true);
*/
        return v;
    }

}
