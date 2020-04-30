package com.example.todo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;

public class KategorieDialog extends DialogFragment {
    private EditText editTextKategorie;
    private KategorieListener listener;
    private ArrayList itemList;
    private ArrayAdapter adapter;
    private EditText editTextUpdateItem;
    private Button btnUpdateItem;
    DatabaseHelper db;
    private String katPrio;



    public KategorieDialog(ArrayList _itemList, String _katPrio) {
        this.itemList = _itemList;
        this.katPrio = _katPrio;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        db = new DatabaseHelper(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_bearbeiten_layout, null);


        final SwipeMenuListView listViewKategorie = view.findViewById(R.id.listViewKategorie);
        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, itemList);
        listViewKategorie.setAdapter(adapter);

        builder.setView(view).setTitle("").setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Hinzufügen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String kategorie = editTextKategorie.getText().toString();
                listener.applyText(kategorie.trim());
            }
        });
        editTextKategorie = view.findViewById(R.id.editTextKategorie);


        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "edit" item
                SwipeMenuItem editItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());
                // set item background
                editItem.setBackground(new ColorDrawable(Color.rgb(0,
                        139, 0)));
                // set item width
                editItem.setWidth(170);
                // set a icon
                editItem.setIcon(R.drawable.ic_edit);
                // add to menu
                menu.addMenuItem(editItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());
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
        listViewKategorie.setMenuCreator(creator);

        listViewKategorie.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        updateItemBox((String) itemList.get(position),position);
                        listViewKategorie.setAdapter(adapter);
                        break;
                    case 1:
                       deleteKatPrio(katPrio, position);
                        listViewKategorie.setAdapter(adapter);
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (KategorieListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " implementiere dialog listener");
        }
    }

    public void updateItemBox(String oldItem, final int position){
        final Dialog dialog = new Dialog(getActivity());
        dialog.setTitle("Update Box");
        dialog.setContentView(R.layout.edit_list_view_item);
        editTextUpdateItem = dialog.findViewById(R.id.editTextItemUpdate);
        editTextUpdateItem.setHint(oldItem);
        btnUpdateItem = dialog.findViewById(R.id.btnUpdateItem);
        final String oldName = String.valueOf(itemList.get(position));
        btnUpdateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemList.set(position,editTextUpdateItem.getText().toString());
                katPrioUpdate(oldName, katPrio);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void katPrioUpdate(String _oldName, String _katPrio){
        if (_katPrio.contains("kategorie")) {
            Cursor kategorieID = db.getCategoryID(_oldName);
            int katID = -1;
            while (kategorieID.moveToNext()) {
                katID = kategorieID.getInt(0);
            }
            kategorieID.close();
            db.updateCategory(editTextUpdateItem.getText().toString().trim(), katID, _oldName);
        }else if (_katPrio.contains("prioritaet")) {
            Cursor prioritaetID = db.getPriorityID(_oldName);
            int prioID = -1;
            while (prioritaetID.moveToNext()) {
                prioID = prioritaetID.getInt(0);
            }
            prioritaetID.close();
            db.updatePriority(editTextUpdateItem.getText().toString().trim(), prioID, _oldName);
        }
    }

    public void deleteKatPrio(String _katPrio, int _position){
        if (_katPrio.contains("kategorie")) {
            String name = String.valueOf(itemList.get(_position));
            Cursor kategorieID = db.getCategoryID(name);
            int katID = -1;
            while (kategorieID.moveToNext()) {
                katID = kategorieID.getInt(0);
            }
            kategorieID.close();

            itemList.remove(name);
            db.deleteCategory(katID, name);
        }else if (_katPrio.contains("prioritaet")) {
            String name = String.valueOf(itemList.get(_position));
            Cursor prioritaetID = db.getPriorityID(name);
            int prioID = -1;
            while (prioritaetID.moveToNext()) {
                prioID = prioritaetID.getInt(0);
            }
            prioritaetID.close();

            itemList.remove(name);
            db.deletePriority(prioID, name);
        }
    }

    public interface KategorieListener {
        void applyText(String _kategorie);
    }
}
