package com.pipit.waffle;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kyle on 1/4/2015.
 */
public class UserQuestionsFragment extends Fragment {

    private RecyclerView mRecyclerView;

    private StickyHeadersItemDecoration top;
    private StickyHeadersItemDecoration overlay;
    private RecyclerView.LayoutManager mLayoutManager;

    private UserQuestionsFragmentListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.user_questions_fragment, container, false);

        // TODO: remove
       /* ImageLoader.getInstance().clearDiskCache();
        ImageLoader.getInstance().clearMemoryCache();
        */

        mRecyclerView = (RecyclerView) v.findViewById(R.id.list);

        String[] data = {"Adam", "Becky", "Carol", "David", "Edward"};
        mRecyclerView.setHasFixedSize(true);

                // use a linear layout manager
                mLayoutManager = new LinearLayoutManager(v.getContext());
                mRecyclerView.setLayoutManager(mLayoutManager);

        List<String> items = new ArrayList<String>();
        for(String s : data)
        {
            items.add(s);
        }

        mAdapter = new UserQuestionsFragmentListAdapter(v.getContext(), items);
        mRecyclerView.setAdapter(mAdapter);

        top = new StickyHeadersBuilder()
                .setAdapter(mAdapter)
                .setRecyclerView(mRecyclerView)
                .setStickyHeadersAdapter(new HeaderAdapter(items))
                .build();

        mRecyclerView.addItemDecoration(top);

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
        }
        else {
            animator = ObjectAnimator.ofFloat(this, "translationX", 0, displayWidth);
        }

        // TODO: play with interpolator
        animator.setInterpolator(new AccelerateInterpolator(0.8f));
        animator.setDuration(300);
        return animator;
    }


}
