package com.daemonic.eventviewer;

import java.text.Format;
import java.util.Set;
import java.util.Vector;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract.Events;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EventMainActivity extends Activity {
	
    private CalendarManager mCal;
    private static final int REQUEST_CODE_PREFERENCES = 1;
    private int mintMaxItems = 40;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.event_main);
    	
    	// Update our preferences
    	updatePreferences();
        
    	// Setup our View!
    	updateView();
       
    }
    
    private void updateView() {
    	
    	 // Find our insertion point
        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.mainview);
        insertPoint.removeAllViews();
        
        // Get our calendar manager, query database
        mCal = new CalendarManager(this);
        int cnt = mCal.RefreshCursor();
        
        // Set up app title to show total number of items
        String appTitle = getString(R.string.app_name);
        appTitle += " ("+Long.toString(cnt) +")";
        this.setTitle(appTitle);
        
        String title = "";
        long start = 0;
        long end = 0;
        long eventID = 0;
        Format df = DateFormat.getDateFormat(this);
        Format tf = DateFormat.getTimeFormat(this);
        
        int i = 0;
        while (i < mintMaxItems) {
        	
        	// make sure we don't go too far
        	if (mCal.mCursor.isAfterLast()) { break; }
        	mCal.mCursor.moveToNext();
        	
        	// progress through our cursor
        	i++;
        	try {
            	title = mCal.mCursor.getString(0);
            	start = mCal.mCursor.getLong(1);
            	end = mCal.mCursor.getLong(2);
            	eventID = mCal.mCursor.getLong(3);
            } catch (Exception e) {
            	//ignore
            }
        	
        	// Setup TextViews for start, end, title
        	TextView q = null;
        	View tv = getLayoutInflater().inflate(R.layout.event_item, null);
            q = (TextView) tv.findViewById(R.id.event_item_datestart);
            q.setText(df.format(start) + " " + tf.format(start));
            q = (TextView) tv.findViewById(R.id.event_item_dateend);
            q.setText(df.format(end) + " " + tf.format(end));
            q = (TextView) tv.findViewById(R.id.event_item_title);
            q.setText(title);
            q.setTag(eventID);
            
            // Set up on click listener
            q.setOnClickListener( new View.OnClickListener() {
            	public void onClick(View v) {
            		Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, (Long) v.getTag());
            		Intent intent = new Intent(Intent.ACTION_VIEW).setData(uri);
            		startActivity(intent);
	            }
	        });           
            
            // Attach to display
            insertPoint.addView(tv);
        }
        
        // Clear our memory
        mCal.UnhookCursor();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_main, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh:
            	updateView();
                return true;
            case R.id.filter:
            	 // app icon in action bar clicked; go home
                Intent intent = new Intent().setClass(this, SettingsActivity.class);
                // Make it a subactivity so we know when it returns
                startActivityForResult(intent, REQUEST_CODE_PREFERENCES);
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // The preferences returned if the request code is what we had given
        // earlier in startSubActivity
        if (requestCode == REQUEST_CODE_PREFERENCES) {
            // Read a sample value they have set
        	updatePreferences();
        	updateView();
        }
    }

    private void updatePreferences() {
        // Since we're in the same package, we can use this context to get
        // the default shared preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        try {
        	String t = sharedPref.getString(SettingsActivity.KEY_ITEMS_TO_DISPLAY, "40");
        	mintMaxItems = (int) Long.parseLong(t.trim());
        	
        	Set<String> sValues = sharedPref.getStringSet(SettingsActivity.KEY_CALS_TO_DISPLAY, null);
        	
        	// Convert to integers
        	Vector<String> vValues = new Vector<String>();
        	vValues.addAll(sValues);
        	mCal.filterCalendars(vValues);
        	
    	} catch (Exception e) {
    		AlertDialog alertDialog;
    		alertDialog = new AlertDialog.Builder(this).create();
    		alertDialog.setTitle("Error");
    		alertDialog.setMessage(e.getMessage());
    		alertDialog.show();
        }
    }
}
