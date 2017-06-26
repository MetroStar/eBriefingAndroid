/*
Copyright (C) 2017 MetroStar Systems

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

The full license text can be found is the included LICENSE file.

You can freely use any of this software which you make publicly
available at no charge.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
*/

package com.metrostarsystems.ebriefing.BookPage;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class ActivityPageViewPager extends ViewPager {
	
	private static final String TAG = ActivityPageViewPager.class.getSimpleName();
	
	private ActivityPage mParent;
	
	private long touchDown = 0;
	private long touchUp = 0;
	
	private long touchSeconds = 135; // 135

	private boolean mEnabled;
	
	public ActivityPageViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mEnabled = true;

        setOffscreenPageLimit(4);
	}
	
//	@Override
//	public boolean dispatchTouchEvent(MotionEvent ev) {
//	    Log.v("FROM_DISPATCH_TOUCH_EVENT", "Action = " + ev.getAction());
//	    return true; 
//	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if(mEnabled) {
            Log.i(TAG, "touch");

			if(event.getAction() == MotionEvent.ACTION_UP) {
				touchUp = System.currentTimeMillis();
				
				if(mParent.isNotesOpen()) {
					mParent.notesEditor().close();
					//mParent.showNoteIndicator();
					
					return false;
				}
				
				if(mParent.isContentsOpen()) {
					mParent.hideContents();
				}
				
				if(touchUp - touchDown < touchSeconds) {
					
					mParent.toggleBars();
					
					return false;
				}
			
				
			}

			// Saves the notes when the page is moved
			if(event.getAction() == MotionEvent.ACTION_DOWN) {
				touchDown = System.currentTimeMillis();
			}

            //return false;
			return super.onInterceptTouchEvent(event);
		} else {
			
			if(event.getAction() == MotionEvent.ACTION_UP) {
				touchUp = System.currentTimeMillis();
				
				if(mParent.isNotesOpen()) {
					mParent.notesEditor().close();
					//mParent.showNoteIndicator();
					
					return false;
				}
			}

		}
		
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mEnabled) {

			return super.onTouchEvent(event);
		}
		return false;
	}
	
	public void setPagingEnabled(boolean enabled) {
		mEnabled = enabled;
	}
	
	public void setParent(ActivityPage activity) {
		mParent = activity;
	}

	
}
