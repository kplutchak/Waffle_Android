package com.pipit.waffle;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Kyle on 1/4/2015.
 */
public class UserQuestionsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.user_questions_fragment, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.questions_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(v.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        String[] data = {"Adam", "Becky", "Carol", "David", "Edward", "Francis", "George", "Harry", "Issac"};

        mAdapter = new UserQuestionsFragmentListAdapter(data);
        mRecyclerView.setAdapter(mAdapter);

        return v;
    }

}
