package com.stream.myvoa;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.stream.myvoa.bean.LRCObject;
import com.stream.myvoa.bean.VOAObject;
import com.stream.myvoa.utils.LRCLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PlayActivity extends AppCompatActivity {
    private ArrayList<LRCObject> rowList,lrcList;
    private TextView tvLRC;
    private MediaPlayer mediaPlayer;
    private int mPlayerTimerDuration;
    private int nowTime;
    private int nowLRCIndex;
    private Timer mTimer;
    private TimerTask mTask;
    float posX,posY,curPosX,curPosY;
    boolean moved;
    long firstClick, secondClick, count=0; //处理双击事件

    final static String TAG = "PlayActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        tvLRC = findViewById(R.id.tv_content);

        getTouchView();

        Intent intent = getIntent();
        VOAObject voaObject = (VOAObject) intent.getParcelableExtra("voa");
        Log.e(TAG, voaObject.getTitle());
        Log.e(TAG, voaObject.getlrc_path());
        Log.e(TAG, voaObject.getmp3_url());

        String webURL = voaObject.getlrc_path();
        String mp3URL = voaObject.getmp3_url();
        getLRCText(webURL);
        playVOA(mp3URL);

    }

    /*
    只要切走了就不再播放
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    private void getTouchView(){
        View v = findViewById(R.id.rootView);
        // 设置滑动监听
        setOnViewTouchListener(v);
    }

    private void setOnViewTouchListener(View v){
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        posX = event.getX();
                        posY = event.getY();
                        count++;
                        if(count == 1){
                            firstClick = System.currentTimeMillis();
                        }else if(count == 2){
                            secondClick = System.currentTimeMillis();
                            count = 0;
                            if(secondClick - firstClick < 300){ //若两次点击之间的间隔小于300ms，视为双击
                                if(mediaPlayer.isPlaying()){
                                    mediaPlayer.pause();
                                }
                                else{
                                    mediaPlayer.start();
                                }
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        moved = true;
                        //保证坐标是本次滑动后才得到的，而非上次滑动的数据；否则滑动后再点击一下也会切换
                        curPosX = event.getX();
                        curPosY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        if ((curPosX - posX > 0) && (Math.abs(curPosX - posX) > 25)){
                            Log.v(TAG,"向右滑动");
                            if(nowLRCIndex < lrcList.size() - 1) {
                                nowLRCIndex += 1; //到下一句
                                mediaPlayer.seekTo((int) lrcList.get(nowLRCIndex).getStartTime());
                                tvLRC.setText(lrcList.get(nowLRCIndex).getContent());
                            }
                            else{
                                Toast.makeText(PlayActivity.this,"已经是最后一句啦~",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if ((curPosX - posX < 0) && (Math.abs(curPosX-posX) > 25)){
                            Log.v(TAG,"向左滑动");
                            if(nowLRCIndex > 0) {
                                nowLRCIndex -= 1; //回退到上一句
                                mediaPlayer.seekTo((int) lrcList.get(nowLRCIndex).getStartTime());
                                tvLRC.setText(lrcList.get(nowLRCIndex).getContent());
                            }
                            else{
                                Toast.makeText(PlayActivity.this,"已经是第一句啦~",Toast.LENGTH_SHORT).show();
                            }
                        }
                        moved = false;
//                        if ((curPosY - posY > 0) && (Math.abs(curPosY - posY) > 25)){
//                            Log.v(TAG,"向下滑动");
//                        }
//                        else if ((curPosY - posY < 0) && (Math.abs(curPosY-posY) > 25)){
//                            Log.v(TAG,"向上滑动");
//                        }
                        break;
                }
                return true;
            }
        });
    }


    // 展示当前句子, 随着MP3播放进度自动显示下一句
    class LrcTask extends TimerTask{
        @Override
        public void run() {
            nowTime = mediaPlayer.getCurrentPosition();
            PlayActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(nowLRCIndex + 1 < lrcList.size()) {
                        if (nowTime >= lrcList.get(nowLRCIndex + 1).getStartTime()) {
                            nowLRCIndex += 1;
                            Log.e("LrcTask", String.valueOf(nowLRCIndex));
                        }
                    }
                    tvLRC.setText(lrcList.get(nowLRCIndex).getContent());
                }
            });
        }
    }

    /**
     * 在线播放mp3文件
     */
    public void playVOA(String mp3Path){
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(mp3Path);
            mediaPlayer.prepare();
        } catch (IOException e) {
            Log.e("mp3", e.getMessage());
        }
        if(!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            mPlayerTimerDuration = mediaPlayer.getDuration();
            Log.e("mPlayerTimerDuration", String.valueOf(mPlayerTimerDuration));
            nowLRCIndex = 0;
            mTimer = new Timer();
            mTask = new LrcTask();
            mTimer.scheduleAtFixedRate(mTask, 0, 200);
        }
    }


    /**
     * 通过HTTP链接从服务器上读取LRC文件并解析
     */
    public void getLRCText(String webURL){
//        Log.e("lrc",DataLoader.download(webURL));
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1000) {
                    rowList = msg.getData().getParcelableArrayList("LRC");
                    lrcList = LRCLoader.LRCParser(rowList);
                }
            }
        };
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ArrayList<LRCObject> tmpList;
                try {
                    tmpList = LRCLoader.getLRCList(webURL);
                    Message message = new Message();
                    message.what= 1000;
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("LRC", tmpList);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();//线程启动读取网络数据
    }
}