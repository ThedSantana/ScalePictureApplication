package com.marco.scalepictureapplication;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ScalePictureActivity extends AppCompatActivity {
    private ImageView img;
    private int       actionbarHeight, notifiHeight; // ActionBar高度和状态栏高度
    private View rootView;
    private float maxScaleRate = 5f, minScaleRate = 0.2f;
    private boolean canMove = false;
    private float diffX, diffY;//移动
    private float diff1X, diff1Y, diff2X, diff2Y;//缩放
    private float now1X, now2X, now1Y, now2Y;
    private float rateAfter = 1.0f; //存储图片最后缩放比例

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootView = getLayoutInflater().inflate(R.layout.activity_main, null);
        img = (ImageView) findViewById(R.id.scalePicture);
        rootView.post(new Runnable() {
            @Override
            public void run() {
                if (getSupportActionBar() != null) {
                    actionbarHeight = getSupportActionBar().getHeight();
                }

                Rect frame = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                notifiHeight = frame.top;
            }
        });
    }

    /**
     * 触摸事件处理
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                doActionDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                doActionMove(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                doActionCancel(event);
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 触摸按下
     * 判断触摸焦点是否在图片上
     *
     * @param event
     */
    public void doActionDown(MotionEvent event) {
        if (event.getPointerCount() == 1) {
            //移动图片
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) img.getLayoutParams();
            if (event.getX() > params.leftMargin &&
                    event.getX() < params.leftMargin + img.getWidth() &&
                    event.getY() - actionbarHeight - notifiHeight > params.topMargin &&
                    event.getY() - actionbarHeight - notifiHeight < params.topMargin + img.getHeight()
                    ) {
                canMove = true;
            } else {
                canMove = false;
            }
            diffX = event.getX() - params.leftMargin;
            diffY = event.getY() - params.topMargin;
        }
    }

    /**
     * 初始化图片缩放数据
     * @param event
     */
    private void initScaleData(MotionEvent event) {
        if (diff1X == 0 || diff1Y == 0 || diff2X == 0 || diff2Y == 0) {
            diff1X = event.getX(0);
            diff2X = event.getX(1);
            diff1Y = event.getY(0);
            diff2Y = event.getY(1);
        }

        now1X = event.getX(0);
        now2X = event.getX(1);
        now1Y = event.getY(0);
        now2Y = event.getY(1);
    }

    /**
     * 触摸移动
     *
     * @param event
     */
    public void doActionMove(MotionEvent event) {
        if (event.getPointerCount()> 1) {
            //缩放图片
            initScaleData(event);
            double lineDiff = Math.sqrt((diff2X - diff1X) * (diff2X - diff1X) + (diff2Y - diff1Y) * (diff2Y - diff1Y));
            double lineNow = Math.sqrt((now2X - now1X) * (now2X - now1X) + (now2Y - now1Y) * (now2Y - now1Y));

            if (lineDiff != 0) {
                float rate = (float) (lineNow / lineDiff) * rateAfter;
                System.out.println("eventMoveRate--->" + rate);
                scalePicture(rate);
            }

        } else {
            //移动图片
            if (canMove) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) img.getLayoutParams();
                movePicture(
                        params, (int) (event.getX() - diffX), (int) (event.getY() - diffY)
                           );
            }
        }
    }

    /**
     * 释放焦点
     *
     * @param event
     */
    public void doActionCancel(MotionEvent event) {
        rateAfter = img.getScaleX();
    }

    /**
     * 移动图片
     */
    public void movePicture(RelativeLayout.LayoutParams params, int width, int height) {
        params.leftMargin = width;
        params.topMargin = height;
        img.setLayoutParams(params);
    }

    /**
     * 放大图片
     */
    public void scalePicture(float rate) {
        if (rate > maxScaleRate) {
            rate = maxScaleRate;
        } else if (rate < minScaleRate) {
            rate = minScaleRate;
        }
        img.setScaleX(rate);
        img.setScaleY(rate);
    }
}
