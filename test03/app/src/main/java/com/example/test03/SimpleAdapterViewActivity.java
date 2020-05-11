package com.example.test03;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleAdapterViewActivity extends AppCompatActivity {
    ListView listView;
    String[] names = new String[]{"Lion", "Tiger", "Monkey", "Dog", "Cat", "Elephant"};
    int[] images =
        {R.drawable.lion,R.drawable.tiger,R.drawable.monkey,R.drawable.dog,R.drawable.cat,R.drawable.elephant};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_adapter_view);

        listView = findViewById(R.id.listView);
        List<Map<String,Object>> listItems = new ArrayList<>();
        for(int i = 0; i < names.length; i++) {
            Map<String,Object> map = new HashMap<>();
            map.put("name", names[i]);
            map.put("image", images[i]);
            listItems.add(map);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
                R.layout.simple_adapter_view_item, new String[]{"name", "image"},
                new int[]{R.id.textView, R.id.imageView});
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast toast = Toast.makeText(getApplicationContext(),names[position],Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
