package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
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

/**
 * @author Sergej Cerkasin
 * @version TodoApp 2020/05/01
 */
public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    ItemAdapter itemAdapter;
    FloatingActionButton fabAdd;
    SwipeMenuListView listViewTodoItem;
    TextView time;
    DatabaseHelper db;
    ArrayList<ToDoItem> listItem;

    /**
     * Bei Start wird die Activity aufgebaut, die ListView mit Elementen gefuellt
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        db = new DatabaseHelper(this);

        fabAdd = findViewById(R.id.addToDO);
        listViewTodoItem = findViewById(R.id.listView);
        time = findViewById(R.id.time);

        listItem = new ArrayList<>();
        populateTodoListView();
        swipeListItems();

        listViewTodoItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    }

    /**
     * Erstellt eine Einstellung zum Swipen der List Elemente sowie die Loesch Funktion.
     */
    private void swipeListItems() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                deleteItem.setWidth(170);
                deleteItem.setIcon(R.drawable.ic_delete);
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
                        db.deleteTodoKategorie(itemID);
                        break;
                }
                return false;
            }
        });
    }

    /**
     * Liest Daten aus der Datenbank und setzt es in eine ListView.
     */
    private void populateTodoListView() {
        Cursor todoCursor = db.getTodo();
        if (todoCursor.getCount() == 0) {
            Toast.makeText(this, "Du hast noch keine TODO erstellt", Toast.LENGTH_LONG).show();
        } else {
            while (todoCursor.moveToNext()) {
                listItem.add(new ToDoItem(todoCursor.getString(1), todoCursor.getString(2), todoCursor.getString(3), todoCursor.getInt(4)));
            }
            todoCursor.close();
            itemAdapter = new ItemAdapter(MainActivity.this, listItem);
            listViewTodoItem.setAdapter(itemAdapter);
        }
    }

    /**
     * Erstellt ein Menue aus der angegebenen XML Datei zum aufklappen.
     *
     * @param menu Menue.
     * @return true - wenn aufgebaut.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Startet Einstellungen Activity.
     *
     * @param item Menu Element.
     * @return Boolean - false - in MainActivity bleiben - true - Einstellungen oeffnen.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.itemSettings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
