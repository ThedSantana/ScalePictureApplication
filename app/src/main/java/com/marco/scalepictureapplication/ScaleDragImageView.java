package com.marco.scalepictureapplication;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * User: KdMobiB
 * Date: 2016/6/30
 * Time: 17:12
 */
public class ScaleDragImageView extends ImageView {
    private static       int mode      = 0;// 初始状态
    private static final int MODE_DRAG = 1;
    private static final int MODE_ZOOM = 2;

    /**
     * 开始结束坐标
     */
    private PointF startPoint = new PointF();
    private PointF endPoint   = new PointF();
    private PointF midPoint   = new PointF();//中间点

    /**
     * 两手指开始结束距离
     */
    private float startDis;
    private float endDis;
    /**
     * 图片的变换矩阵
     */
    private Matrix matrix        = new Matrix();
    private Matrix currentMatrix = new Matrix();

    public ScaleDragImageView(Context context) {
        super(context);
        initView();
    }

    public ScaleDragImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ScaleDragImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setScaleType(ScaleType.MATRIX);
        setImageResource(R.mipmap.test);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()& MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                onActionDown(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                onActionPointerDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                onActionMove(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                onActionUp(event);
                break;
        }

        setImageMatrix(matrix);
        return true;
    }

    /**
     * 手指按下效果
     *
     * @param event
     */
    public void onActionDown(MotionEvent event) {
        mode = MODE_DRAG;
        // 记录ImageView当前的移动位置
        currentMatrix.set(getImageMatrix());
        startPoint.set(event.getX(), event.getY());
    }

    /**
     * 新手指按下监听事件
     *
     * @param event
     */
    public void onActionPointerDown(MotionEvent event) {
        mode = MODE_ZOOM;
        /** 计算两个手指间的距离 */
        startDis = distance(event);
        /** 计算两个手指间的中间点 */
        if (startDis > 10f) { // 两个手指并拢在一起的时候像素大于10
            midPoint = mid(event);
            //记录当前ImageView的缩放倍数
            currentMatrix.set(getImageMatrix());
        }
    }

    /**
     * 滑动手指
     *
     * @param event
     */
    public void onActionMove(MotionEvent event) {
        // 拖拉图片
        if (mode == MODE_DRAG) {
            float dx = event.getX() - startPoint.x; // 得到x轴的移动距离
            float dy = event.getY() - startPoint.y; // 得到x轴的移动距离
            // 在没有移动之前的位置上进行移动
            matrix.set(currentMatrix);
            matrix.postTranslate(dx, dy);
        }
        // 放大缩小图片
        else if (mode == MODE_ZOOM) {
            float endDis = distance(event);// 结束距离
            if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10
                float scale = endDis / startDis;// 得到缩放倍数
                matrix.set(currentMatrix);
                matrix.postScale(scale, scale, midPoint.x, midPoint.y);
            }
        }
    }

    /**
     * 触摸事件结束
     *
     * @param event
     */
    public void onActionUp(MotionEvent event) {
        mode = 0;
    }

    /**
     * 计算两个手指间的距离
     */
    private float distance(MotionEvent event) {
        try {
            /** 使用勾股定理返回两点之间的距离 */
            float dx = event.getX(1) - event.getX(0);
            float dy = event.getY(1) - event.getY(0);
            return (float) Math.sqrt(dx * dx + dy * dy);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        return 1.0f;
    }

    /**
     * 计算两个手指间的中间点
     */
    private PointF mid(MotionEvent event) {
        float midX = (event.getX(1) + event.getX(0)) / 2;
        float midY = (event.getY(1) + event.getY(0)) / 2;
        return new PointF(midX, midY);
    }
}
