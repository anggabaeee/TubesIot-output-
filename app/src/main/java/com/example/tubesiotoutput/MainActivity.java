package com.example.tubesiotoutput;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TextView statusku ;

    private MediaPlayer mediaPlayer;
    private String urlRadio="http://streaming.girifm.com:8010/;stream.nsv?type=.mp3/;stream.mp3&autostart=1";
    private TextToSpeech myTTS;

    private DatabaseReference mDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusku = findViewById(R.id.status);
        mediaPlayer = new MediaPlayer();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String data= dataSnapshot.child("Voice").getValue(String.class);
                statusku.setText(data);
                if(data.equals("radio")){
                    radioPlay();
                }else if(data.equals("jam")){
                    mediaPlayer.reset();
                    Calendar calender = Calendar.getInstance();
                    SimpleDateFormat time = new SimpleDateFormat("HH:mm");
                    String timeString = "sekarang pukul" + time.format(calender.getTime());
                    speak(timeString);
                }else if(data.equals("tanggal")){
                    Date tanggal = new Date();
                    SimpleDateFormat date = new SimpleDateFormat("dd/MMMM/yyyy");
                    String dateString = "sekarang tanggal" + date.format(tanggal);
                    speak(dateString);
                } else{
                    mediaPlayer.reset();
                    speak("instruksi salah");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        textToSpeech();
    }

    private void textToSpeech() {
        myTTS = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i!= TextToSpeech.ERROR){
                    myTTS.setLanguage(new Locale("id", "ID"));
                }
            }
        });
    }

    private void speak(String message){
        if(Build.VERSION.SDK_INT>=21){
            myTTS.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        }else {
            myTTS.speak(message, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void radioPlay(){
        try{
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(urlRadio);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
        }catch (IOException e){}
    }
}
