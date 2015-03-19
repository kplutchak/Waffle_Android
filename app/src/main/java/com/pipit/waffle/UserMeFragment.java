package com.pipit.waffle;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Kyle on 1/4/2015.
 */
public class UserMeFragment extends Fragment {

    private ImageView user_image;

    private LinearLayout holder_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ToolbarActivity.current_fragment_id = Constants.USER_ME_FRAGMENT_ID;
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.user_me_fragment, container, false);

        holder_layout = (LinearLayout) v;

        user_image = (ImageView) v.findViewById(R.id.user_settings_picture);

        ImageLoader.getInstance().loadImage("http://www.brandingmagazine.com/wp-content/uploads/2014/02/mila-kunis-jim-bean-cover.jpg", new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                user_image.setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

      // TODO: set logout click behavior

        return v;
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float displayWidth = size.x;

        Animator animator = null;

        if(enter) {
            animator = ObjectAnimator.ofFloat(this, "translationX", displayWidth, 0);
            // TODO: play with interpolator
            animator.setInterpolator(new AccelerateInterpolator(0.8f));
            animator.setDuration(getResources().getInteger(R.integer.transition_time));
        }
        else
        {
            if(getFragmentManager().getBackStackEntryCount() == 0)
            {
                animator = ObjectAnimator.ofFloat(this, "translationX", 0, displayWidth);
                // TODO: play with interpolator
                animator.setInterpolator(new AccelerateInterpolator(0.8f));
                animator.setDuration(getResources().getInteger(R.integer.transition_time));
            }

        }

        return animator;
    }

    public void disableClicks() {
        if(holder_layout != null)
        {
            disable(holder_layout);
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