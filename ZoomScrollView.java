package com.example.zoomsrollview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;


public class ZoomScrollView extends ScrollView {

    private View zoomView;
    private float mLastMotionY;
    private int allScroll = -1;
    private int height = 0;
    private int zoomId;
    private int maxZoom;
    private ScrollViewListener scrollViewListener = null;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            allScroll -= 25;
            if(allScroll < 0){
                allScroll = 0;
            }
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) zoomView.getLayoutParams();
            lp.height = (int) (height + allScroll/2);
            zoomView.setLayoutParams(lp);
            if(allScroll != 0){
                handler.sendEmptyMessageDelayed(1,10);
            }else{
                allScroll = -1;
            }
        }
    };
    public ZoomScrollView(Context context) {
        super(context);
    }

    public ZoomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ZoomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        zoomView = findViewById(zoomId);
    }

    @SuppressLint("Recycle")
	private void init(AttributeSet attrs){
        TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.ObsetrvableView);
        zoomId = t.getResourceId(R.styleable.ObsetrvableView_zoomId,-1);
        maxZoom = t.getDimensionPixelOffset(R.styleable.ObsetrvableView_maxZoom,0);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(zoomView == null || maxZoom == 0){
            return super.dispatchTouchEvent(event);
        }

        final int action = event.getAction();

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            if(allScroll != -1){
                handler.sendEmptyMessageDelayed(1,10);
            }
            return super.dispatchTouchEvent(event);
        }

        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                final float y = event.getY();
                Log.i("gety", "gety"+y);
                final float diff, absDiff;
                diff = y - mLastMotionY;
                mLastMotionY = y;
                absDiff = Math.abs(diff);
                if(allScroll >= 0 && absDiff > 1){
                    allScroll += diff;

                    if(allScroll < 0){
                        allScroll = 0;
                    }else if(allScroll > maxZoom){
                        allScroll = maxZoom;
                    }
                    Log.i("allScroll","allScroll:"+allScroll);
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) zoomView.getLayoutParams();
                    lp.height = (int) (height + allScroll/2);
                    zoomView.setLayoutParams(lp);
                    if(allScroll == 0){
                        allScroll = -1;
                    }
                    return false;
                }
                if (isReadyForPullStart()) {
                    if (absDiff > 0 ) {
                        if (diff >= 1f && isReadyForPullStart()) {
                            mLastMotionY = y;
                            allScroll = 0;
                            height = zoomView.getHeight();
                            return true;
                        }
                    }
                }
                break;
            }


        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(allScroll != -1){
            Log.i("ScrollView","onTouchEvent");
            return false;
        }
        return super.onTouchEvent(ev);
    }



  
    protected boolean isReadyForPullStart() {
        return getScrollY() == 0;
    }


    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }
    public interface ScrollViewListener {

        void onScrollChanged(ZoomScrollView scrollView, int x, int y, int oldx, int oldy);

    }
}
