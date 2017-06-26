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

import java.util.ArrayList;
import java.util.Stack;

import android.os.AsyncTask;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Chapter.Chapter;
import com.metrostarsystems.ebriefing.Data.Framework.Page.Page;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Services.SyncService.Delete.DeleteBookObject.DeleteObjectListener;
import com.metrostarsystems.ebriefing.Services.SyncService.Delete.DeleteBookObject.FileType;

public class DeleteBook implements DeleteObjectListener {
	
	private static final String	TAG = DeleteBook.class.getSimpleName();

	private MainApplication						mApp;
	private ServerConnection					mConnection;
	
	private Book								mBook;
	private DeleteTask 							mTask;
	
	private Stack<DeleteBookObject> 				mDeleteFiles = new Stack<DeleteBookObject>();
	private DeleteBookObject						mCurrentFile;
	
	
	public DeleteBook(MainApplication main, Book book) {
		mApp 		= main;
		mConnection = main.serverConnection();
		mBook 		= book;
		
		// Add the file paths 
		// ---------------------------------------------------------------------------------------------------	
		DeleteBookObject book_small_image = new DeleteBookObject(mBook.smallImageFilePath(), FileType.TYPE_BOOK_SMALL_IMAGE);
		mDeleteFiles.push(book_small_image);

		DeleteBookObject book_large_image = new DeleteBookObject(mBook.largeImageFilePath(), FileType.TYPE_BOOK_LARGE_IMAGE);
		mDeleteFiles.push(book_large_image);

		ArrayList<Page> pages = mApp.data().database().pagesDatabase().pagesByBook(mBook.id());
		for(int index = 0; index < pages.size(); index++) {
			DeleteBookObject page_file = new DeleteBookObject(pages.get(index).filePath(), FileType.TYPE_PAGE);
			mDeleteFiles.push(page_file);
		}

		ArrayList<Chapter> chapters = mApp.data().database().chaptersDatabase().chaptersByBook(mBook.id());
		for(int index = 0; index < chapters.size(); index++) {
			DeleteBookObject chapter_small_image = new DeleteBookObject(chapters.get(index).smallImageFilePath(), FileType.TYPE_CHAPTER_SMALL_IMAGE);
			mDeleteFiles.push(chapter_small_image);

			DeleteBookObject chapter_large_image = new DeleteBookObject(chapters.get(index).largeImageFilePath(), FileType.TYPE_CHAPTER_LARGE_IMAGE);
			mDeleteFiles.push(chapter_large_image);
		}
		// ---------------------------------------------------------------------------------------------------
	}
	
	public void execute() {
		process();
	}
	
	private void process() {
		if(!mDeleteFiles.isEmpty()) {
			
			mCurrentFile = mDeleteFiles.pop();
			
			mTask = new DeleteTask(mConnection, this, mCurrentFile);
			mTask.execute();
		}
	}
	
	@Override
	public void onDeleteObjectFinished(DeleteBookObject file) {
		// There was an error push the file back to the stack and continue
		if(file == null) {
			//Log.i(TAG, "delete failed, adding page to queue");
			mDeleteFiles.push(mCurrentFile);
		}
		
		process();
		
	}
	
	
	public static class DeleteTask extends AsyncTask<Void, Void, DeleteBookObject> {
		
		private ServerConnection		mConnection;
		private DeleteObjectListener 	mListener;
		private DeleteBookObject 			mDelete;
		private String 					mFileName;
		
		public DeleteTask(ServerConnection connection, DeleteObjectListener listener, DeleteBookObject object) {
			mConnection = connection;
			mListener = listener;
			mDelete = object;
			mFileName = mDelete.fileName();
			
			
		}

		@Override
		protected DeleteBookObject doInBackground(Void... params) {

			return doResponse(mFileName);
		}
		
		private DeleteBookObject doResponse(String fileName) {
			
			mConnection.fileReadHandle().setFileName(fileName);
			
			if(mConnection.fileReadHandle().delete()) {
				return mDelete;
			} else {
				return null;
			}
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(DeleteBookObject result) {
			super.onPostExecute(result);
			mListener.onDeleteObjectFinished(result);
		}
		
	}

	
}


