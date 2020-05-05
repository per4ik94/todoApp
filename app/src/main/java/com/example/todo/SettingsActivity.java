package com.example.todo;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

/**
 * @author Sergej Cerkasin
 * @version TodoApp 2020/05/01
 */
public class SettingsActivity extends AppCompatActivity {

    /**
     * Bei Start wird Instanz aufgebaut und das Layout gesetzt.
     *
     * @param savedInstanceState Gespeicherte zustand der Instanz.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Zurueck auf die HauptActivity.
     *
     * @param item Menu Element.
     * @return Boolean - false - im Menue bleiben - true - Menue verlassen.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
