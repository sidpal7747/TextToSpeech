package com.example.siddhesh.texttospeech;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by SIDDHESH on 07-02-2017.
 */

public class UserString extends AppCompatActivity implements TextToSpeech.OnInitListener{
    TextView textInput;
    LinearLayout linearLayout;
    String str;
    int result = 0;
    TextToSpeech tts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text);
        tts = new TextToSpeech(this, this);
        textInput = (TextView)findViewById(R.id.textInput);
        linearLayout = (LinearLayout)findViewById(R.id.touchArea);
        str = getIntent().getStringExtra("userValue");
        textInput.setText(""+str);
        Toast.makeText(this,"Tap to Play",Toast.LENGTH_SHORT).show();
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOut(str);
            }
        });
    }
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            //if TTS initialized than set language
            result = tts.setLanguage(Locale.US);

            // tts.setPitch(5); // you can set pitch level
            // tts.setSpeechRate(2); //you can set speech speed rate

            //check language is supported or not
            //check language data is available or not
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Missing data", Toast.LENGTH_LONG).show();
                //disable button
                // btnSpeak.setEnabled(false);
            } else {
                //if all is good than enable button convert text to speech
                //btnSpeak.setEnabled(true);
            }
        } else {
            Log.e("TTS", "Initilization Failed");
        }
    }
    private void speakOut(String text) {
        if(result!=tts.setLanguage(Locale.US))
        {
            Toast.makeText(getApplicationContext(), "Enter right Words...... ", Toast.LENGTH_LONG).show();
        }else
        {
            //speak given text
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
    @Override
    public void onDestroy() {
        // Don't forget to shutdown!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        finish();
    }
}
