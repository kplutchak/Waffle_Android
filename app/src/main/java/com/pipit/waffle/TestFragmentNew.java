package com.pipit.waffle;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Kyle on 4/2/2015.
 */
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

public class TestFragmentNew extends ActionBarActivity {
    private static final String TAG = "DemoActivity";


    private ListView listview;
    private boolean mAlreadyAtTop;
    private SlidingUpPanelLayout mLayout;

    private LinearLayout top_comments_bar;
    private LinearLayout entire_drag_view;

    private boolean offsetIsAlreadyOne = true;

    private boolean isExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        setSupportActionBar((Toolbar)findViewById(R.id.main_toolbar));


        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        top_comments_bar = (LinearLayout) findViewById(R.id.top_comments_bar);
        entire_drag_view = (LinearLayout) findViewById(R.id.dragView);



       // mLayout.setDragView(findViewById(R.id.comments_listview_scroller));
        //mLayout.setEnableDragViewTouchEvents(true);
        mLayout.setPanelState(PanelState.HIDDEN);
        mLayout.setPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
                if(isExpanded)
                {

                }

            }


            @Override
            public void onPanelExpanded(View panel) {
                Log.i(TAG, "onPanelExpanded");
                isExpanded = true;
               mLayout.setDragView(top_comments_bar);


            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed");
                if(isExpanded)
                {

                }

            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.i(TAG, "onPanelHidden");
            }
        });



        TextView t = (TextView) findViewById(R.id.name);
        t.setText("Mila Kunis asked...");

        listview = (ListView) findViewById(R.id.comments_listview);
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

    public void disableListViewClicking() {
        listview.setClickable(false);
        listview.setFocusable(false);
        listview.setEnabled(false);
    }

    public void enableListViewClicking() {
        listview.setClickable(true);
        listview.setFocusable(true);
        listview.setEnabled(true);
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