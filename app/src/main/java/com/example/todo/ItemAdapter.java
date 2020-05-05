package com.example.todo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.preference.PreferenceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Sergej Cerkasin
 * @version TodoApp 2020/05/01
 */
public class ItemAdapter extends BaseAdapter {
    private static final String TAG = "ItemAdapter";
    private static final String MY_KEY_SIZE = "size";
    private static final String MY_KEY_VISIBILITY = "prio_visibility";
    Activity myActivity;
    ArrayList<ToDoItem> listItem;
    DatabaseHelper db;

    public ItemAdapter(Activity myActivity, ArrayList<ToDoItem> _listItem) {
        this.myActivity = myActivity;
        this.listItem = _listItem;
    }

    /**
     * Zaehlt alle Elemente in der Liste.
     *
     * @return Anzahl Elemente.
     */
    @Override
    public int getCount() {
        return listItem.size();
    }

    /**
     * Gibt das gesuchte Objekt zurueck.
     *
     * @param position Position des gesuchten Elements.
     * @return Das Objekt an der uebergebenen Position
     */
    @Override
    public Object getItem(int position) {
        return listItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Setzt Todo Elemente in ein Benutzerdefiniertes List Layout.
     *
     * @param position  Position des Todos
     * @param item_line Benutzerdefiniertes Layout fuer ein Element der Liste.
     * @param parent    View Gruppe.
     * @return Benutzerdefiniertes Element mit Inhalt.
     */
    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View item_line, ViewGroup parent) {
        db = new DatabaseHelper(myActivity);
        LayoutInflater layoutInflater = (LayoutInflater) myActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        item_line = layoutInflater.inflate(R.layout.item_line, parent, false);

        TextView textTitel = item_line.findViewById(R.id.textTitel);
        TextView textPrio = item_line.findViewById(R.id.tvTextPrio);
        TextView tvPrioUeberschrift = item_line.findViewById(R.id.tvPrioUeberschrift);
        TextView tvDaysLeft = item_line.findViewById(R.id.tvDaysLeft);
        ToDoItem item = (ToDoItem) this.getItem(position);

        textTitel.setText(item.getTitel());

        String textSize = PreferenceManager.getDefaultSharedPreferences(myActivity).getString(MY_KEY_SIZE, "20");
        try {
            textTitel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Float.parseFloat(textSize));
        } catch (NumberFormatException ex) {
            Log.e(TAG, "TextSize: Es sind nur Zahlen erlaubt!");
        }

        Cursor prioCursor = db.getPrioritaet(item.getPrioritaetID());
        String priority = "";
        while (prioCursor.moveToNext()) {
            priority = prioCursor.getString(1);
        }
        prioCursor.close();

        boolean visibility = PreferenceManager.getDefaultSharedPreferences(myActivity).getBoolean(MY_KEY_VISIBILITY, false);
        if (visibility) {
            textPrio.setVisibility(item_line.INVISIBLE);
            tvPrioUeberschrift.setVisibility(item_line.INVISIBLE);
        } else {
            textPrio.setVisibility(item_line.VISIBLE);
            tvPrioUeberschrift.setVisibility(item_line.VISIBLE);
            textPrio.setText(priority);
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Date futureDate = dateFormat.parse(item.getDatum());
            tvDaysLeft.setText(Detail_Activity.getCountdown(futureDate));
        } catch (ParseException e) {
            Log.e(TAG, "dateFormat: " + e.getMessage());
        }
        return item_line;
    }

}

