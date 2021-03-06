package com.example.astri.popularmovies.view;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.example.astri.popularmovies.R;

public class SettingsActivity extends AppCompatPreferenceActivity {

   private static Preference.OnPreferenceChangeListener bindPreferenceSummaryToValueListener=new Preference.OnPreferenceChangeListener() {
       @Override
       public boolean onPreferenceChange(Preference preference, Object newValue) {
           String stringValue = newValue.toString();

           if (preference instanceof ListPreference) {
               // For list preferences, look up the correct display value in
               // the preference's 'entries' list.
               ListPreference listPreference = (ListPreference) preference;
               int index = listPreference.findIndexOfValue(stringValue);

               // Set the summary to reflect the new value.
               preference.setSummary(
                       index >= 0
                               ? listPreference.getEntries()[index]
                               : null);

           } else {
               // For all other preferences, set the summary to the value's
               // simple string representation.
               preference.setSummary(stringValue);
           }
           return true;
       }
   };


    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #bindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(bindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        bindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate ( final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        getFragmentManager().beginTransaction().replace(android.R.id.content, new
                MainPreferenceFragment()).commit();


    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    //inner class with main options

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class MainPreferenceFragment extends  PreferenceFragment {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_general_pref);
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sort_order_key)));
        }
    }
}
