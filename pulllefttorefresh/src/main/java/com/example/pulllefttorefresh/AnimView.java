package com.example.pulllefttorefresh;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by yzl on 2016/6/2.
 */
public class AnimView extends View {

    boolean isBezierBackDone = false;

    private int mWidth;
    private int mHeight;
    private int MAX_RECT_WIDTH;//矩形的最大宽度
    private int PULL_DELTA;

    private long mStart;
    private long mStop;
    private int mBezierDistance;//贝塞尔曲线控制点离竖直的起点和终点线的长度

    private long bezierBackDur;

    private Paint animPaint;//画贝塞尔曲线和矩形的画笔
    private Path mPath;

    private AnimatorStatus mAniStatus = AnimatorStatus.PULL_RECT;

    enum AnimatorStatus {
        PULL_RECT,//仅显示矩形
        DRAG_BEZI,//显示矩形和贝塞尔曲线
        RELEASE,//释放
    }

    public AnimView(Context context) {
        this(context, null, 0);
    }

    public AnimView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        //pd转px
        MAX_RECT_WIDTH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
        PULL_DELTA = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, context.getResources().getDisplayMetrics());

        mPath = new Path();
        animPaint = new Paint();
        animPaint.setAntiAlias(true);
        animPaint.setStyle(Paint.Style.FILL);
    }

    private static final String TAG = "AnimView";

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (width > PULL_DELTA + MAX_RECT_WIDTH) {//宽度最多100
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(PULL_DELTA + MAX_RECT_WIDTH, MeasureSpec.getMode(widthMeasureSpec));
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mWidth = getWidth();
            mHeight = getHeight();

            if (mWidth < MAX_RECT_WIDTH) {
                mAniStatus = AnimatorStatus.PULL_RECT;
            }

            switch (mAniStatus) {
                case PULL_RECT:
                    if (mWidth >= MAX_RECT_WIDTH) {
                        mAniStatus = AnimatorStatus.DRAG_BEZI;
                    }
                    break;
            }

        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mAniStatus) {
            case PULL_RECT:
                canvas.drawRect(0, 0, mWidth, mHeight, animPaint);
                break;
            case DRAG_BEZI:
                drawDrag(canvas);
                break;
            case RELEASE:
                drawBack(canvas, getBezierDackDistance());
                break;
        }
    }

    private void drawDrag(Canvas canvas) {
        canvas.drawRect(mWidth - MAX_RECT_WIDTH, 0, mWidth, mHeight, animPaint);

        mPath.reset();
        mPath.moveTo(mWidth - MAX_RECT_WIDTH, 0);
        mPath.quadTo(0, mHeight / 2, mWidth - MAX_RECT_WIDTH, mHeight);
        canvas.drawPath(mPath, animPaint);
    }

    private void drawBack(Canvas canvas, int delta) {
        mPath.reset();
        mPath.moveTo(mWidth, 0);
        mPath.lineTo(mWidth - MAX_RECT_WIDTH, 0);
        mPath.quadTo(delta, mHeight / 2, mWidth - MAX_RECT_WIDTH, mHeight);
        mPath.lineTo(mWidth, mHeight);
        canvas.drawPath(mPath, animPaint);

        invalidate();

        if (bezierBackRatio == 1) {
            isBezierBackDone = true;
        }

        if (isBezierBackDone && mWidth <= MAX_RECT_WIDTH) {
            drawFooterBack(canvas);
        }
    }

    private void drawFooterBack(Canvas canvas) {
        canvas.drawRect(0, 0, mWidth, mHeight, animPaint);
    }

    public void releaseDrag() {
        mAniStatus = AnimatorStatus.RELEASE;
        mStart = System.currentTimeMillis();
        mStop = mStart + bezierBackDur;
        mBezierDistance = mWidth - MAX_RECT_WIDTH;
        isBezierBackDone = false;
        requestLayout();
    }

    public void setBezierBackDur(long bezierBackDur) {
        this.bezierBackDur = bezierBackDur;
    }

    private float bezierBackRatio;//贝塞尔曲线后缩的比例

    /**
     * 获取贝塞尔曲线后缩的长度
     * @return
     */
    private int getBezierDackDistance() {
        bezierBackRatio = getBezierBackRatio();
        return (int) (mBezierDistance * bezierBackRatio);
    }


    private float getBezierBackRatio() {
        if (System.currentTimeMillis() >= mStop) {
            return 1;
        }
        float ratio = (System.currentTimeMillis() - mStart) / (float) bezierBackDur;
        return Math.min(1, ratio);
    }

    public void setBgColor(int color) {
        animPaint.setColor(color);
    }
}
