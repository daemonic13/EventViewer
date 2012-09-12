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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.Format;
import java.util.Date;

import android.text.format.DateFormat;

public class EventMainActivity extends Activity {
	
	private Cursor mCursor;
    private static final String[] COLS = new String[] { Events.TITLE, Events.DTSTART, Events.DTEND, Events._ID};

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_main);
        
        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.mainview);
        
        Format df = DateFormat.getDateFormat(this);
    	Format tf = DateFormat.getTimeFormat(this);
    	String title = "";
        long start = 0;
        long end = 0;
        long eventID = 0;
        
        Date d = new Date();
        long startQ = d.getTime();
        String startQS = Long.toString(startQ);
        
        // Query our Events Calendar
        mCursor = getContentResolver().query(Events.CONTENT_URI, COLS, 
        			"DTSTART >= ?",new String[] { startQS }, "DTSTART");
        mCursor.moveToFirst();
                
        int i = 0;
        while (i < 40) {
        	if (mCursor.isAfterLast()) { break; }
        	mCursor.moveToNext();
        	i++;
        	try {
            	title = mCursor.getString(0);
            	start = mCursor.getLong(1);
            	end = mCursor.getLong(2);
            	eventID = mCursor.getLong(3);
            } catch (Exception e) {
            	//ignore
            }
        	TextView q = null;
        	View tv = getLayoutInflater().inflate(R.layout.event_item, null);
            q = (TextView) tv.findViewById(R.id.event_item_datestart);
            q.setText(df.format(start) + " " + tf.format(start));
            q = (TextView) tv.findViewById(R.id.event_item_dateend);
            q.setText(df.format(end) + " " + tf.format(end));
            q = (TextView) tv.findViewById(R.id.event_item_title);
            q.setText(title);
            q.setTag(eventID);
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
}
