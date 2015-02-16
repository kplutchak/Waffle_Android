package com.pipit.waffle;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eowise.recyclerview.stickyheaders.StickyHeadersAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Kyle on 2/16/2015.
 */
public class HeaderAdapter implements StickyHeadersAdapter<HeaderAdapter.ViewHolder> {

    private List<String> items;

    // TODO: remove this
    private List<String> random_strings;

    public HeaderAdapter(List<String> items) {
        this.items = items;

        // TODO: remove this
        String[] data = {"Today", "Tomorrow", "Yesterday"};

        random_strings = new ArrayList<>();
        for(String s : data)
        {
            random_strings.add(s);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.letter_header, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder headerViewHolder, int position) {
        // TODO: fix this
       // headerViewHolder.letter.setText(items.get(position).subSequence(0, 1));
        headerViewHolder.letter.setText(random_strings.get(randInt(0, 2)));
    }

    @Override
    public long getHeaderId(int position) {
        return items.get(position).charAt(0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView letter;

        public ViewHolder(View itemView) {
            super(itemView);
            letter = (TextView) itemView.findViewById(R.id.letter);
        }
    }

    // TODO: remove this
    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

}