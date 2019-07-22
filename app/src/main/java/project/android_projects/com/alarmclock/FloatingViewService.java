package project.android_projects.com.alarmclock;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class FloatingViewService extends Service implements View.OnClickListener {
    private WindowManager winMgr;
    private View mFloatingView;
    private View collapsedView;
    private View expandedView;

    private View closeViewLayout;
    private ImageView closeBtnForeground, closeBtnBackground;

    private int closeButtonPos;
    private int screenWidth,screenHeight;
    private int mWidth;

    public FloatingViewService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        closeViewLayout = LayoutInflater.from(this).inflate(R.layout.remove_floating_widget_layout, null);

       /* closeBtnForeground = closeViewLayout.findViewById(R.id.close_floating_view_btn_foreground);
        closeBtnBackground = closeBtnBackground.findViewById(R.id.close_floating_view_btn_background);*/

        addWidgetView();//add view and drag & move widget
    }

    private void addWidgetView() {
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.floating_widget_layout, null);

        //setting the layout parameters
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.x = 0;
        params.y = 100;
        params.gravity = Gravity.TOP | Gravity.LEFT;

        winMgr = (WindowManager) getSystemService(WINDOW_SERVICE);
        winMgr.addView(mFloatingView, params);

        final Point sizePoint=new Point();
        Display display = winMgr.getDefaultDisplay();
        display.getSize(sizePoint);
        screenWidth = sizePoint.x; screenHeight = sizePoint.y;

        //getting the collapsed and expanded view from the floating view
        collapsedView = mFloatingView.findViewById(R.id.layout_collapsed);
        //expandedView = mFloatingView.findViewById(R.id.layout_expanded);

        //adding click listener to close button and expanded view
        mFloatingView.findViewById(R.id.floating_widget_root_container).setOnClickListener(this);
        collapsedView.setOnClickListener(this);

        final RelativeLayout flotingWidgetLayout = (RelativeLayout)mFloatingView.findViewById(R.id.floating_widget_root_container);
        ViewTreeObserver viewTreeObserver = flotingWidgetLayout.getViewTreeObserver();

        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                flotingWidgetLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = flotingWidgetLayout.getMeasuredWidth();

                //To get the accurate middle of the screen we subtract the width of the floating widget.
                mWidth = sizePoint.x - width;
            }
        });

        //On touch listener to drag and move the widget
        mFloatingView.findViewById(R.id.layout_collapsed).setOnTouchListener(new View.OnTouchListener() {
            private int initialX, initialY;
            private float initialTouchX, initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //When pressed
                        //closeViewLayout.setVisibility(View.VISIBLE);
                       // closeBtnForeground.setVisibility(View.VISIBLE);
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        //When released
                       // collapsedView.setVisibility(View.VISIBLE);
                       // closeViewLayout.setVisibility(View.GONE);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Move the widget around the screen with fingers
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        if(params.y > screenHeight * 0.8){
                            mFloatingView.setVisibility(View.GONE);
                            //closeBtnBackground.setVisibility(View.VISIBLE);
                            stopSelf();
                        }

                       /* if(params.x < screenWidth / 2){
                            collapsedView.setVisibility(View.VISIBLE);
                            closeViewLayout.setVisibility(View.VISIBLE);
                            closeBtnForeground.setVisibility(View.VISIBLE);
                        }*/

                        winMgr.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
        // dragMoveWidget(params,winMgr);

    }

    private void dragMoveWidget(final WindowManager.LayoutParams params, WindowManager windowManager) {

        //On touch listener to drag and move the widget
        mFloatingView.findViewById(R.id.floating_widget_root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX, initialY;
            private float initialTouchX, initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //When pressed
                        closeViewLayout.setVisibility(View.VISIBLE);
                        closeBtnForeground.setVisibility(View.VISIBLE);
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        //When released
                        collapsedView.setVisibility(View.VISIBLE);
                        closeViewLayout.setVisibility(View.GONE);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Move the widget around the screen with fingers
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        if(params.y > screenHeight * 0.8){
                            mFloatingView.setVisibility(View.GONE);
                            closeBtnBackground.setVisibility(View.VISIBLE);
                            stopSelf();
                        }

                        if(params.x < screenWidth / 2){
                            collapsedView.setVisibility(View.VISIBLE);
                            closeViewLayout.setVisibility(View.VISIBLE);
                            closeBtnForeground.setVisibility(View.VISIBLE);
                        }

                        winMgr.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.layout_expanded:
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
                break;*/
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) winMgr.removeView(mFloatingView);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
