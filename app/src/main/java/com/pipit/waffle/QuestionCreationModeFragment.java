package com.pipit.waffle;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
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
        text_shadow.setAlpha(0.2f);
        final Animation anim = AnimationUtils.loadAnimation(v.getContext(), R.anim.scale_up);
        final Animation anim_reverse = AnimationUtils.loadAnimation(v.getContext(), R.anim.scale_circle_reverse);


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

        final ImageView camera_shadow = (ImageView) v.findViewById(R.id.camera_shadow);
        camera_shadow.setAlpha(0.2f);


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
                    return true;
                }
                return true;
            }
        });

        final ImageView mic_shadow = (ImageView) v.findViewById(R.id.voice_shadow);
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
