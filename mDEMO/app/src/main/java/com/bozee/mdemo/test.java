package com.bozee.mdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class test extends View {

    private Paint mPaint;       //画笔
    private Path mPath;         //路径
    private float mLastX;       //上一个点的X坐标
    private float mLastY;       //上一个点的Y坐标
    private Bitmap mBufferBitmap;
    private Canvas mCanvas;     //画布

    private static final int MAX_CACHE_STEP = 20;
    private List<DrawingInfo> mDrawingList;
    private boolean mCanEraser;
    public enum Mode {
        DRAW
    }

    private Mode mMode = Mode.DRAW;

    public test(Context context) {
        this(context,null);
    }

    public test(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDrawingCacheEnabled(true);
        mPaint = new Paint();       //实例化画笔
        //mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);     //设置画笔样式，取值有:
                                                 //Paint.Style.FILL :填充内部
                                                 //Paint.Style.FILL_AND_STROKE ：填充内部和描边
                                                 //Paint.Style.STROKE ：仅描边
       // mPaint.setFilterBitmap(true);
        //mPaint.setStrokeJoin(Paint.Join.ROUND);
        //mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(20);      //画笔大小
        mPaint.setColor(Color.BLACK);   //画笔颜色
    }


    private abstract static class DrawingInfo {
        Paint paint;
        abstract void draw(Canvas canvas);
    }

    private static class PathDrawingInfo extends DrawingInfo{

        Path path;

        @Override
        void draw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }
    }

    private void saveDrawingPath(){
        if (mDrawingList == null) {
            mDrawingList = new ArrayList<>(MAX_CACHE_STEP);
        } else if (mDrawingList.size() == MAX_CACHE_STEP) {
            mDrawingList.remove(0);
        }
        Path cachePath = new Path(mPath);
        Paint cachePaint = new Paint(mPaint);
        PathDrawingInfo info = new PathDrawingInfo();
        info.path = cachePath;
        info.paint = cachePaint;
        mDrawingList.add(info);
        mCanEraser = true;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBufferBitmap != null) {
            canvas.drawBitmap(mBufferBitmap, 0, 0, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction() & MotionEvent.ACTION_MASK;
        final float x = event.getX();
        final float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                if (mPath == null) {
                    mPath = new Path();
                }
                mPath.moveTo(x,y);
                break;
            case MotionEvent.ACTION_MOVE:
                //这里终点设为两点的中心点的目的在于使绘制的曲线更平滑，如果终点直接设置为x,y，效果和lineto是一样的,实际是折线效果
                mPath.quadTo(mLastX, mLastY, (x + mLastX) / 2, (y + mLastY) / 2);
                if (mBufferBitmap == null) {
                    mBufferBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                    mCanvas = new Canvas(mBufferBitmap);
                }

                mCanvas.drawPath(mPath,mPaint);
                invalidate();
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                if (mMode == Mode.DRAW || mCanEraser) {
                    saveDrawingPath();
                }
                mPath.reset();
                break;
        }
        return true;
    }
}