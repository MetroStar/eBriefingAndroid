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

import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Services.Requests.AbstractSoapRequest;
import com.metrostarsystems.ebriefing.Services.Requests.GetMultiNotesRequest;
import com.metrostarsystems.ebriefing.Services.Requests.GetSyncRequest;
import com.metrostarsystems.ebriefing.Services.SyncService.AbstractSyncObject;
import com.metrostarsystems.ebriefing.Services.SyncService.AbstractSyncTask;
import com.metrostarsystems.ebriefing.Services.SyncService.Annotations.GetMyAnnotationsObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Annotations.GetMyAnnotationsRequest;
import com.metrostarsystems.ebriefing.Services.SyncService.Book.SyncBookAfterDownloadTask.SyncBookAfterDownloadObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Bookmarks.GetMyBookmarksObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Bookmarks.GetMyBookmarksRequest;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.MultiNotes.GetAllNotesObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.MultiNotes.GetAllNotesRequest;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.MultiNotes.GetNotesUpdatesObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.MultiNotes.GetNotesUpdatesRequest;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.SyncNotes.GetMyNotesObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.SyncNotes.GetMyNotesRequest;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncManager;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;

import org.ksoap2.serialization.SoapObject;

public class SyncBookAfterDownloadTask extends AbstractSyncTask<Void, Void, SyncBookAfterDownloadObject> {

	private Book					mBook;

	private SyncBookRequest 		            mRequest;
	private SyncBookAfterDownloadObject         mObject;

	private SyncBookAfterDownloadTaskListener	mListener;

	public SyncBookAfterDownloadTask(SyncManager manager, ServerConnection serverConnection, Book book) {
		super(SyncBookAfterDownloadTask.class, SyncTaskType.TYPE_SYNC_BOOK_AFTER_DOWNLOAD, serverConnection);
		
		mListener = (SyncBookAfterDownloadTaskListener) manager;
		mBook = book;
		
		mRequest = new SyncBookRequest(serverConnection, book);
		mObject = new SyncBookAfterDownloadObject(book);
	}


	@Override
	protected SyncBookAfterDownloadObject doInBackground(Void... params) {

		SoapObject response = null;
		
		if(SyncService.isNetworkAvailable()) {

			if(!serverConnection().isMultiNotes()) {
				try {
					response = new GetSyncRequest(serverConnection()).execute(SyncBookAfterDownloadTask.class, mRequest.getSyncNotesRequest());
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				mObject.setGetMyNotesObjects(serverConnection(), response);
			} else {
				try {
					response = new GetMultiNotesRequest(serverConnection()).execute(SyncBookAfterDownloadTask.class, mRequest.getNotesUpdatesRequest());
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				mObject.setGetNotesUpdatesObjects(serverConnection(), response);
			}
			
			try {
				response = new GetSyncRequest(serverConnection()).execute(SyncBookAfterDownloadTask.class, mRequest.getBookmarksRequest());
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			mObject.setBookmarkObjects(serverConnection(), response);
			
			try {
				response = new GetSyncRequest(serverConnection()).execute(SyncBookAfterDownloadTask.class, mRequest.getAnnotationsRequest());
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			mObject.setAnnotationObjects(serverConnection(), response);
			
			
			
		}
		
		return mObject;
	}
	
	@Override
	protected void onPostExecute(SyncBookAfterDownloadObject result) {
		super.onPostExecute(result);
		
		mListener.onSyncBookAfterDownloadTaskFinishedListener(this, result);
	}


	@Override
	protected SyncBookAfterDownloadObject executeRequest(AbstractSoapRequest request) {
		return null;
	}
	

	public static interface SyncBookAfterDownloadTaskListener {
		public abstract void onSyncBookAfterDownloadTaskFinishedListener(SyncBookAfterDownloadTask task, SyncBookAfterDownloadObject object);
	}
	
	public static class SyncBookAfterDownloadObject {
		
		private Book					mBook;
		private AbstractSyncObject		mNoteObjects;
		private GetMyBookmarksObject    mBookmarkObjects;
		private GetMyAnnotationsObject  mAnnotationObjects;
		
		public SyncBookAfterDownloadObject(Book book) {
			mBook = book;
		}
		
		public Book book() {
			return mBook;
		}
		
		public GetMyNotesObject notesSyncNotes() {
			return (GetMyNotesObject) mNoteObjects;
		}
		
		public GetNotesUpdatesObject notesMultiNotes() {
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
            if(connection == null || response == null || response.getPropertyCount() <= 0) {
                mNoteObjects = new GetNotesUpdatesObject(false);

                return;
            }

            mNoteObjects = new GetNotesUpdatesObject.Builder().generate(connection, response).build();
        }
		
		public void setGetAllNotesObjects(ServerConnection connection, SoapObject response) {
			if(connection == null || response == null) {
				mNoteObjects = new GetAllNotesObject(false);
				
				return;
			}
			
			mNoteObjects = new GetAllNotesObject.Builder().generate(connection, response).build();
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

		public GetMyNotesRequest getSyncNotesRequest() {
			return new GetMyNotesRequest(mConnection, mBook.id());
		}

        public GetNotesUpdatesRequest getNotesUpdatesRequest() {
            return new GetNotesUpdatesRequest(mConnection, mBook.id(), mBook.dateSynced());
        }
		
		public GetAllNotesRequest getMultiNotesRequest(String startOffset, int pageSize) {
			return new GetAllNotesRequest(mConnection, mBook.id(), startOffset, pageSize);
		}
		
		public GetMyBookmarksRequest getBookmarksRequest() {
			return new GetMyBookmarksRequest(mConnection, mBook.id());
		}
		
		public GetMyAnnotationsRequest getAnnotationsRequest() {
			return new GetMyAnnotationsRequest(mConnection, mBook.id());
		}
	}
}
