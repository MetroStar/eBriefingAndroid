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

package com.metrostarsystems.ebriefing.Services.CoreService.Book;

import java.util.ArrayList;

import android.util.Log;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.Settings;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book.BookStatus;
import com.metrostarsystems.ebriefing.Services.CoreService.Book.GetBooksObject.ServerBookObject;
import com.metrostarsystems.ebriefing.Services.DownloadService.DownloadService;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;

public class ProcessGetBooksTaskResponses {

	private static final String TAG = ProcessGetBooksTaskResponses.class.getSimpleName();
	
	private MainApplication mApp;
	
	public ProcessGetBooksTaskResponses(MainApplication app) {
		mApp = app;
	}
	
	public void processGetBooksResponse(GetBooksObject object) {
		if(mApp == null || object == null) {
			return;
		}
		
		processGetBooks(object);
	}
	
	private void processGetBooks(GetBooksObject object) {
		if(mApp == null || object == null || !object.isValid()) {
			return;
		}
		
		ArrayList<ServerBookObject> book_objects = object.objects();
		
		if(book_objects == null) {
			return;
		}

        if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Processing: " + String.valueOf(book_objects.size()) + " books..."); }
		
		ArrayList<Book> books = mApp.data().database().booksDatabase().getAll();
		
		for(ServerBookObject book_object : book_objects) {
			boolean found = false;
			
			for(Book book : books) {
				
				if(book.id().equalsIgnoreCase(book_object.id())) {
					found = true;
					
					if(book.bookVersion() < book_object.bookVersion() &&
							book.status() == BookStatus.STATUS_DEVICE) {
						// Server Book Version is greater than Local Book Version so we need to update
						Book updated_book = new Book.Builder()
												.fromServer(book_object)
												.updated(true)
												.build();
						
						mApp.data().database().booksDatabase().update(updated_book);
						
						break;
					}
					
					break;
				}
				
			}
			
			// Server Book does not exist locally so add it
			if(!found) {
				Book new_book = new Book.Builder()
												.fromServer(book_object)
												.status(BookStatus.STATUS_SERVER)
												.build();
				
				mApp.data().database().booksDatabase().insert(new_book);
			}
		}
	}
}
