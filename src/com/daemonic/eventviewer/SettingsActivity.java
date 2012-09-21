package com.daemonic.eventviewer;

import java.util.Set;

import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceFragment;
import android.app.Activity;
import android.app.AlertDialog;
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
	            Object[] calData = cm.getCalendars();
	            
	            // Test for missing data
	            if (calData == null) { return; }
	            
	            String[] calIDs = (String[]) calData[0];
	            String[] calNames = (String[]) calData[1];
	            
	            Log.w("com.daemonic.eventviewer",calIDs[0]);
	            Log.w("com.daemonic.eventviewer",calNames[0]);
            
	            // Build up our preference list
	            MultiSelectListPreference mPref = (MultiSelectListPreference) findPreference(SettingsActivity.KEY_CALS_TO_DISPLAY);
	            
	            Set<String> mT = mPref.getValues();
	            mPref.setValues(mT);
	            Log.w("com.daemonic.eventviewer","Entries");
	            for (String z : mT) {
	            	Log.w("com.daemonic.eventviewer",z);
	            }
	            
	            // Set Entries
	            if (calIDs.length > 0) {
	                mPref.setEntries(calNames);
	                mPref.setEntryValues(calIDs);
	                mPref.setDefaultValue(calIDs);
	            } else {
	            	mPref.setEntries(new String[]{ "All"});
	            	mPref.setEntryValues(new String[]{ "1" });
	            }
            }
	       catch (Exception e) {
	    	   	AlertDialog alertDialog;
	    		alertDialog = new AlertDialog.Builder(getActivity().getApplicationContext()).create();
	    		alertDialog.setTitle("Error");
	    		alertDialog.setMessage(e.getMessage());
	    		alertDialog.show();
	       }
            
        }
        
        String[] concat(String[] A, String[] B) {
        	// utility function to append two arrays of strings
        	String[] C= new String[A.length+B.length];
        	System.arraycopy(A, 0, C, 0, A.length);
    	   	System.arraycopy(B, 0, C, A.length, B.length);
    	   	return C;
    	}
    	    
    }

}