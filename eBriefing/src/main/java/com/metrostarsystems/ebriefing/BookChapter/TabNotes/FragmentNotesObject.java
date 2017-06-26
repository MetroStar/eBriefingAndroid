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

import com.metrostarsystems.ebriefing.Data.Framework.Notes.Note;

public class FragmentNotesObject implements AbstractNotesObject {
	
	private ArrayList<Note> mNotes;
	private boolean mIsCollapsed = false;
	
	public Note note(int index) { 
		if(index >= mNotes.size()) {
			return null;
		}
		
		return mNotes.get(index); 
	}

	@Override
	public boolean isHeader() {
		return false;
	}

	@Override
	public boolean isPage() {
		return false;
	}
	
	public boolean isCollapsed() { return mIsCollapsed; }
	
	public void setCollapsed(boolean collapsed) {
		mIsCollapsed = collapsed;
	}
	
	private FragmentNotesObject(Builder build) {
		mNotes = build.mNotes;
	}
	
	public static class Builder {
		private ArrayList<Note> mNotes;
		
		public Builder() {
			mNotes = new ArrayList<Note>();
		}
		
		public Builder note(Note note) { mNotes.add(note); return this; }
		
		public FragmentNotesObject build() {
			return new FragmentNotesObject(this);
		}
	}

}
