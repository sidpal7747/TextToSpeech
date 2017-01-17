package com.example.siddhesh.texttospeech;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by SIDDHESH on 16-01-2017.
 */

public class History extends AppCompatActivity {
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
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
}
