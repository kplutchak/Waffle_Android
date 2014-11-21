package com.pipit.waffle;

import android.graphics.Outline;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by Kyle on 11/19/2014.
 */
public class ModeSelectionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //Outline

        View v = inflater.inflate(R.layout.mode_selection_fragment, container, false);

        ImageView profile = (ImageView) v.findViewById(R.id.profile);

        ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Or read size directly from the view's width/height
                int size = getResources().getDimensionPixelSize(R.dimen.profile_size);
                outline.setOval(0, 0, size, size);
            }
        };
        profile.setOutlineProvider(viewOutlineProvider);

        profile.setClipToOutline(true);


        return v;
    }

}
