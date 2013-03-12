package com.daemonic.eventviewer;

import java.text.Format;
import java.util.Set;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CalendarContract.Events;
import android.text.format.DateFormat;
import android.widget.RemoteViews;

public class EventViewerWidget extends AppWidgetProvider {
	
    private EventReader mCal;
    private int mintMaxItems = 40;
	
    public EventViewerWidget() {
    }

    @Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    	
    	// read shared configuration!
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);    	
    	// read shared configurations
    	String t = sharedPref.getString(SettingsActivity.KEY_ITEMS_TO_DISPLAY, "40");
    	mintMaxItems = (int) Long.parseLong(t.trim());    	
    	Set<String> sValues = sharedPref.getStringSet(SettingsActivity.KEY_CALS_TO_DISPLAY, null);
    	
    	// Convert to integers
    	mCal.filterCalendars(sValues);
    	
    	for (int appWidgetID : appWidgetIds) {
    		updateView(context,appWidgetID);
    	}
    	
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
    
    private void updateView(Context context, int appWidgetId) {
    	
   	   // Find our insertion point
       RemoteViews insertPoint = new RemoteViews(context.getPackageName(), R.layout.event_widget);
       
       // Get our calendar manager, query database
       //int cnt = 
       mCal.RefreshCursor();
       
       String title = "";
       long start = 0;
       long end = 0;
       long eventID = 0;
       Format df = DateFormat.getDateFormat(context);
       Format tf = DateFormat.getTimeFormat(context);
       
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
       		RemoteViews tv = new RemoteViews(context.getPackageName(), R.layout.event_item);
		   tv.setTextViewText(R.id.event_item_datestart,df.format(start) + " " + tf.format(start));
		   tv.setTextViewText(R.id.event_item_dateend,df.format(end) + " " + tf.format(end));
		   tv.setTextViewText(R.id.event_item_title,title);
		   
		   // Set up on click listener
		   Intent intent = new Intent(Intent.ACTION_VIEW);
		   Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI,eventID);
		   intent.setData(uri);
		   PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		   tv.setOnClickPendingIntent(R.id.widget_main, pendingIntent);
		   
		   // Attach to display
		   insertPoint.addView(R.id.widget_main, tv);
       }
       
       // Clear our memory
       mCal.UnhookCursor();
   }


	@Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
