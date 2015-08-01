package com.cdy.kle.voicetrans;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
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
    private MediaRecorder recorder;
    private MediaPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recoderBtn = (Button) this.findViewById(R.id.record_btn);
        playBtn = (Button)this.findViewById(R.id.play_btn);
        fileNameTv = (TextView)this.findViewById(R.id.fileName_tv);
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
        String name = null;
        if((name  = fileNameTv.getText().toString()) != null) {
            try{
                mPlayer = new MediaPlayer();
                mPlayer.setDataSource(name);
                mPlayer.prepare();
                mPlayer.start();
            }catch(Exception e){
                Log.e("kle","播放失败");
            }

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.record_btn:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        String fileName = getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".amr";
                        recorder = new MediaRecorder();
                        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        recorder.setOutputFile(fileName);
                        recorder.prepare();
                        recorder.start();
                        fileNameTv.setText(fileName);
                    } catch (Exception e) {
                        Log.v("kle", Log.getStackTraceString(e));
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (recorder != null) {
                        recorder.stop();
                        recorder.release();
                    }

                }
                break;
        }
        return false;
    }
}
