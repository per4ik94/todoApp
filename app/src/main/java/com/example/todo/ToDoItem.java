package com.example.todo;

/***
 * @author Sergej Cerkasin
 * @version TodoApp 2020/05/01
 */

public class ToDoItem {
    private String titel, datum, beschreibung;
    private int prioritaetID;

    public ToDoItem(String _datum, String _titel, String _beschreibung, int _prioID) {
        this.titel = _titel;
        this.beschreibung = _beschreibung;
        this.prioritaetID = _prioID;
        this.datum = _datum;
    }


    /**
     * Getter Methode fuer Titel.
     *
     * @return Titel Name.
     */
    public String getTitel() {
        return titel;
    }

    /**
     * Getter Methode fuer Beschreibung.
     *
     * @return Beschreibung Name.
     */
    public String getBeschreibung() {
        return beschreibung;
    }

    /**
     * Getter Methode fuer Prioritaet.
     *
     * @return Prioritaet Name.
     */
    public int getPrioritaetID() {
        return prioritaetID;
    }

    /**
     * Getter Methode fuer Datum.
     *
     * @return Datum
     */
    public String getDatum() {
        return datum;
    }


}
