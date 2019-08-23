package com.bozee.mdemo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public abstract class Action {

    public int color;
    Action(){
        color = Color.RED;
    }

    Action(int color){
        this.color = color;
    }
    public abstract void draw(Canvas canvas);
    public abstract void eraser(Canvas canvas);
    public abstract void move(float mx, float my,float x,float y);


}

/*画曲线*/
class MyPath extends Action{
    Path path;  //路径
    int size;   //画笔大小

    MyPath(float x, float y, int size, int color) {
        super(color);
        path = new Path();
        this.size = size;
        path.moveTo(x,y);
        //path.lineTo(x, y);
    }

    /*画笔*/
    public void draw(Canvas canvas){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(color);
        paint.setStrokeWidth(size);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawPath(path,paint);
    }


    /*橡皮檫*/
    public void eraser(Canvas canvas){



        Paint eraserPaint = new Paint();
        eraserPaint.setAlpha(0);        //设置透明度
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));    //PorterDuff.Mode.DST_IN:只在源图像和目标图像相交的地方绘制目标图像
        eraserPaint.setAntiAlias(true);
        eraserPaint.setStyle(Paint.Style.STROKE);
        eraserPaint.setStrokeJoin(Paint.Join.ROUND);        //拐角属性： ROUND：圆 MITER:尖角
        eraserPaint.setStrokeWidth(20);
        canvas.drawPath(path,eraserPaint);
    }

    @Override
    public void move(float mx, float my,float x,float y) {
        //path.lineTo(mx, my);
        path.quadTo(mx, my, x,y);
    }


}

