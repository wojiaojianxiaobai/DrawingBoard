package com.bozee.mdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class mySurfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable {

    /*SurfaceHolder 实例*/
    private static SurfaceHolder mSurfaceHolder;

    /*Canvas 画布*/
    private static Canvas mCanvas;

    /*控制子线程*/
    private boolean startDraw;

    /*Path 路径实例*/
    public static Path mPath = new Path();

    private static Path lastPath = new Path();
    //private static

    /*Paint 画笔实例*/
    private Paint mPaint = new Paint();

    /*橡皮擦实例*/
    private Paint eraserPaint;



    float startX ;
    float startY ;

    public mySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }


    /*SurfaceHolder.Callback*/
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        initBorad();    //白板初始化
        startDraw = true;
        new Thread(this).start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        startDraw = false;

    }

    private static void initBorad(){
        /*白板初始化*/
        mCanvas = mSurfaceHolder.lockCanvas();  //实际执行SurfaceHolder(dirty),返回Canvas用于dirty矩形区域绘制，
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        //第一次绘制
        if (mCanvas != null){
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
        //第二次绘制
        Canvas mCanvas2 = mSurfaceHolder.lockCanvas();  //实际执行SurfaceHolder(dirty),返回Canvas用于dirty矩形区域绘制，
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        if (mCanvas != null){
            mSurfaceHolder.unlockCanvasAndPost(mCanvas2);
        }

        Canvas canvas = mSurfaceHolder.lockCanvas(new Rect(0, 0, 0, 0));

        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }


    /*Runnable*/
    @Override
    public void run() {
        while (startDraw){

            while (MainActivity.drawingBoardStatus == View.VISIBLE){
                /*绘制图案*/
                draw();
            }

        }
    }

    /*初始化控件*/
    private void initView(){
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);


        /*画笔初始化*/
        mPaint.setStyle(Paint.Style.STROKE);        //设置画笔样式，取值有:
        //Paint.Style.FILL :填充内部
        //Paint.Style.FILL_AND_STROKE ：填充内部和描边
        //Paint.Style.STROKE ：仅描边
        mPaint.setStrokeWidth(6);       //画笔宽度
        mPaint.setColor(Color.RED);   //画笔颜色
        mPaint.setAntiAlias(true);      //抗锯齿

        /*橡皮檫实例化*/
        eraserPaint = new Paint();
        eraserPaint.setAlpha(0);        //设置透明度
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));    //PorterDuff.Mode.DST_IN:只在源图像和目标图像相交的地方绘制目标图像
        eraserPaint.setAntiAlias(true);
        eraserPaint.setStyle(Paint.Style.STROKE);
        eraserPaint.setStrokeJoin(Paint.Join.ROUND);        //拐角属性： ROUND：圆 MITER:尖角
        eraserPaint.setStrokeWidth(20);
        eraserPaint.setColor(Color.WHITE);


        setZOrderOnTop(true);//设置画布  背景透明
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        setFocusable(true); //可获取焦点
        setFocusableInTouchMode(true);

        this.setKeepScreenOn(true); //设置长亮

    }

    private void draw(){


        /*画笔初始化*/
        mCanvas = mSurfaceHolder.lockCanvas();  //实际执行SurfaceHolder(dirty),返回Canvas用于dirty矩形区域绘制，
        // mSurfaceLock是一个ReentrantLock，是一个可重入互斥锁

        switch (MainActivity.UtilSelector){

            case 1 :{
                mCanvas.drawPath(mPath,mPaint);
                //Log.i("TAG_status:",MainActivity.UtilSelector+"");
                break;
            }
            case 2 :{
                mCanvas.drawPath(mPath,eraserPaint);
                //Log.i("TAG_status:",MainActivity.UtilSelector+"");
                break;
            }
        }
        /*对画布内容进行提交*/
        if (mCanvas != null){
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startX = x;
                startY = y;
                //mPath.reset();
                mPath.moveTo(x, y);//将 Path 起始坐标设为手指按下屏幕的坐标
                break;
            case MotionEvent.ACTION_MOVE:

                switch (MainActivity.UtilSelector){

                    case 1 :{
                        mPath.quadTo(startX, startY, (x + startX) / 2, (y + startY) / 2);
                        Log.i("TAG_status:",MainActivity.UtilSelector+"");
                        break;
                    }
                    case 2 :{
                        mPath.quadTo(startX, startY, (x + startX) / 2, (y + startY) / 2);
                        Log.i("TAG_status:",MainActivity.UtilSelector+"");
                        break;
                    }
                }

                //mPath.quadTo(startX, startY, (x + startX) / 2, (y + startY) / 2);
                //绘制贝塞尔曲线光滑的曲线，如果此处使用 lineTo 方法滑出的曲线会有折角
                startX = x;
                startY = y;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    /*重置画布*/
    public static void reset(){
        initBorad();    //白板初始化
    }



}