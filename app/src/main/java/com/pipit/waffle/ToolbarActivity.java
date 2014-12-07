package com.pipit.waffle;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.FragmentManager;
import android.graphics.Outline;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;

public class ToolbarActivity extends ActionBarActivity {

    private ActionBarDrawerToggle toggle;
    public int current_fragment_id;

    /**
     * onCreate for ToolbarActivity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);

        // Set enter/transitions for the activity
        getWindow().setExitTransition(new Fade());
        getWindow().setEnterTransition(new Fade());
        setContentView(R.layout.activity_toolbar);

        // Set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the drawer
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        toggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(toggle);

        // Create the buttons
        Button picture = (Button) findViewById(R.id.fab);

        ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Or read size directly from the view's width/height
                int size = getResources().getDimensionPixelSize(R.dimen.fab_size);
                outline.setOval(0, 0, size, size);
            }
        };
        picture.setOutlineProvider(viewOutlineProvider);

        picture.setClipToOutline(true);

        // Set the swap icon
        final ImageView swap_icon = (ImageView) findViewById(R.id.swap_icon);

        swap_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragments(-1);

                // Flip the swap_icon 360 degrees along the y-axis
                Animator anim = AnimatorInflater
                        .loadAnimator(v.getContext(), R.animator.flip_on_vertical);
                anim.setTarget(swap_icon);
                anim.start();

            }
        });



        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            ModeSelectionFragment firstFragment = new ModeSelectionFragment();

            // Set the current fragment ID
            this.current_fragment_id = Constants.MODE_SELECTION_FRAGMENT_ID;

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    /**
     * switchFragments is called when we hit the "toggle mode" button in the upper left and when
     * we select a mode in ModeSelectionFragment
     *
     * @param key - Used to determine what fragment to switch to if we are in ModeSelectionFragment
     *              A key value of 0 will send us to AnsweringFragment
     *              A key value of 1 will send us to QuestionCreationModeFragment
     */
    public void switchFragments(int key) {

        //  If currently answering questions or in the initial question creation screen, a call to
        // switchFragments will cause answering -> question creation and question creation -> answering
        if (current_fragment_id == Constants.ANSWERING_FRAGMENT_ID) {
            QuestionCreationModeFragment frag = new QuestionCreationModeFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            frag.setArguments(getIntent().getExtras());

            // Flip to the back.

            // Create and commit a new fragment transaction that adds the fragment for the back of
            // the card, uses custom animations, and is part of the fragment manager's back stack.

            getFragmentManager()
                    .beginTransaction()

                            // Replace the default fragment animations with animator resources representing
                            // rotations when switching to the back of the card, as well as animator
                            // resources representing rotations when flipping back to the front (e.g. when
                            // the system Back button is pressed).
                    .setCustomAnimations(
                            R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                            R.animator.card_flip_left_in, R.animator.card_flip_left_out)

                            // Replace any fragments currently in the container view with a fragment
                            // representing the next page (indicated by the just-incremented currentPage
                            // variable).
                    .replace(R.id.fragment_container, frag)

                            // Add this transaction to the back stack, allowing users to press Back
                            // to get to the front of the card.
                    .addToBackStack(null)

                            // Commit the transaction.
                    .commit();
            this.current_fragment_id = Constants.QUESTION_CREATION_MODE_FRAGMENT_ID;
            return;
        }

        if (current_fragment_id == Constants.QUESTION_CREATION_MODE_FRAGMENT_ID) {
            AnsweringFragment frag = new AnsweringFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            frag.setArguments(getIntent().getExtras());

            // Flip to the back.

            // Create and commit a new fragment transaction that adds the fragment for the back of
            // the card, uses custom animations, and is part of the fragment manager's back stack.

            getFragmentManager()
                    .beginTransaction()

                            // Replace the default fragment animations with animator resources representing
                            // rotations when switching to the back of the card, as well as animator
                            // resources representing rotations when flipping back to the front (e.g. when
                            // the system Back button is pressed).
                    .setCustomAnimations(
                            R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                            R.animator.card_flip_left_in, R.animator.card_flip_left_out)

                            // Replace any fragments currently in the container view with a fragment
                            // representing the next page (indicated by the just-incremented currentPage
                            // variable).
                    .replace(R.id.fragment_container, frag)

                            // Add this transaction to the back stack, allowing users to press Back
                            // to get to the front of the card.
                    .addToBackStack(null)

                            // Commit the transaction.
                    .commit();
            this.current_fragment_id = Constants.ANSWERING_FRAGMENT_ID;
            return;
        }


        // If the current fragment is ModeSelectionFragment, then use @key to determine what
        // fragment to enter
        if(current_fragment_id == 0)
        {
            // Create a new Fragment to be placed in the activity layout

            if(key == 1) {
                QuestionCreationModeFragment frag = new QuestionCreationModeFragment();

                // In case this activity was started with special instructions from an
                // Intent, pass the Intent's extras to the fragment as arguments
                frag.setArguments(getIntent().getExtras());

                current_fragment_id = Constants.QUESTION_CREATION_MODE_FRAGMENT_ID;
                // Flip to the back.

                // Create and commit a new fragment transaction that adds the fragment for the back of
                // the card, uses custom animations, and is part of the fragment manager's back stack.

                getFragmentManager()
                        .beginTransaction()

                                // Replace the default fragment animations with animator resources representing
                                // rotations when switching to the back of the card, as well as animator
                                // resources representing rotations when flipping back to the front (e.g. when
                                // the system Back button is pressed).
                        .setCustomAnimations(
                                R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                                R.animator.card_flip_left_in, R.animator.card_flip_left_out)

                                // Replace any fragments currently in the container view with a fragment
                                // representing the next page (indicated by the just-incremented currentPage
                                // variable).
                        .replace(R.id.fragment_container, frag)

                                // Add this transaction to the back stack, allowing users to press Back
                                // to get to the front of the card.
                        .addToBackStack(null)

                                // Commit the transaction.
                        .commit();
            }
            else if(key == 0) {
                AnsweringFragment frag = new AnsweringFragment();

                // In case this activity was started with special instructions from an
                // Intent, pass the Intent's extras to the fragment as arguments
                frag.setArguments(getIntent().getExtras());

                current_fragment_id = Constants.ANSWERING_FRAGMENT_ID;
                // Flip to the back.

                // Create and commit a new fragment transaction that adds the fragment for the back of
                // the card, uses custom animations, and is part of the fragment manager's back stack.

                getFragmentManager()
                        .beginTransaction()

                                // Replace the default fragment animations with animator resources representing
                                // rotations when switching to the back of the card, as well as animator
                                // resources representing rotations when flipping back to the front (e.g. when
                                // the system Back button is pressed).
                        .setCustomAnimations(
                                R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                                R.animator.card_flip_left_in, R.animator.card_flip_left_out)

                                // Replace any fragments currently in the container view with a fragment
                                // representing the next page (indicated by the just-incremented currentPage
                                // variable).
                        .replace(R.id.fragment_container, frag)

                                // Add this transaction to the back stack, allowing users to press Back
                                // to get to the front of the card.
                        .addToBackStack(null)

                                // Commit the transaction.
                        .commit();
            }

        }




    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }



}
