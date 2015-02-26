package com.pipit.waffle;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.pipit.waffle.Objects.ClientData;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Kyle on 1/4/2015.
 */
public class UserQuestionsFragmentListAdapter extends RecyclerView.Adapter<UserQuestionsFragmentListAdapter.ViewHolder>  {

    // TODO: comment this class (CONFUSING)

    private List<String> items;
    private Context mContext;
    private static int screenWidth;
    // TODO: remove this
    private static int num_loaded;

    public UserQuestionsFragmentListAdapter(Context context, List<String> items) {
        this.mContext = context;
        this.items = items;
        num_loaded = 0;
        setHasStableIds(true);
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
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

        private Animation fade_in_left;
        private Animation fade_in_right;

        private boolean should_fade_left = false;
        private boolean should_fade_right = false;

        private Animation fade_out_spinner_left;
        private Animation fade_out_spinner_right;

        private Bitmap left_bitmap;
        private Bitmap right_bitmap;

        private CountDownLatch latch = new CountDownLatch(2);
       // private OnRemoveListener removeListener;
       // private OnEditListener editListener;

        public class ImageSetterTask extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... params) {

                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {

                if(should_fade_right|| should_fade_left) {
                    left.setVisibility(View.INVISIBLE);
                    right.setVisibility(View.INVISIBLE);
                    left.setImageBitmap(left_bitmap);
                    right.setImageBitmap(right_bitmap);
                    left.startAnimation(fade_in_left);
                    right.startAnimation(fade_in_right);
                }
                else {
                    left.setImageBitmap(left_bitmap);
                    right.setImageBitmap(right_bitmap);
                }
            }

            @Override
            protected void onPreExecute() {
            }

        }

        public ViewHolder(View itemView) {
            super(itemView);
            fade_in_left = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.fade_in);
            fade_in_left.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    left.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            fade_in_right = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.fade_in);
            fade_in_right.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    right.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            fade_out_spinner_left = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.fade_out);
            fade_out_spinner_left.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    spinner_left.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            fade_out_spinner_right = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.fade_out);
            fade_out_spinner_right.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    spinner_right.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

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
            // TODO: fade-out spinner

            String a = "Adam";
            String b = "Andy";
            String  c = "Alex";
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

            ImageSetterTask l_task = new ImageSetterTask();
            l_task.execute();

            // technically, only checking if we have 1 or more sizes for the particular URLs
            List<String> l_keys = MemoryCacheUtils.findCacheKeysForImageUri(url_left, ImageLoader.getInstance().getMemoryCache());
            List<String> r_keys = MemoryCacheUtils.findCacheKeysForImageUri(url_right, ImageLoader.getInstance().getMemoryCache());

            boolean l_is_cached = false;
            boolean r_is_cached = false;

                if (!l_keys.isEmpty()) {
                    l_is_cached = true;
                    Log.d("UserQuestionsFragmentListAdapter", "Left image is mem-cached!");
                }

                if (!r_keys.isEmpty()) {
                    r_is_cached = true;
                    Log.d("UserQuestionsFragmentListAdapter", "Right image is mem-cached!");
                }

            File file_left = ImageLoader.getInstance().getDiskCache().get(url_left);
            if (!file_left.exists())
                Log.d("UserQuestionsFragmentListAdapter", "Left image not disk-cached!");

            File file_right = ImageLoader.getInstance().getDiskCache().get(url_right);
            if (!file_right.exists())
                Log.d("UserQuestionsFragmentListAdapter", "Right image not disk-cached!");


            // only fade-in if the mem-cache does not contain the image
            DisplayImageOptions options_left;
            if(!l_is_cached || !r_is_cached) {
                options_left = new DisplayImageOptions.Builder().cacheInMemory(true)
                        .cacheOnDisk(true)
                        .build();
                should_fade_left = true;

            }
            else
            {
                options_left = new DisplayImageOptions.Builder().cacheInMemory(true)
                        .cacheOnDisk(true)
                        .build();
                should_fade_left = false;
            }

            DisplayImageOptions options_right;
            if(!r_is_cached || !l_is_cached) {
                options_right = new DisplayImageOptions.Builder().cacheInMemory(true)
                        .cacheOnDisk(true)
                        .build();
                should_fade_right = true;
            }
            else
            {
                options_right = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisk(true)
                        .build();
                should_fade_right = false;
            }


            final boolean finalR_is_cached = r_is_cached;
            final boolean finalL_is_cached = l_is_cached;

            Resources r = itemView.getResources();
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160, r.getDisplayMetrics());
            ImageSize target_size = new ImageSize(screenWidth, height);

            ImageLoader.getInstance().loadImage(url_left, target_size, options_left, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    super.onLoadingStarted(imageUri, view);
                    spinner_left.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    Log.d("UserQuestionsFragmentListAdapter", "Loaded image: " + ++num_loaded + " images loaded.");
                    left_bitmap = loadedImage;
                    latch.countDown();
                    // hide the spinner
                    if(finalR_is_cached || finalL_is_cached)
                        spinner_left.startAnimation(fade_out_spinner_left);
                    else
                        spinner_left.setVisibility(View.INVISIBLE);
                }
            });

            ImageLoader.getInstance().loadImage(url_right, target_size, options_right, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    super.onLoadingStarted(imageUri, view);
                    spinner_right.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    Log.d("UserQuestionsFragmentListAdapter", "Loaded image: " + ++num_loaded + " images loaded.");
                    right_bitmap = loadedImage;
                    latch.countDown();
                    // hide the spinner
                    if(finalR_is_cached || finalL_is_cached)
                        spinner_right.startAnimation(fade_out_spinner_right);
                    else
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