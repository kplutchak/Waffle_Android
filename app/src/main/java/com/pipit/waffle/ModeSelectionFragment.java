package com.pipit.waffle;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Outline;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;

/**
 * Created by Kyle on 11/19/2014.
 */
public class ModeSelectionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.mode_selection_fragment, container, false);

        // Mode selection buttons
        Button answering_button = (Button) v.findViewById(R.id.answering_button);

        ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Or read size directly from the view's width/height
                int size = getResources().getDimensionPixelSize(R.dimen.button_answering_size);
                outline.setOval(0, 0, size, size);
            }
        };
        answering_button.setOutlineProvider(viewOutlineProvider);
        answering_button.setClipToOutline(true);

        // Click behavior: switch to Answering
        answering_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ToolbarActivity) getActivity()).switchFragments(0);

            }
        });

        Button question_button = (Button) v.findViewById(R.id.question_button);

        ViewOutlineProvider viewOutlineProvider2 = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Or read size directly from the view's width/height
                int size = getResources().getDimensionPixelSize(R.dimen.button_answering_size);
                outline.setOval(0, 0, size, size);
            }
        };
        question_button.setOutlineProvider(viewOutlineProvider);
        question_button.setClipToOutline(true);

        // Click behavior: switch to Question Creation
        question_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ToolbarActivity) getActivity()).switchFragments(1);

            }
        });

        Button me_button = (Button) v.findViewById(R.id.me_button);

        ViewOutlineProvider viewOutlineProvider3 = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Or read size directly from the view's width/height
                int size = getResources().getDimensionPixelSize(R.dimen.button_answering_size);
                outline.setOval(0, 0, size, size);
            }
        };
        me_button.setOutlineProvider(viewOutlineProvider);
        me_button.setClipToOutline(true);


        return v;
    }

}
