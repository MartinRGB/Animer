package com.martinrgb.animer.component.recyclerview;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.martinrgb.animer.Animer;

public class AnRecyclerView extends RecyclerView {
    private Context mContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LayoutManager mLayoutManager;

    private boolean mIsRootOnTouch = false;

    private int mTouchSlop,mMinFlingVelocity;

    private Animer mFlingAnimer, mSpringAnimer;
    private boolean mShouldFling = true,mShouldSpringBack = true;

    private float mRootCurrentTransValue,mDragStartValue,mCurrentVelocity,mPrevFrameValue,mFlingStartVelocity,mFlingCurrentVelocity;
    private VelocityTracker mRootVelocityTracker;

    public AnRecyclerView(Context context) {
        super(context);
        init(context, null);
    }

    public AnRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AnRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        mAdapter = adapter;
        super.setAdapter(adapter);
    }

    private static final int LINEAR_LAYOUT = 0;
    private static final int GRID_LAYOUT = 1;
    private static final int STAGGERED_LAYOUT=2;
    private int LAYOUT_MODE = 0;

    @Override
    public void setLayoutManager(LayoutManager layout) {

        if(layout instanceof LinearLayoutManager){
            LAYOUT_MODE = LINEAR_LAYOUT;
        }

        if(layout instanceof GridLayoutManager){
            LAYOUT_MODE = GRID_LAYOUT;
        }

        if(layout instanceof StaggeredGridLayoutManager){
            LAYOUT_MODE = STAGGERED_LAYOUT;
        }

        mLayoutManager = layout;
        super.setLayoutManager(layout);
    }

    @Override
    public void scrollToPosition(int position) {
        super.scrollToPosition(position);
    }

    @Override
    public void smoothScrollToPosition(int position) {
        super.smoothScrollToPosition(position);
    }

    private void init(Context context, AttributeSet attributeSet) {
        // Custom Attribute in XML
//        if (context != null && attributeSet != null) {
//            TypedArray a = context.getTheme().obtainStyledAttributes(
//                    attributeSet, R.styleable.RecyclerViewBouncy,
//                    0, 0
//            );
//        }
        setOverScrollMode(OVER_SCROLL_NEVER);

        mContext = context;
        mRecyclerView = this;
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMinFlingVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();

        scrollToPosition(0);
        initOnScrollListener();
        initTouchListener();
        initAnimer();
    }

    private void initAnimer(){

        mFlingAnimer = new Animer();
        mFlingAnimer.setSolver(Animer.flingDroid(0,0.5f));
        // velocity is 1000ms velocity/60 is frame velocity;
        mFlingAnimer.setMinimumVisibleChange(1f);
        mFlingAnimer.setUpdateListener(new Animer.UpdateListener() {
            @Override
            public void onUpdate(float value, float velocity, float progress) {
                if(directionVertical()){
                    if(mRecyclerView.getTranslationY() < 0){
                        mRecyclerView.setTranslationY(Math.max(0,mRecyclerView.getTranslationY() - velocity/(1000/16)));
                    }
                    else if(mRecyclerView.getTranslationY() > 0){
                        mRecyclerView.setTranslationY(Math.min(0,mRecyclerView.getTranslationY() - velocity/(1000/16)));
                    }
                    else {
                        mRecyclerView.scrollBy( 0, (int) velocity/(1000/16));
                    }

                }
                if(!directionVertical()) {
                    if(mRecyclerView.getTranslationX() < 0){
                        mRecyclerView.setTranslationX(Math.max(0,mRecyclerView.getTranslationX() - velocity/(1000/16)));
                    }
                    else if(mRecyclerView.getTranslationX() > 0){
                        mRecyclerView.setTranslationX(Math.min(0,mRecyclerView.getTranslationX() - velocity/(1000/16)));
                    }
                    else {
                        mRecyclerView.scrollBy((int) velocity/(1000/16), 0);

                    }
                }

                mFlingCurrentVelocity = velocity;
            }
        });


        mSpringAnimer = new Animer();
        mSpringAnimer.setSolver(Animer.springDroid(150,0.99f));
        mSpringAnimer.setMinimumVisibleChange(1f);
        mSpringAnimer.setUpdateListener(new Animer.UpdateListener() {
            @Override
            public void onUpdate(float value, float velocity, float progress) {
                if(directionVertical()){
                    mRecyclerView.setTranslationY(value);
                }
                else {
                    mRecyclerView.setTranslationX(value);
                }
            }
        });

    }


    private void initOnScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                // top springback
                if(directionVertical() && !recyclerView.canScrollVertically(-1)){
                    if(mShouldFling){
                        mSpringAnimer.setVelocity(-mFlingCurrentVelocity);
                        mFlingAnimer.cancel();
                        mSpringAnimer.setEndValue(0);
                        mShouldFling = false;
                    }
                }

                // bottom springBack
                if(directionVertical() && !recyclerView.canScrollVertically(1)){
                    if(mShouldFling){
                        mSpringAnimer.setVelocity(-mFlingCurrentVelocity);
                        mFlingAnimer.cancel();
                        mSpringAnimer.setEndValue(0);
                        mShouldFling = false;
                    }
                }
                // left springBack
                if(!directionVertical() && !recyclerView.canScrollHorizontally(-1)){
                    if(mShouldFling){
                        mSpringAnimer.setVelocity(-mFlingCurrentVelocity);
                        mFlingAnimer.cancel();
                        mSpringAnimer.setEndValue(0);
                        mShouldFling = false;
                    }
                }

                // right springBack
                if(!directionVertical() && !recyclerView.canScrollHorizontally(1)){
                    if(mShouldFling){
                        mSpringAnimer.setVelocity(-mFlingCurrentVelocity);
                        mFlingAnimer.cancel();
                        mSpringAnimer.setEndValue(0);
                        mShouldFling = false;
                    }
                }

            }
        });

    }

    private void initTouchListener() {
        mRecyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        // add vt
                        if (mRootVelocityTracker == null) { mRootVelocityTracker = VelocityTracker.obtain(); }
                        else { mRootVelocityTracker.clear();}
                        mRootVelocityTracker.addMovement(e);

                        // reset val
                        mDragStartValue = (directionVertical())?e.getRawY():e.getRawX();
                        mPrevFrameValue = (directionVertical())?e.getRawY():e.getRawX();
                        mCurrentVelocity = 0;
                        mFlingStartVelocity = 0;
                        mRootCurrentTransValue = (directionVertical())?mRecyclerView.getTranslationY():mRecyclerView.getTranslationX();

                        rv.stopScroll();

                        // Method-I ,but when spring is not easy to click,
                        // detect is interact on root or item
//                        if(!mFlingAnimer.isRunning() && !mSpringAnimer.isRunning()){
//                            Log.e("isNotRunning","isNotRunning");
//                            mIsRootOnTouch = false;
//                        }
//
//                        if(mFlingAnimer.isRunning() || mSpringAnimer.isRunning()){
//                            Log.e("isRunning","is Running");
//                            mFlingAnimer.cancel();
//                            mSpringAnimer.cancel();
//                            mIsRootOnTouch = true;
//                        }

                        // Method-II
                        if(!mFlingAnimer.isRunning()){
                            mIsRootOnTouch = false;
                        }

                        if(mFlingAnimer.isRunning()){
                            mFlingAnimer.cancel();
                            mIsRootOnTouch = true;
                        }

                        if(mSpringAnimer.isRunning()){
                            mSpringAnimer.cancel();
                            // disable this for rapid click when spring is running
                            //mIsRootOnTouch = true;
                        }


                        break;

                    case MotionEvent.ACTION_MOVE:

                        float mCurrentVal = ((directionVertical())?e.getRawY():e.getRawX());
                        float mAbsTransValue = Math.abs( mCurrentVal - mPrevFrameValue );

                        // if TouchMove bigger than slop,group action
                        // Method -I - mMinFlingVelocity , Method - II - mTouchSlop
                        if( mAbsTransValue > mTouchSlop){
                            mIsRootOnTouch = true;
                        }
                        // if TouchMove smaller than slop,group action
                        else {
                            mIsRootOnTouch = false;
                        }

                        break;

                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        // OverRangeScroll -> SpringBack
                        mSpringAnimer.setFrom((directionVertical())?mRecyclerView.getTranslationY():mRecyclerView.getTranslationX());
                        mSpringAnimer.setEndValue(0);
                        break;
                }

                return mIsRootOnTouch;
            }


            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

                mIsRootOnTouch = false;

                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dragValue = overDragFucntion(((directionVertical())?e.getRawY():e.getRawX()) - mDragStartValue);
                        float mTransValue = mRootCurrentTransValue + dragValue;

                        mRootVelocityTracker.addMovement(e);
                        mRootVelocityTracker.computeCurrentVelocity(1000);
                        mFlingStartVelocity = (directionVertical())? mRootVelocityTracker.getYVelocity(): mRootVelocityTracker.getXVelocity();

                        // top overscroll
                        if(directionVertical() && mTransValue > 0 && !rv.canScrollVertically(-1)){
                            mRecyclerView.setTranslationY(mTransValue);//3
                            mShouldSpringBack = (mFlingStartVelocity >= 0)? true:false;
                        }
                        // bottom overscroll
                        else if(directionVertical() && mTransValue < 0 &&  !rv.canScrollVertically(1)){
                            mRecyclerView.setTranslationY(mTransValue); //3
                            mShouldSpringBack = (mFlingStartVelocity <= 0)? true:false;
                        }

                        // left overscroll
                        else if(!directionVertical() && mTransValue > 0 && !rv.canScrollHorizontally(-1)){
                            mRecyclerView.setTranslationX(mTransValue); //3
                            mShouldSpringBack = (mFlingStartVelocity >= 0)? true:false;
                        }
                        // right overscroll
                        else if(!directionVertical() && mTransValue < 0 &&  !rv.canScrollHorizontally(1)){
                            mRecyclerView.setTranslationX(mTransValue); //3
                            mShouldSpringBack = (mFlingStartVelocity <= 0)? true:false;
                        }
                        // normal scroll
                        else{
                            scrollBy((int)-mCurrentVelocity);
                            mShouldSpringBack = false;
                        }
                        mCurrentVelocity = ((directionVertical())?e.getRawY():e.getRawX()) - mPrevFrameValue;
                        mPrevFrameValue = ((directionVertical())?e.getRawY():e.getRawX());

                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // OverRangeScroll -> SpringBack

                        if(mShouldSpringBack){
                            mSpringAnimer.setFrom((directionVertical())?mRecyclerView.getTranslationY():mRecyclerView.getTranslationX());
                            mSpringAnimer.setEndValue(0);
                        }
                        // Scroll -> Fling
                        else {
                            // if velocity greter than Min Fling Vel,then Fling
                            if(Math.abs(mFlingStartVelocity) > mMinFlingVelocity){
                                // Method-II
                                if(mSpringAnimer.isRunning()){
                                    mSpringAnimer.cancel();
                                }
                                mShouldFling = true;

                                mFlingAnimer.setArgument1(-mFlingStartVelocity);
                                mFlingAnimer.start();
                            }
                            // otherwise SpringBack
                            else {
                                mSpringAnimer.setFrom((directionVertical()) ? mRecyclerView.getTranslationY() : mRecyclerView.getTranslationX());
                                mSpringAnimer.setEndValue(0);
                            }
                        }
                        break;
                }

            }
        });
    }


    private float overDragFucntion(float value){
        return value/3;
    }


    private void scrollBy(int dist) {
        if (directionVertical()) {
            mRecyclerView.scrollBy(0, dist);
        } else {
            mRecyclerView.scrollBy(dist, 0);
        }
    }

    private boolean directionVertical() {
        switch (LAYOUT_MODE){
            case LINEAR_LAYOUT:
                return ((LinearLayoutManager)mLayoutManager).getOrientation() == RecyclerView.VERTICAL;
            case GRID_LAYOUT:
                return ((GridLayoutManager)mLayoutManager).getOrientation() == RecyclerView.VERTICAL;
            case STAGGERED_LAYOUT:
                return ((StaggeredGridLayoutManager)mLayoutManager).getOrientation() == RecyclerView.VERTICAL;
        }
        return true;
    }

    private double dpToPx(double dp) {
        Resources resources = mContext.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return  dp * ((double) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
