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

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.metrostarsystems.ebriefing.Settings;
import com.metrostarsystems.ebriefing.Tags;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection.ServerConnectionRequest;
import com.metrostarsystems.ebriefing.Services.Requests.GetSyncRequest;
import com.metrostarsystems.ebriefing.Services.Requests.AbstractSoapRequest;
import com.metrostarsystems.ebriefing.Services.Requests.SyncRequest;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncManager;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;
import com.metrostarsystems.ebriefing.Services.SyncService.AbstractSyncTask;

public class GetMyBookmarksTask extends AbstractSyncTask<Void, Void, GetMyBookmarksObject> {
	
	private static final String TAG = GetMyBookmarksTask.class.getSimpleName();
	
	private GetMyBookmarksTaskListener	mListener;
	private Book						mBook;
	
	public GetMyBookmarksTask(SyncManager manager, ServerConnection connection, Book book) {
		super(GetMyBookmarksTask.class, SyncTaskType.TYPE_GET_MY_BOOKMARKS, connection);
		
		mListener = (GetMyBookmarksTaskListener) manager;
		mBook = book;
	}
	
	@Override
	protected GetMyBookmarksObject doInBackground(Void... params) {
		
		if(serverConnection() == null || mBook == null) {
			return new GetMyBookmarksObject(false);
		}
		
		SyncRequest request = new SyncRequest(serverConnection(), ServerConnectionRequest.REQUEST_SYNC_GET_MY_BOOKMARKS);
				
		request.addPropertyString(Tags.SYNC_GET_MY_BOOKMARKS_REQUEST_BOOK_ID, mBook.id());
		
		return executeRequest(request);
	}

	@Override
	protected void onPostExecute(GetMyBookmarksObject result) {
		super.onPostExecute(result);
		
		mListener.onGetMyBookmarksTaskFinishedListener(result, mBook.id());
	}

	@Override
	protected GetMyBookmarksObject executeRequest(AbstractSoapRequest request) {
		if(serverConnection() == null || request == null) {
			Log.i(TAG, TAG + " failed");
			return new GetMyBookmarksObject(false);
		}
		
		SoapObject response = null;
		
		if(SyncService.isNetworkAvailable()) {
			if(Settings.DEBUG) { Log.i(TAG, "Start " + TAG + " response"); }
			
			try {
				response = new GetSyncRequest(serverConnection()).execute(GetMyBookmarksTask.class, request);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			if(Settings.DEBUG) { Log.i(TAG, "Done " + TAG + " response"); }
			
			if(response == null) {
				return new GetMyBookmarksObject(false);
			}
			
			return (GetMyBookmarksObject) new GetMyBookmarksObject.Builder().generate(serverConnection(), response).build();
		}
		
		return new GetMyBookmarksObject(false);
	}
	
	public static interface GetMyBookmarksTaskListener {
		public abstract void onGetMyBookmarksTaskFinishedListener(GetMyBookmarksObject result, String bookId);
	}

}
