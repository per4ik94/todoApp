package com.example.todo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ItemAdapter extends BaseAdapter {
    Activity mainActivity;
    float textSize = 20 ;
    ArrayList<ToDoItem> listItem;
    DatabaseHelper db;

    public ItemAdapter(Activity mainActivity, ArrayList<ToDoItem> _listItem) {
        this.mainActivity = mainActivity;
        this.listItem = _listItem;
    }

    public void setTextSizes(float textSize) {
        this.textSize = textSize;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public Object getItem(int position) {
        return listItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        db = new DatabaseHelper(mainActivity);
        View item_line;
        LayoutInflater layoutInflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        item_line = layoutInflater.inflate(R.layout.item_line, parent, false);
        TextView textTitel = item_line.findViewById(R.id.textTitel);
        TextView textPrio = item_line.findViewById(R.id.textPrio);
        TextView tvDaysLeft = item_line.findViewById(R.id.tvDaysLeft);
        textTitel.setTextSize(textSize);
        ToDoItem item = (ToDoItem) this.getItem(position);
        textTitel.setText(item.getTitel());

        Cursor cursor = db.getPriority(item.getPrioritaetID());
        String priority = "";
        while (cursor.moveToNext()) {
            priority = cursor.getString(1);
        }

        textPrio.setText(priority);
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Date futureDate = dateFormat.parse(item.getDatum());
            tvDaysLeft.setText(Detail_Activity.getCountdownText(mainActivity, futureDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return item_line;
    }

}

