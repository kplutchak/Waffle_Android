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

    /*
    private GestureDetector mGestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
           // new Flinger(mScroller).start((int)velocityY);
            //invalidate();
            Log.d(TAG, "Fling detected!");
            return true;
        }
    });

*/

    // variable keeping track of scroll/fling offset
    private int              offset     = 0;

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
            Log.d(TAG, "DOWN!" + getScrollY() + " " + mScroller.getCurrY());

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
        scrollTo(0, mCurScreen * height);
    }



    private GestureDetector  gestureDetector;
    private int up_position;
    private long last_movement_time;

    private boolean isTop = true;

    private boolean greaterThanThreshold = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // forward all touch events to the GestureDetector
        if(event.getAction() == MotionEvent.ACTION_UP)
        {
            flung = false;
            // TODO: more thresholds depending on where we came from
            int upper_threshold = (int) ((2.0/4.0) * (double) computeViewableHeight());
            int lower_threshold = (int) ((2.0/4.0) * (double) computeViewableHeight());
            Log.d(TAG, "Is " + Math.abs(getScrollY()) + " less than " + upper_threshold + "?");
            if(Math.abs(getScrollY()) < upper_threshold)
            {
                snapToDestination(false);
                greaterThanThreshold = false;
                Log.d(TAG, "Snap up and consume!");
                isTop = true;
               // return true;
            }


            Log.d(TAG, "Is " + Math.abs(getScrollY()) + " greater than " + lower_threshold + "?");
            if(Math.abs(getScrollY()) >= lower_threshold)
            {
                snapToDestination(true);
                greaterThanThreshold = true;
                Log.d(TAG, "Snap down and consume!");
                isTop = false;
                //return true;
            }
           // Log.d(TAG, "We're stuck in the middle...");
        }



       /* if(event.getAction() == MotionEvent.ACTION_UP)
        {
            long diff = System.currentTimeMillis() - last_movement_time;
            if(diff>100) {

                Log.d(TAG, "Been too long for a a fling!");
                    Log.d(TAG, "We aren't flinging!");
                    flung = false;
                    if(Math.abs(getScrollY()) < computeViewableHeight()/2) {
                        Log.d(TAG, "Snap to top!");
                        snapToDestination(false);
                        offset = 0;
                    }
                    else
                    {
                        Log.d(TAG, "Snap to bottom!");
                        snapToDestination(true);
                        offset = computeViewableHeight();
                    }

                return gestureDetector.onTouchEvent(event);
            }

/*
            Log.d(TAG, "ACTION_UP ScrollY: " + Math.abs(getScrollY()) + ", current velocity: " + mScroller.getCurrVelocity());
            if(-500 < mScroller.getCurrVelocity() && mScroller.getCurrVelocity() < 500) {
                Log.d(TAG, "We aren't flinging!");
                flung = false;
                if(Math.abs(getScrollY()) < computeViewableHeight()/2) {
                    Log.d(TAG, "Snap to top!");
                    snapToDestination(false);
                    offset = 0;
                }
                else
                {
                    Log.d(TAG, "Snap to bottom!");
                    snapToDestination(true);
                    offset = computeViewableHeight();
                }
                return true;
            }

            up_position = Math.abs(getScrollY());
        }
        if(event.getAction() == MotionEvent.ACTION_MOVE)
        {
            last_movement_time = System.currentTimeMillis();
        }
    */
        return gestureDetector.onTouchEvent(event);
    }



    // called when the GestureListener detects scroll
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

    // called when the GestureListener detects fling
    public void fling(int velocityY) {
        //mScroller.forceFinished(true);
        //Log.d(TAG, "Position at start of fling: " + getScrollY() + ". Velocity: " + velocityY);
        final int viewable_height = computeViewableHeight();
        //Log.d(TAG, "Screen height: " + viewable_height);
        /*
        if(velocityY < 0 && velocityY < -1500 && Math.abs(getScrollY()) < viewable_height/2)
        {
            Log.d(TAG, "Snap to top!");
            flung = false;
            snapToDestination(false);
            offset = 0;
            return;
        }
        */

        // TODO: consider trying for a max velocity??
        mScroller.fling(0, offset, 0, velocityY, 0, 50, 0, computeViewableHeight());
        Log.d(TAG, "We are headed towards: " + mScroller.getFinalY());
        if(mScroller.getFinalY() == 0 || mScroller.getFinalY() == computeViewableHeight()) {
            Log.d(TAG, "We're flinging!");
            flung = true;
            return;
        }
        // TODO: improve on this, currently we snap all the way in the direction of the velocity no matter what
        // TODO: eventually, decide if we should return/proceed depending on magnitude of velocity
        else // Don't have sufficient velocity to reach either side, but we still got flung...so...
        {
            Log.d(TAG, "Weak fling...");
            if(isTop) {
                snapToDestination(true);
                isTop = false;
            }
            else {
                snapToDestination(false);
                isTop = true;
            }
        }
       /* if(velocityY > 500)
            snapToDestination(true);
        else if(velocityY < -500)
            snapToDestination(false);
        else
            snapToDestination(true);

            */

        /*
        if(mScroller.getFinalY() > 0 && mScroller.getFinalY() < computeViewableHeight())
        {
            flung = false;
            if(!mAtTop) {
                Log.d(TAG, "Snap to bottom!");
                flung = false;
                snapToDestination(true);
                offset = computeViewableHeight();
            }
            else
            {
                Log.d(TAG, "Snap to top!");
                flung = false;
                snapToDestination(false);
                offset = 0;
            }

            return;
        }
        */
        flung = false;

        if(mScroller.getFinalY() == 0)
            mAtTop = true;
        else
            mAtTop = false;
        //scrollTo(0, -mScroller.getFinalY());

        //invalidate();
    }

    private void checkOffset() {
        if (offset < 0) {             offset = 0;         } else if (offset > 2000) {
            offset = 2000;
        }
    }

    public void snapToTop() {}

    public void snapToDestination(boolean bottom) {
        final int screenWidth = getWidth();
        final int screenHeight = getHeight();
        final int destScreen;
        if(bottom)
            mAtTop = false;
        else
            mAtTop = true;

        if(!bottom)
            destScreen = (getScrollY() + screenHeight / 2) / screenHeight;
        else
            destScreen = (getScrollY() + screenHeight / 2) / screenHeight + screenHeight;

        Log.d(TAG, "destScreen: " + destScreen);
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
        Log.d(TAG, "whichScreen: " + whichScreen);
        if (/*getScrollY() != (whichScreen * getHeight())*/ true) {
            int delta;
            if(!bottom)
                 delta = whichScreen * getHeight() - getScrollY();
            else
                delta = Math.abs(getScrollY()) - computeViewableHeight();
            Log.d(TAG, "delta: " + delta);
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
                Log.d(TAG, "FINISHED!");

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
