package com.daemonic.eventviewer;

import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceFragment;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

public class SettingsActivity extends Activity {
	
	public static final String KEY_ITEMS_TO_DISPLAY = "items_to_display";  
	public static final String KEY_CALS_TO_DISPLAY = "cal_display";  

	
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
            
            // Get our calendar manager
            //CalendarManager cm = new CalendarManager(getActivity().getApplicationContext());
            
            MultiSelectListPreference mPref = (MultiSelectListPreference) this.findPreference(SettingsActivity.KEY_CALS_TO_DISPLAY);
            String[] cals = new String[] { "All", "Google" };
            String[] ids = new String[] { "1", "2" };
            mPref.setEntries(cals);
            mPref.setEntryValues(ids);
        }
    	    
    }


}