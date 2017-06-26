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

package com.metrostarsystems.ebriefing.Services.SyncService.Delete;

import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection.ServerConnectionRequest;
import com.metrostarsystems.ebriefing.Services.Requests.AbstractSoapRequest;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncManager;
import com.metrostarsystems.ebriefing.Services.SyncService.AbstractSyncTask;

public class DeleteDeviceBookTask extends AbstractSyncTask<Void, Void, DeleteBookObject> {

	private static final String TAG = DeleteDeviceBookTask.class.getSimpleName();
	
	private DeleteDeviceBookTaskListener			mListener;
	
	private Book 								mBook;
	
	public DeleteDeviceBookTask(SyncManager manager, ServerConnection connection, Book book) {
		super(DeleteDeviceBookTask.class, SyncTaskType.TYPE_DELETE_BOOK, connection);
		
		mListener = (DeleteDeviceBookTaskListener) manager;
		mBook = book;
	}
	
	@Override
	protected DeleteBookObject doInBackground(Void... params) {
		return executeRequest(null);
	}
	
	

	@Override
	protected void onPostExecute(DeleteBookObject result) {
		super.onPostExecute(result);
		
		mListener.onDeleteDeviceBookTaskFinishedListener(result, mBook.id());
	}

	@Override
	protected DeleteBookObject executeRequest(AbstractSoapRequest request) {
		if(serverConnection() == null || mBook == null) {
			return new DeleteBookObject(false);
		}
		
		// Deletes book data
		serverConnection().app().data().database().chaptersDatabase().deleteChapters(mBook.id());
		serverConnection().app().data().database().pagesDatabase().deletePages(mBook.id());
		serverConnection().app().data().database().notesDatabase().deleteNotes(mBook.id());
		serverConnection().app().data().database().bookmarksDatabase().deleteBookmarks(mBook.id());
		serverConnection().app().data().database().annotationsDatabase().deleteAnnotations(mBook.id());
		
		// Deletes file data
		new DeleteBook(serverConnection().app(), mBook).execute();
		
		return new DeleteBookObject(true);
	}
	
	public static interface DeleteDeviceBookTaskListener {
		public abstract void onDeleteDeviceBookTaskFinishedListener(DeleteBookObject result, String bookId);
	}
}
