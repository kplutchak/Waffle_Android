package com.pipit.waffle;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by Kyle on 3/12/2015.
 */
public class UserSettingsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ToolbarActivity.current_fragment_id = Constants.USER_SETTINGS_FRAGMENT_ID;

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.user_settings_fragment, container, false);

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
            animator.setDuration(300);
        }


        return animator;
    }
}