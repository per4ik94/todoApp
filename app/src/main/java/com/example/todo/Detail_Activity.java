package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

/**
 * @author Sergej Cerkasin
 * @version TodoApp 2020/05/01
 */
public class Detail_Activity extends AppCompatActivity implements KategoriePrioDialog.KategoriePrioListener {
    private static final String TAG = "DetailActivity";
    ArrayList prioritaetList, kategorieList;
    String prio;
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

    /**
     * Bei Start die Activity aufbauen, Listen erstellen und fuellen, Buttons Initialisieren und Funktion zuweisen.
     *
     * @param savedInstanceState Gespeicherte zustand der Instanz
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_);
        getSupportActionBar().hide();
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LOCKED);

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

        kategorieList = new ArrayList();
        prioritaetList = new ArrayList();
        db = new DatabaseHelper(this);

        //Todo Inhalt wiederherstellen
        restoreTodoItemDetails();
        //Prioritaet Liste fuellen
        populatePrioList();
        //Kategorie Liste fuellen
        populateKategorieList();

        //Button Erstellen
        btnErstellen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnErstellenAction();
            }
        });
        //Button Aktualisieren
        btnAktualisieren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAktualisierenAction();
            }
        });
        //Button Loeschen
        btnLoeschen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteToDo(todoID);
                db.deleteTodoKategorie(todoID);
                startActivity(new Intent(v.getContext(), MainActivity.class));
            }
        });
        //Button Abbrechen
        btnAbbrechen2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //Button Abbrechen
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

        //Kategorie auswaehlen
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
        //Kategorie hinzufuegen
        imgAddKategorie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KategoriePrioDialog kategoriePrioDialog = new KategoriePrioDialog(kategorieList, "kategorie");
                kategoriePrioDialog.show(getSupportFragmentManager(), "KAT");
                addKategorieClick = true;
                addPrioClick = false;
            }
        });

        //Prioritaet hinzufuegen
        imgAddPrioritaet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KategoriePrioDialog prioritaetDialog = new KategoriePrioDialog(prioritaetList, "prioritaet");
                prioritaetDialog.show(getSupportFragmentManager(), "PRIO");
                addKategorieClick = false;
                addPrioClick = true;
            }
        });
        //Datum auswaehlen
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
                String datum = dayOfMonth + "." + month + "." + year;
                textInputEditTextDatum.setText(datum);
            }
        };
    }

    /**
     * Erstellt einen neuen Eintrag mit den ausgefuellten Daten.
     */
    private void btnErstellenAction() {
        String titel = textInputEditTextTitel.getText().toString().trim();
        String beschreibung = textInputEditTextBeschreibung.getText().toString().trim();
        String datum = textInputEditTextDatum.getText().toString();
        String prioritaet = (String) spinnerPrioritaet.getSelectedItem();
        String kategorie = textInputEditTextKategorieCheckable.getText().toString();

        Cursor prioCursor = db.getPrioritaetID(prioritaet);
        int prioID = -1;
        while (prioCursor.moveToNext()) {
            prioID = prioCursor.getInt(0);
        }
        prioCursor.close();

        if (todoEintragIsValid(titel, beschreibung, kategorie, prioritaet, datum)) {
            addTodoDB(titel, beschreibung, datum, prioID);
            addTodoKategorieDB(getTodoIdFromDB(titel), kategorieItemsSelected);
        } else {
            Snackbar snackbar = Snackbar.make(layoutErstellen, "Es müssen alle Felder ausgefüllt sein!", Snackbar.LENGTH_LONG).setTextColor(Color.RED);
            snackbar.show();
        }
    }

    /**
     * Aktualisiert die Daten eines bereits bestehenden Todo Elements.
     */
    private void btnAktualisierenAction() {
        String titel = textInputEditTextTitel.getText().toString();
        String beschreibung = textInputEditTextBeschreibung.getText().toString();
        String datum = textInputEditTextDatum.getText().toString();
        String prioritaet = spinnerPrioritaet.getSelectedItem().toString();
        String kategorie = textInputEditTextKategorieCheckable.getText().toString();

        Cursor prioIdCursor = db.getPrioritaetID(prioritaet);
        int prioID = -1;
        while (prioIdCursor.moveToNext()) {
            prioID = prioIdCursor.getInt(0);
        }
        prioIdCursor.close();

        ArrayList selecteItems = new ArrayList();
        String[] splitedItems;
        splitedItems = kategorie.split(", ");
        for (String str : splitedItems) {
            selecteItems.add(str);
        }

        if (todoEintragIsValid(titel, beschreibung, kategorie, prioritaet, datum)) {
            db.updateToDo(titel, beschreibung, datum, prioID, todoID);
            db.deleteTodoKategorie(todoID);
            addTodoKategorieDB(getTodoIdFromDB(titel), selecteItems);
            Intent intent = new Intent(Detail_Activity.this, MainActivity.class);
            startActivity(intent);
        } else {
            Snackbar snackbar = Snackbar.make(layoutErstellen, "Es müssen alle Felder ausgefüllt sein!", Snackbar.LENGTH_LONG).setTextColor(Color.RED);
            snackbar.show();
        }

    }

    /**
     * Stellt die Todo Item Details wieder her.
     */
    private void restoreTodoItemDetails() {
        Bundle itemDetail = getIntent().getExtras();
        if (itemDetail != null) {
            String titel = itemDetail.getString("titel");
            todoID = itemDetail.getInt("id");
            String layoutWahl = itemDetail.getString("layout");

            if (layoutWahl.contains("bearbeiten")) {
                layoutBearbeiten.setVisibility(View.VISIBLE);
                layoutErstellen.setVisibility(View.INVISIBLE);
            }

            String beschreibung = getStringOfTodoTable(todoID, 3);
            String date = getStringOfTodoTable(todoID, 1);
            getPrioFromDB(getPrioIntOfTodoTable(todoID, 4));

            textInputEditTextTitel.setText(titel);
            textInputEditTextBeschreibung.setText(beschreibung);
            textInputEditTextKategorieCheckable.setText(getKategorieFromTodoKategorieDB());
            textInputEditTextDatum.setText(date);
        }
    }

    /**
     * Liest Spalte einer Todo aus der Datenbank.
     *
     * @param _todoID Todo ID.
     * @param _spalte Int Spalte
     * @return Inhalt der _spalte als Int
     */
    private int getPrioIntOfTodoTable(int _todoID, int _spalte) {
        Cursor todoCursor = db.getToDo(_todoID);
        int result = -1;
        while (todoCursor.moveToNext()) {
            result = todoCursor.getInt(_spalte);
        }
        todoCursor.close();
        return result;
    }

    /**
     * Liest Spalte einer Todo aus der Datenbank.
     *
     * @param _todoID Todo ID.
     * @param _spalte Int Spalte.
     * @return Inhalt der _spalte als String.
     */
    private String getStringOfTodoTable(int _todoID, int _spalte) {
        Cursor todoCursor = db.getToDo(_todoID);
        String result = "";
        while (todoCursor.moveToNext()) {
            result = todoCursor.getString(_spalte);
        }
        todoCursor.close();
        return result;
    }

    /**
     * Liest die Kategorien aus der Datenbank aus.
     *
     * @return Kategorien.
     */
    private String getKategorieFromTodoKategorieDB() {
        StringBuilder sb = new StringBuilder();
        ArrayList kategorieIDs = new ArrayList();
        Cursor todoKategorieCursor = db.getTodoKategorie(todoID);
        int kategorieID = -1;
        while (todoKategorieCursor.moveToNext()) {
            kategorieID = todoKategorieCursor.getInt(1);
            kategorieIDs.add(kategorieID);
        }
        todoKategorieCursor.close();

        for (int i = 0; i < kategorieIDs.size(); i++) {
            Cursor kategorieCursor = db.getKategorie((Integer) kategorieIDs.get(i));
            String kategorie = "";
            while (kategorieCursor.moveToNext()) {
                kategorie = kategorieCursor.getString(1);
                sb.append(kategorie + ", ");
            }
            kategorieCursor.close();
        }
        return removeLastTwoCharacter(sb.toString());
    }

    /**
     * Liest den Prioritaet Namen aus der Datenbank.
     *
     * @param prioID Prioritaet ID.
     * @return Prioritaet Name.
     */
    public String getPrioFromDB(int prioID) {
        Cursor prioCursor = db.getPrioritaet(prioID);
        while (prioCursor.moveToNext()) {
            prio = prioCursor.getString(1);
        }
        prioCursor.close();
        return prio;
    }

    /**
     * Liest die ID eines Todos aus der Datenbank aus.
     *
     * @param _titel Todo Titel
     * @return Todo ID
     */
    public int getTodoIdFromDB(String _titel) {
        Cursor todoCursor = db.getToDoID(_titel);
        int todoID = -1;
        while (todoCursor.moveToNext()) {
            todoID = todoCursor.getInt(0);
        }
        todoCursor.close();
        return todoID;
    }

    /**
     * liest die ID einer Kategorie aus der Datenbank aus.
     *
     * @param _kategorie Kategorie Name
     * @return Kategorie ID
     */
    public int getKategorieIdFromDB(String _kategorie) {
        Cursor kategorieCursor = db.getKategorieID(_kategorie);
        int kategorieID = -1;
        while (kategorieCursor.moveToNext()) {
            kategorieID = kategorieCursor.getInt(0);
        }
        kategorieCursor.close();
        return kategorieID;
    }


    /**
     * Fuegt Kategorie ID und Todo ID in Datenbank.
     *
     * @param _todoID             Todo ID
     * @param _selectedKategorien Liste mit ausgewählten Kategorien
     */
    private void addTodoKategorieDB(int _todoID, ArrayList<String> _selectedKategorien) {
        if (_selectedKategorien.size() != 0) {
            for (String kategorie : _selectedKategorien) {
                boolean insertData = db.addTodoKategorie(_todoID, getKategorieIdFromDB(kategorie));
                if (insertData) {
                    Log.d(TAG, "addToDOKategorieDB: " + kategorie + " to TODO " + getStringOfTodoTable(_todoID, 2));
                } else {
                    toastMessage("Versuch es noch ein Mal");
                }
            }
        }
    }

    /**
     * Prioritaet Elemente werden aus der Datenbank in die Liste geladen
     */
    private void populatePrioList() {
        Cursor prioCursor = db.getPrioritaet();
        if (prioCursor.getCount() == 0) {
            Toast.makeText(this, "Es ist noch keine Priorität Erstellt wurden", Toast.LENGTH_LONG).show();
        } else {
            while (prioCursor.moveToNext()) {
                prioritaetList.add(prioCursor.getString(1));
            }
        }
        prioCursor.close();
    }

    /**
     * Kategorie Elemente werden aus der Datenbank in die Liste geladen
     */
    private void populateKategorieList() {
        Cursor kategorieCursor = db.getKategorie();
        if (kategorieCursor.getCount() == 0) {
            Toast.makeText(this, "Es ist noch keine Kategorie Erstellt wurden", Toast.LENGTH_LONG).show();
        } else {
            while (kategorieCursor.moveToNext()) {
                kategorieList.add(kategorieCursor.getString(1));
            }
        }
        kategorieCursor.close();
    }

    /**
     * Fuegt TODO Element in die Datenbank hinzu.
     *
     * @param _titel        Todo Titel
     * @param _beschreibung Todo Beschreibung
     * @param _datum        Todo Datum
     * @param _prioID       Todo Prioritaet ID
     */
    private void addTodoDB(String _titel, String _beschreibung, String _datum, int _prioID) {
        boolean insertData = db.addData(_titel, _beschreibung, _datum, _prioID);
        if (insertData) {
            toastMessage(_titel + " Hinzugefügt");
            Log.d("TAG", "addTodoDB: " + _titel + _beschreibung + _datum + getPrioFromDB(_prioID));
            Intent mainActivityIntent = new Intent(Detail_Activity.this, MainActivity.class);
            startActivity(mainActivityIntent);
        } else {
            toastMessage("Versuch es noch ein Mal");
        }
    }


    /**
     * Fuegt Prioritaet in die Datenbank hinzu.
     *
     * @param _prioritaet - Prioritaet Name.
     */
    private void addPrioritaetDB(String _prioritaet) {
        boolean insertData = db.addPrioritaet(_prioritaet);
        if (insertData) {
            toastMessage("Priorität \"" + _prioritaet + "\" Hinzugefügt");
        } else {
            toastMessage("Versuch es noch ein Mal");
        }
    }

    /**
     * Fuegt Kategorie in die Datenbank hinzu.
     *
     * @param _kategorie Kategorie Name.
     */
    private void addKategorieDB(String _kategorie) {
        boolean insertData = db.addKategorie(_kategorie);
        if (insertData) {
            toastMessage("Kategorie \"" + _kategorie + "\" Hinzugefügt");
        } else {
            toastMessage("Versuch es noch ein Mal");
        }
    }

    /**
     * Oeffnet DatePicker Dialog, wo man Datum auswaehlen kann.
     */
    public void datePicker() {
        Calendar calendar = Calendar.getInstance();
        int jahr = calendar.get(Calendar.YEAR);
        int monat = calendar.get(Calendar.MONTH);
        int tag = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(Detail_Activity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, jahr, monat, tag);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }

    /**
     * Oeffnet Dialog Fenster mit vorhandenen Kategorien, die man auswählen kann.
     */
    public void kategorieAlertDialog() {
        final String[] items = new String[kategorieList.size()];
        for (int i = 0; i < kategorieList.size(); i++) {
            items[i] = String.valueOf(kategorieList.get(i));
        }

        kategorieItemsSelected = new ArrayList();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Wähle Kategorien");
        builder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selectedItemId,
                                boolean isSelected) {
                if (isSelected) {
                    kategorieItemsSelected.add(items[selectedItemId]);
                } /*else if (kategorieItemsSelected.contains(selectedItemId)) {
                    kategorieItemsSelected.remove(Integer.valueOf(selectedItemId));
                }*/
            }
        }).setPositiveButton("Fertig!", new DialogInterface.OnClickListener() {
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

    /**
     * Löscht die letzten beiden Stellen eines Strings.
     *
     * @param str String.
     * @return String ohne letzten beiden Stellen.
     */
    public String removeLastTwoCharacter(String str) {
        String result = null;
        if ((str != null) && (str.length() > 0)) {
            result = str.substring(0, str.length() - 2);
        }
        return result;
    }

    /**
     * Speichert Kategorie/Prioritaet in Array & DB
     *
     * @param _eingabeItem Kategorie / Prioritaet
     */
    @Override
    public void applyEingabeText(String _eingabeItem) {
        if (addKategorieClick && kategorieIsValid(_eingabeItem)) {
            kategorieList.add(_eingabeItem);
            addKategorieDB(_eingabeItem);
            addKategorieClick = false;
            addPrioClick = false;
        } else if (addPrioClick && prioritaetIsValid(_eingabeItem)) {
            prioritaetList.add(_eingabeItem);
            addPrioritaetDB(_eingabeItem);
            addPrioClick = false;
            addKategorieClick = false;
        }
    }

    /**
     * Ueberprueft ob Todo Elemente nicht leer sind.
     *
     * @param _titel        Titel
     * @param _beschreibung Beschreibung
     * @param _kategorie    Kategorie
     * @param _prioritaet   Prioritaet
     * @param _datum        Datum
     * @return true - wenn Todo Elemente nicht leer sind. false - wenn Todo Elemente leer sind.
     */
    private boolean todoEintragIsValid(String _titel, String _beschreibung, String _kategorie, String _prioritaet, String _datum) {
        if (datumIsValid(_datum) | beschreibungIsValid(_beschreibung) | kategorieIsValid(_kategorie) | prioritaetIsValid(_prioritaet) | titleIsValid(_titel)) {
            if (titleIsValid(_titel) && beschreibungIsValid(_beschreibung) && kategorieIsValid(_kategorie) && datumIsValid(_datum) && prioritaetIsValid(_prioritaet)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Ueberprueft ob der Titel String leer ist.
     *
     * @param _titel Titel.
     * @return true wenn nicht leer und false wenn leer.
     */
    public boolean titleIsValid(String _titel) {
        if (_titel.isEmpty()) {
            textInputLayoutTitle.setError("Bitte ein Titel eingeben!");
            return false;
        } else {
            textInputLayoutTitle.setError(null);
            return true;
        }
    }

    /**
     * Ueberprueft ob der Beschreibung String leer ist.
     *
     * @param _beschreibung Beschreibung.
     * @return true wenn nicht leer und false wenn leer.
     */
    public boolean beschreibungIsValid(String _beschreibung) {
        if (_beschreibung.isEmpty()) {
            textInputLayoutBeschreibung.setError("Bitte eine beschreibung eingeben!");
            return false;
        } else {
            textInputLayoutBeschreibung.setError(null);
            return true;
        }
    }

    /**
     * Ueberprueft ob der Kategorie String leer ist.
     *
     * @param _kategorie Kategorie.
     * @return true wenn nicht leer und false wenn leer.
     */
    public boolean kategorieIsValid(String _kategorie) {
        if (_kategorie.isEmpty()) {
            textInputEditTextKategorieCheckable.setError("Bitte eine Kategorie auswählen!");
            return false;
        } else {
            textInputEditTextKategorieCheckable.setError(null);
            return true;
        }
    }

    /**
     * Ueberprueft ob der Prioritaet String leer ist.
     *
     * @param _prioritaet Prioritaet
     * @return true wenn nicht leer und false wenn leer.
     */
    public boolean prioritaetIsValid(String _prioritaet) {
        if (_prioritaet == null || _prioritaet.isEmpty()) {
            toastMessage("Versuche es noch ein Mal!");
            return false;
        } else {
            textInputLayoutTitle.setError(null);
            return true;
        }
    }

    /**
     * Ueberprueft ob der Datum String leer ist.
     *
     * @param _datum Datum
     * @return true wenn nicht leer und false wenn leer.
     */
    public boolean datumIsValid(String _datum) {
        if (_datum.isEmpty()) {
            textInputEditTextDatum.setError("Bitte ein Datum Auswählen!");
            return false;
        } else {
            textInputEditTextDatum.setError(null);
            return true;
        }
    }

    /**
     * Selektiert ein Element aus dem Spinner.
     * Vergleicht alle Elemente aus dem Spinner mit uebergebenem _prioItem.
     *
     * @param _spinner  Spinner.
     * @param _prioItem Prioritaet Titel.
     * @return Integer Stelle der Prioritaet.
     */
    private int selectPrioItem(Spinner _spinner, String _prioItem) {
        for (int i = 0; i < _spinner.getCount(); i++) {
            if (_spinner.getItemAtPosition(i).toString().equalsIgnoreCase(_prioItem)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Berechnet wie viel Tage verbleiben
     *
     * @param _futureDate Ausgewähltes Datum
     * @return verbleibende Tage bis ausgewähltem Datum.
     */
    public static CharSequence getCountdown(Date _futureDate) {
        StringBuilder countdownText = new StringBuilder();
        long timeRemaining = _futureDate.getTime() - new Date().getTime();
        if (timeRemaining > 0) {
            int days = (int) TimeUnit.MILLISECONDS.toDays(timeRemaining);
            if (days >= 0) {
                countdownText.append(days);
            }
        }
        return countdownText.toString();
    }

    /**
     * Zeigt eine Toast Nachricht an.
     *
     * @param _message Nachricht
     */
    private void toastMessage(String _message) {
        Toast.makeText(this, _message, Toast.LENGTH_SHORT).show();
    }
}
