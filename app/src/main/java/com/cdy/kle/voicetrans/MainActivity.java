package com.cdy.kle.voicetrans;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private Button recoderBtn;
    private Button playBtn;
    private TextView fileNameTv;
    private TextView volTv;
    private MediaRecorder recorder;
    private MediaPlayer mPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recoderBtn = (Button) this.findViewById(R.id.record_btn);
        playBtn = (Button) this.findViewById(R.id.play_btn);
        fileNameTv = (TextView) this.findViewById(R.id.fileName_tv);
        volTv = (TextView) this.findViewById(R.id.fileName_tv);
        recoderBtn.setOnTouchListener(this);
        playBtn.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (mPlayer != null) {
            try {
                mPlayer.start();
            } catch (Exception e) {
                Log.e("kle", "播放失败");
            }

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.record_btn:
                try {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        String fileName = getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".amr";
                        recorder = new MediaRecorder();
                        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        recorder.setOutputFile(fileName);
                        recorder.prepare();
                        recorder.start();
                        updateMicStatus();
                        fileNameTv.setTag(fileName);

                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (recorder != null) {
                            recorder.stop();
                            recorder.release();
                            recorder = null;
                            mPlayer = new MediaPlayer();
                            mPlayer.setDataSource(fileNameTv.getTag().toString());
                            mPlayer.prepare();
                            fileNameTv.setText("文件：" + fileNameTv.getTag().toString() + "时长：" + mPlayer.getDuration() / 1000.0 + "s");

                        }

                    }
                } catch (Exception e) {
                    Log.v("kle", Log.getStackTraceString(e));
                }
                break;
        }
        return false;
    }

    /**
     * 更新话筒状态
     */
    private int BASE = 1;
    private int SPACE = 100;// 间隔取样时间

    private void updateMicStatus() {
        if (recorder != null) {
            double ratio = (double) recorder.getMaxAmplitude() / BASE;
            double db = 0;// 分贝
            if (ratio > 1)
                db = 20 * Math.log10(ratio);
            Log.d("kle", "分贝值：" + db);

            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
            mHandler.sendEmptyMessage((int) Math.round(db / 100 * 36));
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            volTv.setText("音波高度:" + msg.what);
        }
    };
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };
}
