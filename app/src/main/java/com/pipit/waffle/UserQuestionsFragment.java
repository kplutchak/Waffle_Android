package com.pipit.waffle;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.eowise.recyclerview.stickyheaders.DrawOrder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

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



}
