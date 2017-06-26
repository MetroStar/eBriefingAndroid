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

package com.metrostarsystems.ebriefing.Services.SyncService.Notes.SyncNotes;

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

public class GetMyNotesTask extends AbstractSyncTask<Void, Void, GetMyNotesObject> {
	
	private static final String TAG = GetMyNotesTask.class.getSimpleName();
	
	private GetMyNotesTaskListener					mListener;
	private Book									mBook;
	
	public GetMyNotesTask(SyncManager manager, ServerConnection connection, Book book) {
		super(GetMyNotesTask.class, SyncTaskType.TYPE_GET_MY_NOTES, connection);
		
		mListener = (GetMyNotesTaskListener) manager;
		
		mBook = book;
	}
	
	@Override
	protected GetMyNotesObject doInBackground(Void... params) {
		
		if(serverConnection() == null) {
			return new GetMyNotesObject(false);
		}
		
		SyncRequest request = new SyncRequest(serverConnection(), ServerConnectionRequest.REQUEST_SYNC_GET_MY_NOTES);
		
		request.addPropertyString(Tags.SYNC_GET_MY_NOTES_REQUEST_BOOK_ID, mBook.id());
		
		return executeRequest(request);
	}

	@Override
	protected void onPostExecute(GetMyNotesObject result) {
		super.onPostExecute(result);
		
		mListener.onGetMyNotesTaskFinishedListener(this, result, mBook.id());
	}

	@Override
	protected GetMyNotesObject executeRequest(AbstractSoapRequest request) {
		if(serverConnection() == null || request == null) {
			Log.i(TAG, TAG + " failed");
			return new GetMyNotesObject(false);
		}
		
		SoapObject response = null;
		
		if(SyncService.isNetworkAvailable()) {
			if(Settings.DEBUG) { Log.i(TAG, "Start " + TAG + " response"); }
			
			try {
				response = new GetSyncRequest(serverConnection()).execute(GetMyNotesTask.class, request);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			if(Settings.DEBUG) { Log.i(TAG, "Done " + TAG + " response"); }
			
			if(response == null) {
				return new GetMyNotesObject(false);
			}
			
			return (GetMyNotesObject) new GetMyNotesObject.Builder().generate(serverConnection(), response).build();
		}
		
		return new GetMyNotesObject(false);
	}
	
	public static interface GetMyNotesTaskListener {
		public abstract void onGetMyNotesTaskFinishedListener(GetMyNotesTask task, GetMyNotesObject result, String bookId);
	}

}
