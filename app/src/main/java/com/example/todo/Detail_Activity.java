package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Detail_Activity extends AppCompatActivity implements KategorieDialog.KategorieListener {
    ArrayList prioritaetList, kategorieList;
    String datum, kategorie, prio;
    EditText editTextKategorie;
    TextInputLayout textInputLayoutTitle, textInputLayoutBeschreibung;
    TextInputEditText textInputEditTextBeschreibung, textInputEditTextTitel, textInputEditTextKategorieCheckable, textInputEditTextDatum;
    Button btnErstellen, btnAbbrechen2, btnAbbrechen, btnLoeschen, btnAktualisieren;
    Spinner spinnerPrioritaet;
    ImageButton imgAddKategorie, imgAddPrioritaet;
    ConstraintLayout layoutBearbeiten, layoutErstellen;
    ListView listViewKategorie;
    DatePickerDialog.OnDateSetListener dateSetListener;
    Dialog kategorieDialog;
    boolean addPrioClick = false;
    boolean addKategorieClick = false;
    DatabaseHelper db;
    ArrayList kategorieItemsSelected;
    int todoID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_);
        getSupportActionBar().hide();
        createLayoutItems();

        kategorieList = new ArrayList();
        prioritaetList = new ArrayList();

        //-----------------------NEU--------------------------------------------------------------------------------------------------------------------------------------------------------------------//

        db = new DatabaseHelper(this);

        Bundle itemDetail = getIntent().getExtras();
        if (itemDetail != null) {
            String titel = itemDetail.getString("titel");
            todoID = itemDetail.getInt("id");
            String layoutWahl = itemDetail.getString("layout");

            if (layoutWahl.contains("bearbeiten")) {
                layoutBearbeiten.setVisibility(View.VISIBLE);
                layoutErstellen.setVisibility(View.INVISIBLE);
            }

            //-------------------//
            Cursor todoCursor = db.getToDo(todoID);
            String beschreibung = "";
            String date = "";
            int prioID = -1;
            while (todoCursor.moveToNext()) {
                beschreibung = todoCursor.getString(3);
                prioID = todoCursor.getInt(4);
                date = todoCursor.getString(1);
            }
            //------------------------//


            getPrioFromDB(prioID);
            textInputEditTextTitel.setText(titel);
            textInputEditTextBeschreibung.setText(beschreibung);
            textInputEditTextKategorieCheckable.setText(getKategorieFromTodoKategorieDB());
            //System.out.println(test()+"<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            textInputEditTextDatum.setText(date);

        }

        btnErstellen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titel = textInputEditTextTitel.getText().toString();
                String beschreibung = textInputEditTextBeschreibung.getText().toString();
                String datum = textInputEditTextDatum.getText().toString();
                String focusPrio = spinnerPrioritaet.getSelectedItem().toString();

                Cursor cursor = db.getPriorityID(focusPrio);
                int prioID = -1;
                while (cursor.moveToNext()) {
                    prioID = cursor.getInt(0);
                }
                addTodoDB(datum, titel, beschreibung, prioID);
                addTodoKategorieDB(getTodoIdFromDB(titel), kategorieItemsSelected);
            }
        });

        populatePrio();
        populateCategory();
        //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//

        btnAktualisieren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titel = textInputEditTextTitel.getText().toString();
                String beschreibung = textInputEditTextBeschreibung.getText().toString();
                String datum = textInputEditTextDatum.getText().toString();
                String focusPrio = spinnerPrioritaet.getSelectedItem().toString();
                Cursor cursor = db.getPriorityID(focusPrio);
                int prioID = -1;
                while (cursor.moveToNext()) {
                    prioID = cursor.getInt(0);
                }

                Intent intent = new Intent(v.getContext(), MainActivity.class);
                db.updateToDo(titel, beschreibung, datum, prioID, todoID);
                db.deleteToDoCategories(todoID);
                addTodoKategorieDB(getTodoIdFromDB(titel), kategorieItemsSelected);
                startActivity(intent);
            }
        });

        btnLoeschen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                db.deleteToDo(todoID);
                db.deleteToDoCategories(todoID);
                startActivity(intent);
            }
        });

        btnAbbrechen2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAbbrechen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        ArrayAdapter adapterPrio = new ArrayAdapter(this, android.R.layout.simple_spinner_item, prioritaetList);
        adapterPrio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrioritaet.setAdapter(adapterPrio);


        spinnerPrioritaet.setSelection(selectPrioItem(spinnerPrioritaet, prio));
        spinnerPrioritaet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //focusPrio = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        textInputEditTextKategorieCheckable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!kategorieList.isEmpty()) {
                    kategorieAlertDialog();
                    textInputEditTextKategorieCheckable.setError(null);
                } else {
                    textInputEditTextKategorieCheckable.setError("sss");
                    Snackbar snackbar = Snackbar.make(layoutErstellen, "Füge erst eine Kategorie hinzu!", Snackbar.LENGTH_LONG).setTextColor(Color.RED);
                    snackbar.show();
                }
            }
        });

        imgAddKategorie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KategorieDialog kategorieDialog = new KategorieDialog(kategorieList, "kategorie");
                kategorieDialog.show(getSupportFragmentManager(), "KAT");
                addKategorieClick = true;
                addPrioClick = false;
            }
        });

        imgAddPrioritaet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KategorieDialog prioritaetDialog = new KategorieDialog(prioritaetList, "prioritaet");
                prioritaetDialog.show(getSupportFragmentManager(), "PRIO");
                addKategorieClick = false;
                addPrioClick = true;
            }
        });

        textInputEditTextDatum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                if (month < 10) {
                    month = 0 + month;
                }

                String datum = dayOfMonth + "." + month + "." + year;

                textInputEditTextDatum.setText(datum);
            }
        };


    }

    private String getKategorieFromTodoKategorieDB() {
        StringBuilder sb = new StringBuilder();
        ArrayList katIDs = new ArrayList();
        Cursor todoKategorieCursor = db.getToDoCategory(todoID);
        int kategorieID = -1;
        while (todoKategorieCursor.moveToNext()) {
            kategorieID = todoKategorieCursor.getInt(1);
            katIDs.add(kategorieID);
            System.out.println(kategorieID + "<<>>>><<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>><<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        }
        todoKategorieCursor.close();
        for (int i = 0; i < katIDs.size(); i++) {
            Cursor kategorieCursor = db.getCategory((Integer) katIDs.get(i));
            String kategorie = "";
            while (kategorieCursor.moveToNext()) {
                kategorie = kategorieCursor.getString(1);
                sb.append(kategorie + ", ");
                // System.out.println(sb.toString() + ">>>>><<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            }
            kategorieCursor.close();
        }
        return removeLastTwoCharacter(sb.toString());
    }


    public String getPrioFromDB(int prioID) {
        Cursor prioCursor = db.getPriority(prioID);
        while (prioCursor.moveToNext()) {
            prio = prioCursor.getString(1);
        }
        return prio;
    }

    public int getTodoIdFromDB(String titel) {
        Cursor todoCursor = db.getToDoID(titel);
        int todoID = -1;
        while (todoCursor.moveToNext()) {
            todoID = todoCursor.getInt(0);
        }
        todoCursor.close();
        return todoID;
    }

    public int getKategorieIdFromDB(String kategorie) {

        Cursor kategorieCursor = db.getCategoryID(kategorie);
        int kategorieID = -1;
        while (kategorieCursor.moveToNext()) {
            kategorieID = kategorieCursor.getInt(0);
        }
        kategorieCursor.close();
        return kategorieID;
    }

    private void addTodoKategorieDB(int todoID, ArrayList<String> selectedCategories) {
        if (selectedCategories.size() != 0) {
            for (String kategorie : selectedCategories) {
                boolean insertData = db.addTodoCategory(todoID, getKategorieIdFromDB(kategorie));
                if (insertData) {
                    Log.d("TAG", "addToDOCategory: Added " + kategorie + " (" + getKategorieIdFromDB(kategorie) + ") " + " to TODO " + todoID);
                } else {
                    toastMessage("Something went wrong");
                }
            }
        }
    }


    private void populatePrio() {
        Cursor cursor = db.getPriority();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "Noch keine Prioritäten Erstellt", Toast.LENGTH_LONG).show();
        } else {
            while (cursor.moveToNext()) {
                prioritaetList.add(cursor.getString(1));
            }
            //itemAdapter = new ItemAdapter(MainActivity.this, listItem);
            //listViewTodoItem.setAdapter(itemAdapter);
        }
    }

    private void populateCategory() {
        Cursor cursor = db.getCategory();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "Noch keine Kategorien Erstellt", Toast.LENGTH_LONG).show();
        } else {
            while (cursor.moveToNext()) {
                kategorieList.add(cursor.getString(1));
            }
            //itemAdapter = new ItemAdapter(MainActivity.this, listItem);
            //listViewTodoItem.setAdapter(itemAdapter);
        }
    }

    private void addTodoDB(String title, String description, String datetime, int _prioID) {
        boolean insertData = db.addData(title, description, datetime, _prioID);
        if (insertData) {
            toastMessage("Hinzugefügt");
            Intent mainActivityIntent = new Intent(Detail_Activity.this, MainActivity.class);
            startActivity(mainActivityIntent);
        } else {
            toastMessage("Nicht Hinzugefügt");
        }
    }


    /**
     * Adds a new priority to the database.
     *
     * @param name - priority name
     */
    private void addPrioritaetDB(String name) {
        boolean insertData = db.addPriority(name);
        if (insertData) {
            toastMessage("Data successfully inserted");
            //updatePriorityList();
        } else {
            toastMessage("Something went wrong");
        }
    }

    /**
     * Adds a new category to the database.
     *
     * @param name - category name
     */
    private void addKategorieDB(String name) {
        boolean insertData = db.addCategory(name);
        if (insertData) {
            toastMessage("Data successfully inserted");
        } else {
            toastMessage("Something went wrong");
        }
    }

    public void datePicker() {
        Calendar calendar = Calendar.getInstance();
        int jahr = calendar.get(Calendar.YEAR);
        int monat = calendar.get(Calendar.MONTH);
        int tag = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(Detail_Activity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, jahr, monat, tag);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }

    public void kategorieAlertDialog() {

        String[] items = new String[kategorieList.size()];
        for (int i = 0; i < kategorieList.size(); i++) {
            items[i] = String.valueOf(kategorieList.get(i));
        }

        kategorieItemsSelected = new ArrayList();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Wähle Kategorien");
        final String[] finalItems = items;
        builder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selectedItemId,
                                boolean isSelected) {
                if (isSelected) {
                    kategorieItemsSelected.add(finalItems[selectedItemId]);
                } else if (kategorieItemsSelected.contains(selectedItemId)) {
                    kategorieItemsSelected.remove(Integer.valueOf(selectedItemId));
                }
            }
        })
                .setPositiveButton("Fertig!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        StringBuilder sb = new StringBuilder();
                        for (Object obj : kategorieItemsSelected) {
                            sb.append(obj.toString() + ", ");
                        }


                        textInputEditTextKategorieCheckable.setText(removeLastTwoCharacter(sb.toString()));

                    }
                })
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        kategorieDialog = builder.create();
        kategorieDialog.show();
    }

    public String removeLastTwoCharacter(String str) {
        String result = null;
        if ((str != null) && (str.length() > 0)) {
            result = str.substring(0, str.length() - 2);
        }
        return result;
    }

    @Override
    public void applyText(String _eingabeItem) {
        if (addKategorieClick) {
            kategorieList.add(_eingabeItem);
            addKategorieDB(_eingabeItem);
            addKategorieClick = false;
            addPrioClick = false;
        } else if (addPrioClick) {
            prioritaetList.add(_eingabeItem);
            addPrioritaetDB(_eingabeItem);
            addPrioClick = false;
            addKategorieClick = false;
        }
    }

    public boolean titleIsValid() {
        String titel = textInputLayoutTitle.getEditText().getText().toString().trim();
        if (titel.isEmpty()) {
            textInputLayoutTitle.setError("Bitte ein Titel eingeben!");
            return false;
        } else {
            textInputLayoutTitle.setError(null);
            return true;
        }
    }

    public boolean beschreibungIsValid() {
        String beschreibung = textInputLayoutTitle.getEditText().getText().toString().trim();
        if (beschreibung.isEmpty()) {
            textInputLayoutBeschreibung.setError("Bitte eine beschreibung eingeben!");
            return false;
        } else {
            textInputLayoutBeschreibung.setError(null);
            return true;
        }
    }

    public boolean kategorieIsValid() {
        if (kategorie.isEmpty()) {
            textInputEditTextKategorieCheckable.setError("Bitte mindestens eine Kategorie auswählen!");
            return false;
        } else {
            textInputEditTextKategorieCheckable.setError(null);
            return true;
        }
    }

    public boolean datumIsValid() {
        if (datum.isEmpty()) {
            textInputEditTextDatum.setError("Bitte ein Datum Auswählen!");
            return false;
        } else {
            textInputEditTextDatum.setError(null);
            return true;
        }
    }

    private int selectPrioItem(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }
        return 0;
    }

    public static CharSequence getCountdownText(Context context, Date futureDate) {
        StringBuilder countdownText = new StringBuilder();
        // Calculate the time between now and the future date.
        long timeRemaining = futureDate.getTime() - new Date().getTime();

        // If there is no time between (ie. the date is now or in the past), do nothing
        if (timeRemaining > 0) {
            Resources resources = context.getResources();

            // Calculate the days within the time difference.
            int days = (int) TimeUnit.MILLISECONDS.toDays(timeRemaining);

            // For each time unit, add the quantity string to the output, with a space.
            if (days > 0) {
                countdownText.append(days);
                countdownText.append(" ");
            }
        }
        return countdownText.toString();
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void createLayoutItems() {
        layoutBearbeiten = findViewById(R.id.bearbeiten_button_layout);
        layoutErstellen = findViewById(R.id.erstellen_button_layout);
        textInputLayoutTitle = findViewById(R.id.textInputLayoutTitle);
        textInputLayoutBeschreibung = findViewById(R.id.textInputLayoutBeschreibung);
        textInputEditTextTitel = findViewById(R.id.textTitel);
        textInputEditTextBeschreibung = findViewById(R.id.textBeschreibung);
        textInputEditTextDatum = findViewById(R.id.inputEditTextDatum);
        textInputEditTextKategorieCheckable = findViewById(R.id.inputEditTextKategorieCheckable);

        btnErstellen = findViewById(R.id.btnErstelln);
        btnAbbrechen2 = findViewById(R.id.btnAbbrechen2);
        btnAbbrechen = findViewById(R.id.btnAbbrechen);
        spinnerPrioritaet = findViewById(R.id.spinnerPrioritaet);
        btnLoeschen = findViewById(R.id.btnLoeschen);
        btnAktualisieren = findViewById(R.id.btnAktualisieren);
        imgAddKategorie = findViewById(R.id.imgAddKategorie);
        imgAddPrioritaet = findViewById(R.id.imgAddPrioritaet);
        editTextKategorie = findViewById(R.id.editTextKategorie);
        listViewKategorie = findViewById(R.id.listViewKategorie);
    }

}
