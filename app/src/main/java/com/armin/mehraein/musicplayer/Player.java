package com.armin.mehraein.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.io.FileOutputStream;
import java.io.IOException;

public class Player extends AppCompatActivity implements View.OnClickListener {

    ImageView back , music_picture ;
    String picture , music_name , music_artist , link ;
    ImageView download_btn , repeat_btn ;
    TextView artist , name ;
    TextView music_end , music_first ;
    SeekBar seekBar ;
    ImageView play_btn , next_btn , pereve_btn ;

    MediaPlayer mediaPlayer ;
    Convertor convertor = new Convertor();
    Handler handler = new Handler();
    boolean rep = false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_view);

        initView();

        picture = getIntent().getExtras().getString("picture");
        music_name = getIntent().getExtras().getString("name");
        music_artist = getIntent().getExtras().getString("artist");
        link = getIntent().getExtras().getString("link");

        Picasso.with(this).load(picture).into(music_picture);
        artist.setText(music_artist);
        name.setText(music_name);

        setMediaPlayer();
        new playMusic().execute();
        setSeekBar();
        repeatManage();
        btnMange();
    }

    private void initView() {
        seekBar = findViewById(R.id.seekbar);
        back = findViewById(R.id.back);
        music_picture = findViewById(R.id.music_picture);
        download_btn = findViewById(R.id.download_btn);
        repeat_btn = findViewById(R.id.repeat_btn);
        name = findViewById(R.id.name);
        artist = findViewById(R.id.artist);
        music_first = findViewById(R.id.muisc_first);
        music_end = findViewById(R.id.muisc_end);
        pereve_btn = findViewById(R.id.pereve_btn);
        play_btn = findViewById(R.id.play_btn);
        next_btn = findViewById(R.id.next_btn);
    }

    private void btnMange(){
        back.setOnClickListener(this);
        download_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back){
            if (mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
            finish();
        }
        if (v.getId() == R.id.download_btn){
            DownloadTask downloadTask = new DownloadTask(Player.this);
            downloadTask.execute(link,music_name);
        }

    }

    public void setMediaPlayer(){
        mediaPlayer = new MediaPlayer();
        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    play_btn.setImageResource(R.drawable.btn_play);
                }else {
                    mediaPlayer.start();
                    play_btn.setImageResource(R.drawable.btn_pause);
                }
            }
        });
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentTime = mediaPlayer.getCurrentPosition();
                if (currentTime + 5000 <= mediaPlayer.getDuration()){
                    mediaPlayer.seekTo(currentTime + 5000);
                }else {
                    mediaPlayer.seekTo(mediaPlayer.getDuration());
                }
            }
        });
        pereve_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentTime = mediaPlayer.getCurrentPosition();
                if (currentTime - 5000 >= 0){
                    mediaPlayer.seekTo(currentTime - 5000);
                }else {
                    mediaPlayer.seekTo(0);
                }
            }
        });
    }

    public class playMusic extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                mediaPlayer.setDataSource(link);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            mediaPlayer.start();
            play_btn.setImageResource(R.drawable.btn_pause);
            updateMusic();
        }
    }

    public void updateMusic(){
        int currentTiem = mediaPlayer.getCurrentPosition();
        int duration = mediaPlayer.getDuration();
        music_first.setText("" + convertor.milliSecondsToTimer(currentTiem));
        music_end.setText("" + convertor.milliSecondsToTimer(duration));
        int progress = (int)(convertor.getProgressPercentage(currentTiem,duration));
        seekBar.setProgress(progress);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateMusic();
            }
        };handler.postDelayed(runnable,1000);
    }

    public void setSeekBar(){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(null);
                int duration = mediaPlayer.getDuration();
                int current = convertor.progressToTimer(seekBar.getProgress(),duration);
                mediaPlayer.seekTo(current);
                updateMusic();
            }
        });
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                seekBar.setSecondaryProgress(percent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        super.onBackPressed();
    }

    public void repeatManage(){
        repeat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rep){
                    rep = false ;
                    Toast.makeText(Player.this, "تکرار غیر فعال شد", Toast.LENGTH_SHORT).show();
                    repeat_btn.setImageResource(R.drawable.btn_repeat);
                }else {
                    rep = true ;
                    Toast.makeText(Player.this, "تکرار فعال شد", Toast.LENGTH_SHORT).show();
                    repeat_btn.setImageResource(R.drawable.btn_repeat_on);
                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                handler.removeCallbacks(null);
                play_btn.setImageResource(R.drawable.btn_play);
                if (rep){
                    mediaPlayer.start();
                    play_btn.setImageResource(R.drawable.btn_pause);
                    updateMusic();
                }else {
                    seekBar.setProgress(0);
                    music_end.setText("00:00");
                    music_first.setText("00:00");
                }
            }
        });
    }
}
