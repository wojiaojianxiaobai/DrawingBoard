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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class mySurfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable {

    /*SurfaceHolder 实例*/
    private static SurfaceHolder mSurfaceHolder;

    /*Canvas 画布*/
    private static Canvas mCanvas;

    /*控制子线程*/
    public static boolean startDraw;

    /*Path 路径实例*/
    public static Path mPath = new Path();

    private static Path lastPath = new Path();
    //private static

    /*Paint 画笔实例*/
    private Paint mPaint = new Paint();

    /*橡皮擦实例*/
    private Paint eraserPaint;

    /*记录画笔的列表*/
    private static List<Action> mActions;      //操作集合
    private Action singoAction = null;  //单次操作
    private  HashMap<String,Object> myOption ;  //记录单次操作为画笔\橡皮檫
    private static List<HashMap> optionList = new ArrayList();


    public static List<HashMap> MyPathList = new ArrayList<>();   //路线集合



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
        mActions = new ArrayList<Action>();
        //new Thread(this).start();

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


        setZOrderOnTop(true);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);//设置画布  背景透明
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

                if (startDraw){

                    mCanvas.drawColor(Color.WHITE);
                    if (MyPathList != null && MyPathList.size()>= 1){
                        for (int i = 0; i < MyPathList.size(); i++){
                            mCanvas.drawPath((Path) MyPathList.get(i).get("path"),mPaint);
                        }
                        //mSurfaceHolder.unlockCanvasAndPost(canvas1);

                    }

                    //mCanvas.drawPath(mPath,mPaint);
                }

                //Log.i("TAG_status:",MainActivity.UtilSelector+"");
                break;
            }
            case 2 :{
                if (startDraw){
                    mCanvas.drawPath(mPath,eraserPaint);
                }

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
        float touchX = event.getX();
        float touchY = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startX = touchX;
                startY = touchY;
                //mPath.reset();
                //mPath.moveTo(touchX, touchY);//将 Path 起始坐标设为手指按下屏幕的坐标
                singoAction = new MyPath(touchX, touchY,10, Color.RED);     //设置画笔起始位置和属性
                break;
            case MotionEvent.ACTION_MOVE:


                switch (MainActivity.UtilSelector){     //画笔
                    case 1 :{
                        Canvas canvas = mSurfaceHolder.lockCanvas();



                        for (int i = 0; i < mActions.size();i++){
                            int selecter = (int) optionList.get(i).get("option");
                            if (selecter == 1){     //画笔
                                mActions.get(i).draw(canvas);
                            }else if (selecter == 2){      //橡皮檫
                                mActions.get(i).eraser(canvas);
                            }
                        }
/*                        for (Action a:mActions){
                            a.draw(canvas);
                        }*/
                        singoAction.move(startX, startY, (touchX + startX) / 2, (touchY + startY) / 2);
                        singoAction.draw(canvas);
                        mSurfaceHolder.unlockCanvasAndPost(canvas);

                        break;
                    }
                    case 2 :{       //橡皮檫
                        Canvas canvas = mSurfaceHolder.lockCanvas();

                        for (int i = 0; i < mActions.size();i++){
                            int selecter = (int) optionList.get(i).get("option");
                            if (selecter == 1){     //画笔
                                mActions.get(i).draw(canvas);
                            }else if (selecter == 2){      //橡皮檫
                                mActions.get(i).eraser(canvas);
                            }

                        }
/*                        for (Action a:mActions){
                            a.eraser(canvas);
                        }*/
                        singoAction.move(startX, startY, (touchX + startX) / 2, (touchY + startY) / 2);
                        singoAction.eraser(canvas);
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                        break;
                    }
                }
                startX = touchX;
                startY = touchY;
                break;
            case MotionEvent.ACTION_UP:
                mActions.add(singoAction);
                 myOption = new HashMap<>();  //操作集合
                myOption.put("option",MainActivity.UtilSelector);
                optionList.add(myOption);
                Log.i("TAG_","options:" + optionList.toString());
                singoAction = null;

                break;
        }
        return true;
    }

    /*重置画布*/
    public static void reset(){
        initBorad();    //白板初始化
    }

    /*撤销操作*/
    public static boolean back() {
        initBorad();
        if (mActions != null && mActions.size() > 0) {
            mActions.remove(mActions.size() - 1);   //删除最后一步
            optionList.remove(optionList.size() - 1);
            Canvas canvas = mSurfaceHolder.lockCanvas();
            //canvas.drawColor(Color.WHITE);

            for (int i = 0; i < mActions.size();i++){
                int selecter = (int) optionList.get(i).get("option");
                if (selecter == 1){     //画笔
                    mActions.get(i).draw(canvas);
                }else if (selecter == 2){      //橡皮檫
                    mActions.get(i).eraser(canvas);
                }

            }
/*            for (Action a : mActions) {
                a.draw(canvas);
            }*/
            mSurfaceHolder.unlockCanvasAndPost(canvas);
            return true;
        }
        return false;
    }


}