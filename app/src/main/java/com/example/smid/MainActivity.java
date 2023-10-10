package com.example.smid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    protected ImageView img,img1,img2,img3,img4,img5,img6,img7,img8,img9,
            img_recyle,imgZoomOut,imgZoomIn,thief_img;
    protected EditText time_CounDown;
    protected RadioGroup rdoG_mode;
    protected Button button_Add;
    protected MediaPlayer player;
    protected CountDownTimer timer;
    protected RelativeLayout relativeLayout;
    protected View.DragShadowBuilder shadowBuilder;
    protected int countTurn,img_location=0,thief_location=-1;
    protected float locationDefaultX,locationDefaultY,scaleX,scaleY;
    //Khởi tạo các biến của UI
    protected void InitUI(){
        //FindID
        relativeLayout = findViewById(R.id.container);
        time_CounDown = (EditText) findViewById(R.id.editText_timeCountDown);
        img=(ImageView) findViewById(R.id.imageViewMain);
        img1=(ImageView)findViewById(R.id.imageView1);
        img2=(ImageView)findViewById(R.id.imageView2);
        img3=(ImageView)findViewById(R.id.imageView3);
        img4=(ImageView)findViewById(R.id.imageView4);
        img5=(ImageView)findViewById(R.id.imageView5);
        img6=(ImageView)findViewById(R.id.imageView6);
        img7=(ImageView)findViewById(R.id.imageView7);
        img8=(ImageView)findViewById(R.id.imageView8);
        img9=(ImageView)findViewById(R.id.imageView9);
        imgZoomOut=(ImageView)findViewById(R.id.imageViewZoomOut);
        imgZoomIn=(ImageView)findViewById(R.id.imageViewZoomIn);
        img_recyle=(ImageView) findViewById(R.id.imageViewRe);
        thief_img=(ImageView) findViewById(R.id.imageViewThief);
        rdoG_mode=(RadioGroup)findViewById(R.id.radioGroup_mode);
        button_Add=(Button) findViewById(R.id.buttonAdd);
        //Logic
        countTurn=0;
        time_CounDown.setText(String.valueOf(SettingUI.timeCountdownDefault));
    }
    //Lấy hình ảnh từ file đã chọn sử dụng trong button_Add
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Nếu kết quả là thành công
        if (resultCode == RESULT_OK) {
            if (data ==null) {
                return;
            }
            // Lấy URI của tệp ảnh
            Uri uri = data.getData();
            // Truy cập tệp ảnh
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                // Hiển thị tệp ảnh
                img.setImageBitmap(bitmap);
                //Thông báo kết quả
                if(button_Add.getText().equals("Update"))
                {
                    Toast.makeText(this, "Update Success", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "Add Success", Toast.LENGTH_SHORT).show();
                }
                button_Add.setText("Update");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Khởi tạo UI
        InitUI();
        img.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN)
                {
                    //Lưu vị trí mặc định của img trong lần chạm đầu tiên
                    if(countTurn==0)
                    {
                        locationDefaultX=img.getX();
                        locationDefaultY=img.getY();
                        gamePlay();
                    }
                    else if(countTurn==-1){
                        timer.onFinish();
                    }

                    countTurn++;//Lưu số lần chạm
                    //Khởi tạo Bóng và bắt đầu kéo
                    ClipData data = ClipData.newPlainText("" , "");
                    shadowBuilder= new View.DragShadowBuilder(v);
                    v.startDragAndDrop(data , shadowBuilder , v , 0);
                }
                return false;
            }

        });
        relativeLayout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if(event.getAction()==DragEvent.ACTION_DRAG_STARTED)
                {
                    Log.d("msg" , "Action is DragEvent.ACTION_DRAG_STARTED");
                    return true;
                }
                else if (event.getAction()==DragEvent.ACTION_DRAG_LOCATION) {
                    img.setX(event.getX()-110);
                    img.setY(event.getY()-40);
                    setBackGround(event);//Hàm xử lý hiệu ứng khi Drag
                    Log.d("msg" , "Action is DragEvent.ACTION_DRAG_LOCATION");
                    return true;
                }
                else if (event.getAction()==DragEvent.ACTION_DROP) {
                    if(InTrue(event,img_recyle))//Kiểm tra img có nằm trong vùng thả của img_recycle
                    {
                        img.setVisibility(View.GONE);//Ẩn img
                        img.setScaleX(SettingUI.minScaleX);
                        img.setScaleY(SettingUI.minScaleY);
                        countTurn=0;
                        timer.cancel();
                        timer.onFinish();
                        //Thông báo đã xóa
                        Toast.makeText(MainActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //Cập nhật vị trí img tại điểm drop
                        img.setX(event.getX()-100);
                        img.setY(event.getY()-40);
                        Log.d("msg" , "Action is DragEvent.ACTION_DROP");
                    }
                    return true;
                }
                else if (event.getAction()==DragEvent.ACTION_DRAG_ENTERED) {
                    Log.d("msg" , "Action is DragEvent.ACTION_DRAG_ENTERED");
                    return true;
                }
                else if (event.getAction()==DragEvent.ACTION_DRAG_EXITED) {
                    Log.d("","Action is DragEvent.ACTION_DRAG_EXITED");
                    return true;
                }
                else if (event.getAction()==DragEvent.ACTION_DRAG_ENDED) {
                    if(winCheck())
                    {
                        Log.d("TAG", "You Win");
                        timer.cancel();
                        countTurn=-1;
                        if(player!=null)
                        {
                            player.release();
                            player=null;
                        }
                        player = MediaPlayer.create(getApplication(),R.raw.win);
                        player.start();
                    }

                    Log.d("msg" , "Action is DragEvent.ACTION_DRAG_ENDED");
                    return true;
                }
                return false;
            }
        });
        button_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khởi tạo một đối tượng Intent mới với loại ACTION_GET_CONTENT
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //Nếu không tồn tại biến img thì cho hiển thị và set vị trí ban đầu
                if(img.getVisibility()==View.GONE){
                    img.setX(locationDefaultX);
                    img.setY(locationDefaultY);
                    img.setVisibility(View.VISIBLE);
                }

                // Đặt các thuộc tính MIME_TYPE và EXTRA_ALLOW_MULTIPLE của đối tượng Intent
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Khởi chạy hoạt động Intent
                startActivityForResult(intent,MODE_APPEND);
            }
        });

    }
    protected void gamePlay(){
        if(player==null)
        {
            player = MediaPlayer.create(getApplication(),R.raw.impossible);
            player.start();
            timer=new CountDownTimer(Integer.parseInt(time_CounDown.getText().toString())*1000,gameMode()) {
                @Override
                public void onTick(long millisUntilFinished) {
                    time_CounDown.setText(millisUntilFinished/1000+"");
                    thiefBorn();
                }
                @Override
                public void onFinish() {
                    time_CounDown.setText(String.valueOf(SettingUI.timeCountdownDefault));
                    if(winCheck()==false)
                    {
                        Log.d("TAG", "You Lose");
                        timer.cancel();
                        countTurn=-1;
                        if(player!=null)
                        {
                            player.release();
                            player=null;
                        }
                        player = MediaPlayer.create(getApplication(),R.raw.lose);
                        player.start();
                        //Gán winCheck về = true
                        thief_location=0;
                        img_location=0;
                        winCheck();
                        thief_img.setVisibility(View.VISIBLE);
                    }
                    else {
                        setUIDefault();
                    }
                }
            };
            timer.start();
        }
    }
    protected long gameMode(){
        int option =rdoG_mode.getCheckedRadioButtonId();
        if(option==R.id.radioButton_medium){
            return 1000;
        }
        else if(option==R.id.radioButton_hard){
            return 500;
        }else if(option==R.id.radioButton_impossible){
            return 100;
        }
        return 2000;
    }
    protected void setUIDefault() {
        img.setX(locationDefaultX);
        img.setY(locationDefaultY);
        button_Add.setText("Add");
        //Reset setting default của img
        if(player!=null)
        {
            player.release();
            player=null;
        }
        img_location=0;
        thief_location=-1;
        thief_img.setVisibility(View.GONE);
    }
    //Cài đặt hiệu ứng cho con trỏ chuột
    protected void setBackGround(DragEvent event){
        if(InTrue(event,img1))
        {
            img_location=1;
            img1.setBackgroundColor(SettingUI.colorChange);
        }
        else if(InTrue(event,img1)==false)
        {
            img1.setBackgroundColor(SettingUI.colorDefault);
        }

        if(InTrue(event,img2))
        {
            img_location=2;
            img2.setBackgroundColor(SettingUI.colorChange);

        }
        else if(InTrue(event,img2)==false)
        {
            img2.setBackgroundColor(SettingUI.colorDefault);
        }

        if(InTrue(event,img3))
        {
            img_location=3;
            img3.setBackgroundColor(SettingUI.colorChange);
        }
        if(InTrue(event,img3)==false)
        {
            img3.setBackgroundColor(SettingUI.colorDefault);
        }

        if(InTrue(event,img4))
        {
            img_location=4;
            img4.setBackgroundColor(SettingUI.colorChange);
        }
        if(InTrue(event,img4)==false)
        {
            img_location=4;
            img4.setBackgroundColor(SettingUI.colorDefault);
        }

        if(InTrue(event,img5))
        {
            img_location=5;
            img5.setBackgroundColor(SettingUI.colorChange);
        }
        if(InTrue(event,img5)==false)
        {
            img5.setBackgroundColor(SettingUI.colorDefault);
        }

        if(InTrue(event,img6))
        {
            img_location=6;
            img6.setBackgroundColor(SettingUI.colorChange);
        }
        if(InTrue(event,img6)==false)
        {
            img6.setBackgroundColor(SettingUI.colorDefault);
        }

        if(InTrue(event,img7))
        {
            img_location=7;
            img7.setBackgroundColor(SettingUI.colorChange);
        }
        if(InTrue(event,img7)==false)
        {
            img7.setBackgroundColor(SettingUI.colorDefault);
        }

        if(InTrue(event,img8))
        {
            img_location=8;
            img8.setBackgroundColor(SettingUI.colorChange);
        }
        if(InTrue(event,img8)==false)
        {
            img8.setBackgroundColor(SettingUI.colorDefault);
        }

        if(InTrue(event,img9))
        {
            img_location=9;
            img9.setBackgroundColor(SettingUI.colorChange);
        }
        if(InTrue(event,img9)==false)
        {
            img9.setBackgroundColor(SettingUI.colorDefault);
        }
        if(InTrue(event,imgZoomIn))
        {
            if(scaleX>SettingUI.maxScaleX&&scaleY>SettingUI.maxScaleY)
            {
                scaleX=SettingUI.maxScaleX;
                scaleY=SettingUI.maxScaleY;
            }
            else{
                img.setScaleX(scaleX+=0.2);
                img.setScaleY(scaleY+=0.2);
            }

        }
        if(InTrue(event,imgZoomOut))
        {
            if(scaleX<SettingUI.minScaleX&&scaleY<SettingUI.minScaleY)
            {
                scaleX=SettingUI.minScaleX;
                scaleY=SettingUI.minScaleY;
            }
            img.setScaleX(scaleX-=0.2);
            img.setScaleY(scaleY-=0.2);
        }
        if(InTrue(event,thief_img)){
            thief_img.setVisibility(View.VISIBLE);
        } else if (InTrue(event,thief_img)==false) {
            thief_img.setVisibility(View.INVISIBLE);
        }
    }
    private boolean winCheck() {
        if(img_location==thief_location)
        {
            return true;
        }
        return false;
    }
    //Kiểm tra con trỏ chuột có nằm trong vùng Image chỉ định
    protected boolean InTrue(DragEvent event,ImageView imageView){
        // Lấy tọa độ của image view 1
        int x1 = (int) event.getX();
        int y1 = (int)event.getY();
        // Lấy tọa độ của image view 2
        int x2 = (int)imageView.getX();
        int y2 = (int)imageView.getY();
        int width2 = imageView.getWidth();
        int height2 = imageView.getHeight();
        // So sánh tọa độ của hai image view
        if ((x1 >= x2 && x1 <= x2 + width2) &&
                (y1 >= y2 && y1 <= y2 + height2)) {
            // Image view 1 nằm trong tọa độ của image view 2
            return true;
        } else {
            // Image view 1 không nằm trong tọa độ của image view 2
            return false;
        }// Lấy tọa độ của image view 1
    }
    protected void thiefBorn(){
        Random random = new Random();
        int n;
        do{
            n = random.nextInt(9);
        }while (n==img_location-1);
        switch (n)
        {
            case 0:{
                ViewGroup.LayoutParams layoutParams = new RelativeLayout.LayoutParams(img1.getWidth(), img1.getHeight());
                thief_img.setLayoutParams(layoutParams);
                thief_img.setX(img1.getX());
                thief_img.setY(img1.getY());
                thief_location=1;
                break;
            }

            case 1:
            {
                ViewGroup.LayoutParams layoutParams = new RelativeLayout.LayoutParams(img1.getWidth(), img1.getHeight());
                thief_img.setLayoutParams(layoutParams);
                thief_img.setX(img2.getX());
                thief_img.setY(img2.getY());
                thief_location=2;
                break;
            }
            case 2:
            {
                ViewGroup.LayoutParams layoutParams = new RelativeLayout.LayoutParams(img1.getWidth(), img1.getHeight());
                thief_img.setLayoutParams(layoutParams);
                thief_img.setX(img3.getX());
                thief_img.setY(img3.getY());
                thief_location=3;
                break;
            }
            case 3:
            {
                ViewGroup.LayoutParams layoutParams = new RelativeLayout.LayoutParams(img1.getWidth(), img1.getHeight());
                thief_img.setLayoutParams(layoutParams);
                thief_img.setX(img4.getX());
                thief_img.setY(img4.getY());
                thief_location=4;
                break;
            }
            case 4:
            {
                ViewGroup.LayoutParams layoutParams = new RelativeLayout.LayoutParams(img1.getWidth(), img1.getHeight());
                thief_img.setLayoutParams(layoutParams);
                thief_img.setX(img5.getX());
                thief_img.setY(img5.getY());
                thief_location=5;
                break;
            }
            case 5:
            {
                ViewGroup.LayoutParams layoutParams = new RelativeLayout.LayoutParams(img1.getWidth(), img1.getHeight());
                thief_img.setLayoutParams(layoutParams);
                thief_img.setX(img6.getX());
                thief_img.setY(img6.getY());
                thief_location=6;
                break;
            }
            case 6:
            {
                ViewGroup.LayoutParams layoutParams = new RelativeLayout.LayoutParams(img1.getWidth(), img1.getHeight());
                thief_img.setLayoutParams(layoutParams);
                thief_img.setX(img7.getX());
                thief_img.setY(img7.getY());
                thief_location=7;
                break;
            }
            case 7:
            {
                ViewGroup.LayoutParams layoutParams = new RelativeLayout.LayoutParams(img1.getWidth(), img1.getHeight());
                thief_img.setLayoutParams(layoutParams);
                thief_img.setX(img8.getX());
                thief_img.setY(img8.getY());
                thief_location=8;
                break;
            }
            case 8:
            {
                ViewGroup.LayoutParams layoutParams = new RelativeLayout.LayoutParams(img1.getWidth(), img1.getHeight());
                thief_img.setLayoutParams(layoutParams);
                thief_img.setX(img9.getX());
                thief_img.setY(img9.getY());
                thief_location=9;
                break;
            }
        }
    }
}