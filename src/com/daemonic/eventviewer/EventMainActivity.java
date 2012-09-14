package com.daemonic.eventviewer;

import java.text.Format;

import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EventMainActivity extends Activity {
	
    private CalendarManager mCal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.event_main);
        
        // Find our insertion point
        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.mainview);
        
        // Get our calendar manager, query database
        mCal = new CalendarManager(this);
        mCal.RefreshCursor();
        
        String title = "";
        long start = 0;
        long end = 0;
        long eventID = 0;
        Format df = DateFormat.getDateFormat(this);
        Format tf = DateFormat.getTimeFormat(this);
        
        int i = 0;
        while (i < 40) {
        	if (mCal.mCursor.isAfterLast()) { break; }
        	mCal.mCursor.moveToNext();
        	i++;
        	try {
            	title = mCal.mCursor.getString(0);
            	start = mCal.mCursor.getLong(1);
            	end = mCal.mCursor.getLong(2);
            	eventID = mCal.mCursor.getLong(3);
            } catch (Exception e) {
            	//ignore
            }
        	
        	// Setup TextView
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
            
            insertPoint.addView(tv);
        }
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
