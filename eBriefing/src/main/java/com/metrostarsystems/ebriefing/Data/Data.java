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

package com.metrostarsystems.ebriefing.Data;

import java.util.ArrayList;
import java.util.Collections;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.Settings;
import com.metrostarsystems.ebriefing.Utilities;
import com.metrostarsystems.ebriefing.Dashboard.Sort.SortDirection;
import com.metrostarsystems.ebriefing.Dashboard.Sort.SortOption;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Database.DatabaseHandle;
import com.metrostarsystems.ebriefing.Data.Framework.Managers.ManagerImages;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;

import android.os.AsyncTask;
import android.util.Log;

public class Data {
	
	private static final String TAG = Data.class.getSimpleName();

	public static MainApplication				mApp;
	
	private DatabaseHandle						mDatabase;
	private SortOption							mSort = SortOption.TITLE;
	private SortDirection						mSortDirection = SortDirection.ASCENDING;
	private String								mSearchText = "";
	
	private Book								mCurrentBook;

	private ManagerImages						mImageManager;
	
	public Data(MainApplication main) {
		mApp = main;	

		database();
		
		mImageManager	= new ManagerImages(mApp);
	}
	
	public DatabaseHandle database() {
		if(mDatabase == null) {
            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Database: " + mApp.serverConnection().database()); }
			mDatabase = new DatabaseHandle(mApp, mApp.serverConnection().database());
		}
		
		return mDatabase;
	}

	public ManagerImages imageManager() { return mImageManager; }
	
	public void setCurrentBook(Book book) {
		mCurrentBook = book;
	}
	
	public Book currentBook() {
		return mCurrentBook;
	}
	
	public void setSort(SortOption option) { mSort = option; }	
	public SortOption sort() { return mSort; }
	public void setSortDirection(SortDirection direction) { mSortDirection = direction; }
	public SortDirection sortDirection() { return mSortDirection; }
	public void setSearchText(String text) { mSearchText = text; }
	public String searchText() { return mSearchText; }
}
