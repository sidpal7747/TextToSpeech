package com.example.siddhesh.texttospeech;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by SIDDHESH on 16-01-2017.
 */

public class History extends AppCompatActivity implements TextToSpeech.OnInitListener,AdapterView.OnItemLongClickListener {
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    private TextToSpeech tts;
    boolean flag = false;
    ArrayList<String> list = new ArrayList();
    int result = 0;
    String[] values;
    SQLiteDatabase sqLiteDatabase;
    DBhelper dBhelper;
    ImageView his,menus,delete;
    TextView counter;
    Toolbar tool,tool_del;
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
        tool = (Toolbar)findViewById(R.id.tool);
        tool_del = (Toolbar)findViewById(R.id.tool_del);
        his = (ImageView)findViewById(R.id.history_icon);
        menus = (ImageView)findViewById(R.id.menuic);
        counter = (TextView)tool_del.findViewById(R.id.delItemCount);
        delete = (ImageView)tool_del.findViewById(R.id.del_icon);
        listView = (ListView)findViewById(R.id.history);
        listView.setOnItemLongClickListener(this);
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
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqLiteDatabase = dBhelper.getWritableDatabase();
                for(int i=0;i<list.size();i++){
                    sqLiteDatabase.execSQL("delete from HISTORY where Usertext=\'"+list.get(i)+"\'");
                }
                getHistory();
                tool_del.setVisibility(View.GONE);
                tool.setVisibility(View.VISIBLE);
                listView.setOnItemLongClickListener(History.this);
                list.clear();
                flag = false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = (String) parent.getItemAtPosition(position);
                if(flag){
                    if(list.contains(parent.getItemAtPosition(position).toString())){
                        parent.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);
                        list.remove(list.indexOf(parent.getItemAtPosition(position).toString()));
                        counter.setText(""+list.size());
                        if(list.isEmpty()) {
//                            Toast.makeText(getApplicationContext(), "List is empty", Toast.LENGTH_LONG).show();
                            flag = false;
                            tool.setVisibility(View.VISIBLE);
                            tool_del.setVisibility(View.GONE);
                            listView.setOnItemLongClickListener(History.this);
                        }
                    } else {
                        list.add((String) parent.getItemAtPosition(position));
                        parent.getChildAt(position).setBackgroundColor(Color.parseColor("#ffb300"));
                        counter.setText(""+list.size());
                    }
                } else {
                    speakOut(str);
                }
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
        if(flag){
            flag = false;
            //list.clear();
        } else {
            startActivity(new Intent(History.this, MainActivity.class));
        }
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
                listView.setEnabled(false);
            } else {
                //if all is good than enable button convert text to speech
                //btnSpeak.setEnabled(true);
                listView.setEnabled(true);
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
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        tool.setVisibility(View.GONE);
        tool_del.setVisibility(View.VISIBLE);
        flag = true;
        parent.getChildAt(position).setBackgroundColor(Color.parseColor("#ffb300"));
        list.add((String)parent.getItemAtPosition(position));
        counter.setText(""+list.size());
        listView.setOnItemLongClickListener(null);
        return true;
    }
}
