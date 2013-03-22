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
 * Custom layout that arranges children in a grid-like manner, optimizing for even horizontal and
 * vertical whitespace.
 */
public class DashboardLayout extends ViewGroup {
 
    private static final int UNEVEN_GRID_PENALTY_MULTIPLIER = 10;
 
    private int mMaxChildWidth = 0;
    private int mMaxChildHeight = 0;
 
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
        
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        
        Log.w(EventMainActivity.LOG_NAME,"Parent Height: " + Integer.toString(parentHeight) + ", width: " + Integer.toString(parentWidth));
        
        mMaxChildWidth = parentWidth;
        mMaxChildHeight = 0;
        
        // Measure once to find the maximum child size. 
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);
 
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            } 
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec); 
            mMaxChildWidth = Math.max(mMaxChildWidth, child.getMeasuredWidth());
            mMaxChildHeight = Math.max(mMaxChildHeight, child.getMeasuredHeight());
        }
 
        // Measure again for each child to be exactly the same size.
        childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                mMaxChildWidth, MeasureSpec.EXACTLY);
        childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                mMaxChildHeight, MeasureSpec.EXACTLY);
 
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
             child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
        
        
       setMeasuredDimension(
                resolveSize(mMaxChildWidth, parentWidth),
                resolveSize(mMaxChildHeight, heightMeasureSpec));
    }
 
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int height = b - t;
 
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
 
        // Horizontal and vertical space between items
        int hSpace = 0;
        int vSpace = 0;
 
        int cols = 2;
        int rows = count / 2 + 1;
  
        // Lay out children based on calculated best-fit number of rows and cols.
 
        // If we chose a layout that has negative horizontal or vertical space, force it to zero.
        hSpace = Math.max(0, hSpace);
        vSpace = Math.max(0, vSpace);
 
        // Re-use width/height variables to be child width/height.
        width = (width - hSpace * (cols + 1)) / cols;
        height = (height - vSpace * (rows + 1)) / rows;
 
        int left, top;
        int col, row;
        int visibleIndex = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
 
            row = visibleIndex / cols;
            col = visibleIndex % cols;
 
            left = hSpace * (col + 1) + width * col;
            top = vSpace * (row + 1) + height * row;
 
            child.layout(left, top,
                    (hSpace == 0 && col == cols - 1) ? r : (left + width),
                    (vSpace == 0 && row == rows - 1) ? b : (top + height));
            ++visibleIndex;
        }
    }
}