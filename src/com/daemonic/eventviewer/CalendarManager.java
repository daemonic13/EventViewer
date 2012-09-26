package com.daemonic.eventviewer;

import java.util.Vector;
import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract.Calendars;
import android.util.Log;

public class CalendarManager {
	
	private Vector<String> sCalendarIDs = new Vector<String>();
	private Vector<String> sCalendarNames = new Vector<String>();
	private Context mContext = null;

	// Calendar Strings
	private static final String[] CCOLS = 
			new String[] {
				Calendars._ID,
				Calendars.NAME
			};
	
	public CalendarManager(Context iContext) {
		mContext = iContext;
	}
	
	public int getCalendars() {
		
		Cursor calsCursor = null;
		
		// Retrieve Calendars
		calsCursor = mContext.getContentResolver().query(Calendars.CONTENT_URI, CCOLS, 
			null,null,null);
		calsCursor.moveToFirst();
	
		// Determine our count
		int cLen = calsCursor.getCount();		
		if (cLen == 0) { return 0; }
		Log.w(EventMainActivity.LOG_NAME,Long.toString(cLen));
		
		// Move to first item
		calsCursor.moveToFirst();
		sCalendarIDs.clear();
		sCalendarNames.clear();
		
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
			if (id == null) {
				id = "0";
			}
			Log.w(EventMainActivity.LOG_NAME,name);
			Log.w(EventMainActivity.LOG_NAME,id);
			
			// Assign to our array
			sCalendarIDs.add(id);
			sCalendarNames.add(name);
		}
		
		// Clean up the cursor
		calsCursor.close();
		
		// Convert to arrays on the way out
		return sCalendarIDs.size();
	}
	
	public Vector<String> getCalendarNames() {
		return sCalendarNames;
	}
	
	public Vector<String> getCalendarIDs() {
		return sCalendarIDs;
	}
	
}
