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

import java.util.ArrayList;

import android.util.Log;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.Settings;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Notes.Note;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.SyncNotes.GetMyNotesObject.NoteObject;

public class SyncNotesProcessTaskResponses {
	
	private static final String TAG = SyncNotesProcessTaskResponses.class.getSimpleName();
	
	private MainApplication mApp;
	private String			mBookId = "";
	private Book			mBook;
	
	public SyncNotesProcessTaskResponses(MainApplication app) {
		mApp = app;
	} 

	public void processGetMyNotesResponse(String bookId, GetMyNotesObject notes) {
		if(mApp == null || bookId == null || bookId.isEmpty()) {
			return;
		}
		
		mBookId = bookId;
		
		processGetMyNotes(notes);
	}
	
	public void processSetMyNoteResponse(boolean result, String bookId, int pageNumber) {
		if(mApp == null || bookId == null || bookId.isEmpty()) {
			return;
		}
		
		mBookId = bookId;
		
		processSetMyNote(result, pageNumber);
	}
	
	public void processRemoveMyNoteResponse(boolean result, String bookId, int pageNumber) {
		if(mApp == null || bookId == null || bookId.isEmpty()) {
			return;
		}
		
		mBookId = bookId;
		
		processRemoveMyNote(result, pageNumber);
	}
	
	
	private void processGetMyNotes(GetMyNotesObject notes_object) {
		mBook = mApp.data().database().booksDatabase().book(mBookId);
		
		// notes can be empty
		if(mApp == null || mBook == null || notes_object == null) {
			
			mBook.setSyncedNotes(true);
			mApp.data().database().booksDatabase().update(mBook);
			
			Log.i(TAG, "Syncing: " + mBook.isSyncedNotes() + " " + 
					 mBook.isSyncedBookmarks() + " " +
					 mBook.isSyncedAnnotations());
			
			if(mBook.isSyncedNotes() && mBook.isSyncedBookmarks() && mBook.isSyncedAnnotations()) {
				SyncService.syncServiceBookComplete(mBook);
			}
			
			return;
		}
		
		
		ArrayList<NoteObject> note_objects = notes_object.objects();
		
		if(note_objects == null || note_objects.size() <= 0) {
			
			mBook.setSyncedNotes(true);
			mApp.data().database().booksDatabase().update(mBook);
			
			Log.i(TAG, "Syncing: " + mBook.isSyncedNotes() + " " + 
					 mBook.isSyncedBookmarks() + " " +
					 mBook.isSyncedAnnotations());
			
			if(mBook.isSyncedNotes() && mBook.isSyncedBookmarks() && mBook.isSyncedAnnotations()) {
				SyncService.syncServiceBookComplete(mBook);
			}
			
			return;
		}
		
		if(Settings.DEBUG_MESSAGES) { Log.i(TAG, mBook.title() + " Found " + String.valueOf(note_objects.size()) + " notes"); }
		
		ArrayList<Note> notes = mApp.data().database().notesDatabase().notesByBook(mBook.id());
		
		
		
		for(NoteObject note_object : note_objects) {
			boolean found = false;
			
			for(Note note : notes) {
				
				// Note exists
				if(note.pageNumber() == note_object.mPageNumber) {
					found = true;
					
					if(note_object.mRemoved) {
						mApp.data().database().notesDatabase().deleteByNumber(note);
						
						break;
					}
					
					// Server Note is newer so update the local note
					if(!note.isNewer(note_object.mDateModified)) {
						Note updated_note = new Note.Builder()
															.id()
															.bookId(note_object.mBookId)
															.bookVersion(note_object.mBookVersion)
															.chapterId(note_object.mChapterId)
															.pageId(note_object.mPageId)
															.pageNumber(note_object.mPageNumber)
															.valueUrl(note_object.mValueUrl)
															.dateModified(note_object.mDateModified)
															.isSynced(true)
															.content(note_object.mContent)
															.build();
							
						mApp.data().database().notesDatabase().updateByNumber(updated_note);
						
						break;
					}
				}
			}
			
			// If it gets this far then the Note doesn't exist
			if(!found && !note_object.mRemoved) {

				Note new_note = new Note.Builder()
													.id()
													.bookId(note_object.mBookId)
													.bookVersion(note_object.mBookVersion)
													.chapterId(note_object.mChapterId)
													.pageId(note_object.mPageId)
													.pageNumber(note_object.mPageNumber)
													.valueUrl(note_object.mValueUrl)
													.dateModified(note_object.mDateModified)
													.isSynced(true)
													.content(note_object.mContent)
													.build();
											
				mApp.data().database().notesDatabase().insert(new_note);
			}
		}
		
		mBook.setSyncedNotes(true);
		mApp.data().database().booksDatabase().update(mBook);
		
		Log.i(TAG, "Syncing: " + mBook.isSyncedNotes() + " " + 
				 mBook.isSyncedBookmarks() + " " +
				 mBook.isSyncedAnnotations());
		
		if(mBook.isSyncedNotes() && mBook.isSyncedBookmarks() && mBook.isSyncedAnnotations()) {
			SyncService.syncServiceBookComplete(mBook);
		}
		
//		mBook.syncComplete(true, mBook.isSyncBookmarksComplete(), mBook.isSyncAnnotationsComplete());
	}
	
	private void processSetMyNote(boolean result, int pageNumber) {
		mBook = mApp.data().database().booksDatabase().book(mBookId);
		
		if(mApp == null || mBook == null) {
			return;
		}
		
		if(result) {
			
			Note note = mApp.data().database().notesDatabase().noteByNumber(mBook.id(), pageNumber);
				
			if(note.isRemoved()) {
				mApp.data().database().notesDatabase().deleteByNumber(note);
			} else {
				Note updated_note = new Note.Builder()
						.fromNote(note)
						.isSynced(true)
						.content(note.content())
						.build();

				mApp.data().database().notesDatabase().updateByNumber(updated_note);
			}
		}
		
	}
	
	private void processRemoveMyNote(boolean result, int pageNumber) {
		mBook = mApp.data().database().booksDatabase().book(mBookId);
		
		if(mApp == null || mBook == null) {
			return;
		}
		
		if(result) {
			Note note = mApp.data().database().notesDatabase().noteByNumber(mBook.id(), pageNumber);
			
			mApp.data().database().notesDatabase().deleteByNumber(note);

		}
	}
}
