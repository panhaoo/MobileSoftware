package com.example.test03;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class XmlDefineMenuActivity extends AppCompatActivity {
    EditText text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xml_menu);
        text = findViewById(R.id.editText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.xml_menu_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_font_small:
                text.setTextSize(10);
                return true;
            case R.id.menu_font_middle:
                text.setTextSize(16);
                return true;
            case R.id.menu_font_big:
                text.setTextSize(20);
                return true;
            case R.id.menu_normal:
                Toast.makeText(XmlDefineMenuActivity.this, "你点击了普通菜单项", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_color_red:
                text.setTextColor(Color.RED);
                return true;
            case R.id.menu_color_black:
                text.setTextColor(Color.BLACK);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
