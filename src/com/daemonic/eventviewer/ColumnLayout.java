package com.daemonic.eventviewer;
 
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
 
/**
 * Custom Layout that create a configurable column layout
 */
public class ColumnLayout extends ViewGroup {
	
	private int mColumnCount = 1;
 
    public ColumnLayout(Context context) {
        super(context, null);
    }
 
    public ColumnLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        
        TypedArray a = context.obtainStyledAttributes(attrs,
        		R.styleable.ColumnLayout);
        	 
        // Get our Custom Properties
    	final int N = a.getIndexCount();
    	for (int i = 0; i < N; ++i)
    	{
    	    int attr = a.getIndex(i);
    	    switch (attr)
    	    {
    	        case R.styleable.ColumnLayout_numColumns:
    	        	mColumnCount = a.getInteger(attr, 1);
    	            break;
    	    }
    	}
    	a.recycle();

    }
 
    public ColumnLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
 
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	
		int widthSize = MeasureSpec.getSize(widthMeasureSpec) - getPaddingRight();
		Log.w(EventMainActivity.LOG_NAME,"WidthSize = " + Integer.toString(widthSize));
		Log.w(EventMainActivity.LOG_NAME,"onMeasure widthSpec:" + Integer.toString(widthMeasureSpec) + ", heightSpec:" + Integer.toString(heightMeasureSpec));
		int height = 0;

		final int count = getChildCount();
		
		if (count == 0) {
			// Nothing to draw
			Log.w(EventMainActivity.LOG_NAME,"Nothing to draw...");
			setMeasuredDimension(resolveSize(0,widthMeasureSpec),resolveSize(0,heightMeasureSpec));
			return;
		}
		
		// Determine Measure Specs for Children
		int maxChildSize = (widthSize/mColumnCount);
		Log.w(EventMainActivity.LOG_NAME,"Max Width: " + Integer.toString(maxChildSize));
		int cwidthMeasureSpec = MeasureSpec.makeMeasureSpec( maxChildSize, MeasureSpec.AT_MOST);
		int cheightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		
		int[] nHeights = new int[mColumnCount];
		
		// Loop through all the children
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			measureChild(child, cwidthMeasureSpec, cheightMeasureSpec);
			Log.w(EventMainActivity.LOG_NAME,"Measured child: " 
								+ Integer.toString(child.getMeasuredWidth()) + ","
								+ Integer.toString(child.getMeasuredHeight()));
			nHeights[i % mColumnCount] += child.getMeasuredHeight();
		}
		
		height = nHeights[0];
		for (int i = 0; i < mColumnCount; i++) {
			height = Math.max(height,nHeights[i]);
		}
		
		// Add padding, halve height
		height += getPaddingBottom() + getPaddingTop();
		Log.w(EventMainActivity.LOG_NAME,"Measured: "     + Integer.toString(widthSize)
													+ "," + Integer.toString(height));
		
		// Set our dimensions
		setMeasuredDimension(resolveSize(widthSize, widthMeasureSpec),
				resolveSize(height, heightMeasureSpec));
	}
 
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int height = b - t;
        Log.w(EventMainActivity.LOG_NAME,"Layout Width:" + Integer.toString(width) 
        							 + ", Layout Height:"+ Integer.toString(height));
 
        final int count = getChildCount();
 
        // Calculate the number of visible children.
        int visibleCount = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            ++visibleCount;
        }
 
        if (visibleCount == 0) {
            return;
        }
 
        int cols = mColumnCount;
        int rows = visibleCount / cols;
        if (visibleCount % cols != 0) {
        	rows++;
        }
        Log.w(EventMainActivity.LOG_NAME,"Rows == " + Integer.toString(rows));
        
        if (rows == 0) {
        	return;
        }
 
        // Re-use width/height variables to be child width/height.
        int cWidth = width / cols;
        int cHeight = height / rows;
        Log.w(EventMainActivity.LOG_NAME,"Child Width: " + Long.toString(cWidth) 
        							 + ", Child Height: " + Long.toString(cHeight));
 
        int col = 0, row = 0;
        int visibleIndex = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
 
            // Row & Column Position
            col = visibleIndex % cols;
            row = visibleIndex / cols;
 
            // Calculate our children's size
            int cLeft = cWidth * col;
            int cTop = cHeight * row;            
            int cBottom = cTop + cHeight;
            int cRight = cLeft + cWidth;
            Log.w(EventMainActivity.LOG_NAME,"Child:" + Integer.toString(visibleIndex) +  "(" + Long.toString(cLeft) + "," + Long.toString(cTop)
            		+ "," + Long.toString(cRight) + "," + Long.toString(cBottom) + ")");
            
            child.layout(cLeft, cTop, cRight, cBottom);
            ++visibleIndex;
        }
    }
}