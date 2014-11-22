package com.pipit.waffle;

import android.graphics.Outline;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by Kyle on 11/19/2014.
 */
public class ModeSelectionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Outline

        View v = inflater.inflate(R.layout.mode_selection_fragment, container, false);

        CardView cv = (CardView) v.findViewById(R.id.card_view);

        // CardView movement
        View.OnTouchListener tl = new View.OnTouchListener()
        {
            public float offsetX;
            public float offsetY;

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int theAction = event.getAction();
                switch (theAction)
                {
                    case MotionEvent.ACTION_DOWN:
                        // Button down
                        offsetX = v.getX() - event.getRawX();
                        offsetY = v.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // Button moved
                        float newX = event.getRawX() + offsetX;
                        float newY = event.getRawY() + offsetY;
                        v.setX(newX);
                        //v.setY(newY);
                        break;
                    case MotionEvent.ACTION_UP:
                        // Button up
                        break;
                    default:
                        break;
                }
                return true;
            }
        };
        cv.setOnTouchListener(tl);

        return v;
    }

}
