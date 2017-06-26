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

package com.metrostarsystems.ebriefing.Dashboard.Search;

import com.metrostarsystems.ebriefing.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;

public class ClearableAutoCompleteTextView extends AutoCompleteTextView {
	
	boolean justCleared = false;

	public ClearableAutoCompleteTextView(Context context) {
		super(context);
		init();
	}
 
	/* Required methods, not used in this implementation */
	public ClearableAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
 
	/* Required methods, not used in this implementation */
	public ClearableAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		// Set the bounds of the button
		setCompoundDrawablesWithIntrinsicBounds(null, null,
				imgClearButton, null);
 
		// if the clear button is pressed, fire up the handler. Otherwise do nothing
		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
 
				ClearableAutoCompleteTextView et = ClearableAutoCompleteTextView.this;
 
				if (et.getCompoundDrawables()[2] == null)
					return false;
 
				if (event.getAction() != MotionEvent.ACTION_UP)
					return false;
 
				if (event.getX() > et.getWidth() - et.getPaddingRight()	- imgClearButton.getIntrinsicWidth()) {
					onClearListener.onClear();
					justCleared = true;
				}
				return false;
			}
		});
	}
	
	private OnClearListener defaultClearListener = new OnClearListener() {
		 
		@Override
		public void onClear() {
			ClearableAutoCompleteTextView et = ClearableAutoCompleteTextView.this;
			et.setText("");
		}
	};
 
	private OnClearListener onClearListener = defaultClearListener;
 
	// The image we defined for the clear button
	public Drawable imgClearButton = getResources().getDrawable(
			R.drawable.ic_clear_search);
 
	public interface OnClearListener {
		void onClear();
	}
	
	public void setImgClearButton(Drawable imgClearButton) {
		this.imgClearButton = imgClearButton;
	}
 
	public void setOnClearListener(final OnClearListener clearListener) {
		onClearListener = clearListener;
	}
 
	public void hideClearButton() {
		setCompoundDrawables(null, null, null, null);
	}
 
	public void showClearButton() {
		setCompoundDrawablesWithIntrinsicBounds(null, null, imgClearButton, null);
	}
}
