package com.shishimao.rtcatdemo1;

import android.Manifest;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.shishimao.sdk.AbstractStream;
import com.shishimao.sdk.Errors;
import com.shishimao.sdk.LocalStream;
import com.shishimao.sdk.RTCat;
import com.shishimao.sdk.apprtc.AppRTCAudioManager;
import com.shishimao.sdk.tools.L;
import com.shishimao.sdk.view.VideoPlayer;

public class MainActivity extends AppCompatActivity {

    RTCat cat;
    LocalStream localStream;
    VideoPlayer videoPlayer;
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] permssions = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
        };
        ActivityCompat.requestPermissions(this,permssions, 111);

        iv=(ImageView)findViewById(R.id.myImageView1);
        videoPlayer =(VideoPlayer)findViewById(R.id.video_player);
        cat = new RTCat(MainActivity.this,true,true,true,false, AppRTCAudioManager.AudioDevice.SPEAKER_PHONE,RTCat.CodecSupported.H264, L.VERBOSE);
        cat.initVideoPlayer(videoPlayer);
    }

    public void createStream(View view){

        localStream = cat.createStream(true,true,15,RTCat.VideoFormat.Lv9, LocalStream.CameraFacing.FRONT);
        localStream.addObserver(new LocalStream.StreamObserver() {
            @Override
            public void afterSwitch(boolean b) {

            }

            @Override
            public void error(Errors errors) {

            }

            @Override
            public void accepted() {
                localStream.play(videoPlayer);
            }
        });

        localStream.init();
    }


    public void switchCamera(View view){
        if(localStream != null){
            localStream.switchCamera();
        }else{
            tos();
        }
    }

    public void takePicture(View view)
    {
        if(localStream != null){
            localStream.takePicture(new LocalStream.CaptureCallback() {
                @Override
                public void onCapture(final Bitmap bm) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iv.setImageBitmap(bm);
                        }
                    });
                }
            });
        }else{
            tos();
        }
    }

    void tos(){
        Toast.makeText(this,"请先获得本地视频",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(localStream != null){
            localStream.release();
            localStream = null;
        }

        if(videoPlayer != null)
        {
            videoPlayer.release();
            videoPlayer = null;
        }

        if (cat !=null){
            cat.release();
            cat = null;
        }
    }
}
