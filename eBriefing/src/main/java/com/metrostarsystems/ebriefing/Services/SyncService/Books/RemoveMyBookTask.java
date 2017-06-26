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

package com.metrostarsystems.ebriefing.Services.SyncService.Books;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Services.Requests.GetSyncRequest;
import com.metrostarsystems.ebriefing.Services.Requests.AbstractSoapRequest;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncManager;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;
import com.metrostarsystems.ebriefing.Services.SyncService.AbstractSyncTask;

public class RemoveMyBookTask extends AbstractSyncTask<Void, Void, GetMyBooksObject> {

	private static final String TAG = RemoveMyBookTask.class.getSimpleName();
	
	private RemoveMyBookTaskListener				mListener;
	private Book									mBook;

	public RemoveMyBookTask(SyncManager manager, ServerConnection connection, Book book) {
		super(RemoveMyBookTask.class, SyncTaskType.TYPE_REMOVE_MY_BOOK, connection);
	
		mListener = (RemoveMyBookTaskListener) manager;
	
		mBook = book;
	}
	
	@Override
	protected GetMyBooksObject doInBackground(Void... params) {
		
		if(mBook == null) {
			return new GetMyBooksObject(false);
		}
		
		SetMyBooksRequest request = new SetMyBooksRequest(serverConnection());
		
		request.addBookData(mBook.id(), mBook.bookVersion(), mBook.isFavorite(), Book.dateNow(), true);	
		
		return executeRequest(request);
	}

	@Override
	protected void onPostExecute(GetMyBooksObject result) {
		super.onPostExecute(result);
		
		mListener.onRemoveMyBookTaskFinishedListener(result, mBook.id());
	}

	@Override
	protected GetMyBooksObject executeRequest(AbstractSoapRequest request) {
		if(serverConnection() == null || request == null) {
//			Log.i(TAG, TAG + " failed");
			return new GetMyBooksObject(false);
		}
		
		SoapObject response = null;
		
		if(SyncService.isNetworkAvailable()) {
			Log.i(TAG, "Start " + TAG + " response");
			
			try {
				response = new GetSyncRequest(serverConnection()).execute(RemoveMyBookTask.class, request);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			if(response == null) {
				return new GetMyBooksObject(false);
			}
			
			Log.i(TAG, "Done " + TAG + " response");
		}
		
		return new GetMyBooksObject(true);
	}
	
	public static interface RemoveMyBookTaskListener {
		public abstract void onRemoveMyBookTaskFinishedListener(GetMyBooksObject result, String bookId);
	}
}
