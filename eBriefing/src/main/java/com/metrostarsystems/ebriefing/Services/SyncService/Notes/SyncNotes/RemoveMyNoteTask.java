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

import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Notes.Note;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Services.Requests.GetSyncRequest;
import com.metrostarsystems.ebriefing.Services.Requests.AbstractSoapRequest;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncManager;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;
import com.metrostarsystems.ebriefing.Services.SyncService.AbstractSyncTask;

public class RemoveMyNoteTask extends AbstractSyncTask<Void, Void, GetMyNotesObject> {

	private static final String TAG = RemoveMyNoteTask.class.getSimpleName();
	
	private RemoveMyNoteTaskListener				mListener;
	private Book									mBook;
	private Note								mNote;

	public RemoveMyNoteTask(SyncManager manager, ServerConnection connection, Book book, Note note) {
		super(RemoveMyNoteTask.class, SyncTaskType.TYPE_REMOVE_MY_NOTE, connection);
		mListener = (RemoveMyNoteTaskListener) manager;
		mBook = book;
		mNote = note;
	}
	
	@Override
	protected GetMyNotesObject doInBackground(Void... params) {
		
		if(mBook == null || mNote == null) {
			Log.i(TAG, TAG + " failed");
			return new GetMyNotesObject(false);
		}

		RemoveMyNoteRequest request = new RemoveMyNoteRequest(serverConnection());
		
		request.addNoteData(mNote.bookId(), mNote.pageId(), mNote.dateModified());	
		
		return executeRequest(request);
	}
	
	

	@Override
	protected void onPostExecute(GetMyNotesObject result) {
		super.onPostExecute(result);
		
		mListener.onRemoveMyNoteTaskFinishedListener(this, result, mBook.id(), mNote.pageNumber());
	}

	@Override
	protected GetMyNotesObject executeRequest(AbstractSoapRequest request) {
		if(serverConnection() == null || request == null) {
			Log.i(TAG, TAG + " failed");
			return new GetMyNotesObject(false);
		}
		
		SoapObject response = null;
		
		if(SyncService.isNetworkAvailable()) {
			Log.i(TAG, "Start " + TAG + " response");
			
			try {
				response = new GetSyncRequest(serverConnection()).execute(RemoveMyNoteTask.class, request);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			Log.i(TAG, "Done " + TAG + " response");
			
			if(response == null) {
				return new GetMyNotesObject(false);
			}
			
			return new GetMyNotesObject(true);
		}
		
		return new GetMyNotesObject(false);
	}
	
	public static interface RemoveMyNoteTaskListener {
		public abstract void onRemoveMyNoteTaskFinishedListener(RemoveMyNoteTask task, GetMyNotesObject result, String bookId, int pageNumber);
	}
}
