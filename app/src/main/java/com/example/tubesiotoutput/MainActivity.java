package com.example.tubesiotoutput;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TextView statusku;

    private MediaPlayer mediaPlayer;
    private String urlRadio = "http://streaming.girifm.com:8010/;stream.nsv?type=.mp3/;stream.mp3&autostart=1";
    private TextToSpeech myTTS;
    private String urlWeb = "https://www.tribunnews.com/news";

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
                String data = dataSnapshot.child("Voice").getValue(String.class);
                statusku.setText(data);
                if (data.equals("radio")) {
                    myTTS.stop();
                    radioPlay();
                } else if (data.equals("jam")) {
                    mediaPlayer.reset();
                    Calendar calender = Calendar.getInstance();
                    SimpleDateFormat time = new SimpleDateFormat("HH:mm");
                    String timeString = "sekarang pukul" + time.format(calender.getTime());
                    speak(timeString);
                } else if (data.equals("tanggal")) {
                    mediaPlayer.reset();
                    Date tanggal = new Date();
                    SimpleDateFormat date = new SimpleDateFormat("dd/MMMM/yyyy");
                    String dateString = "sekarang tanggal" + date.format(tanggal);
                    speak(dateString);
                } else if(data.equals("berita")) {
                    mediaPlayer.reset();
                    new doit().execute();
                }else if (data.equals("berita pertama")){
                    mediaPlayer.reset();
                    new bacaBerita1().execute();
                }else if(data.equals("berita kedua")){
                    mediaPlayer.reset();
                    new bacaBerita2().execute();
                }else if(data.equals("berita ketiga")){
                    mediaPlayer.reset();
                    new bacaBerita3().execute();
                }else if(data.equals("berita ke-4")){
                    mediaPlayer.reset();
                    new bacaBerita4().execute();
                }else if(data.equals("berita ke-5")){
                    mediaPlayer.reset();
                    new bacaBerita5().execute();
                }else if(data.equals("berhenti")){
                    myTTS.stop();
                    mediaPlayer.reset();
                }
                else {
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
                if (i != TextToSpeech.ERROR) {
                    myTTS.setLanguage(new Locale("id", "ID"));
                }
            }
        });
    }

    private void speak(String message) {
        if (Build.VERSION.SDK_INT >= 21) {
            myTTS.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            myTTS.speak(message, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void radioPlay() {
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(urlRadio);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
        } catch (IOException e) {
        }
    }

    public class doit extends AsyncTask<Void, Void, Void> {

        String newPage1, newPage2, newPage3, newPage4, newPage5;


        @Override
        protected Void doInBackground(Void... voids) {
            try {

                Document doc = Jsoup.connect(urlWeb).get();
                Elements newsRawTag1 = doc.select("div.populer ul li:eq(0) h3 a.txt-oev-3");
                Elements newsRawTag2 = doc.select("div.populer ul li:eq(1) h3 a.txt-oev-3");
                Elements newsRawTag3 = doc.select("div.populer ul li:eq(2) h3 a.txt-oev-3");
                Elements newsRawTag4 = doc.select("div.populer ul li:eq(3) h3 a.txt-oev-3");
                Elements newsRawTag5 = doc.select("div.populer ul li:eq(4) h3 a.txt-oev-3");
                newPage1 = newsRawTag1.text();
                newPage2 = newsRawTag2.text();
                newPage3 = newsRawTag3.text();
                newPage4 = newsRawTag4.text();
                newPage5 = newsRawTag5.text();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            speak("berita pertama."+newPage1+". "+"berita ke dua."+newPage2+". "+"berita ke tiga."+newPage3+". "+"berita ke empat."+newPage4+". "+"berita ke lima."+newPage5+".");
        }
    }

    public static String maxString(String str, int len) {
        if (str.length() > len) {
            return str.substring(0, len);
        } else {
            return str;
        }}

    public class bacaBerita1 extends AsyncTask<Void, Void, Void>{
        String link, page1, word;
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(urlWeb).get();

                Elements link1 = doc.select("div.populer ul li:eq(0) h3 a[href]");

                link = link1.attr("href");


                Document berita1 = Jsoup.connect(link+"?page=all").get();
                Elements news1 = berita1.select("div.txt-article p");
                page1 = news1.text();
                word = maxString(page1, 3999);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            speak(word);
        }
    }

    public class bacaBerita2 extends AsyncTask<Void, Void, Void>{
        String link, page1, word;
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(urlWeb).get();

                Elements link1 = doc.select("div.populer ul li:eq(1) h3 a[href]");

                link = link1.attr("href");


                Document berita1 = Jsoup.connect(link+"?page=all").get();
                Elements news1 = berita1.select("div.txt-article p");
                page1 = news1.text();
                word = maxString(page1, 3999);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            speak(word);
        }
    }

    public class bacaBerita3 extends AsyncTask<Void, Void, Void>{
        String link, page1, word;
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(urlWeb).get();

                Elements link1 = doc.select("div.populer ul li:eq(2) h3 a[href]");

                link = link1.attr("href");


                Document berita1 = Jsoup.connect(link+"?page=all").get();
                Elements news1 = berita1.select("div.txt-article p");
                page1 = news1.text();
                word = maxString(page1, 3999);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            speak(word);
        }
    }

    public class bacaBerita4 extends AsyncTask<Void, Void, Void>{
        String link, page1, word;
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(urlWeb).get();

                Elements link1 = doc.select("div.populer ul li:eq(3) h3 a[href]");

                link = link1.attr("href");


                Document berita1 = Jsoup.connect(link+"?page=all").get();
                Elements news1 = berita1.select("div.txt-article p");
                page1 = news1.text();
                word = maxString(page1, 3999);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            speak(word);
        }
    }

    public class bacaBerita5 extends AsyncTask<Void, Void, Void>{
        String link, page1, word;
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(urlWeb).get();
                Elements link1 = doc.select("div.populer ul li:eq(4) h3 a[href]");
                link = link1.attr("href");


                Document berita1 = Jsoup.connect(link+"?page=all").get();
                Elements news1 = berita1.select("div.txt-article p");
                page1 = news1.text();
                word = maxString(page1, 3999);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            speak(word);
        }
    }

}

