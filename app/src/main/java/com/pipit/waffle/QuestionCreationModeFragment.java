package com.pipit.waffle;

import android.content.Intent;
import android.graphics.Outline;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;

/**
 * Created by Kyle on 11/19/2014.
 */
public class QuestionCreationModeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.question_creation_mode_fragment, container, false);

        //Outline(s)
        Button fab = (Button) v.findViewById(R.id.answer_text);

        ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Or read size directly from the view's width/height
                int size = getResources().getDimensionPixelSize(R.dimen.button_answering_size);
                outline.setOval(0, 0, size, size);
            }
        };
        fab.setOutlineProvider(viewOutlineProvider);

        fab.setClipToOutline(true);

        // Press behavior

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.stay);

            }
        });

        Button fab2 = (Button) v.findViewById(R.id.answer_camera);

        ViewOutlineProvider viewOutlineProvider2 = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Or read size directly from the view's width/height
                int size = getResources().getDimensionPixelSize(R.dimen.button_answering_size);
                outline.setOval(0, 0, size, size);
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

        return v;
    }

}
