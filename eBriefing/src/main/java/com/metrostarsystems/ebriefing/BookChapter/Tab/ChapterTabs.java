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

package com.metrostarsystems.ebriefing.BookChapter.Tab;

import java.util.ArrayList;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.BookChapter.TabAnnotations.FragmentAnnotations;
import com.metrostarsystems.ebriefing.BookChapter.TabBookmarks.FragmentBookmarks;
import com.metrostarsystems.ebriefing.BookChapter.TabChapters.FragmentChapters;
import com.metrostarsystems.ebriefing.BookChapter.TabNotes.FragmentNotes;
import com.metrostarsystems.ebriefing.BookChapter.TabOverview.FragmentOverview;
import com.metrostarsystems.ebriefing.Data.Framework.AbstractPagerFragment;

public class ChapterTabs {
	
	private MainApplication mApp;
	private ArrayList<ChapterPagerTab> mTabs;
	
	public ChapterTabs(MainApplication main) {
		mApp = main;
		
		mTabs = new ArrayList<ChapterPagerTab>();
		
		mTabs.add((ChapterPagerTab) new ChapterPagerTab.Builder(mApp)
												.tab(ChapterTab.TAB_OVERVIEW)
												.title("Overview")
												.showCount(false)
												.fragment(new FragmentOverview())
												.indicatorColor("#FF004D95")
												.dividerColor("#FF888888")
												.build());
		
		
		mTabs.add((ChapterPagerTab) new ChapterPagerTab.Builder(mApp)
												.tab(ChapterTab.TAB_CHAPTERS)
												.title("Chapters")
												.showCount(false)
												.fragment(new FragmentChapters())
												.indicatorColor("#FF004D95")
												.dividerColor("#FF888888")
												.build());
		
		mTabs.add((ChapterPagerTab) new ChapterPagerTab.Builder(mApp)
												.tab(ChapterTab.TAB_BOOKMARKS)
												.title("Bookmarks")
												.fragment(new FragmentBookmarks())
												.indicatorColor("#FF004D95")
												.dividerColor("#FF888888")
												.build());
		
		mTabs.add((ChapterPagerTab) new ChapterPagerTab.Builder(mApp)
												.tab(ChapterTab.TAB_NOTES)
												.title("Notes")
												.fragment(new FragmentNotes())
												.indicatorColor("#FF004D95")
												.dividerColor("#FF888888")
												.build());
		
		mTabs.add((ChapterPagerTab) new ChapterPagerTab.Builder(mApp)
												.tab(ChapterTab.TAB_ANNOTATIONS)
												.title("Annotations")
												.fragment(new FragmentAnnotations())
												.indicatorColor("#FF004D95")
												.dividerColor("#FF888888")
												.build());
	}
	
	public int total() {
		return mTabs.size();
	}
	
	public String tabTitle(int position) {
		ChapterPagerTab tab = mTabs.get(position);
		
		return tab.title();
	}

	public AbstractPagerFragment processTab(int index) {
		ChapterPagerTab tab = mTabs.get(index);
		
		return (AbstractPagerFragment) tab.fragment();
	}
	
	public static enum ChapterTab {
		TAB_OVERVIEW,
		TAB_CHAPTERS,
		TAB_BOOKMARKS,
		TAB_NOTES,
		TAB_ANNOTATIONS;
	}
}
