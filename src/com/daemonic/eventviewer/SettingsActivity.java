package com.daemonic.eventviewer;

import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class SettingsActivity extends Activity  {
	
	public static final String KEY_ITEMS_TO_DISPLAY = "items_to_display";  
	public static final String KEY_CALS_TO_DISPLAY = "caldisplay";
	private SettingsFragment k = new SettingsFragment();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction() 
                .replace(R.id.settingsview,k)
                .commit();
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_settings, menu);
        return true;
    }

    protected void onResume() {
        super.onResume();
        // Instance field for listener
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	prefs.registerOnSharedPreferenceChangeListener(k); 
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public static class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
    	
        @Override
        public void onCreate(Bundle savedInstanceState) {
        	
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.settings);
            
            // Get our calendar manager and calendar names/IDs
            CalendarManager cm = new CalendarManager(getActivity().getApplicationContext());
            int calCnt = cm.getCalendars();
            
            // Test for missing data
            if (calCnt == 0) { return; }
            
            // Get data
            String[] calIDs = new String[calCnt];
            cm.getCalendarIDs().toArray(calIDs);
            String[] calNames = new String[calCnt];
            cm.getCalendarNames().toArray(calNames);
            
            // Build up our preference list
            MultiSelectListPreference mPref = (MultiSelectListPreference) findPreference(SettingsActivity.KEY_CALS_TO_DISPLAY);
            
            // Set Entries
            if (calIDs.length > 0) {
                mPref.setEntries(calNames);
                mPref.setEntryValues(calIDs);
                mPref.setDefaultValue(new String[0]);
            } else { 
            	mPref.setEntries(new String[]{ "All"});
            	mPref.setEntryValues(new String[]{ "1" });
            }            
        }
        
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(KEY_ITEMS_TO_DISPLAY)) {
                Preference connectionPref = findPreference(key);
                // Set summary to be the user-description for the selected value
                connectionPref.setSummary(R.string.items_to_display_summary + "(" + sharedPreferences.getString(key, "") + ")");
                Log.w(EventMainActivity.LOG_NAME, "Setting Dialog Preference");
            }
        }
   	    
    }

}