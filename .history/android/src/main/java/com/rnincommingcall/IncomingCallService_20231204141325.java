package com.rnincommingcall;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import android.widget.TextView;
import android.widget.RelativeLayout;

import android.view.MotionEvent; // Add this import
import android.graphics.Color;
import com.rnincommingcall.R;


import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.os.Build;

public class IncomingCallService extends Service {
    static View view = null;
    static WindowManager wm;
    static LayoutInflater inflater;
    static WindowManager.LayoutParams params;
    private static TextView txtIncomingnumber;
    static boolean processingAction = false;

        // Đối với những phần mới thêm vào
    private static final int MOVE_THRESHOLD = 10; // Độ chênh lệch tối thiểu để xem là đang di chuyển

    private float initialX, initialY;
    private int initialTouchX, initialTouchY;
    private boolean isMoving = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onCreate();
         params = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, 350,
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.O ?
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY :
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        
        params.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        params.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
      

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.activity_main,null);
        wm.addView(view, params);

     
    

        RelativeLayout popupImcommimgCall = (RelativeLayout)view.findViewById(R.id.popupIncommingCall);
        if(popupImcommimgCall != null){
          popupImcommimgCall.setBackgroundColor(Color.parseColor("#FF0000") );
        }

        String phoneNumber =  intent.getStringExtra("IncomingNumber");

        txtIncomingnumber = (TextView)view.findViewById(R.id.textCallerName);
        if(txtIncomingnumber != null){
          txtIncomingnumber.setText(phoneNumber);
        }


        Animation slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_right);
        view.startAnimation(slideInAnimation);

        // Thêm sự kiện onTouch cho view
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Lưu giữ tọa độ khi người dùng nhấn
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = (int) event.getRawX();
                        initialTouchY = (int) event.getRawY();
                        isMoving = false;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        // Tính toán sự chênh lệch giữa tọa độ mới và tọa độ đầu tiên
                        int deltaX = (int) event.getRawX() - initialTouchX;
                        int deltaY = (int) event.getRawY() - initialTouchY;

                        // Nếu sự chênh lệch đủ lớn, xem là đang di chuyển
                        if (Math.abs(deltaX) > MOVE_THRESHOLD || Math.abs(deltaY) > MOVE_THRESHOLD) {
                            isMoving = true;
                        }

                        // Di chuyển view
                        if (isMoving) {
                            params.x = (int) initialX + deltaX;
                            params.y = (int) initialY + deltaY;
                            wm.updateViewLayout(view, params);
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        // Nếu không di chuyển, xem là một sự kiện click và xử lý nút tắt ở đây
                        if (!isMoving) {
                            // handleButtonClick();
                        }
                        return true;

                    default:
                        return false;
                }
            }
        });
        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(view != null) {
            if(view.isEnabled()){
                wm.removeView(view);
                view = null;
            }           
        }
     
        // Các công việc khác khi dịch vụ bị hủy
    }


    public void onCloseButtonClick(View view) {
          onDestroy(); // Gọi hàm ẩn overlay khi nút X được nhấn
      }

    public static void clearView(Context context) {
        wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        if(view != null) {
            if(view.isEnabled()){
                wm.removeView(view);
                view = null;
            }           
        }
    }
}