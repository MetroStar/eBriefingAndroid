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

package com.metrostarsystems.ebriefing.BookPage.Contents.Tab;

import java.util.ArrayList;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.BookPage.Contents.TabAll.FragmentAll;
import com.metrostarsystems.ebriefing.BookPage.Contents.TabAnnotated.FragmentAnnotated;
import com.metrostarsystems.ebriefing.BookPage.Contents.TabBookmarked.FragmentBookmarked;
import com.metrostarsystems.ebriefing.BookPage.Contents.TabNoted.FragmentNoted;
import com.metrostarsystems.ebriefing.Data.Framework.AbstractPagerFragment;

public class ContentTabs {

	private MainApplication mApp;
	private ArrayList<ContentPagerTab> mTabs;
	
	public ContentTabs(MainApplication main) {
		mApp = main;
		
		mTabs = new ArrayList<ContentPagerTab>();
		
		mTabs.add((ContentPagerTab) new ContentPagerTab.Builder(mApp)
												.tab(ContentTab.TAB_ALL)
												.title("All")
												.fragment(FragmentAll.newInstance())
												.indicatorColor("#FF004D95")
												.dividerColor("#FF888888")
												.build());
		mTabs.add((ContentPagerTab) new ContentPagerTab.Builder(mApp)
												.tab(ContentTab.TAB_BOOKMARKS)
												.title("Bookmarks")
												.fragment(FragmentBookmarked.newInstance())
												.indicatorColor("#FF004D95")
												.dividerColor("#FF888888")
												.build());
		
		mTabs.add((ContentPagerTab) new ContentPagerTab.Builder(mApp)
												.tab(ContentTab.TAB_NOTES)
												.title("Notes")
												.fragment(FragmentNoted.newInstance())
												.indicatorColor("#FF004D95")
												.dividerColor("#FF888888")
												.build());
		
		mTabs.add((ContentPagerTab) new ContentPagerTab.Builder(mApp)
												.tab(ContentTab.TAB_ANNOTATIONS)
												.title("Annotations")
												.fragment(FragmentAnnotated.newInstance())
												.indicatorColor("#FF004D95")
												.dividerColor("#FF888888")
												.build());
	}
	
	public int total() {
		return mTabs.size();
	}
	
	public String tabTitle(int position) {
		ContentPagerTab tab = mTabs.get(position);
		
		return tab.title();
	}
	
	public int tabIndicatorColor(int position) {
		ContentPagerTab tab = mTabs.get(position);
		
		return tab.indicatorColor();
	}
	
	public int tabDividerColor(int position) {
		ContentPagerTab tab = mTabs.get(position);
		
		return tab.dividerColor();
	}
	
	public AbstractPagerFragment processTab(int index) {
		ContentPagerTab tab = mTabs.get(index);
		
		return (AbstractPagerFragment) tab.fragment();
	}
	
	public static enum ContentTab {
		TAB_ALL,
		TAB_BOOKMARKS,
		TAB_NOTES,
		TAB_ANNOTATIONS;
	}
}
