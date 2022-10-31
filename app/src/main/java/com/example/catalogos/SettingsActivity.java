package com.example.catalogos;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate (savedInstanceState);
        setContentView (R.layout.settings_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
//        View dbFileId = findViewById (R.id.database_file_id);
        setSupportActionBar(toolbar);
//        toolbar.setTitle (R.string.catalogues);
        toolbar.setLogo (R.drawable.ic_baseline_settings_24_white);
        if (savedInstanceState == null) {
            getSupportFragmentManager ()
                    .beginTransaction ()
                    .replace (R.id.settings, new SettingsFragment ())
                    .commit ();
        }
        ActionBar actionBar = getSupportActionBar ();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled (true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey){
            setPreferencesFromResource (R.xml.root_preferences, rootKey);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}