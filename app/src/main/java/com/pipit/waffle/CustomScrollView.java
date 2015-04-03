package com.pipit.waffle;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by Kyle on 4/2/2015.
 */
public class CustomScrollView extends ScrollView {

    private int top;
    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    public void fling(int velocityY) {
        super.fling(velocityY);

        ObjectAnimator animator = ObjectAnimator.ofInt(this, "scrollY",  top);
        // TODO: set variable duration depending on position/velocity
        animator.setDuration(1200);
        animator.start();
    }

    public void set(int t)
    {
        top = t;
    }
}
