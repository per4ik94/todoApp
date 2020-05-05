package com.example.todo;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

/**
 * @author Sergej Cerkasin
 * @version TodoApp 2020/05/01
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * Bei Start wird die Instanz geladen und der Präferenz Listener gesetzt.
     *
     * @param savedInstanceState Gespeicherte zustand der Instanz.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * Beim Erstellen der Präferenzen,
     * wird das Layout gesetzt und
     * die Präferenzzusammenfassungen ausgelesen und gesetzt.
     *
     * @param savedInstanceState Gespeicherte zustand der Instanz.
     * @param rootKey            Root Key.
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.setting_prefs);
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();

        for (int i = 0; i < count; i++) {
            Preference preference = prefScreen.getPreference(i);
            if (!(preference instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummary(preference, value);
            }
        }
    }

    /**
     * Setzt die Zusammenfassung für die EditText Voreinstellungen.
     *
     * @param preference Voreinstellung.
     * @param value      Wert das gesetzt wird.
     */
    private void setPreferenceSummary(Preference preference, String value) {
        if (preference instanceof EditTextPreference) {
            preference.setSummary(value);
        }
    }

    /**
     * Ueberprueft welche Voreinstellung sich geaendert hat und Aktualisiert die Zusammenfassung.
     *
     * @param sharedPreferences Shared preference.
     * @param key               Voreinstellung Key.
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (preference != null) {
            if (!(preference instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(preference.getKey(), "20");
                setPreferenceSummary(preference, value);
            }
        }
    }
}