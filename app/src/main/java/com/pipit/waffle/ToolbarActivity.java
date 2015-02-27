package com.pipit.waffle;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.pipit.waffle.Objects.Choice;
import com.pipit.waffle.Objects.ClientData;
import com.pipit.waffle.Objects.Question;
import com.pipit.waffle.Objects.Self;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

public class ToolbarActivity extends ActionBarActivity {

    private ActionBarDrawerToggle toggle;
    public static int current_fragment_id;
    private Typewriter writer_toolbar;
    private ListView drawerListView;
    private android.support.v7.widget.Toolbar rl;
    private DrawerLayout drawerLayout;

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

        ClientData.getNextUnansweredQuestion(this);

        // Get the Typewriter
        writer_toolbar = new Typewriter(this);

        rl = (Toolbar) findViewById(R.id.toolbar);
        Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.START;
        Resources r = getResources();
        int margin_start = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, r.getDisplayMetrics());
        params.setMarginStart(margin_start);

        writer_toolbar.setTextSize(20);
        writer_toolbar.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        writer_toolbar.setTextColor(getResources().getColor(R.color.off_white));

        writer_toolbar.setText("Waffle");

        rl.addView(writer_toolbar);

        // Set the drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerLayout.setScrimColor(getResources().getColor(R.color.black_tint_medium));

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        toggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(toggle);

        // TODO: add click behavior for nav. drawer

        RelativeLayout rl_me = (RelativeLayout) findViewById(R.id.nav_item1);
        rl_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if we aren't already on the "My Questions" tab
                if (current_fragment_id != Constants.USER_QUESTIONS_FRAGMENT_ID) {

                    // TODO: disable clicks during break
                    Timer delay_frag_trans = new Timer();
                    delay_frag_trans.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
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

                                                    // Replace any fragments currently in the container view with a fragment
                                                    // representing the next page (indicated by the just-incremented currentPage
                                                    // variable).
                                            .replace(R.id.fragment_container, frag)

                                                    // Add this transaction to the back stack, allowing users to press Back
                                                    // to get to the front of the card.
                                            .addToBackStack(null)

                                                    // Commit the transaction.
                                            .commit();

                                    Animation fade_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_toolbar_text);
                                    fade_in.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            writer_toolbar.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {

                                        }
                                    });

                                    rl.removeView(writer_toolbar);
                                    rl.addView(writer_toolbar);
                                    writer_toolbar.setCharacterDelay(2);

                                    writer_toolbar.animateText("My Questions");
                                    writer_toolbar.startAnimation(fade_in);
                                    current_fragment_id = Constants.USER_QUESTIONS_FRAGMENT_ID;
                                }
                            });
                        }
                    }, 300);
                }
                // Close the drawer after the item has been clicked
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        RelativeLayout rl_settings = (RelativeLayout) findViewById(R.id.nav_item4);
        rl_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if we aren't already on the "Settings" tab
                if (current_fragment_id != Constants.USER_SETTINGS_FRAGMENT_ID) {

                    // TODO: error handling in this block

                    final CountDownLatch latch = new CountDownLatch(2);
                    new Thread(new Runnable(){
                        @Override
                        public void run(){
                            DisplayImageOptions options;
                            options = new DisplayImageOptions.Builder().cacheInMemory(true)
                                    .cacheOnDisk(true)
                                    .build();

                            ImageLoader.getInstance().loadImage("http://www.brandingmagazine.com/wp-content/uploads/2014/02/mila-kunis-jim-bean-cover.jpg", options, new ImageLoadingListener() {
                                @Override
                                public void onLoadingStarted(String imageUri, View view) {

                                }

                                @Override
                                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                                }

                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    latch.countDown();
                                }

                                @Override
                                public void onLoadingCancelled(String imageUri, View view) {

                                }
                            });
                        }
                    }).start();

                    new Thread(new Runnable(){
                        @Override
                        public void run(){
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            latch.countDown();
                        }
                    }).start();

                    new Thread(new Runnable(){
                        @Override
                        public void run(){
                            try {
                                latch.await();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();


                    SettingsPrepTask task = new SettingsPrepTask(latch);
                    task.execute();
                }

            }
        });


        /*
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
                    Question testQuestion = new Question("No Question Retrieved", Self.getUser());
                    testQuestion = ClientData.getNextUnansweredQuestion(mcontext);
                    String text = testQuestion.getQuestionBody() + " " + testQuestion.getChoices().get(0).getUrl();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(mcontext, text, duration);
                    toast.show();
                }
            }
        });

        */
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

        // Set the camera icon
        swap_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (current_fragment_id == Constants.ANSWERING_FRAGMENT_ID || current_fragment_id == Constants.USER_QUESTIONS_FRAGMENT_ID) {
                    Intent intent = new Intent(v.getContext(), CameraActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.stay);
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


    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {

        } else {
            // TODO: disable clicks during the break
            Timer delay_back = new Timer();
            delay_back.schedule(new TimerTask() {

                @Override
                public void run() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Animation fade_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_toolbar_text);
                            fade_in.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    writer_toolbar.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });


                            rl.removeView(writer_toolbar);
                            rl.addView(writer_toolbar);
                            writer_toolbar.setCharacterDelay(2);

                            writer_toolbar.animateText("Waffle");
                            writer_toolbar.startAnimation(fade_in);
                            getFragmentManager().popBackStack();
                        }
                    });
                }
            }, 300);

        }
    }

    public class SettingsPrepTask extends AsyncTask<Void, Void, Void> {

        private CountDownLatch cd;
        public SettingsPrepTask(CountDownLatch latch) {
            cd = latch;
        }
        @Override
        protected Void doInBackground(Void... params) {

            try {
                cd.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            UserSettingsFragment frag = new UserSettingsFragment();

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

                            // Replace any fragments currently in the container view with a fragment
                            // representing the next page (indicated by the just-incremented currentPage
                            // variable).
                    .replace(R.id.fragment_container, frag)

                            // Add this transaction to the back stack, allowing users to press Back
                            // to get to the front of the card.
                    .addToBackStack(null)

                            // Commit the transaction.
                    .commit();

            Animation fade_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_toolbar_text);
            fade_in.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    writer_toolbar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            // Close the drawer after the item has been clicked
            drawerLayout.closeDrawer(Gravity.LEFT);

            rl.removeView(writer_toolbar);
            rl.addView(writer_toolbar);
            writer_toolbar.setCharacterDelay(2);

            writer_toolbar.animateText("Settings");
            writer_toolbar.startAnimation(fade_in);
            current_fragment_id = Constants.USER_SETTINGS_FRAGMENT_ID;
        }

        @Override
        protected void onPreExecute() {
        }

    }

}
