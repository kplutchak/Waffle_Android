package com.pipit.waffle;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kyle on 4/2/2015.
 */
public class TestFragment extends Activity {

    private static final String TAG = "TestFragment";

    private boolean mAlreadyAtTop = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.test_fragment);
        super.onCreate(savedInstanceState);

        final ListView listview = (ListView) findViewById(R.id.comments_listview);
        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
                "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
                "Android", "iPhone", "WindowsMobile" };

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }

        listview.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if(!canScrollUp(view) && !mAlreadyAtTop) {
                    Log.d(TAG, "We've hit the top again.");
                    mAlreadyAtTop = true;
                }
                else
                    mAlreadyAtTop = false;




            }

            int mPosition=0;
            int mOffset=0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
               // if(!canScrollUp(view))
                   // Log.d(TAG, "We're at the bottom of the comments ListView first.");
            }
        });


        CommentsArrayAdapter adapter = new CommentsArrayAdapter(this, values);
        listview.setAdapter(adapter);
    }

    public boolean atTopOfComments() {
        return mAlreadyAtTop;
    }

    public boolean canScrollUp(View view) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (view instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) view;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView
                        .getChildAt(0).getTop() < absListView.getPaddingTop());
            } else {
                return view.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(view, -1);
        }
    }

    public class CommentsArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] values;

        public CommentsArrayAdapter(Context context, String[] values) {
            super(context, R.layout.comments_list_item, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.comments_list_item, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.comments_text);
            textView.setText(values[position]);

            return rowView;
        }
    }

}
