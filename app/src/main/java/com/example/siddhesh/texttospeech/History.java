package com.example.siddhesh.texttospeech;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by SIDDHESH on 16-01-2017.
 */

public class History extends AppCompatActivity implements TextToSpeech.OnInitListener {
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    private TextToSpeech tts;
    int result = 0;
    String[] values;
    SQLiteDatabase sqLiteDatabase;
    DBhelper dBhelper;
    ImageView his,menus;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        sharedPreferences = getSharedPreferences("MyData",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        dBhelper = new DBhelper(this);
        tts = new TextToSpeech(this, this);
        his = (ImageView)findViewById(R.id.history_icon);
        menus = (ImageView)findViewById(R.id.menuic);
        listView = (ListView)findViewById(R.id.history);
        menus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(History.this, v);
                popupMenu.inflate(R.menu.menu_main);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(!item.getTitle().equals("Exit")) {
                            sqLiteDatabase = dBhelper.getWritableDatabase();
                            sqLiteDatabase.execSQL("delete from HISTORY");
                            editor.putString("status","true");
                            editor.commit();
                            Toast.makeText(History.this, "History Cleared.", Toast.LENGTH_SHORT).show();
                            listView.setAdapter(null);
                            return true;
                        } else {
                            System.exit(1);
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = (String) parent.getItemAtPosition(position);
                speakOut(str);
            }
        });
        his.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(History.this,History.class));
                finish();
            }
        });
        getHistory();
    }
    public void getHistory() {
        try {
            int i = 0;
            sqLiteDatabase = dBhelper.getWritableDatabase();
            String[] columns = {"Usertext"};
            Cursor cursor = sqLiteDatabase.query("HISTORY", columns, null, null, null, null, null);
            values = new String[cursor.getCount()];
            while (cursor.moveToNext()) {
                values[i] = cursor.getString(0).toString();
                i++;
            }
            arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
            listView.setAdapter(arrayAdapter);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "" + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(History.this,MainActivity.class));
        finish();
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
                listView.setClickable(false);
            } else {
                //if all is good than enable button convert text to speech
                //btnSpeak.setEnabled(true);
                listView.setClickable(true);
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

}
