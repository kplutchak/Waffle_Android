package com.pipit.waffle;

/**
 * Created by Kyle on 4/2/2015.
 */
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.OverScroller;
import android.widget.Scroller;

public class ScrollLayout extends ViewGroup {
    private static final String TAG = "ScrollLayout";
    private OverScroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mCurScreen;
    private int mDefaultScreen = 0;
    private static final int TOUCH_STATE_REST = 0;
    private static final int TOUCH_STATE_SCROLLING = 1;
    private static final int SNAP_VELOCITY = 600;
    private int mTouchState = TOUCH_STATE_REST;
    private int mTouchSlop;
    private float mLastMotionX;
    private float mLastMotionY;
    private boolean flung;

    private boolean mAtTop;


    private OnViewChangeListener mOnViewChangeListener;

    // variable keeping track of scroll/fling offset
    private int offset = 0;

    private int bottom_bar_height;

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    public class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private ScrollLayout trainView;

        public GestureListener(ScrollLayout trainView) {
            this.trainView = trainView;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.d(TAG, "Down..." + getScrollY() + " " + mScroller.getCurrY());
            return true;
        }



        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            trainView.scroll((int) distanceY);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
           /* if((e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY)
                ||
            e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) */
                trainView.fling((int) velocityY);
            return true;
        }
    }
    private boolean isScroll = true;
    public void setIsScroll(boolean b) {
        this.isScroll = b;
    }

    public ScrollLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // Height of bottom bar
        bottom_bar_height = (int) (46 * getResources().getDisplayMetrics().density);
        mScroller = new OverScroller(context);
        // TODO: adjust friction
        mScroller.setFriction(ViewConfiguration.getScrollFriction());
        mCurScreen = mDefaultScreen;
        // this is our custom implementation of the OnGestureListener interface
        GestureListener gestureListener = new GestureListener(this);
        gestureDetector = new GestureDetector(context, gestureListener);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childTop = 0;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View childView = getChildAt(i);
            if (childView.getVisibility() != View.GONE) {
                final int childHeight = childView.getMeasuredHeight();
                childView.layout(0, childTop, childView.getMeasuredWidth(),
                        childTop + childHeight);
                childTop += childHeight;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Log.e(TAG, "onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException(
                    "ScrollLayout only canmCurScreen run at EXACTLY mode!");
        }
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException(
                    "ScrollLayout only can run at EXACTLY mode!");
        }

        // The children are given the same width and height as the scrollLayout
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
        // Log.e(TAG, "moving to screen "+mCurScreen);
        //scrollTo(0, mCurScreen * height);
        // Set the initial scroll (placing the bar at the bottom)
        scrollTo(0, -computeViewableHeight());
        offset = computeViewableHeight();
    }

    private GestureDetector  gestureDetector;

    // Bottom bar starts at the bottom
    private boolean isTop = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            Log.d(TAG, "ScrollY on down: " + getScrollY());
            // Check coordinates of press
            if(event.getRawY() <= -getScrollY())
            {
                // Ignore/pass to the rest of the view
                Log.d(TAG, "Didn't click low enough. Clicked at: " + event.getRawY());
            }

            // If we are headed towards the very top or bottom on ACTION_DOWN, alert
            if(mScroller.getFinalY() == 0)
            {
                isTop = true;
            }
            else if(mScroller.getFinalY() == -computeViewableHeight())
            {
                isTop = false;
            }
        }
        else if(event.getAction() == MotionEvent.ACTION_UP)
        {
            flung = false;
            int threshold;

            if(isTop)
            {
                threshold = (int) ((1.0/3.0) * (double) computeViewableHeight());
                if(Math.abs(getScrollY()) < threshold) {
                    snapToDestination(false);
                    Log.d(TAG, "Snap up and consume!");
                }
                else
                {
                    snapToDestination(true);
                    Log.d(TAG, "Snap down and consume!");
                }
               // return true;
            }
            else
            {
                threshold = (int) ((2.0/3.0) * (double) computeViewableHeight());
                if(Math.abs(getScrollY()) >= threshold) {
                    snapToDestination(true);
                    Log.d(TAG, "Snap down and consume!");
                }
                else
                {
                    snapToDestination(false);
                    Log.d(TAG, "Snap up and consume!");
                }
                //return true;
            }
        }

        // Forward all touch events to the GestureDetector
        return gestureDetector.onTouchEvent(event);
    }

    // Called when the GestureListener detects scroll
    public void scroll(int distanceY) {
        //mScroller.forceFinished(true);

        if(offset - distanceY > computeViewableHeight() || offset - distanceY < 0)
            return;
        offset -= distanceY;

        Log.d(TAG, "Offset: " + offset + ", ScrollY: " + getScrollY());
        //checkOffset();
        scrollTo(0, -offset);
        invalidate();
    }

    public void checkPositionAndSnap() {

    }

    // Called when the GestureListener detects fling
    public void fling(int velocityY) {
        //mScroller.forceFinished(true);
        final int viewable_height = computeViewableHeight();

        // TODO: consider trying for a max velocity??

        mScroller.fling(0, offset, 0, velocityY, 0, 50, 0, computeViewableHeight());
        Log.d(TAG, "We are headed towards: " + mScroller.getFinalY());
        if(mScroller.getFinalY() == 0 || mScroller.getFinalY() == computeViewableHeight()) {
            Log.d(TAG, "We're flinging!");
            flung = true;
            return;
        }
        // Don't have sufficient velocity to reach either side, but we still got flung
        else
        {
            // Decide if we should return/proceed depending on magnitude of velocity
            // TODO: play with this threshold magnitude
            if(isTop)
                Log.d(TAG, "Weak fling with velocity of: " + velocityY + ". We started at the top!");
            else
                Log.d(TAG, "Weak fling with velocity of: " + velocityY + ". We started at the bottom!");

            if(isTop) {
                if(velocityY > 1000)
                    snapToDestination(true);
                else
                    snapToDestination(false);
            }
            else {
                if(velocityY < -1000)
                    snapToDestination(false);
                else
                    snapToDestination(true);
            }
        }

        flung = false;
        //invalidate();
    }

    private void checkOffset() {
        if (offset < 0) {             offset = 0;         } else if (offset > 2000) {
            offset = 2000;
        }
    }

    // TODO: clean up this code
    public void snapToDestination(boolean bottom) {
        final int screenWidth = getWidth();
        final int screenHeight = getHeight();
        final int destScreen;
        //if(bottom)
           // isTop = false;
       // else
            //isTop = true;

        if(!bottom)
            destScreen = (getScrollY() + screenHeight / 2) / screenHeight;
        else
            destScreen = (getScrollY() + screenHeight / 2) / screenHeight + screenHeight;

        snapToScreen(destScreen, bottom);
    }

    public void snapToScreen(int whichScreen, boolean bottom) {
        if(!isScroll) {
            this.setToScreen(whichScreen);
            return;
        }

        scrollToScreen(whichScreen, bottom);
    }

    public void scrollToScreen(int whichScreen, boolean bottom) {
        Log.d(TAG, "Scrolling snap!");
        // get the valid layout page
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        if (/*getScrollY() != (whichScreen * getHeight())*/ true) {
            int delta;
            if(!bottom)

                 delta = whichScreen * getHeight() - getScrollY();
            else
                delta = Math.abs(getScrollY()) - computeViewableHeight();
            //Log.d(TAG, "delta: " + delta);
            mScroller.startScroll(0, getScrollY(), 0, delta,
                    Math.abs(delta) * 1);
            mCurScreen = whichScreen;
            invalidate(); // Redraw the layout

            if (mOnViewChangeListener != null)
            {
                mOnViewChangeListener.OnViewChange(mCurScreen);
            }
        }
    }

    public void setToScreen(int whichScreen) {
        Log.d(TAG, "Setting snap!");
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        mCurScreen = whichScreen;
        scrollTo(0, whichScreen * getHeight());

        if (mOnViewChangeListener != null)
        {
            mOnViewChangeListener.OnViewChange(mCurScreen);
        }
    }

    public int getCurScreen() {
        return mCurScreen;
    }

    @Override
    public void computeScroll() {

        if (mScroller.computeScrollOffset() && flung) {
            scrollTo(0, -mScroller.getCurrY());

            //Log.d(TAG, "Flung to " + Math.abs(-mScroller.getCurrY()) + ", ScrollY: " + getScrollY());
            offset = mScroller.getCurrY();
            if(mScroller.isFinished())
                Log.d(TAG, "Finished with the fling!");

            postInvalidate();
            return;
        }

        if (mScroller.computeScrollOffset() && !flung) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            offset = -mScroller.getCurrY();
            postInvalidate();
        }
    }

/*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isScroll) {
            return false;
        }

        mGestureDetector.onTouchEvent(event);

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //Log.e(TAG, "event down!");
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastMotionX = x;

                //---------------New Code----------------------
                mLastMotionY = y;
                //---------------------------------------------

                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) (mLastMotionX - x);

                //---------------New Code----------------------
                int deltaY = (int) (mLastMotionY - y);
               // if(Math.abs(deltaX) < 200 && Math.abs(deltaY) > 10)
                    //break;
               // if(Math.abs(deltaX) > 10 && Math.abs(deltaY) < 200)
                    //break;

                mLastMotionY = y;
                //-------------------------------------

                mLastMotionX = x;
                scrollBy(0, deltaY);
                break;
            case MotionEvent.ACTION_UP:
                //Log.e(TAG, "event : up");
                // if (mTouchState == TOUCH_STATE_SCROLLING) {
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000);
                int velocityY = (int) velocityTracker.getYVelocity();
                //Log.e(TAG, "velocityX:" + velocityX);
                if (velocityY > SNAP_VELOCITY && mCurScreen > 0) {
                    // Fling enough to move left
                    //Log.e(TAG, "snap left");
                    //snapToScreen(mCurScreen - 1);
                } else if (velocityY < -SNAP_VELOCITY
                        && mCurScreen < getChildCount() - 1) {
                    // Fling enough to move right
                    //Log.e(TAG, "snap right");
                    //snapToScreen(mCurScreen + 1);
                } else {
                    //snapToDestination();
                }
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                // }
                mTouchState = TOUCH_STATE_REST;
                break;
            case MotionEvent.ACTION_CANCEL:
                mTouchState = TOUCH_STATE_REST;
                break;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onInterceptTouchEvent-slop:" + mTouchSlop);
        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE)
                && (mTouchState != TOUCH_STATE_REST)) {
            return true;
        }
        final float x = ev.getX();
        final float y = ev.getY();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                final int yDiff = (int) Math.abs(mLastMotionY - y);
                if (yDiff > mTouchSlop) {
                    mTouchState = TOUCH_STATE_SCROLLING;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                mLastMotionY = y;
                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
                        : TOUCH_STATE_SCROLLING;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mTouchState = TOUCH_STATE_REST;
                break;
        }
        return mTouchState != TOUCH_STATE_REST;
    }


*/

    public void SetOnViewChangeListener(OnViewChangeListener listener)
    {
        mOnViewChangeListener = listener;
    }


    public int computeViewableHeight() {
        // adjust the size of the LinearLayout that is contained within the ScrollView
        final TypedArray styledAttributes = getContext().getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        int mActionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }

        // Height of the screen minus the Toolbar and Status Bar
        return point.y - mActionBarSize - getStatusBarHeight();
    }

    // TODO: devices that have no status bar/different status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }



    public interface OnViewChangeListener {
        public void OnViewChange(int view);
    }
}
