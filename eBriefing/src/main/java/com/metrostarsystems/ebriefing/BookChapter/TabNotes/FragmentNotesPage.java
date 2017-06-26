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

package com.metrostarsystems.ebriefing.BookChapter.TabNotes;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;

import com.metrostarsystems.ebriefing.Data.Framework.Page.Page;

public class FragmentNotesPage implements AbstractNotesObject {
	
	private Page mPage;
	private boolean mIsCollapsed = false;
	private ArrayList<FragmentNotesObject> mNotes;
	
	public int pageNumber() { 
		return mPage.pageNumber(); 
	}
	
	public String pageId() {
		return mPage.id();
	}
	
	@Override
	public boolean isHeader() {
		return false;
	}
	
	@Override
	public boolean isPage() {
		return true;
	}
	
	public Page page() { return mPage; }
	
	public void add(FragmentNotesObject object) {
		if(mNotes == null) {
			mNotes = new ArrayList<FragmentNotesObject>();
		}
		
		mNotes.add(object);
	}
	
	public boolean hasNotes() {
		return mNotes != null ? mNotes.size() > 0 : false;
	}
	
	public FragmentNotesObject firstNote() {
		return mNotes.get(0);
	}
	
	public void setCollapsed(final View view, final boolean collapsed) {
		mIsCollapsed = collapsed;
		
		for(FragmentNotesObject object : mNotes) {
			object.setCollapsed(collapsed);
		}
	}
	
	public boolean isCollapsed() { return mIsCollapsed; }
	
	private FragmentNotesPage(Builder build) {
		mPage = build.mPage;
	}
	
	public static class Builder {
		private Page mPage;
		
		public Builder() {
			
		}
		
		public Builder page(Page page) { mPage = page; return this; }
		
		
		public FragmentNotesPage build() {
			return new FragmentNotesPage(this);
		}
	}

}
