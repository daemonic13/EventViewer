package com.daemonic.eventviewer;

import java.util.Date;
import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.util.Log;

public class CalendarManager {
	
	private Vector<Long> iCalendarIDs = new Vector<Long>();
	public Cursor mCursor = null;
	private Context mContext = null;
	
	// Event Calendar Strings
	private static final String[] ECOLS = 
			new String[] { 
				Events.TITLE, 
				Events.DTSTART, 
				Events.DTEND, 
				Events._ID
			};
	
	// Calendar Strings
	private static final String[] CCOLS = 
			new String[] {
				Calendars._ID,
				Calendars.NAME
			};
	
	public CalendarManager(Context iContext) {
		mContext = iContext;
	}
	
	public Object[] getCalendars() {
		
		String[] calIDs = null;
		String[] calNames = null;
		Cursor calsCursor = null;
		
		// Retrieve Calendars
		calsCursor = mContext.getContentResolver().query(Calendars.CONTENT_URI, CCOLS, 
			null,null,null);
		calsCursor.moveToFirst();
	
		// Determine our count
		int cLen = calsCursor.getCount();		
		if (cLen == 0) { return null; }
		Log.w("com.daemonic.eventviewer",Long.toString(cLen));
		
		// Move to first item
		calsCursor.moveToFirst();
		
		calIDs = new String[cLen];
		calNames = new String[cLen];
		
		// Push through our calendar entries, building the strings
		for (int i = 0; i < cLen; i++) {
			
			// Cursor Manipulation
			if (calsCursor.isLast()) { break; }
			calsCursor.moveToNext();
			
			// Get the name and id as strings
			String id = Long.toString(calsCursor.getLong(0));
			String name = calsCursor.getString(1);
			if (name == null) {
				name = "My Calendar";
			}
			Log.w("com.daemonic.eventviewer",name);
			Log.w("com.daemonic.eventviewer",id);
			
			// Assign to our array
			calIDs[i] = id;
			calNames[i] = name;
		}
		
		// Clean up the cursor
		calsCursor.close();
		
		return new Object[] {calIDs, calNames};
	}
	
	public void filterCalendars(Vector<Long> vCalendarIDs) {
		// get a list of calendar IDs
		iCalendarIDs.clear();
		iCalendarIDs.addAll(vCalendarIDs);
		
		// re-set internal cursor
		if (mCursor != null) UnhookCursor();
		RefreshCursor();
		
	}
	
	public int RefreshCursor() {
		
        Date d = new Date();
        long startQ = d.getTime();
        String startQS = Long.toString(startQ);
        
        // Query our Events Calendar
        // Select everything that starts now or later
        //    and everything that ends now or later
        // this will catch events in progress and not remove them until they are complete
        mCursor = mContext.getContentResolver().query(Events.CONTENT_URI, ECOLS, 
        			"DTSTART >= ? OR DTEND >= ?",new String[] { startQS, startQS }, "DTSTART, DTEND");
        mCursor.moveToFirst();
        
        return mCursor.getCount();
	}
	
	public void UnhookCursor() {
		// Clean up cursor, unhook from database, clear memory
		mCursor.close();
		mCursor = null;
	}
	
}
