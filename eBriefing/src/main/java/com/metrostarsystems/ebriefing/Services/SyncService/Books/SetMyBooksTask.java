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

import android.util.Log;

import java.util.ArrayList;

import org.ksoap2.serialization.SoapObject;

import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book.BookStatus;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Services.Requests.GetSyncRequest;
import com.metrostarsystems.ebriefing.Services.Requests.AbstractSoapRequest;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncManager;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;
import com.metrostarsystems.ebriefing.Services.SyncService.AbstractSyncTask;
import com.metrostarsystems.ebriefing.Settings;

public class SetMyBooksTask extends AbstractSyncTask<Void, Void, GetMyBooksObject> {

	private static final String TAG = SetMyBooksTask.class.getSimpleName();
	
	private SetMyBooksTaskListener		mListener;
	
	private GetMyBooksObject.Builder	mObjects = new GetMyBooksObject.Builder();

	public SetMyBooksTask(SyncManager manager, ServerConnection connection) {
		super(SetMyBooksTask.class, SyncTaskType.TYPE_SET_MY_BOOKS, connection);
		
		mListener = (SetMyBooksTaskListener) manager;
	}
	
	@Override
	protected GetMyBooksObject doInBackground(Void... params) {
		
		if(serverConnection() == null) {
			return new GetMyBooksObject(false);
		}

		SetMyBooksRequest request = new SetMyBooksRequest(serverConnection());
		
		ArrayList<Book> books = serverConnection().app().data().database().booksDatabase().getBooksNotSynced();

        if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Syncing " + String.valueOf(books.size()) + " books..."); }
		
		// There are no books to sync so just return valid
		if(books == null || books.size() == 0) {
			return new GetMyBooksObject(true);
		}
		
		for(Book book : books) {
			request.addBookData(book.id(),
								book.bookVersion(),
								book.isFavorite(),
								book.userModified(),
								book.isRemoved());


            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Syncing Book: " + book.title()); }
			
			mObjects.setMyBook(book.id(), true);
		}
		
		return executeRequest(request);
	}

	@Override
	protected void onPostExecute(GetMyBooksObject result) {
		super.onPostExecute(result);
		
		mListener.onSetMyBooksTaskFinishedListener(result);
	}


	@Override
	protected GetMyBooksObject executeRequest(AbstractSoapRequest request) {
		if(serverConnection() == null || request == null) {
//			Log.i(TAG, TAG + " failed");
			return new GetMyBooksObject(false);
		}
		
		SoapObject response = null;
		
		if(SyncService.isNetworkAvailable()) {
			
			try {
				response = new GetSyncRequest(serverConnection()).execute(SetMyBooksTask.class, request);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			if(response == null) {
				return new GetMyBooksObject(false);
			}
			
			return mObjects.build();
		}
		
		return new GetMyBooksObject(false);
	}
	
	public static interface SetMyBooksTaskListener {
		public abstract void onSetMyBooksTaskFinishedListener(GetMyBooksObject result);
	}
}
