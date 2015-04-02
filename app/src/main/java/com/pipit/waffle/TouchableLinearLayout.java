package com.pipit.waffle;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by Kyle on 4/1/2015.
 */
public class TouchableLinearLayout extends LinearLayout {
    public TouchableLinearLayout(Context context) {
        super(context);
    }

    public TouchableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        // do what you need to with the event, and then...

        return super.dispatchTouchEvent(e);
    }
}
