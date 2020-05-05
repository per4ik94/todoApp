package com.example.todo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;

/**
 * @author Sergej Cerkasin
 * @version TodoApp 2020/05/01
 */
public class KategoriePrioDialog extends DialogFragment {
    private static final String TAG = "KategoriePrioDialog";
    private EditText editTextKategoriePrio;
    private KategoriePrioListener kategoriePrioListener;
    private ArrayList itemList;
    private ArrayAdapter adapter;
    private EditText editTextUpdateItem;
    private Button btnUpdateItem;
    DatabaseHelper db;
    private String katPrio;
    SwipeMenuListView listViewKategoriePrio;
    String dialogTitel;


    public KategoriePrioDialog(ArrayList _itemList, String _katPrio) {
        this.itemList = _itemList;
        this.katPrio = _katPrio;
    }

    /**
     * Erstellt ein Dialog Fenster mit List Elementen, zum Bearbeiten der Prioritaet / Kategorie.
     *
     * @param savedInstanceState Gespeicherte zustand der Instanz.
     * @return Dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_bearbeiten_layout, null);

        editTextKategoriePrio = view.findViewById(R.id.editTextKategorie);
        listViewKategoriePrio = view.findViewById(R.id.listViewKategorie);
        db = new DatabaseHelper(getContext());

        if (katPrio.contains("kategorie")) {
            editTextKategoriePrio.setHint("Kategorie eingeben");
            dialogTitel = "Kategorien Bearbeiten";
        } else if (katPrio.contains("prioritaet")) {
            editTextKategoriePrio.setHint("Priorität eingeben");
            dialogTitel = "Prioritäten Bearbeiten";
        }

        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, itemList);
        listViewKategoriePrio.setAdapter(adapter);
        swipeListItem();

        builder.setView(view).setTitle(dialogTitel).setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Hinzufügen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String kategorie = editTextKategoriePrio.getText().toString();
                kategoriePrioListener.applyEingabeText(kategorie.trim());
            }
        });
        return builder.create();
    }

    /**
     * Prioritaet / Kategorie Listener Initialisieren.
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            kategoriePrioListener = (KategoriePrioListener) context;
        } catch (ClassCastException ex) {
            Log.e(TAG, "Implementiere DialogListener " + ex.getMessage());
        }
    }

    /**
     * Erstellt ein Dialog Fenster zum Bearbeiten des ausgewaehlten Elements.
     *
     * @param _oldItemName Alter Prioritaet / Kategorie Name.
     * @param _position    Position einer Prioritaet / Kategorie in der Liste.
     */
    private void updateItemBox(String _oldItemName, final int _position) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.edit_list_view_item);

        editTextUpdateItem = dialog.findViewById(R.id.editTextItemUpdate);
        btnUpdateItem = dialog.findViewById(R.id.btnUpdateItem);

        editTextUpdateItem.setHint(_oldItemName);

        final String oldName = String.valueOf(itemList.get(_position));
        btnUpdateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextUpdateItem.getText().toString().isEmpty()) {
                    itemList.set(_position, editTextUpdateItem.getText().toString().trim());
                    katPrioUpdate(oldName, katPrio);
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Die Eingabefläche darf nicht leer bleiben!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    /**
     * Erstellt eine Einstellung zum Swipen der List Elemente sowie Bearbeitung und die Loesch Funktion.
     */
    private void swipeListItem() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem editItem = new SwipeMenuItem(getActivity().getApplicationContext());
                editItem.setBackground(new ColorDrawable(Color.rgb(0, 139, 0)));
                editItem.setWidth(170);
                editItem.setIcon(R.drawable.ic_edit);
                menu.addMenuItem(editItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity().getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                deleteItem.setWidth(170);
                deleteItem.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(deleteItem);
            }
        };
        listViewKategoriePrio.setMenuCreator(creator);

        listViewKategoriePrio.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        updateItemBox((String) itemList.get(position), position);
                        listViewKategoriePrio.setAdapter(adapter);
                        break;
                    case 1:
                        deleteKatPrio(katPrio, position);
                        listViewKategoriePrio.setAdapter(adapter);
                        break;
                }
                return false;
            }
        });
    }

    /**
     * Benennt eine Prioritaet / Kategorie um.
     *
     * @param _oldName Alter Prioritaet / Kategorie Name.
     * @param _katPrio Prioritaet oder Kategorie Entscheidung.
     */
    private void katPrioUpdate(String _oldName, String _katPrio) {
        if (_katPrio.contains("kategorie")) {
            Cursor kategorieID = db.getKategorieID(_oldName);
            int katID = -1;
            while (kategorieID.moveToNext()) {
                katID = kategorieID.getInt(0);
            }
            kategorieID.close();
            db.updateKategorie(editTextUpdateItem.getText().toString().trim(), katID, _oldName);
        } else if (_katPrio.contains("prioritaet")) {
            Cursor prioritaetID = db.getPrioritaetID(_oldName);
            int prioID = -1;
            while (prioritaetID.moveToNext()) {
                prioID = prioritaetID.getInt(0);
            }
            prioritaetID.close();
            db.updatePrioritaet(editTextUpdateItem.getText().toString().trim(), prioID, _oldName);
        }
    }

    /**
     * Loescht eine Prioritaet / Kategorie.
     *
     * @param _katPrio  Prioritaet / Kategorie Auswahl
     * @param _position Prioritaet / Kategorie Position in der Liste.
     */
    private void deleteKatPrio(String _katPrio, int _position) {
        if (_katPrio.contains("kategorie")) {
            String kategorieName = String.valueOf(itemList.get(_position));
            Cursor kategorieID = db.getKategorieID(kategorieName);
            int katID = -1;
            while (kategorieID.moveToNext()) {
                katID = kategorieID.getInt(0);
            }
            kategorieID.close();

            itemList.remove(kategorieName);
            db.deleteKategorie(katID, kategorieName);

        } else if (_katPrio.contains("prioritaet")) {
            String prioritaetName = String.valueOf(itemList.get(_position));
            Cursor prioritaetID = db.getPrioritaetID(prioritaetName);
            int prioID = -1;
            while (prioritaetID.moveToNext()) {
                prioID = prioritaetID.getInt(0);
            }
            prioritaetID.close();

            itemList.remove(prioritaetName);
            db.deletePrioritaet(prioID, prioritaetName);
        }
    }

    /**
     * Interface fuer das Speichern des Eingegebenem Text
     */
    public interface KategoriePrioListener {
        void applyEingabeText(String _katPrio);
    }
}
