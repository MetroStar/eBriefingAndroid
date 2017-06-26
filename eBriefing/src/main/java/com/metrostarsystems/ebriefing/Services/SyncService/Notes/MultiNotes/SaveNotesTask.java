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

package com.metrostarsystems.ebriefing.Services.SyncService.Notes.MultiNotes;

import android.util.Log;

import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book.BookStatus;
import com.metrostarsystems.ebriefing.Data.Framework.Bookmarks.Bookmark;
import com.metrostarsystems.ebriefing.Data.Framework.Notes.Note;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Services.Requests.AbstractSoapRequest;
import com.metrostarsystems.ebriefing.Services.Requests.GetMultiNotesRequest;
import com.metrostarsystems.ebriefing.Services.Requests.GetSyncRequest;
import com.metrostarsystems.ebriefing.Services.SyncService.AbstractSyncTask;
import com.metrostarsystems.ebriefing.Services.SyncService.Bookmarks.GetMyBookmarksObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Bookmarks.SetMyBookmarksRequest;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncManager;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class SaveNotesTask extends AbstractSyncTask<Void, Void, ArrayList<Note>> {

	private static final String TAG = SaveNotesTask.class.getSimpleName();

	private SaveNotesTaskListener			mListener;
	private Book							mBook;
    private ArrayList<Note>                 mSyncedNotes;


	public SaveNotesTask(SyncManager manager, ServerConnection connection, Book book) {
		super(SaveNotesTask.class, SyncTaskType.TYPE_MULTINOTES_SAVE_NOTES, connection);
		
		mListener = (SaveNotesTaskListener) manager;
		mBook = book;

        mSyncedNotes = new ArrayList<Note>();
	}
	
	@Override
	protected ArrayList<Note> doInBackground(Void... params) {
		
		if(serverConnection() == null) {
			return null;
		}
		
		SaveNotesRequest request = new SaveNotesRequest(serverConnection());
		
		if(mBook.status() == BookStatus.STATUS_DEVICE) {
				ArrayList<Note> notes = serverConnection().app().data().database().notesDatabase().notesByBook(mBook.id());
				
				for(Note note : notes) {

					if(!note.isSynced()) {
						request.addData(note.id(),
                                        note.bookId(),
										note.bookVersion(),
										note.pageId(),
                                        note.dateCreated(),
										note.dateModified(),
                                        note.content(),
										note.isRemoved());

                        mSyncedNotes.add(note);
					}
				}
			}
		
		return executeRequest(request);
	}

	@Override
	protected void onPostExecute(ArrayList<Note> result) {
		super.onPostExecute(result);
		
		mListener.onSaveNotesTaskFinishedListener(result, mBook.id());
	}

	@Override
	protected ArrayList<Note> executeRequest(AbstractSoapRequest request) {
		if(serverConnection() == null || request == null) {
			Log.i(TAG, TAG + " failed");
			return null;
		}
		
		SoapObject response = null;
		
		if(SyncService.isNetworkAvailable()) {
			Log.i(TAG, "Start " + TAG + " response");
			
			try {
				response = new GetMultiNotesRequest(serverConnection()).execute(SaveNotesTask.class, request);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			Log.i(TAG, "Done " + TAG + " response");
			
			if(response == null) {
				return null;
			}
		}
		
		return mSyncedNotes;
	}
	
	public static interface SaveNotesTaskListener {
		public abstract void onSaveNotesTaskFinishedListener(ArrayList<Note> result, String bookId);
	}

}
