package com.example.siddhesh.texttospeech;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private int result=0;
    private TextToSpeech tts;
    private Button btnSpeak;
    private AutoCompleteTextView txtText;
    SQLiteDatabase sqLiteDatabase;
    DBhelper dBhelper;
    String[] history;
    TextView tv;
    ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tts = new TextToSpeech(this, this);
        btnSpeak = (Button)findViewById(R.id.btnSpeak);
        txtText = (AutoCompleteTextView) findViewById(R.id.txtText);
        tv = (TextView)findViewById(R.id.heading);
        dBhelper = new DBhelper(this);
        setCustomAdapter();
        //button on click event
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                speakOut();
                insert();
                setCustomAdapter();
            }
        });
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqLiteDatabase = dBhelper.getWritableDatabase();
                sqLiteDatabase.execSQL("delete from HISTORY");
                Toast.makeText(MainActivity.this, "History Cleared.", Toast.LENGTH_SHORT).show();
                setCustomAdapter();
            }
        });
    }
    //shutdown tts when activity destroy
    @Override
    public void onDestroy() {
        // Don't forget to shutdown!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
    //It will called before TTS started
    @Override
    public void onInit(int status) {
        // TODO Auto-generated method stub
        //check status for TTS is initialized or not
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
                btnSpeak.setEnabled(false);
            } else {
                //if all is good than enable button convert text to speech
                btnSpeak.setEnabled(true);
            }
        } else {
            Log.e("TTS", "Initilization Failed");
        }
    }
    //call this method to speak text
    private void speakOut() {
        String text = txtText.getText().toString();
        if(result!=tts.setLanguage(Locale.US))
        {
            Toast.makeText(getApplicationContext(), "Enter right Words...... ", Toast.LENGTH_LONG).show();
        }else
        {
            //speak given text
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
    public void insert() {
        try {
            long id = -1;
            sqLiteDatabase = dBhelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
                contentValues.put("Usertext", txtText.getText().toString());
                id = sqLiteDatabase.insert("HISTORY", null, contentValues);
            if (id < 0) {
                Toast.makeText(this, "Failed to add Record.", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(this, "Record added successfully.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "" + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void offline() {
        try {
                int i = 0;
                sqLiteDatabase = dBhelper.getWritableDatabase();
                String[] columns = {"Usertext"};
                Cursor cursor = sqLiteDatabase.query("HISTORY", columns, null, null, null, null, null);
                history = new String[cursor.getCount()];
                while (cursor.moveToNext()) {
                    history[i] = cursor.getString(0).toString();
                    i++;
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "" + e.toString(), Toast.LENGTH_LONG).show();
        }
    }
    public void setCustomAdapter(){
        offline();
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, history) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                // Set the color here
                textView.setTextColor(Color.BLACK);
                return textView;
            }
        };
        txtText.setAdapter(adapter);
    }
}
