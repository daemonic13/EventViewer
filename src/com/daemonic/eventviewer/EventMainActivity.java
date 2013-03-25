package com.daemonic.eventviewer;

import java.text.Format;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract.Events;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EventMainActivity extends Activity {

	public static final String LOG_NAME = "com.daemonic.eventviewer";
	private EventReader mCal;
	private static final int REQUEST_CODE_PREFERENCES = 1;
	private int mintMaxItems = 40;
	private boolean bIsEmulator = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		String model = Build.MODEL;
		Log.d(LOG_NAME, "model=" + model);
		String product = Build.PRODUCT;
	    Log.d(LOG_NAME, "product=" + product);
	    if (product != null) {
	    	bIsEmulator = product.equals("sdk") || product.contains("_sdk") || product.contains("sdk_");
	    }
	    Log.d(LOG_NAME, "isEmulator=" + bIsEmulator);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_main);

		// Create our calendar manager
		mCal = new EventReader(this);

		// Update our preferences
		updatePreferences();

		// Setup our View!
		updateView();

	}
	
	private EventInstance returnRandomData(int i) {
		
		// Specify Date
		Calendar cDate = Calendar.getInstance();
		int j = (i / 10) + 1;
		Log.w(LOG_NAME,"Day to Add? "+Integer.toString(j));
		
		// Modify Date
		cDate.add(Calendar.DAY_OF_MONTH,j);		
		cDate.add(Calendar.HOUR,i);
		Date t = cDate.getTime();
		
		cDate.add(Calendar.HOUR,2);
		Date t2 = cDate.getTime();
		
		// build some code to return return random data
		EventInstance oEvent = new EventInstance();
		oEvent.set("Testing " + Integer.toString(i),t.getTime() , t2.getTime(), 0);
		Log.w(LOG_NAME,"Random Data Loop #"+Integer.toString(i));
		
		return oEvent;
	}

	private void updateView() {
		
    	// Find our insertion point
		ViewGroup insertPoint = (ViewGroup) findViewById(R.id.mainview);
		insertPoint.removeAllViews();

		int cnt = 0;
		// Get our calendar manager, query database
		if (!bIsEmulator) { cnt = mCal.RefreshCursor(); }

		// Set up app title to show total number of items
		String appTitle = getString(R.string.app_name);
		appTitle += " (" + Long.toString(cnt) + ")";
		this.setTitle(appTitle);

		long prevDate = 0;
		Format tf = DateFormat.getTimeFormat(this);
		
		View currentView = null;
		ViewGroup vq = null;

		int i = 0;
		while (i < mintMaxItems) {
			
			EventInstance oEvent;
			
			if (!bIsEmulator) {
				// make sure we don't go too far
				if (mCal.mCursor.isAfterLast()) {
					break;
				}
				mCal.mCursor.moveToNext();
				
				// Create our instance
				oEvent = new EventInstance();
				
				// progress through our cursor
				try {
					oEvent.set(mCal.mCursor.getString(0),mCal.mCursor.getLong(1),mCal.mCursor.getLong(2),mCal.mCursor.getLong(3));
				} catch (Exception e) {
					// ignore
				}
			} else {
				// use some random data
				oEvent = returnRandomData(i);
			}
			i++;
			TextView q = null;

			// Add date
			Calendar rightNow = Calendar.getInstance();
			rightNow.setTimeInMillis(oEvent.start);
			long itemDay = rightNow.get(Calendar.DAY_OF_MONTH);
			
			if (prevDate != itemDay)
			{
				if (currentView != null){
					insertPoint.addView(currentView);
				}
				currentView = getLayoutInflater().inflate(R.layout.event_dateset, insertPoint, false);
				vq = (ViewGroup) currentView.findViewById(R.id.datesubview);
				
				q = (TextView) currentView.findViewById(R.id.dateheadertext);
				q.setText(DateFormat.format("EEEE MMMM dd, yyyy", oEvent.start));
				rightNow.setTimeInMillis(oEvent.start);
				prevDate = rightNow.get(Calendar.DAY_OF_MONTH);
			}

			// Add event entry
			// Setup TextViews for start, end, title
			View tv = getLayoutInflater().inflate(R.layout.event_item, vq, false);
			q = (TextView) tv.findViewById(R.id.event_item_datestart);
			if (q != null) {
				//q.setText(df.format(start) + " " + tf.format(start));
				q.setText(tf.format(oEvent.start));
				q = (TextView) tv.findViewById(R.id.event_item_dateend);
				//q.setText(df.format(end) + " " + tf.format(end));
				q.setText(tf.format(oEvent.end));
			} else {
				q = (TextView) tv.findViewById(R.id.event_item_datedisplay);
				q.setText(tf.format(oEvent.start) + "\n" + tf.format(oEvent.end));
			}
			q = (TextView) tv.findViewById(R.id.event_item_title);
			q.setText(oEvent.title);
			q.setTag(oEvent.eventID);

			// Set up on click listener
			q.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI,
							(Long) v.getTag());
					Intent intent = new Intent(Intent.ACTION_VIEW).setData(uri);
					startActivity(intent);
				}
			});

			// Attach to display
			vq.addView(tv);
		}
		
		if (currentView != null) {
			insertPoint.addView(currentView);
		}
		
		Log.w(LOG_NAME,"Finished Adding Items...");

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
		case R.id.settings:
			// app icon in action bar clicked; go home
			Intent intent = new Intent().setClass(this, SettingsActivity.class);
			// Make it a sub-activity so we know when it returns
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
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		try {
			String t = sharedPref.getString(
					SettingsActivity.KEY_ITEMS_TO_DISPLAY, "40");
			mintMaxItems = (int) Long.parseLong(t.trim());

			Set<String> sValues = sharedPref.getStringSet(
					SettingsActivity.KEY_CALS_TO_DISPLAY, null);

			// Convert to integers
			if (sValues != null) {
				mCal.filterCalendars(sValues);
			}

		} catch (Exception e) {
			AlertDialog alertDialog;
			alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Error");
			alertDialog.setMessage(e.getMessage());
			alertDialog.show();
		}
	}
}
