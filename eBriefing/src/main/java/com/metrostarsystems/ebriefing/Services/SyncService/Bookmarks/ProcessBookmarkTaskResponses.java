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

import java.util.ArrayList;

import android.util.Log;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.Settings;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Bookmarks.Bookmark;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;
import com.metrostarsystems.ebriefing.Services.SyncService.Bookmarks.GetMyBookmarksObject.BookmarkObject;

public class ProcessBookmarkTaskResponses {
	
	private static final String TAG = ProcessBookmarkTaskResponses.class.getSimpleName();
	
	private MainApplication mApp;
	private String			mBookId = "";
	private Book			mBook;
	
	public ProcessBookmarkTaskResponses(MainApplication app) {
		mApp = app;
	}

	public void processGetMyBookmarksResponse(String bookId, GetMyBookmarksObject bookmarks) {
		if(mApp == null || bookId == null || bookId.isEmpty()) {
			return;
		}
		
		mBookId = bookId;
		
		
		processGetMyBookmarks(bookmarks);
	}
	
	public void processSetMyBookmarksResponse(boolean result) {
		if(mApp == null) {
			return;
		}
		
		processSetMyBookmarks(result);
	}
	
	private void processGetMyBookmarks(GetMyBookmarksObject bookmarks_objects) {
		mBook = mApp.data().database().booksDatabase().book(mBookId);
		
		// bookmarks can be empty
		if(mApp == null || mBook == null || bookmarks_objects == null || !bookmarks_objects.isValid()) {
			mBook.setSyncedBookmarks(true);
			mApp.data().database().booksDatabase().update(mBook);

            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Syncing: " + mBook.isSyncedNotes() + " " +
									 mBook.isSyncedBookmarks() + " " +
									 mBook.isSyncedAnnotations()); }
			
			if(mBook.isSyncedNotes() && mBook.isSyncedBookmarks() && mBook.isSyncedAnnotations()) {
				SyncService.syncServiceBookComplete(mBook);
			}
			
			return;
		}
		
		ArrayList<BookmarkObject> bookmark_objects = bookmarks_objects.objects();
		
		if(bookmark_objects == null || bookmark_objects.size() <= 0) {
			mBook.setSyncedBookmarks(true);
			mApp.data().database().booksDatabase().update(mBook);

            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Syncing: " + mBook.isSyncedNotes() + " " +
									 mBook.isSyncedBookmarks() + " " +
									 mBook.isSyncedAnnotations()); }
			
			if(mBook.isSyncedNotes() && mBook.isSyncedBookmarks() && mBook.isSyncedAnnotations()) {
				SyncService.syncServiceBookComplete(mBook);
			}
			
			return;
		}
		
		if(Settings.DEBUG_MESSAGES) { Log.i(TAG, mBook.title() + " Found " + String.valueOf(bookmark_objects.size()) + " bookmarks"); }
		
		ArrayList<Bookmark> bookmarks = mApp.data().database().bookmarksDatabase().bookmarksByBook(mBook.id());
		
		
		
		for(BookmarkObject bookmark_object : bookmark_objects) {
			boolean found = false;
			
			for(Bookmark bookmark : bookmarks) {
				
				// Bookmark exists
				if(bookmark.pageNumber() == bookmark_object.mPageNumber) {
					found = true;
					
					if(bookmark_object.mRemoved) {
						mApp.data().database().bookmarksDatabase().delete(bookmark);
						
						break;
					}
					
					// Server Bookmark is newer so update the local bookmark
					if(!bookmark.isNewer(bookmark_object.mDateModified)) {
						Bookmark updated_bookmark = new Bookmark.Builder()
															.id()
															.bookId(bookmark_object.mBookId)
															.bookVersion(bookmark_object.mBookVersion)
															.chapterId(bookmark_object.mChapterId)
															.pageId(bookmark_object.mPageId)
															.pageNumber(bookmark_object.mPageNumber)
															.value(bookmark_object.mValue)
															.dateModified(bookmark_object.mDateModified)
															.isSynced(true)
															.isNew(false)
															.build();
							
						mApp.data().database().bookmarksDatabase().updateByNumber(updated_bookmark);
						
						break;
					}
				}
			}
		
			// If it gets this far then the Bookmark doesn't exist
			if(!found && !bookmark_object.mRemoved) {

				Bookmark new_bookmark = new Bookmark.Builder()
													.id()
													.bookId(bookmark_object.mBookId)
													.bookVersion(bookmark_object.mBookVersion)
													.chapterId(bookmark_object.mChapterId)
													.pageId(bookmark_object.mPageId)
													.pageNumber(bookmark_object.mPageNumber)
													.value(bookmark_object.mValue)
													.dateModified(bookmark_object.mDateModified)
													.isSynced(true)
													.isNew(false)
													.build();
											
				mApp.data().database().bookmarksDatabase().insert(new_bookmark);
			}
		}
		
		mBook.setSyncedBookmarks(true);
		mApp.data().database().booksDatabase().update(mBook);

        if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Syncing: " + mBook.isSyncedNotes() + " " +
								 mBook.isSyncedBookmarks() + " " +
								 mBook.isSyncedAnnotations()); }
		
		if(mBook.isSyncedNotes() && mBook.isSyncedBookmarks() && mBook.isSyncedAnnotations()) {
			SyncService.syncServiceBookComplete(mBook);
		}

	}
	
	private void processSetMyBookmarks(boolean result) {
		mBook = mApp.data().database().booksDatabase().book(mBookId);
		
		if(mApp == null || mBook == null) {
			return;
		}
		
		if(result) {
			
			ArrayList<Book> books = mApp.data().database().booksDatabase().getMyBooks();
			
			for(Book book : books) {
				
				ArrayList<Bookmark> bookmarks = mApp.data().database().bookmarksDatabase().bookmarksByBook(book.id());
				
				for(Bookmark bookmark : bookmarks) {
					
					if(bookmark.isRemoved()) {
						mApp.data().database().bookmarksDatabase().delete(bookmark);
					} else {
						Bookmark update_bookmark = new Bookmark.Builder()
															.fromBookmark(bookmark)
															.value(bookmark.value())
															.isSynced(true)
															.build();
						
						mApp.data().database().bookmarksDatabase().updateByNumber(update_bookmark);
					}
					
				}
				
			}

		}
	}
}
