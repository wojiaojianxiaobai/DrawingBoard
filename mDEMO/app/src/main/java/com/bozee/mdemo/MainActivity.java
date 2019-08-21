package com.bozee.mdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button cleanDraw;   //清空画板
    private Button eraser;      //橡皮擦
    private Button paint;       //画笔

    /*工具选择器*/
    private static int paintStatus = 1;     //选择画笔
    private static int eraserStatus = 2;   //选择橡皮擦
    public static int UtilSelector = paintStatus;  //默认选择画笔

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cleanDraw = findViewById(R.id.bt_cleanDraw);
        eraser = findViewById(R.id.bt_eraser);
        paint = findViewById(R.id.bt_paint);

        cleanDraw.setOnClickListener(this);
        eraser.setOnClickListener(this);
        paint.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {


        switch (view.getId()){

            case R.id.bt_cleanDraw :{
                mySurfaceView.mPath.reset();
                mySurfaceView.reset();
                break;
            }

            case R.id.bt_paint :{
                mySurfaceView.mPath.reset();
                UtilSelector = paintStatus;
                break;
            }

            case R.id.bt_eraser :{
                mySurfaceView.mPath.reset();
                UtilSelector = eraserStatus;
                break;
            }
        }

    }
}
