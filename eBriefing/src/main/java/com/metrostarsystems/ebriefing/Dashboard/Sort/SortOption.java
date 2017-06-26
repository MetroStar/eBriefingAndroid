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

package com.metrostarsystems.ebriefing.Dashboard.Sort;

import java.util.ArrayList;

import com.metrostarsystems.ebriefing.Data.Framework.Database.DatabaseHandle;

public enum SortOption {

	TITLE(0,"Title", DatabaseHandle.BOOKS_COLUMN_TITLE),
	DATE_ADDED(1,"Date Added", DatabaseHandle.BOOKS_COLUMN_USER_ADDED),
	DATE_MODIFIED(2,"Date Modified", DatabaseHandle.BOOKS_COLUMN_USER_MODIFIED);
	
	private int mId = -1;
	private String mName = "";
	private String mColumn = "";
	
	private SortOption(int id, String name, String column) {
		mId = id;
		mName = name;
		mColumn = column;
	}
	
	public int id() { return mId; }
	
	@Override
	public String toString() { return mName; }
	
	public String column() { return mColumn; }
	
	public static ArrayList<SortOption> options() {
		ArrayList<SortOption> options = new ArrayList<SortOption>();
		
		for(SortOption option : SortOption.values()) {
			options.add(option);
		}
		
		return options;
	}
	
	
}
