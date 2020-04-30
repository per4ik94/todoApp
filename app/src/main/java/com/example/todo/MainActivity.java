package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String PREFS_FILE = "itemTextSize";
    public static final String MY_KEY = "size";
    ItemAdapter itemAdapter;
    FloatingActionButton fabAdd;
    SwipeMenuListView listViewTodoItem;
    float textsize;
    TextView time;
    DatabaseHelper db;
    ArrayList<ToDoItem> listItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupSharedPreferences();
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        textsize = sharedPreferences.getFloat(MY_KEY, 20);
        db=new DatabaseHelper(this);

        fabAdd = findViewById(R.id.addToDO);
        listViewTodoItem = findViewById(R.id.listView);
        time = findViewById(R.id.time);

        //----------------------------------------------neuer adapter------------------//
        listItem = new ArrayList<>();
        populateTodoListView();

        //--------------------------------------------------------------------------//


        //itemAdapter.setTextSizes(textsize);

        listViewTodoItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), Detail_Activity.class);
                ToDoItem item = listItem.get(position);

                Cursor dataTodoID = db.getToDoID(item.getTitel());
                int itemID = -1;
                while (dataTodoID.moveToNext()) {
                    itemID = dataTodoID.getInt(0);
                }
                dataTodoID.close();


                intent.putExtra("titel", item.getTitel());
                intent.putExtra("id", itemID);
                intent.putExtra("layout", "bearbeiten");
                startActivity(intent);

            }
        });
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Detail_Activity.class);
                startActivity(intent);
            }
        });


        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(170);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        listViewTodoItem.setMenuCreator(creator);

        listViewTodoItem.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        ToDoItem item = listItem.get(position);
                        Cursor dataTodoID = db.getToDoID(item.getTitel());
                        int itemID = -1;
                        while (dataTodoID.moveToNext()) {
                            itemID = dataTodoID.getInt(0);
                        }
                        dataTodoID.close();

                        listItem.remove(item);
                        listViewTodoItem.setAdapter(itemAdapter);
                        db.deleteToDo(itemID);
                        db.deleteToDoCategories(itemID);
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
    }

    private void populateTodoListView() {
        Cursor cursor = db.getTodo();
        if (cursor.getCount() == 0){
            Toast.makeText(this," NO DATA", Toast.LENGTH_LONG).show();
        }else{
            while (cursor.moveToNext()){
                listItem.add(new ToDoItem(cursor.getString(1), cursor.getString(2), cursor.getString(3),cursor.getInt(4)));
            }
            itemAdapter = new ItemAdapter(MainActivity.this, listItem);
            listViewTodoItem.setAdapter(itemAdapter);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.itemSettings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(MY_KEY, textsize);
        editor.apply();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(MY_KEY)) {
            textsize = Float.parseFloat(sharedPreferences.getString(MY_KEY, "20.0"));
            itemAdapter.setTextSizes(textsize);
            onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

}
