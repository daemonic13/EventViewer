package com.daemonic.eventviewer;
/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
 
/**
 * Custom Layout that does a two column view (supposedly...)
 */
public class DashboardLayout extends ViewGroup {
	
	final int mColumnCount = 3;
 
    public DashboardLayout(Context context) {
        super(context, null);
    }
 
    public DashboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }
 
    public DashboardLayout(Context context, AttributeSet attrs, int defStyle) {
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
		
		setMeasuredDimension(resolveSize(widthSize, widthMeasureSpec),
				resolveSize(height, heightMeasureSpec));
		
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
			Log.w(EventMainActivity.LOG_NAME,"Measured child:" 
								+ Integer.toString(child.getMeasuredWidth()) + "," 
								+ Integer.toString(child.getMeasuredWidth()));
			if (i % mColumnCount == 0) {
				nHeights[i % mColumnCount] += child.getMeasuredHeight();
			}
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
        int rows = visibleCount / cols + 1;
        
        if (rows == 0) {
        	return;
        }
 
        // Re-use width/height variables to be child width/height.
        int cWidth = (width - (cols + 1)) / cols;
        int cHeight = (height - (rows + 1)) / rows;
        Log.w(EventMainActivity.LOG_NAME,"Child Width: " + Integer.toString(cWidth) 
        							 + ", Child Height: " + Integer.toString(cHeight));
 
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
            int cBottom = (row == rows - 1) ? b : (cTop + cHeight);
            int cRight = (col == cols - 1) ? r : (cLeft + cWidth);
            Log.w(EventMainActivity.LOG_NAME,"Child:" + Integer.toString(visibleIndex) +  "(" + Integer.toString(cLeft) + "," + Integer.toString(cTop)
            		+ "," + Integer.toString(cRight) + "," + Integer.toString(cBottom) + ")");
            
            child.layout(cLeft, cTop, cRight, cBottom);
            ++visibleIndex;
        }
    }
}