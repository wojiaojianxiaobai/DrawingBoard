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
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.constraintlayout.solver.widgets.Rectangle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class mySurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    /*SurfaceHolder 实例*/
    private static SurfaceHolder mSurfaceHolder;

    /*Canvas 画布*/
    private static Canvas mCanvas;

    /*控制子线程*/
    public static boolean startDraw;

    /*Path 路径实例*/
    public static Path mPath = new Path();
    /*Paint 画笔实例*/
    private Paint mPaint = new Paint();

    /*橡皮擦实例*/
    private Paint eraserPaint;

    /*记录画笔的列表*/
    private static List<Action> mActions = new ArrayList<>();      //操作集合
    private Action singoAction = null;  //单次操作
    private  HashMap<String,Object> myOption ;  //记录单次操作类型为画笔\橡皮檫
    private static List<HashMap> optionList = new ArrayList<HashMap>();

    public static List<Path> pathLists = new ArrayList<>();     //路径数组

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





    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float touchX = event.getX();
        float touchY = event.getY();



        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startX = touchX;
                startY = touchY;
                singoAction = new MyPath(touchX, touchY,10, Color.RED,mActions.size());     //设置画笔起始位置和属性
                break;
            case MotionEvent.ACTION_MOVE:


                switch (MainActivity.UtilSelector){

                    /*画笔*/
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
                        singoAction.move(startX, startY, (touchX + startX) / 2, (touchY + startY) / 2);
                        singoAction.draw(canvas);
                        mSurfaceHolder.unlockCanvasAndPost(canvas);

                        break;
                    }

                    /*橡皮檫*/
                    case 2 :{
                        Canvas canvas = mSurfaceHolder.lockCanvas();

                        for (int i = 0; i < mActions.size();i++){
                            int selecter = (int) optionList.get(i).get("option");
                            if (selecter == 1){     //画笔
                                mActions.get(i).draw(canvas);
                            }else if (selecter == 2){      //橡皮檫
                                mActions.get(i).eraser(canvas);
                            }

                        }
                        singoAction.move(startX, startY, (touchX + startX) / 2, (touchY + startY) / 2);
                        singoAction.eraser(canvas);
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                        break;
                    }

                    /*对象擦*/
                    case 3:{


                        /*隐藏线段主要部分，头尾不隐藏*/
                        /*香蕉检查*/            //TODO
/*                        if (MyPath.pathLists != null && MyPath.pathLists.size() > 0){
                            for (int i = 0;i < MyPath.pathLists.size() ; i ++){
                                Log.i("TAG_mActionSize:",mActions.size() + "");
                                RectF rectF = new RectF();
                                Path path = MyPath.pathLists.get(i);
                                Log.i("TAG_","path" + path);
                                if (path != null){
                                    path.computeBounds(rectF,true);
                                    if (rectF.contains(startX,startY)){
                                        Canvas erasercanvas = mSurfaceHolder.lockCanvas();
                                        erasercanvas.drawPath(path,eraserPaint);
                                        singoAction.eraser(erasercanvas);
                                        mSurfaceHolder.unlockCanvasAndPost(erasercanvas);
                                        Log.i("TAG_","ActionSize" + mActions.size());
                                        Log.i("TAG_","香蕉");
                                    }
                                }

                            }
                        }*/

                        if (MyPath.pathLists != null && MyPath.pathLists.size() > 0){
                            for (int i = 0;i < MyPath.pathLists.size() ; i ++){
                                RectF rectF = new RectF();
                                Path path = MyPath.pathLists.get(i);
                                if (path != null){
                                    path.computeBounds(rectF,true);
                                    if (rectF.contains(startX,startY)){
                                        initBorad();
                                        if (mActions != null && mActions.size() >= 1) {
                                            mActions.remove(i);
                                            optionList.remove(i);
                                            MyPath.pathLists.remove(i);
                                            Canvas canvas = mSurfaceHolder.lockCanvas();
                                            for (int j = 0; j < optionList.size();j++){
                                                int selecter = (int) optionList.get(j).get("option");
                                                if (selecter == 1){     //画笔
                                                    mActions.get(j).draw(canvas);
                                                }else if (selecter == 2){      //橡皮檫
                                                    mActions.get(j).eraser(canvas);
                                                }
                                            }
                                            mSurfaceHolder.unlockCanvasAndPost(canvas);
                                            return true;
                                        }
                                    }
                                }

                            }
                        }


                    }
                }

                startX = touchX;
                startY = touchY;


                break;
            case MotionEvent.ACTION_UP:


                 myOption = new HashMap<>();  //操作集合
                myOption.put("option",MainActivity.UtilSelector);
                if (MainActivity.UtilSelector != 3){
                    optionList.add(myOption);
                    mActions.add(singoAction);
                }

                Log.i("TAG_","options:" + optionList.toString());
                singoAction = null;

                break;
        }
        return true;
    }

    /*重置画布*/
    public static void reset(){
        initBorad();    //白板初始化
        mActions.clear();
        MyPath.pathLists.clear();
        optionList.clear();
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
            mSurfaceHolder.unlockCanvasAndPost(canvas);
            return true;
        }
        return false;
    }




}