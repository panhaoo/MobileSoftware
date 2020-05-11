package com.example.test03;

import android.content.Context;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActionModeActivity extends AppCompatActivity {
    ListView listView;

    String[] data = {"One", "Two", "Three", "Four", "Five"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_mode);
        listView = findViewById(R.id.actionModeListView);

        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        for(int i = 0; i < data.length; i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put("data", data[i]);
            list.add(map);
        }

        final MySimpleAdapter mySimpleAdapter = new MySimpleAdapter(this, list,
                R.layout.action_mode_item, new String[]{"data"},
                new int[]{R.id.textNum});

        listView.setAdapter(mySimpleAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener(){
            int n = 0;

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                n = 0;
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.action_mode_option, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.itemDelete:
                        Toast.makeText(getApplicationContext(), "删除成功",Toast.LENGTH_SHORT).show();
                        n = 0;
                        mySimpleAdapter.clearSelection();
                        mode.finish();
                }
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mySimpleAdapter.clearSelection();
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                Toast.makeText(getApplicationContext(), position + 1 + "",Toast.LENGTH_SHORT).show();
                if (checked) {
                    n++;
                    mySimpleAdapter.setNewSelection(position, true);
                } else {
                    n--;
                    mySimpleAdapter.removeSelection(position);
                }
                mode.setTitle(n + " selected");

            }
        });

    }

    private class MySimpleAdapter extends SimpleAdapter {

        private HashMap<Integer, Boolean> mSelection = new HashMap<>();

        MySimpleAdapter(Context context, List<HashMap<String, String>> data, int resource, String[] from, int[] to){
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            v.setBackgroundColor(getResources().getColor(android.R.color.background_light, null));

            if (mSelection.get(position) != null) {
                v.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light, null));
            }
            return v;
        }

        public void setNewSelection(int position, boolean value) {
            mSelection.put(position, value);
            notifyDataSetChanged();
        }

        public void removeSelection(int position) {
            mSelection.remove(position);
            notifyDataSetChanged();
        }

        public void clearSelection() {
            mSelection = new HashMap<>();
            notifyDataSetChanged();
        }
    }
}


