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

public class FragmentNotesChapter implements AbstractNotesObject {
	private String mChapter = "";
	private String mTitle = "";
	
	public String chapter() { return mChapter; }
	public String title() { return mTitle; }
	
	@Override
	public boolean isHeader() {
		return true;
	}
	
	@Override
	public boolean isPage() {
		return false;
	}
	
	private FragmentNotesChapter(Builder build) {
		mChapter = build.mChapter;
		mTitle = build.mTitle;
	}
	
	public static class Builder {
		private String mChapter = "";
		private String mTitle = "";
		
		public Builder() { }

		public Builder chapter(String chapter) { mChapter = chapter; return this; }
		public Builder title(String title) { mTitle = title; return this; }
		
		public FragmentNotesChapter build() {
			return new FragmentNotesChapter(this);
		}
	}

	
}
