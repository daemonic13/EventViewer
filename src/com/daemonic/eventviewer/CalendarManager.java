package com.daemonic.eventviewer;

import java.util.Date;
import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;

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
	
	public void FilterCalendars(Vector<Long> vCalendarIDs) {
		// get a list of calendar IDs
		iCalendarIDs.clear();
		iCalendarIDs.addAll(vCalendarIDs);
		
		// re-set internal cursor
		
	}
	
	public void RefreshCursor() {
		
        Date d = new Date();
        long startQ = d.getTime();
        String startQS = Long.toString(startQ);
        
        // Query our Events Calendar
        mCursor = mContext.getContentResolver().query(Events.CONTENT_URI, ECOLS, 
        			"DTSTART >= ?",new String[] { startQS }, "DTSTART");
        mCursor.moveToFirst();
	}
	
}
