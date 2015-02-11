package com.pipit.waffle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Outline;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.Gravity;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pipit.waffle.Objects.Choice;
import com.pipit.waffle.Objects.ClientData;
import com.pipit.waffle.Objects.Question;
import com.pipit.waffle.Objects.Self;

import java.util.ArrayList;
import java.util.List;

public class ToolbarActivity extends ActionBarActivity {

    private ActionBarDrawerToggle toggle;
    public int current_fragment_id;


    private ListView drawerListView;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("current_frag", current_fragment_id);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * onCreate for ToolbarActivity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        final Context mcontext = this;
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            current_fragment_id = savedInstanceState.getInt("current_frag");
        }

        // Set enter/transitions for the activity
        getWindow().setExitTransition(new Fade());
        getWindow().setEnterTransition(new Fade());
        setContentView(R.layout.activity_toolbar);


        // Set the toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the drawer's ListView
        drawerListView = (ListView) findViewById(R.id.left_drawer_list);

        ClientData.getNextUnansweredQuestion(this);

        List<DrawerItem> items = new ArrayList<DrawerItem>();

        DrawerItem me_item = new DrawerItem("Me", 0);
        DrawerItem ask_item = new DrawerItem("Ask", 1);
        DrawerItem answer_item = new DrawerItem("Answer", 2);
        DrawerItem settings_item = new DrawerItem("Settings", 3);
        items.add(0, me_item);
        items.add(1, ask_item);
        items.add(2, answer_item);
        items.add(3, settings_item);

        drawerListView.setAdapter(new DrawerListAdapter(this,
                R.layout.drawer_list_item, items));

        // Set the drawer
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        toggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(toggle);

        // Set drawer item click behavior
        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0) {
                    int currentapiVersion = android.os.Build.VERSION.SDK_INT;

                    // RecyclerView is only available in Android L and up
                    if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {

                        UserQuestionsFragment frag = new UserQuestionsFragment();

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
                            /*
                            .setCustomAnimations(R.animator., R.animator.card_flip_right_out,
                                    R.animator.card_flip_left_in, R.animator.card_flip_left_out)
                            */


                                        // Replace any fragments currently in the container view with a fragment
                                        // representing the next page (indicated by the just-incremented currentPage
                                        // variable).
                                .replace(R.id.fragment_container, frag)

                                        // Add this transaction to the back stack, allowing users to press Back
                                        // to get to the front of the card.
                                .addToBackStack(null)

                                        // Commit the transaction.
                                .commit();
                        current_fragment_id = Constants.USER_QUESTIONS_FRAGMENT_ID;
                        // Close the drawer after the item has been clicked and we open the correct fragment
                        drawerLayout.closeDrawer(Gravity.LEFT);
                    }
                }
                else if (position==1){
                    /*Post test question*/
                    Question testQuestion = new Question("Generic test question from Android", Self.getUser());
                    Choice testAnswerOne = new Choice();
                    Choice testAnswerTwo = new Choice();
                    testQuestion.addChoice(testAnswerOne);
                    testQuestion.addChoice(testAnswerOne);
                    Network.postQuestion(getApplicationContext(), testQuestion);

                    Context context = getApplicationContext();
                    CharSequence text = "Attempted Network.postQuestion";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(getBaseContext(), text, duration);
                    toast.show();
                }
                else if (position==2){
                    /*Attempt to retrieve next question*/
                    Question testQuestion = new Question("No Question Retrieved", Self.getUser());
                    testQuestion = ClientData.getNextUnansweredQuestion(mcontext);
                    String text = testQuestion.getQuestionBody() + " " + testQuestion.getChoices().get(0).getUrl();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(mcontext, text, duration);
                    toast.show();
                }
            }
        });

        // Create the buttons
        final Button swap_icon = (Button) findViewById(R.id.fab);

        ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Or read size directly from the view's width/height
                int size = getResources().getDimensionPixelSize(R.dimen.fab_size);
                outline.setOval(0, 0, size, size);
            }
        };
        swap_icon.setOutlineProvider(viewOutlineProvider);

        swap_icon.setClipToOutline(true);

        // Set the swap icon

        swap_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (current_fragment_id == Constants.ANSWERING_FRAGMENT_ID || current_fragment_id == Constants.USER_QUESTIONS_FRAGMENT_ID) {

                    Intent intent = new Intent(v.getContext(), CameraActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.stay);
                    /*

                }
                if(current_fragment_id == Constants.QUESTION_CREATION_MODE_FRAGMENT_ID)
                {
                    AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(v.getContext(),
                            R.animator.card_flip_left_in);
                    set.setTarget(swap_icon);

                    AnimatorSet set2 = (AnimatorSet) AnimatorInflater.loadAnimator(v.getContext(),
                            R.animator.card_flip_left_out);

                    set2.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {


                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

                    set2.setTarget(swap_icon_blue);
                    TransitionDrawable transition = (TransitionDrawable) toolbar.getBackground();

                    // ensure the transition occurred completely
                    transition.startTransition(0);
                    transition.reverseTransition(400);



                    set.start();
                    set2.start();

                }
                switchFragments();
            }

            */
                }
            }
        });

        /*
        swap_icon_blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(current_fragment_id == Constants.ANSWERING_FRAGMENT_ID) {
                    AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(v.getContext(),
                            R.animator.card_flip_right_in);
                    set.setTarget(swap_icon_blue);

                    AnimatorSet set2 = (AnimatorSet) AnimatorInflater.loadAnimator(v.getContext(),
                            R.animator.card_flip_right_out);

                    set2.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    set2.setTarget(swap_icon);
                    TransitionDrawable transition = (TransitionDrawable) toolbar.getBackground();
                    transition.reverseTransition(0);
                    transition.startTransition(400);

                    set.start();
                    set2.start();

                }
                if(current_fragment_id == Constants.QUESTION_CREATION_MODE_FRAGMENT_ID)
                {
                    AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(v.getContext(),
                            R.animator.card_flip_left_in);
                    set.setTarget(swap_icon);

                    AnimatorSet set2 = (AnimatorSet) AnimatorInflater.loadAnimator(v.getContext(),
                            R.animator.card_flip_left_out);

                    set2.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            swap_icon.setClickable(true);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

                    set2.setTarget(swap_icon_blue);
                    TransitionDrawable transition = (TransitionDrawable) toolbar.getBackground();

                    // ensure the transition occurred completely
                    transition.startTransition(0);
                    transition.reverseTransition(400);
                    set.start();
                    set2.start();

                }
                switchFragments();
            }
        });

        */

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
            AnsweringFragment firstFragment = new AnsweringFragment();

            // Set the current fragment ID
            this.current_fragment_id = Constants.ANSWERING_FRAGMENT_ID;

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
     */

    public void switchFragments() {

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
                    .setCustomAnimations(R.animator.card_flip_right_in, R.animator.card_flip_right_out,
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
                            R.animator.card_flip_left_in, R.animator.card_flip_left_out,
                            R.animator.card_flip_right_in, R.animator.card_flip_right_out)

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

    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {

        } else {
            getFragmentManager().popBackStack();
        }
    }



}
