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

import java.util.ArrayList;

import android.util.Log;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book.BookStatus;
import com.metrostarsystems.ebriefing.Services.DownloadService.DownloadService;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;
import com.metrostarsystems.ebriefing.Services.SyncService.Books.GetMyBooksObject.BookObject;
import com.metrostarsystems.ebriefing.Settings;

public class ProcessBooksTaskResponses {

	private static final String TAG = ProcessBooksTaskResponses.class.getSimpleName();
	
	private MainApplication mApp;
	
	public ProcessBooksTaskResponses(MainApplication app) {
		mApp = app;
	}
	
	public void processGetMyBooksResponse(GetMyBooksObject books) {
		if(mApp == null || books == null || !books.isValid()) {
			return;
		}
		
		processGetMyBooks(books);
	}
	
	private void processGetMyBooks(GetMyBooksObject object) {
		if(mApp == null || object == null || !object.isValid()) {
			return;
		}
		
		ArrayList<BookObject> book_objects = object.objects();
		
		if(book_objects == null) {
			return;
		}
		
		ArrayList<Book> books = mApp.data().database().booksDatabase().getAll();
		
		for(BookObject book_object : book_objects) {
			
			Book book = null;
			
			for(int index = 0; index < books.size(); index++) {
				book = books.get(index);
				
				// Book was removed so set it to the server
				if(book.id().equalsIgnoreCase(book_object.mId) && book_object.mRemoved) {

                    book.setSynced(true);
                    Book.setStatusServer(mApp.data().database().booksDatabase(), book);

					break;
					// TODO delete book
				}

				// Book is already downloaded, so update it
				if(book.id().equalsIgnoreCase(book_object.mId) && 
						book.status() == BookStatus.STATUS_DEVICE &&
						book.bookVersion() < book_object.mVersion) {
				
					// Download the book
					DownloadService.downloadDeviceBookData(book);
					break;
				} 

				// Book is not downloaded, so download it
				if(book.id().equalsIgnoreCase(book_object.mId) &&
						book.status() == BookStatus.STATUS_SERVER) {
					
					DownloadService.downloadDeviceBookData(book);
					break;
				}
			
				// Book is already downloaded, set favorite and sync
				if(book.id().equalsIgnoreCase(book_object.mId) &&
						book.status() == BookStatus.STATUS_DEVICE) {
					
					if(book_object.mFavorite) {
						book.setFavorite(true);
						mApp.data().database().booksDatabase().update(book);
					}
					
					SyncService.syncServiceGetMyBookComplete(book);
				}
				
				// Book is stuck syncing, sync it again
				if(book.id().equalsIgnoreCase(book_object.mId) &&
						book.status() == BookStatus.STATUS_SYNCING) {
					
					if(book_object.mFavorite) {
						book.setFavorite(true);
						mApp.data().database().booksDatabase().update(book);
					}
					
					SyncService.syncServiceGetMyBookComplete(book);
				}

			}
		}
	}
	
	public void processSetMyBooksResponse(GetMyBooksObject books) {
		if(mApp == null || books == null || !books.isValid()) {
			return;
		}
		
		processSetMyBooks(books);
	}
	
	private void processSetMyBooks(GetMyBooksObject object) {
		ArrayList<BookObject> book_objects = object.objects();
		
		if(book_objects == null || book_objects.size() == 0) {
			return;
		}
		
		ArrayList<Book> books = mApp.data().database().booksDatabase().getAll();
		
		for(BookObject book_object : book_objects) {
			Book book = null;
			
			for(int index = 0; index < books.size(); index++) {
				book = books.get(index);
				
				if(book.id().equalsIgnoreCase(book_object.mId)) {
                    if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Processing SetMyBooks " + book.title() + "..."); }
					book.setSynced(true);
                    book.setRemoved(book_object.mRemoved);
					mApp.data().database().booksDatabase().update(book);
					break;
				}

			}
		}
	}

}
