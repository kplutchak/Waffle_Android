package com.pipit.waffle;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Kyle on 1/4/2015.
 */
public class UserQuestionsFragmentListAdapter extends BaseAdapter implements
        StickyListHeadersAdapter, SectionIndexer {
    private List<String> mDataset;
    private Context mContext;
    private LayoutInflater mInflater;
    private int[] mSectionIndices;
    private Character[] mSectionLetters;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public View mLayoutView;
        public TextView image1;
        public TextView image2;
        public ViewHolderClicksListener mListener;
        public ViewHolder(View v, ViewHolderClicksListener listener) {
            super(v);
            mListener = listener;
            mLayoutView = v;
            image1 = (TextView) v.findViewById(R.id.image_prev1);
            image1.setOnClickListener(this);
            // necessary?
            mLayoutView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (v instanceof TextView){
                mListener.onTomato((TextView) v);
            }
            else {
                mListener.onPotato(v);
            }
        }

        public static interface ViewHolderClicksListener {
            public void onPotato(View caller);
            public void onTomato(TextView callerImage);
        }
    }

    public UserQuestionsFragmentListAdapter(Context context, List<String> data){
        mContext = context;
        mDataset = data;
        mInflater = LayoutInflater.from(context);
    }


    // Create new views (invoked by the layout manager)
    @Override
    public View getView(int position, View convertView, ViewGroup parent)  {

        ViewHolder holder;

        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.user_questions_list_item, parent, false);

            UserQuestionsFragmentListAdapter.ViewHolder.ViewHolderClicksListener l = new UserQuestionsFragmentListAdapter.ViewHolder.ViewHolderClicksListener() {
                public void onPotato(View caller) {
                    Toast.makeText(caller.getContext(), "Layout Clicked!", Toast.LENGTH_SHORT).show();
                }

                public void onTomato(TextView callerImage) {
                    Toast.makeText(callerImage.getContext(), "TextView Clicked!", Toast.LENGTH_SHORT).show();
                }
            };

            holder = new ViewHolder(convertView, l);
            holder.image1 = (TextView) convertView.findViewById(R.id.image_prev1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.image1.setText(mDataset.get(position));

        return convertView;
    }


    // Return the size of the dataset
    @Override
    public int getCount() {
        return mDataset.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataset.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mDataset.get(position).hashCode();
    }

    class HeaderViewHolder {
        TextView text;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;

        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.letter_header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.letter);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        // set header text as first char in name
        CharSequence headerChar = mDataset.get(position).subSequence(0, 1);
        holder.text.setText(headerChar);

        return convertView;
    }

    /**
     * Remember that these have to be static, postion=1 should always return
     * the same Id that is.
     */
    @Override
    public long getHeaderId(int position) {
        // return the first character of the country as ID because this is what
        // headers are based upon
        return mDataset.get(position).subSequence(0, 1).charAt(0);
    }

    @Override
    public int getPositionForSection(int section) {
        if (mSectionIndices.length == 0) {
            return 0;
        }

        if (section >= mSectionIndices.length) {
            section = mSectionIndices.length - 1;
        } else if (section < 0) {
            section = 0;
        }
        return mSectionIndices[section];
    }

    @Override
    public int getSectionForPosition(int position) {
        for (int i = 0; i < mSectionIndices.length; i++) {
            if (position < mSectionIndices[i]) {
                return i - 1;
            }
        }
        return mSectionIndices.length - 1;
    }

    @Override
    public Object[] getSections() {
        return mSectionLetters;
    }
}
