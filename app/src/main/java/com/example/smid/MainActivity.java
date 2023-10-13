package com.example.smid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.Random;
/**
 * Activity for loading layout resources
 *
 * It contains the logic for the game, including the drag-and-drop functionality,
 * the timer, and the scoring system.

 * @author Hồ Ngọc Hòa, Phạm Nguyễn Hoài Nam
 * @version 2010.1105
 * @since 1.0
 */
public class MainActivity extends AppCompatActivity {
    /**
     Ảnh đại diện cho hình ảnh chính trong trò chơi. */ protected ImageView img;

    /**
     9 img tương ứng với 9 ô vuông trong trò chơi. */ protected ImageView img1, img2, img3, img4, img5, img6, img7, img8, img9;

    /**
     Img thùng rác kéo vào để xóa. */ protected ImageView img_recyle;

    /**
     Img phóng to thu nhỏ, kéo vào để sử dụng. */ protected ImageView imgZoomOut, imgZoomIn;

    /**
     Ảnh đại diện cho tên trộm trong trò chơi. */ protected ImageView thief_img;

    /**
     Text view hiển thị điểm số của người chơi. */ protected TextView tv_score;

    /**
     Edit text hiển thị và cho phép người chơi đặt thời gian đếm ngược. */ protected EditText time_CounDown;

    /**
     Radio group cho phép người chơi chọn chế độ trò chơi: dễ, bình thường, khó, cực khó. */ protected RadioGroup rdoG_mode;

    /**
     Nút cho phép người chơi thêm hình ảnh mới vào trò chơi. */ protected Button btn_Add;

    /**
     Media Player phát nhạc trò chơi. */ protected MediaPlayer player, player_state;

    /**
     Bộ đếm thời gian đếm ngược thời gian chơi. */ protected CountDownTimer timer;

    /**
     RelativeLayout chứa tất cả các yếu tố trò chơi. */ protected RelativeLayout relativeLayout;

    /**
     Hộp thoại được hiển thị khi trò chơi kết thúc. */ protected Dialog dialog;

    /**
     Điểm số của người chơi. */ protected int score;

    /**
     Số lần người chơi touh hình ảnh chính. */ protected int countTurn;

    /**
     Vị trí của hình ảnh chính. */ protected int img_location = 0;

    /**
     Vị trí của tên trộm. */ protected int thief_location = -1;

    /**
     Tọa độ X và Y mặc định của hình ảnh chính. */ protected float locationDefaultX, locationDefaultY;

    /**
     Hệ số tỷ lệ cho hình ảnh chính. */ protected float scaleX, scaleY;


    /**
     * Khởi tạo các thành phần giao diện người dùng
     *
     * Phương thức này khởi tạo các yếu tố UI của trò chơi, bao gồm hình ảnh chính, hình ảnh nhỏ hơn, nút phóng to, thu nhỏ, nút xóa và nút thêm hình ảnh mới.
     * @param không có tham số đầu vào
     * @return không có giá trị trả về
     */
    protected void InitUI(){
        //FindID
        relativeLayout = findViewById(R.id.container);
        tv_score=(TextView)findViewById(R.id.textView_scores);
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
        btn_Add=(Button) findViewById(R.id.buttonAdd);
        //Logic
        countTurn=0;
        score=0;
        tv_score.setText("Scores:           "+score);
        time_CounDown.setText(String.valueOf(SettingUI.timeCountdownDefault));
    }


    /**
     * Xử lý kết quả trả về từ hoạt động khác
     *
     * Phương thức này được gọi khi người chơi chọn một tệp hình ảnh. Nó lấy tệp hình ảnh và hiển thị nó trong hình ảnh chính.
     * @param requestCode mã yêu cầu của hoạt động đã khởi chạy
     * @param resultCode mã kết quả của hoạt động đã khởi chạy
     * @param data dữ liệu trả về từ hoạt động đã khởi chạy
     * @return không có giá trị trả về
     */
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
                img.setVisibility(View.VISIBLE);
                //Thông báo kết quả
                if(btn_Add.getText().equals("Update"))
                {
                    Toast.makeText(this, "Update Success", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "Add Success", Toast.LENGTH_SHORT).show();
                }
                btn_Add.setText("Update");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     Khởi tạo hoạt động chính của ứng dụng

     Phương thức này được gọi khi hoạt động được tạo. Nó khởi tạo các biến cần thiết cho trò chơi và bắt đầu trò chơi.
     @param savedInstanceState trạng thái đã lưu của hoạt động

     @return không có giá trị trả về
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Khởi tạo UI
        InitUI();
        //Tạo sự kiện touch cho img
        img.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN)
                {
                    if(countTurn==0)
                    {
                        //Lưu vị trí mặc định của img trong lần chạm đầu tiên
                        locationDefaultX=img.getX();
                        locationDefaultY=img.getY();
                        gamePlay();
                    }
                    countTurn++;//Lưu số lần chạm
                    //Khởi tạo Bóng và bắt đầu kéo
                    ClipData data = ClipData.newPlainText("" , "");
                    View.DragShadowBuilder shadowBuilder= new View.DragShadowBuilder(v);
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
                    return true;
                }
                else if (event.getAction()==DragEvent.ACTION_DRAG_LOCATION) {
                    //Cập nhật liên tục vị trí img khi người dùng kéo
                    img.setX(event.getX()-110);
                    img.setY(event.getY()-40);
                    setBackGround(event);//Hàm xử lý hiệu ứng khi Drag
                    return true;
                }
                else if (event.getAction()==DragEvent.ACTION_DROP) {
                    if(InTrue(event,img_recyle))//Kiểm tra img có nằm trong vùng thả của img_recycle
                    {
                        img.setVisibility(View.GONE);//Ẩn img
                        btn_Add.setText("Add");
                        //Kết thúc bộ đếm ngược của trò chơi
                        timer.cancel();
                        timer.onFinish();
                        //Thông báo đã xóa
                        Toast.makeText(MainActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //Cập nhật vị trí img tại điểm drop
                        img.setX(event.getX()-100);
                        img.setY(event.getY()-40);
                    }
                    return true;
                }
                else if (event.getAction()==DragEvent.ACTION_DRAG_ENTERED) {
                    return true;
                }
                else if (event.getAction()==DragEvent.ACTION_DRAG_EXITED) {
                    return true;
                }
                else if (event.getAction()==DragEvent.ACTION_DRAG_ENDED) {
                    if(winCheck())
                    {
                        //Nếu kiểm tra win = true
                        score++;// Cộng điểm người chơi
                        tv_score.setText("Scores:           "+score);//Hiển thị lên tv_score
                        //Phát nhạc chiến thắng
                        player_state = MediaPlayer.create(getApplication(),R.raw.win);
                        player_state.start();
                    }
                    return true;
                }
                return false;
            }
        });
        btn_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khởi tạo một đối tượng Intent mới với loại ACTION_GET_CONTENT
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //Nếu không tồn tại biến img thì cho hiển thị và set vị trí ban đầu
                if(img.getVisibility()==View.GONE){
                    img.setX(locationDefaultX);
                    img.setY(locationDefaultY);
                }
                // Đặt các thuộc tính MIME_TYPE và EXTRA_ALLOW_MULTIPLE của đối tượng Intent
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Khởi chạy hoạt động Intent
                startActivityForResult(intent,MODE_APPEND);
            }
        });

    }


    /**
     Bắt đầu trò chơi

     Phương thức này bắt đầu trò chơi bằng cách khởi tạo bộ đếm thời gian và phát nhạc trò chơi.
     @param không có tham số đầu vào

     @return không có giá trị trả về
     */
    protected void gamePlay(){
        if(player==null) //Nếu bộ phát nhạc = null
        {
            //Bắt đầu phát nhạc
            player = MediaPlayer.create(getApplication(),R.raw.impossible);
            player.start();
            //Bắt đầu bộ đếm thời gian
            timer=new CountDownTimer(Integer.parseInt(time_CounDown.getText().toString())*1000,gameMode()) {
                @Override
                public void onTick(long millisUntilFinished) {
                    time_CounDown.setText(millisUntilFinished/1000+"");//Hiển thị bộ đếm thời gian ra màn hình
                    thiefBorn(); //Sinh ra trộm mỗi giây đếm ngược
                }
                @Override
                public void onFinish() {
                    time_CounDown.setText(String.valueOf(SettingUI.timeCountdownDefault));//Cập nhật thời gian đếm ngược mặc định
                    openResultdialog(score);//Mở hộp dialog thông báo kết quả số điểm
                    if(score==0){ //Nếu điểm = 0
                        //Phát nhạc thua
                        player_state = MediaPlayer.create(getApplication(),R.raw.lose);
                        player_state.start();
                    }
                    //Cập nhật UI về mặc định
                    setUIDefault();
                }
            };
            timer.start();
        }
    }


    /**
     * Lấy tốc độ tick của trò chơi dựa trên chế độ chơi đã chọn
     *
     * @param không có tham số đầu vào
     * @return tốc độ tick của trò chơi (mili giây)
     */
    protected long gameMode(){
        int option =rdoG_mode.getCheckedRadioButtonId();
        if(option==R.id.radioButton_medium){
            return 1000; //Chế độ thường: Tick 1s 1 lần
        }
        else if(option==R.id.radioButton_hard){
            return 500; //Chế độ khó: Tick 0.5s 1 lần
        }else if(option==R.id.radioButton_impossible){
            return 100; //Chế độ cực khó: Tick 0.1s 1 lần
        }
        return 2000; //Chế độ dễ: Tick 2s 1 lần
    }


    /**
     * Thiết lập giao diện người dùng về trạng thái mặc định
     *
     * @param không có tham số đầu vào
     * @return không có giá trị trả về
     */
    protected void setUIDefault() {
        //Cập nhật vị trí mặc định của img
        img.setX(locationDefaultX);
        img.setY(locationDefaultY);
        //Cập nhật độ scale mặc định của img
        img.setScaleX(SettingUI.minScaleX);
        img.setScaleY(SettingUI.minScaleY);
        if(player!=null)// Nếu nhạc còn đang chạy
        {
            //Làm trống bộ phát nhạc
            player.release();
            player=null;
        }
        //Cập nhật điểm về 0
        score=0;
        tv_score.setText("Scores:           "+score);
        //Cập nhật vị trí bán đầu của img và thief
        img_location=0;
        thief_location=-1;
        //Cập nhật số lần chạm =0 để bắt đầu lại trò chơi
        countTurn=0;
        //Ẩn img thief
        thief_img.setVisibility(View.GONE);
    }


    /**
     Mở hộp thoại kết quả với số điểm đã đạt được

     @param scores số điểm đã đạt được
     */
    protected void openResultdialog(int scores) {
        // Tạo hộp thoại
        dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_result);
        // Lấy cửa sổ của hộp thoại
        Window window=dialog.getWindow();
        if(window==null){
            return;
        }
        // Thiết lập kích thước và nền của cửa sổ
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // Thiết lập vị trí của cửa sổ
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity= Gravity.CENTER;
        window.setAttributes(windowAttributes);
        // Không cho phép đóng hộp thoại khi nhấn bên ngoài
        dialog.setCancelable(false);
        // Lấy các thành phần trong hộp thoại
        TextView tv_totalScore =dialog.findViewById(R.id.textView_total);
        Button btn_Again =dialog.findViewById(R.id.button_playAgain);
        Button btn_result =dialog.findViewById(R.id.button_viewResult);
        // Thiết lập sự kiện cho nút Chơi lại
        btn_Again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); //Tắt hộp thoại
            }
        });
        // Thiết lập sự kiện cho nút Xem kết quả
        btn_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hien Thi database
            }
        });
        // Thiết lập số điểm đã đạt được
        tv_totalScore.setText(String.valueOf(scores));
        // Hiển thị hộp thoại
        dialog.show();

    }


    /**
     * Xử lý hiệu ứng khi kéo hình ảnh
     *
     * Cài đặt hiệu ứng cho con trỏ chuột
     * @param event sự kiện kéo thả
     * @return không có giá trị trả về
     */
    protected void setBackGround(DragEvent event){
        // Kiểm tra xem hình ảnh chính có nằm trong vùng hình ảnh thứ nhất hay không
        if(InTrue(event,img1)) // Nếu true
        {
            img_location=1; // lưu vị trí hiện tại của con trỏ chuột nằm ở img1
            img1.setBackgroundColor(SettingUI.colorChange); // đổi màu background img1
        }
        else if(InTrue(event,img1)==false) //Nếu false
        {
            img1.setBackgroundColor(SettingUI.colorDefault); // trả màu background về màu mặc định
        }
        // Tương tự với các img từ img1 -> img9
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
        // Kiểm tra xem img có nằm trong vùng hình ảnh zoom in hay không
        if(InTrue(event,imgZoomIn)) //Nếu true
        {
            if(scaleX>SettingUI.maxScaleX&&scaleY>SettingUI.maxScaleY) // Nếu vượt qua mức zoom in cho phép
            {
                //set scale = hệ số scale tối đa
                scaleX=SettingUI.maxScaleX;
                scaleY=SettingUI.maxScaleY;
            }
            else //Ngược lại
            {
                //Tăng hệ số scale lên 0.2 đơn vị
                img.setScaleX(scaleX+=0.2);
                img.setScaleY(scaleY+=0.2);
            }

        }
        // Kiểm tra xem img có nằm trong vùng hình ảnh zoom out hay không
        if(InTrue(event,imgZoomOut)) //Nếu true
        {
            if(scaleX<SettingUI.minScaleX&&scaleY<SettingUI.minScaleY)// Nếu vượt qua mức zoom out cho phép
            {
                //set scale = hệ số scale tối thiểu
                scaleX=SettingUI.minScaleX;
                scaleY=SettingUI.minScaleY;
            }
            //Giảm hệ số scale đi 0.2 đơn vị
            img.setScaleX(scaleX-=0.2);
            img.setScaleY(scaleY-=0.2);
        }
        // Kiểm tra xem img có nằm trong vùng hình ảnh thief hay không
        if(InTrue(event,thief_img)){//Nếu true
            thief_img.setVisibility(View.VISIBLE); //Hiển thị thief_img
        } else if (InTrue(event,thief_img)==false) {//Nếu false
            thief_img.setVisibility(View.INVISIBLE); //Ẩn thief_img
        }
    }


    /**
     Kiểm tra xem người chơi có thắng trò chơi hay không bằng cách kiểm tra xem hình ảnh chính có nằm ở vị trí trộm hay không

     Phương thức này kiểm tra xem người chơi có thắng trò chơi hay không bằng cách kiểm tra xem hình ảnh chính có nằm ở vị trí trộm hay không
     @param không có tham số đầu vào
     @return true nếu người chơi thắng, false nếu người chơi thua */
    private boolean winCheck() {
        if(img_location==thief_location)
        {
            return true;
        }
        return false;
    }


    /**
     * Kiểm tra xem con trỏ chuột có nằm trong vùng Image chỉ định hay không
     *
     * @param event sự kiện kéo thả
     * @param imageView hình ảnh cần kiểm tra
     * @return true nếu con trỏ chuột nằm trong vùng Image, false nếu ngược lại
     */
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


    /**
     Sinh ra hình ảnh trộm ở một vị trí ngẫu nhiên

     Phương thức này tạo tên trộm và đặt nó ở một vị trí ngẫu nhiên trong 9 img ô vuông.
     @param không có tham số đầu vào

     @return không có giá trị trả về
     */
    protected void thiefBorn(){
        Random random = new Random();
        int n;
        do{
            n = random.nextInt(9);// Sinh ra một số ngẫu nhiên từ 0 đến 8
        }while (n==img_location-1); // Đảm bảo rằng thief_img không sinh ra ở cùng vị trí img đang drop
        // Đặt vị trí cho hình ảnh trộm
        switch (n)
        {
            case 0:
            {
                //Thiết lập kích thước thief_img = img1
                ViewGroup.LayoutParams layoutParams = new RelativeLayout.LayoutParams(img1.getWidth(), img1.getHeight());
                thief_img.setLayoutParams(layoutParams);
                //Thiết lập vị trí của thief_img=img1
                thief_img.setX(img1.getX());
                thief_img.setY(img1.getY());
                //Lưu vị trí của thief để so sánh trong winCheck
                thief_location=1;
                break;
            }
            //Các trường hợp dưới tương tự như trường hợp trên
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