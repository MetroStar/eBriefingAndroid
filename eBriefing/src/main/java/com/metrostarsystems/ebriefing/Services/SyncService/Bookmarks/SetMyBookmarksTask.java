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

package com.metrostarsystems.ebriefing.Services.SyncService.Bookmarks;

import java.util.ArrayList;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book.BookStatus;
import com.metrostarsystems.ebriefing.Data.Framework.Bookmarks.Bookmark;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Services.Requests.GetSyncRequest;
import com.metrostarsystems.ebriefing.Services.Requests.AbstractSoapRequest;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncManager;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;
import com.metrostarsystems.ebriefing.Services.SyncService.AbstractSyncTask;

public class SetMyBookmarksTask extends AbstractSyncTask<Void, Void, GetMyBookmarksObject> {
	
	private static final String TAG = SetMyBookmarksTask.class.getSimpleName();

	private SetMyBookmarksTaskListener			mListener;
	private Book								mBook;
	
	
	public SetMyBookmarksTask(SyncManager manager, ServerConnection connection, Book book) {
		super(SetMyBookmarksTask.class, SyncTaskType.TYPE_SET_MY_BOOKMARKS, connection);
		
		mListener = (SetMyBookmarksTaskListener) manager;
		mBook = book;
	}
	
	@Override
	protected GetMyBookmarksObject doInBackground(Void... params) {
		
		if(serverConnection() == null) {
			return new GetMyBookmarksObject(false);
		}
		
		SetMyBookmarksRequest request = new SetMyBookmarksRequest(serverConnection());
		
		if(mBook.status() == BookStatus.STATUS_DEVICE) {
				ArrayList<Bookmark> bookmarks = serverConnection().app().data().database().bookmarksDatabase().bookmarksByBook(mBook.id());
				
				for(Bookmark bookmark : bookmarks) {
					
					if(!bookmark.isSynced()) {
						request.addData(bookmark.bookId(), 
										bookmark.bookVersion(), 
										bookmark.pageId(), 
										bookmark.value(), 
										bookmark.dateModified(), 
										bookmark.isRemoved());
					}
				}
			}
		
		return executeRequest(request);
	}

	@Override
	protected void onPostExecute(GetMyBookmarksObject result) {
		super.onPostExecute(result);
		
		mListener.onSetMyBookmarksTaskFinishedListener(result, mBook.id());
	}

	@Override
	protected GetMyBookmarksObject executeRequest(AbstractSoapRequest request) {
		if(serverConnection() == null || request == null) {
			Log.i(TAG, TAG + " failed");
			return new GetMyBookmarksObject(false);
		}
		
		SoapObject response = null;
		
		if(SyncService.isNetworkAvailable()) {
			Log.i(TAG, "Start " + TAG + " response");
			
			try {
				response = new GetSyncRequest(serverConnection()).execute(SetMyBookmarksTask.class, request);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			Log.i(TAG, "Done " + TAG + " response");
			
			if(response == null) {
				return new GetMyBookmarksObject(false);
			}
		}
		
		return new GetMyBookmarksObject(true);
	}
	
	public static interface SetMyBookmarksTaskListener {
		public abstract void onSetMyBookmarksTaskFinishedListener(GetMyBookmarksObject result, String bookId);
	}

}
