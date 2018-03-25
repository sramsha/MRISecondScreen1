package com.fyp.mrisecondscreen.utils;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.fyp.mrisecondscreen.R;

public class ChatHeadService extends Service {

    private WindowManager mWindowManager;
    private View mChatHeadView;
    protected long _touchStartTime;

    public ChatHeadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Inflate the chat head layout we created
        mChatHeadView = LayoutInflater.from(this).inflate(R.layout.layout_chat_head, null);

        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the chat head position
        params.gravity = Gravity.TOP | Gravity.START;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mChatHeadView, params);

        //Set the close button.
        ImageView closeButton = (ImageView) mChatHeadView.findViewById(R.id.close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //close the service and remove the chat head from the window
                stopSelf();
            }
        });

        //Drag and move chat head using user's touch action.
        final ImageView chatHeadImage = mChatHeadView.findViewById(R.id.chat_head_profile_iv);


        int i = 0;
        chatHeadImage.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            int i = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    _touchStartTime = event.getEventTime();

                    //remember the initial position.
                    initialX = params.x;
                    initialY = params.y;

                    //get the touch location
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();

                    Log.e("ACTION:","DOWN");
                    /*Log.e("Euclidean Dist", String.valueOf(Math.sqrt(Math.pow(Math.abs(initialX - initialTouchX),2)
                            + Math.pow(Math.abs(initialY - initialTouchY),2))));*/
                    //return true;
                }

                if (event.getAction() == MotionEvent.ACTION_UP && (event.getEventTime() - _touchStartTime > 2000))
                {
                    Toast.makeText(ChatHeadService.this, "Event detected", Toast.LENGTH_LONG).show();
                    /*Intent intent = new Intent(ChatHeadService.this, ChatActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    //close the service and remove the chat heads
                    stopSelf();*/
                    //return true;
                }

                if (event.getAction() == MotionEvent.ACTION_MOVE)
                {
                    //Calculate the X and Y coordinates of the view.
                    params.x = initialX + (int) (event.getRawX() - initialTouchX);
                    params.y = initialY + (int) (event.getRawY() - initialTouchY);

                    //Update the layout with new X & Y coordinate
                    mWindowManager.updateViewLayout(mChatHeadView, params);
                    Log.e("ACTION:","MOVE");
                    /*Log.e("Euclidean Dist", String.valueOf(Math.sqrt(Math.pow(Math.abs(initialX - initialTouchX),2)
                            + Math.pow(Math.abs(initialY - initialTouchY),2))));*/
                    //return true;
                }
                return true;
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatHeadView != null) mWindowManager.removeView(mChatHeadView);
    }
}