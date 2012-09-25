package com.daemonic.eventviewer;

import java.util.Date;
import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract.Events;
import android.util.Log;

public class EventReader {
	
	public Cursor mCursor = null;
	private Context mContext = null;
	private Vector<String> sCalendarIDs = new Vector<String>();
	
	// Event Calendar Strings
	private static final String[] ECOLS = 
			new String[] { 
				Events.TITLE, 
				Events.DTSTART, 
				Events.DTEND, 
				Events._ID
			};
	
	public EventReader(Context iContext) {
		mContext = iContext;
	}
	
	public void filterCalendars(Vector<String> vCalendarIDs) {
		// get a list of calendar IDs
		sCalendarIDs.clear();
		sCalendarIDs.addAll(vCalendarIDs);
		
		// re-set internal cursor
		if (mCursor != null) UnhookCursor();
		RefreshCursor();
		
	}
	
	public int RefreshCursor() {
		
        Date d = new Date();
        long startQ = d.getTime();
        String startQS = Long.toString(startQ);
        Vector<String> sQueryValues = new Vector<String>();
        
        // Add our first two filter entries
        sQueryValues.add(startQS);
        sQueryValues.add(startQS);
        
        // Setup the filters
        String filterString = "(DTSTART >=? OR DTEND >= ?)";
        if (sCalendarIDs.size() == 0) {
        } else {
        	int i = 0;
        	filterString += " AND (";
        	for (String sCalID : sCalendarIDs) {
        		i++;
        		if (i > 1) {
        			filterString += " OR ";
        		}
        		filterString += Events.CALENDAR_ID + " = ? ";
        		sQueryValues.add(sCalID);
        	}
        	filterString += ")";
        }
        
        Log.w(EventMainActivity.LOG_NAME,filterString);
        String[] sQueryVals = new String[sQueryValues.size()];
        sQueryValues.toArray(sQueryVals);
        for (String f : sQueryVals) {
        	Log.w(EventMainActivity.LOG_NAME,"Filter Value = "+f);
        }
        Log.w(EventMainActivity.LOG_NAME,Long.toString(sQueryVals.length));
        
        // Query our Events Calendar
        // Select everything that starts now or later
        //    and everything that ends now or later
        // this will catch events in progress and not remove them until they are complete
        mCursor = mContext.getContentResolver().query(Events.CONTENT_URI, ECOLS, 
        			filterString, sQueryVals, "DTSTART, DTEND");
        mCursor.moveToFirst();
        
        return mCursor.getCount();
	}
	
	public void UnhookCursor() {
		// Clean up cursor, unhook from database, clear memory
		mCursor.close();
		mCursor = null;
	}

}
