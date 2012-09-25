package com.daemonic.eventviewer;

import java.util.Set;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceFragment;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;

public class SettingsActivity extends Activity {
	
	public static final String KEY_ITEMS_TO_DISPLAY = "items_to_display";  
	public static final String KEY_CALS_TO_DISPLAY = "caldisplay";  

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction() 
                .replace(R.id.settingsview, new SettingsFragment())
                .commit();
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_settings, menu);
        return true;
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
    
    public static class SettingsFragment extends PreferenceFragment {
    	
        @Override
        public void onCreate(Bundle savedInstanceState) {
        	
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.settings);
            
            try {
            
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
	            
	            Log.w(EventMainActivity.LOG_NAME,calIDs[0]);
	            Log.w(EventMainActivity.LOG_NAME,calNames[0]);
            
	            // Build up our preference list
	            MultiSelectListPreference mPref = (MultiSelectListPreference) findPreference(SettingsActivity.KEY_CALS_TO_DISPLAY);
	            
	            Set<String> mT = mPref.getValues();
	            mPref.setValues(mT);
	            Log.w(EventMainActivity.LOG_NAME,"Entries");
	            for (String z : mT) {
	            	Log.w(EventMainActivity.LOG_NAME,z);
	            }
	            
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
	       catch (Exception e) {
	    	   	Log.e(EventMainActivity.LOG_NAME,"Settings Crash == " + e.getMessage());
	       }
            
        }
    	    
    }

}