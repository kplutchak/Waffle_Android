package com.pipit.waffle;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.pipit.waffle.Objects.ClientData;

import java.io.File;
import java.util.List;
import java.util.Map;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Kyle on 1/4/2015.
 */
public class UserQuestionsFragmentListAdapter extends RecyclerView.Adapter<UserQuestionsFragmentListAdapter.ViewHolder>  {

    private List<String> items;
    private Context mContext;


    public UserQuestionsFragmentListAdapter(Context context, List<String> items) {
        this.mContext = context;
        this.items = items;

        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_questions_list_item, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).hashCode();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.label = items.get(position);
        viewHolder.setImages();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
/*
    @Override
    public void onRemove(int position) {
        //personDataProvider.remove(position);
        //notifyItemRemoved(position);
    }

    @Override
    public void onEdit(final int position) {
        final EditText edit = new EditText(mContext);
        edit.setTextColor(Color.BLACK);
        edit.setText(personDataProvider.getItems().get(position));
        new AlertDialog.Builder(mContext).setTitle(R.string.edit).setView(edit).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = edit.getText().toString();
                personDataProvider.update(position, name);
                notifyItemChanged(position);
            }
        }).create().show();
    }
    */

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        String label;
        private ImageView left;
        private ImageView right;
        private ProgressBar spinner_left;
        private ProgressBar spinner_right;
       // private OnRemoveListener removeListener;
       // private OnEditListener editListener;

        public ViewHolder(View itemView) {
            super(itemView);
           //this.label = (TextView) itemView.findViewById(R.id.name);
            this.left = (ImageView) itemView.findViewById(R.id.left_image);
            this.right = (ImageView) itemView.findViewById(R.id.right_image);

            this.spinner_left = (ProgressBar) itemView.findViewById(R.id.progress_bar_left);
            this.spinner_right = (ProgressBar) itemView.findViewById(R.id.progress_bar_right);
            //this.removeListener = personAdapter;
           // this.editListener = personAdapter;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setImages() {
            // TODO: re-show spinner?

            String a = "Adam";
            String b = "Becky";
            String  c = "Carol";
            String d = "David";
            String e = "Edward";

            String url_left = "";
            String url_right = "";

            if(label.equals(a)) {
                url_left = "http://i.imgur.com/0F374vh.jpg";
                url_right = "http://i.imgur.com/PbCFo3P.jpg";
            }
            else if(label.equals(b))
            {
                url_left = "http://i.imgur.com/4mc8A4Y.jpg";
                url_right = "http://i.imgur.com/KEx9hVt.jpg";
            }
            else if(label.equals(c))
            {
                url_left = "http://i.imgur.com/b5f3bJW.jpg";
                url_right = "http://i.imgur.com/dxEfymt.jpg";
            }
            else if(label.equals(d))
            {
                url_left= "http://i.imgur.com/5dPiHsT.jpg";
                url_right= "http://i.imgur.com/vXA60XJ.jpg";
            }
            else if(label.equals(e))
            {
                url_left = "http://i.imgur.com/KaJEpI0.jpg";
                url_right = "http://i.imgur.com/p9vW3mn.jpg";
            }


            // technically, only checking if we have 1 or more sizes for the particular URLs (not necessarily the size we actually
            // need), so might want to change this later to a ClientData HashMap or something (or switch to just portrait)
            List<Bitmap> l_bitmap = MemoryCacheUtils.findCachedBitmapsForImageUri(url_left, ImageLoader.getInstance().getMemoryCache());
            if(l_bitmap.isEmpty())
                Log.d("UserQuestionsFragmentListAdapter", "Left image not mem-cached!");

            List<Bitmap> r_bitmap = MemoryCacheUtils.findCachedBitmapsForImageUri(url_right, ImageLoader.getInstance().getMemoryCache());
            if(r_bitmap.isEmpty())
                Log.d("UserQuestionsFragmentListAdapter", "Right image not mem-cached!");

            File file_left = ImageLoader.getInstance().getDiskCache().get(url_left);
            if (!file_left.exists())
                Log.d("UserQuestionsFragmentListAdapter", "Left image not disk-cached!");

            File file_right = ImageLoader.getInstance().getDiskCache().get(url_right);
            if (!file_right.exists())
                Log.d("UserQuestionsFragmentListAdapter", "Right image not disk-cached!");


            DisplayImageOptions options_left;
            // only fade-in if the mem-cache does not contain the image
            if(l_bitmap.isEmpty()) {
                options_left = new DisplayImageOptions.Builder().cacheInMemory(true)
                        .cacheOnDisk(true).displayer(new FadeInBitmapDisplayer(1000))
                        .build();
            }
            else
            {
                options_left = new DisplayImageOptions.Builder().cacheInMemory(true)
                        .cacheOnDisk(true)
                        .build();
            }

            DisplayImageOptions options_right;
            if(r_bitmap.isEmpty()) {
                options_right = new DisplayImageOptions.Builder().cacheInMemory(true)
                        .cacheOnDisk(true).displayer(new FadeInBitmapDisplayer(1000))
                        .build();
            }
            else
            {
                options_right = new DisplayImageOptions.Builder().cacheInMemory(true)
                        .cacheOnDisk(true)
                        .build();
            }

            ImageLoader.getInstance().displayImage(url_left, left, options_left, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    // hide the spinner
                    spinner_left.setVisibility(View.INVISIBLE);
                }
            });
            ImageLoader.getInstance().displayImage(url_right, right, options_right, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    // hide the spinner
                    spinner_right.setVisibility(View.INVISIBLE);
                }
            });
        }

        @Override
        public void onClick(View v) {
            //removeListener.onRemove(getPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            //editListener.onEdit(getPosition());
            return true;
        }
    }

}