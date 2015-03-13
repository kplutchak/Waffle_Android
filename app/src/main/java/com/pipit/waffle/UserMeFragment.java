package com.pipit.waffle;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Kyle on 1/4/2015.
 */
public class UserMeFragment extends Fragment {

    private ImageView user_image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ToolbarActivity.current_fragment_id = Constants.USER_ME_FRAGMENT_ID;
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.user_me_fragment, container, false);

        user_image = (ImageView) v.findViewById(R.id.user_settings_picture);

        ImageLoader.getInstance().loadImage("http://www.brandingmagazine.com/wp-content/uploads/2014/02/mila-kunis-jim-bean-cover.jpg", new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                user_image.setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

        // set button click behavior (settings + logout)
        TextView settings_tv = (TextView) v.findViewById(R.id.settings_button);
        settings_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Timer delay_frag_trans = new Timer();
                delay_frag_trans.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                UserSettingsFragment frag = new UserSettingsFragment();

                                // In case this activity was started with special instructions from an
                                // Intent, pass the Intent's extras to the fragment as arguments
                                frag.setArguments(getActivity().getIntent().getExtras());

                                // Flip to the back.

                                // Create and commit a new fragment transaction that adds the fragment for the back of
                                // the card, uses custom animations, and is part of the fragment manager's back stack.

                                getFragmentManager()
                                        .beginTransaction()

                                                // Replace the default fragment animations with animator resources representing
                                                // rotations when switching to the back of the card, as well as animator
                                                // resources representing rotations when flipping back to the front (e.g. when
                                                // the system Back button is pressed).

                                                // Replace any fragments currently in the container view with a fragment
                                                // representing the next page (indicated by the just-incremented currentPage
                                                // variable).
                                        .replace(R.id.fragment_container, frag)

                                                // Add this transaction to the back stack, allowing users to press Back
                                                // to get to the front of the card.
                                        .addToBackStack(Constants.USER_SETTINGS_FRAGMENT_NAME)

                                                // Commit the transaction.
                                        .commit();

                                Animation fade_in = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_toolbar_text);
                                fade_in.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        ((ToolbarActivity) getActivity()).writer_toolbar.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });

                                // Close the drawer after the item has been clicked
                                ((ToolbarActivity) getActivity()).drawerLayout.closeDrawer(Gravity.LEFT);

                                ((ToolbarActivity) getActivity()).rl.removeView(((ToolbarActivity) getActivity()).writer_toolbar);
                                ((ToolbarActivity) getActivity()).rl.addView(((ToolbarActivity) getActivity()).writer_toolbar);
                                ((ToolbarActivity) getActivity()).writer_toolbar.setCharacterDelay(2);

                                ((ToolbarActivity) getActivity()).writer_toolbar.animateText("Settings");
                                ((ToolbarActivity) getActivity()).writer_toolbar.startAnimation(fade_in);
                                ToolbarActivity.current_fragment_id = Constants.USER_SETTINGS_FRAGMENT_ID;
                            }
                        });
                    }
                }, 300);

                // Close the drawer after the item has been clicked
                ((ToolbarActivity) getActivity()).drawerLayout.closeDrawer(Gravity.LEFT);

            }

        });

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