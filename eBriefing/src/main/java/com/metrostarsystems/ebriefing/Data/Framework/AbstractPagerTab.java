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

package com.metrostarsystems.ebriefing.Data.Framework;

import com.metrostarsystems.ebriefing.MainApplication;

import android.graphics.Color;

public abstract class AbstractPagerTab<F extends AbstractPagerFragment, T>  {
	
	private static final String TAG = AbstractPagerTab.class.getSimpleName();
	
	private MainApplication		mApp;
	private T 					mTab;
	private boolean				mShowCount = true;
	private String 				mTitle 		= "";
	private F 					mFragment 	= null;
	private int					mIndicatorColor = Color.BLUE;
	private int					mDividerColor = Color.GRAY;
	
	public T tab() { return mTab; }
	
	public void setShowCount(boolean b) {
		mShowCount = b;
	}
	
	public String title() { 
		if(mShowCount) {
			return mTitle + " (" + mFragment.count(mApp) + ")"; 
		} else {
			return mTitle;
		}
	}
	
	public F fragment() { return mFragment; }
	public int indicatorColor() { return mIndicatorColor; }
	public int dividerColor() { return mDividerColor; }
	
	protected AbstractPagerTab(Builder<F, T> build) {
		mApp			= build.mApp;
		mTab			= build.mTab;
		mShowCount		= build.mShowCount;
		mTitle 			= build.mTitle;
		mFragment 		= build.mFragment;
		mIndicatorColor = build.mIndicatorColor;
		mDividerColor 	= build.mDividerColor;
	}
	
	public abstract static class Builder<F extends AbstractPagerFragment, T> {
		private MainApplication		mApp;
		private T 					mTab;
		private boolean				mShowCount = true;
		private String 				mTitle 		= "";
		private F 					mFragment 	= null;
		private int					mIndicatorColor = Color.BLUE;
		private int					mDividerColor = Color.GRAY;
		
		public Builder(MainApplication app) {
			mApp = app;
		}
		
		public Builder<F, T> title(String title) { mTitle = title; return this; }
		public Builder<F, T> fragment(F fragment) { mFragment = fragment; return this; }
		public Builder<F, T> tab(T tab) { mTab = tab; return this; }
		public Builder<F, T> showCount(boolean count) { mShowCount = count; return this; }
		public Builder<F, T> indicatorColor(int color) { mIndicatorColor = color; return this; }
		public Builder<F, T> dividerColor(int color) { mDividerColor = color; return this; }
		public Builder<F, T> indicatorColor(String color) { mIndicatorColor = Color.parseColor(color); return this; }
		public Builder<F, T> dividerColor(String color) { mDividerColor = Color.parseColor(color); return this; }
		
		public abstract AbstractPagerTab<F, T> build();
	}
}
