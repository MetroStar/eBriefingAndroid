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

package com.metrostarsystems.ebriefing.Services.SyncService.Book;

import android.util.Log;

import org.ksoap2.serialization.SoapObject;

import com.metrostarsystems.ebriefing.Services.SyncService.Annotations.GetMyAnnotationsRequest;
import com.metrostarsystems.ebriefing.Services.SyncService.Bookmarks.GetMyBookmarksRequest;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.SyncNotes.GetMyNotesRequest;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Services.Requests.GetMultiNotesRequest;
import com.metrostarsystems.ebriefing.Services.Requests.GetSyncRequest;
import com.metrostarsystems.ebriefing.Services.Requests.AbstractSoapRequest;
import com.metrostarsystems.ebriefing.Services.SyncService.AbstractSyncObject;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncManager;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;
import com.metrostarsystems.ebriefing.Services.SyncService.AbstractSyncTask;
import com.metrostarsystems.ebriefing.Services.SyncService.Annotations.GetMyAnnotationsObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Book.SyncBookTask.SyncBookObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Bookmarks.GetMyBookmarksObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.SyncNotes.GetMyNotesObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.MultiNotes.GetNotesUpdatesRequest;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.MultiNotes.GetNotesUpdatesObject;
import com.metrostarsystems.ebriefing.Settings;

public class SyncBookTask extends AbstractSyncTask<Void, Void, SyncBookObject> {

    private static final String TAG = SyncBookTask.class.getSimpleName();

	private Book					mBook;
	
	private SyncBookRequest 		mRequest;
	private SyncBookObject 			mObject;
	
	private SyncBookTaskListener	mListener;
		
	public SyncBookTask(SyncManager manager, ServerConnection serverConnection, Book book) {
		super(SyncBookTask.class, SyncTaskType.TYPE_SYNC_BOOK, serverConnection);
		
		mListener = (SyncBookTaskListener) manager;
		mBook = book;
		
		mRequest = new SyncBookRequest(serverConnection, book);
		mObject = new SyncBookObject(book);
	}


	@Override
	protected SyncBookObject doInBackground(Void... params) {

		SoapObject response = null;
		
		if(SyncService.isNetworkAvailable()) {

			if(!serverConnection().isMultiNotes()) {
				try {
					response = new GetSyncRequest(serverConnection()).execute(SyncBookTask.class, mRequest.getMyNotesRequest());
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				mObject.setGetMyNotesObjects(serverConnection(), response);
			} else {
				try {
                    if(Settings.DEBUG_MESSAGES) { Log.i(TAG, mBook.title() + "Executing getNotesUpdatesRequest()"); }
					response = new GetMultiNotesRequest(serverConnection()).execute(SyncBookTask.class, mRequest.getNotesUpdatesRequest());
				} catch(Exception e) {
					e.printStackTrace();
				}
                if(Settings.DEBUG_MESSAGES) { Log.i(TAG, mBook.title() + "Finished getNotesUpdatesRequest()"); }
				mObject.setGetNotesUpdatesObjects(serverConnection(), response);
			}
			
			try {
				response = new GetSyncRequest(serverConnection()).execute(SyncBookTask.class, mRequest.getBookmarksRequest());
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			mObject.setBookmarkObjects(serverConnection(), response);
			
			try {
				response = new GetSyncRequest(serverConnection()).execute(SyncBookTask.class, mRequest.getAnnotationsRequest());
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			mObject.setAnnotationObjects(serverConnection(), response);
			
			
			
		}
		
		return mObject;
	}
	
	@Override
	protected void onPostExecute(SyncBookObject result) {
		super.onPostExecute(result);
		
		mListener.onSyncBookTaskFinishedListener(this, result);
	}


	@Override
	protected SyncBookObject executeRequest(AbstractSoapRequest request) {
		return null;
	}
	

	public static interface SyncBookTaskListener {
		public abstract void onSyncBookTaskFinishedListener(SyncBookTask task, SyncBookObject object);
	}
	
	public static class SyncBookObject {
		
		private Book					mBook;
		private AbstractSyncObject		mNoteObjects;
		private GetMyBookmarksObject    mBookmarkObjects;
		private GetMyAnnotationsObject  mAnnotationObjects;
		
		public SyncBookObject(Book book) {
			mBook = book;
		}
		
		public Book book() {
			return mBook;
		}
		
		public GetMyNotesObject getMyNotes() {
			return (GetMyNotesObject) mNoteObjects;
		}
		
		public GetNotesUpdatesObject getNotesUpdates() {
			return (GetNotesUpdatesObject) mNoteObjects;
		}
		
		public GetMyBookmarksObject bookmarks() {
			return mBookmarkObjects;
		}
		
		public GetMyAnnotationsObject annotations() {
			return mAnnotationObjects;
		}
		
		public void setGetMyNotesObjects(ServerConnection connection, SoapObject response) {
			if(connection == null || response == null) {
				mNoteObjects = new GetMyNotesObject(false);
			}
			
			mNoteObjects = new GetMyNotesObject.Builder().generate(connection, response).build();
		}
		
		public void setGetNotesUpdatesObjects(ServerConnection connection, SoapObject response) {
			if(connection == null || response == null) {
				mNoteObjects = new GetNotesUpdatesObject(false);
				
				return;
			}
			
			mNoteObjects = new GetNotesUpdatesObject.Builder().generate(connection, response).build();
		}
		
		public void setBookmarkObjects(ServerConnection connection, SoapObject response) {
			if(connection == null || response == null) {
				mBookmarkObjects = new GetMyBookmarksObject(false);
				
				return;
			}
			
			mBookmarkObjects = (GetMyBookmarksObject) new GetMyBookmarksObject.Builder().generate(connection, response).build();
		}
		
		public void setAnnotationObjects(ServerConnection connection, SoapObject response) {
			if(connection == null || response == null) {
				mAnnotationObjects = new GetMyAnnotationsObject(false);
				
				return;
			}
			
			mAnnotationObjects = (GetMyAnnotationsObject) new GetMyAnnotationsObject.Builder().generate(connection, response).build();
		}
	}
	
	public static class SyncBookRequest {
		
		private ServerConnection mConnection;
		private Book mBook;

		public SyncBookRequest(ServerConnection connection, Book book) {
			mConnection = connection;
			mBook = book;
		}

		public GetMyNotesRequest getMyNotesRequest() {
			return new GetMyNotesRequest(mConnection, mBook.id());
		}

        public GetNotesUpdatesRequest getNotesUpdatesRequest() {
            return new GetNotesUpdatesRequest(mConnection, mBook.id(), mBook.dateSynced());
        }
		
		public GetMyBookmarksRequest getBookmarksRequest() {
			return new GetMyBookmarksRequest(mConnection, mBook.id());
		}
		
		public GetMyAnnotationsRequest getAnnotationsRequest() {
			return new GetMyAnnotationsRequest(mConnection, mBook.id());
		}
	}
}
