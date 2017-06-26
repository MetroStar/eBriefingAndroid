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

package com.metrostarsystems.ebriefing.BookPage.Contents;

import com.metrostarsystems.ebriefing.BookPage.Contents.TabAll.FragmentAll;
import com.metrostarsystems.ebriefing.BookPage.Contents.TabAnnotated.FragmentAnnotated;
import com.metrostarsystems.ebriefing.BookPage.Contents.TabBookmarked.FragmentBookmarked;
import com.metrostarsystems.ebriefing.BookPage.Contents.TabNoted.FragmentNoted;

import android.app.Fragment;
import android.graphics.Color;


public enum ActivityContentsPagerTab {
	TAB_ALL(			0,	"All",				FragmentAll.newInstance(),			"#FF004D95", "#FF888888"),
	TAB_BOOKMARKED(		1,	"Bookmarks", 		FragmentBookmarked.newInstance(),	"#FF004D95", "#FF888888"),
	TAB_NOTED(			2,	"Notes",			FragmentNoted.newInstance(),		"#FF004D95", "#FF888888"),
	TAB_ANNOTATED(		3,	"Annotations",		FragmentAnnotated.newInstance(),	"#FF004D95", "#FF888888"),
	TAB_TOTAL(			4,	"Total",			null,								"#FF004D95", "#FF888888");
	
	private int 		mId 		= 0;
	private String 		mTitle 		= "";
	private Fragment 	mFragment 	= null;
	private int			mIndicatorColor = Color.BLUE;
	private int			mDividerColor = Color.GRAY;
	
	private ActivityContentsPagerTab(int id, String title, Fragment fragment, 
									int indicatorColor, int dividerColor) {
		mId 		= id;
		mTitle 		= title;
		mFragment 	= fragment;
		mIndicatorColor = indicatorColor;
		mDividerColor = dividerColor;
	}
	
	private ActivityContentsPagerTab(int id, String title, Fragment fragment, 
			String indicatorColor, String dividerColor) {
		mId 		= id;
		mTitle 		= title;
		mFragment 	= fragment;
		mIndicatorColor = Color.parseColor(indicatorColor);
		mDividerColor = Color.parseColor(dividerColor);
	}
	
	public int id() { return mId; }
	public String title() { return mTitle; }
	public Fragment fragment() { return mFragment; }
	public int indicatorColor() { return mIndicatorColor; }
	public int dividerColor() { return mDividerColor; }
	
	public static String tabtTitle(int position) {
		ActivityContentsPagerTab tab = ActivityContentsPagerTab.values()[position];
		
		return tab.title();
	}
	
	public static int tabIndicatorColor(int position) {
		ActivityContentsPagerTab tab = ActivityContentsPagerTab.values()[position];
		
		return tab.indicatorColor();
	}
	
	public static int tabDividerColor(int position) {
		ActivityContentsPagerTab tab = ActivityContentsPagerTab.values()[position];
		
		return tab.dividerColor();
	}
	
	public static Fragment processTab(int index) {
		ActivityContentsPagerTab tab = ActivityContentsPagerTab.values()[index];
		
		switch (tab) {
			case TAB_ALL:			return ActivityContentsPagerTab.TAB_ALL.fragment();
			case TAB_BOOKMARKED: 	return ActivityContentsPagerTab.TAB_BOOKMARKED.fragment(); 
			case TAB_NOTED: 		return ActivityContentsPagerTab.TAB_NOTED.fragment(); 
			case TAB_ANNOTATED: 	return ActivityContentsPagerTab.TAB_ANNOTATED.fragment();
	        default: 				return ActivityContentsPagerTab.TAB_ALL.fragment();
		}
	}
}
