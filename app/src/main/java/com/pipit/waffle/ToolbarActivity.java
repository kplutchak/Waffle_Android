package com.pipit.waffle;

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
import android.widget.Button;


public class ToolbarActivity extends ActionBarActivity {
    private ActionBarDrawerToggle toggle;
    public int current_fragment_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);

        getWindow().setExitTransition(new Fade());
        getWindow().setEnterTransition(new Fade());
        setContentView(R.layout.activity_toolbar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        toggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(toggle);

        //Outline
        Button fab = (Button) findViewById(R.id.fab);

        ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Or read size directly from the view's width/height
                int size = getResources().getDimensionPixelSize(R.dimen.fab_size);
                outline.setOval(0, 0, size, size);
            }
        };
        fab.setOutlineProvider(viewOutlineProvider);

        fab.setClipToOutline(true);

        this.current_fragment_id = 0;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragments();
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
            AnsweringPictureFragment firstFragment = new AnsweringPictureFragment();

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

    public void switchFragments() {
        if (current_fragment_id == 1) {
            getFragmentManager().popBackStack();
            this.current_fragment_id = 0;
            return;
        }

        if (current_fragment_id == 2) {
            getFragmentManager().popBackStack(getFragmentManager().getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            this.current_fragment_id = 0;
            return;
        }

        if(current_fragment_id == 0)
        {
            // Create a new Fragment to be placed in the activity layout
            QuestionCreationModeFragment frag = new QuestionCreationModeFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            frag.setArguments(getIntent().getExtras());




            current_fragment_id = 1;
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

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }



}
