package com.example.todo;

public class ToDoItem {
    private String titel, datum, beschreibung;
    private int prioritaetID;
    public ToDoItem(String _datum, String _titel, String _beschreibung, int _prioID) {
        this.titel = _titel;
        this.beschreibung = _beschreibung;
        this.prioritaetID = _prioID;
        this.datum = _datum;
    }




    public String getTitel() {
        return titel;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public int getPrioritaetID() {
        return prioritaetID;
    }

    public String getDatum() {
        return datum;
    }


}
