package com.pipit.waffle;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by Kyle on 4/1/2015.
 */
public class TouchableFrameLayout extends FrameLayout {

    private ScrollView scroll_view;
    private TouchableLinearLayout main_view;

    public TouchableFrameLayout(Context context) {
        super(context);
    }

    public TouchableFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        // do what you need to with the event, and then...
        switch (e.getAction())
        {

            case MotionEvent.ACTION_DOWN:
                float x = e.getRawX();
                float y = e.getRawY();
                Log.d("AnsweringFragment", "X: " + x + ", Y: " + y);
                if(y<800)
                    return main_view.dispatchTouchEvent(e);
                break;
            case MotionEvent.ACTION_MOVE:
                x = e.getRawX();
                y = e.getRawY();
                if(y<800)
                    return main_view.dispatchTouchEvent(e);
                break;
            case MotionEvent.ACTION_UP:
                x = e.getRawX();
                y = e.getRawY();
                if(y<800)
                    return main_view.dispatchTouchEvent(e);
                break;

        }
        return super.dispatchTouchEvent(e);
    }

    public void setViews(ScrollView sv, TouchableLinearLayout mv) {
        scroll_view = sv;
        main_view = mv;
    }
}
