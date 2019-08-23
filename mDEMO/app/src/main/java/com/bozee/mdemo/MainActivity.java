package com.bozee.mdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button cleanDraw;   //清空画板
    private Button eraser;      //橡皮擦
    private Button paint;       //画笔
    private Button holdAndShow; //显示和隐藏笔记
    private Button reAction;      //撤销操作
    private mySurfaceView drawingBoard;    //画板窗口
    private ImageView background;   //背景图片

    public static int drawingBoardStatus;  //画板显示/隐藏状态



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
        holdAndShow = findViewById(R.id.bt_holdAndShow);
        drawingBoard = findViewById(R.id.sv_drawingBoard);
        background = findViewById(R.id.iv_background);
        reAction = findViewById(R.id.reAciton);

        cleanDraw.setOnClickListener(this);
        eraser.setOnClickListener(this);
        paint.setOnClickListener(this);
        holdAndShow.setOnClickListener(this);
        background.setOnClickListener(this);
        reAction.setOnClickListener(this);


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

            case R.id.bt_holdAndShow :{
                if (drawingBoard.getVisibility() == View.VISIBLE){
                    drawingBoard.setVisibility(View.INVISIBLE);
                    drawingBoardStatus = View.INVISIBLE;
                    holdAndShow.setText("显示");
                    mySurfaceView.startDraw = false;
                    Log.i("TAG_Visibility","GONE");
                }else {
                    drawingBoard.setVisibility(View.VISIBLE);
                    drawingBoardStatus = View.VISIBLE;
                    mySurfaceView.startDraw = true;
                    holdAndShow.setText("隐藏");
                    Log.i("TAG_Visibility","VISIBLE");
                }
                break;

            }

            case R.id.iv_background :{
                Toast.makeText(MainActivity.this,"this is background",Toast.LENGTH_SHORT).show();
                Log.i("TAG_","this is background");
                break;
            }

            case R.id.reAciton :{
                mySurfaceView.back();
               // mySurfaceView.MyPathList.remove(mySurfaceView.MyPathList.size()-1);
            }
        }

    }


}
