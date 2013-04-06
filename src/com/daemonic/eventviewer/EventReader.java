package com.daemonic.eventviewer;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import android.util.Log;

public class EventReader {
	
	private Cursor mCursor = null;
	private Context mContext = null;
	private int mDays = 50; 
	private Vector<String> sCalendarIDs = new Vector<String>();
	
	// Event Calendar Strings
	private static final String[] ECOLS = 
			new String[] {
				Events.DTSTART,
				Events.DTEND,
				Events._ID,
				Events.TITLE
				//"MAX("+Events.DTSTART+")", 
				//"MAX("+Events.DTEND+")"
			};

	// Instance events
	private static final String[] ICOLS = 
			new String[] { 
				Instances.TITLE,
				Instances._ID,
				Instances.DTSTART,
				Instances.DTEND,
				Instances.ALL_DAY,
				Instances.EVENT_ID,
				Instances.BEGIN,
				Instances.END
			};
	
	private static final int iTitlePosition = 0;
	private static final int iIDPosition = 5;
	private static final int iDateStartPosition = 6;
	private static final int iDateEndPosition = 7;
	private static final int iAllDayPosition = 4;
	
	private Vector<Long> iEventIDs = new Vector<Long>();
	
	public EventReader(Context iContext, int iDays) {
		mContext = iContext;
		mDays = iDays;
	}
	
	public void setMaxDays(int iDays) {
		mDays = iDays;
	}
	
	public void filterCalendars(Set<String> vCalendarIDs) {
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
        String filterString = "(DTSTART >=? OR DTEND >= ? OR RRULE is not NULL)";
        if (sCalendarIDs.size() > 0) {
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
        
        //Log.w(EventMainActivity.LOG_NAME,filterString);
        String[] sQueryVals = new String[sQueryValues.size()];
        sQueryValues.toArray(sQueryVals);
        for (String f : sQueryVals) {
        	//Log.w(EventMainActivity.LOG_NAME,"Filter Value = "+f);
        }
        //Log.w(EventMainActivity.LOG_NAME,Long.toString(sQueryVals.length));
        
        // Query our Events Calendar
        // Select everything that starts now or later
        //    and everything that ends now or later
        // this will catch events in progress and not remove them until they are complete
        // Doesn't catch events that are recurring with no end date... :|
        Cursor tCursor = mContext.getContentResolver().query(Events.CONTENT_URI, ECOLS,
    			filterString, sQueryVals, null);
        
        // Get list of Event IDs
        tCursor.moveToFirst();
        long endDate = 0;
        while (!tCursor.isAfterLast())
        {
        	long tDate = Math.max(tCursor.getLong(0),tCursor.getLong(1));
        	endDate = Math.max(tDate,endDate);
        	iEventIDs.add(tCursor.getLong(2));
        	//Log.w(EventMainActivity.LOG_NAME,tCursor.getString(3) + ", Event ID: " + Long.toString(tCursor.getLong(2)));
        	
        	// Move to next row 
        	tCursor.moveToNext();
        }
        tCursor.close();    
        tCursor = null;
        
        // Determine End Point
        Calendar c1 = Calendar.getInstance();
        long startOfTime = c1.getTime().getTime();
        c1.add(Calendar.DAY_OF_MONTH, mDays);
        long endOfDate = c1.getTime().getTime();
        //Log.w(EventMainActivity.LOG_NAME,"Start Date "+Long.toString(startOfTime));
        //Log.w(EventMainActivity.LOG_NAME,"End   Date "+Long.toString(endOfDate));    
        //Log.w(EventMainActivity.LOG_NAME,"Number of Days " + Long.toString(mDays));
        
        // Get All Instances over our time period
        mCursor = Instances.query(mContext.getContentResolver(), ICOLS, startOfTime, endOfDate);
        mCursor.moveToFirst();
        
        return mCursor.getCount();
	}
	
	public void UnhookCursor() {
		// Clean up cursor, unhook from database, clear memory
		if (mCursor != null) { 
			mCursor.close();
			mCursor = null;
		}
	}
	
	public EventInstance getNext()	{
		EventInstance eInstance = null;
		
		while (!pastEnd() && !iEventIDs.isEmpty()) {
			
			long eventID = mCursor.getLong(iIDPosition);
			//Log.w(EventMainActivity.LOG_NAME,"Event ID to Add: " + Long.toString(eventID));
			if (iEventIDs.contains(eventID)) {
				// process
				break;
			} else {
				mCursor.moveToNext();
			}
		}
		
		if (!pastEnd()) {
			
			// get next item
			eInstance = new EventInstance();
			
			// progress through our cursor
			try {
				eInstance.set(	mCursor.getString(iTitlePosition),
								mCursor.getLong(iDateStartPosition),
								mCursor.getLong(iDateEndPosition),
								mCursor.getLong(iIDPosition),
								(mCursor.getLong(iAllDayPosition)!=0) );
			} catch (Exception e) {
				// log error?
			}
			
			mCursor.moveToNext();
		}
		
		return eInstance;
	}
	
	public boolean pastEnd()	{
		return mCursor.isAfterLast();
	}
	
	public void resetQuery() {
		mCursor.moveToFirst();
	}
}
