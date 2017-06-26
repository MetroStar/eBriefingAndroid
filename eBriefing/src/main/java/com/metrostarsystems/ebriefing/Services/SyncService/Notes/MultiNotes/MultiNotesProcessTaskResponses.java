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
import com.metrostarsystems.ebriefing.Data.Framework.Notes.Note;
import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.MultiNotes.GetAllNotesObject.GetAllNoteObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.MultiNotes.GetNotesUpdatesObject.GetNoteUpdateObject;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;
import com.metrostarsystems.ebriefing.Settings;

import java.util.ArrayList;

public class MultiNotesProcessTaskResponses {

	private static final String TAG = MultiNotesProcessTaskResponses.class.getSimpleName();

	private MainApplication mApp;

	public MultiNotesProcessTaskResponses(MainApplication app) {
		mApp = app;
	} 

	public void processGetAllNotesResponse(String bookId, GetAllNotesObject objects) {
		if(mApp == null || bookId.isEmpty() || objects == null) {
			return;
		}
		
		Book book = mApp.data().database().booksDatabase().book(bookId);
		
		if(book == null) {
			return;
		}
		
		// Object contains no syncing information so just complete the note sync
		if(!objects.isValid()) {
			book.setSyncedNotes(true);
			mApp.data().database().booksDatabase().update(book);

            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Syncing: " + book.isSyncedNotes() + " " +
                                     book.isSyncedBookmarks() + " " +
                                     book.isSyncedAnnotations()); }
			
			if(book.isSyncedNotes() && book.isSyncedBookmarks() && book.isSyncedAnnotations()) {
				SyncService.syncServiceBookComplete(book);
			}
			return;
		}
		
		ArrayList<GetAllNoteObject> note_objects = objects.objects();
		
		if(note_objects == null || note_objects.size() <= 0) {
			
			book.setSyncedNotes(true);
			mApp.data().database().booksDatabase().update(book);
			
			if(book.isSyncedNotes() && book.isSyncedBookmarks() && book.isSyncedAnnotations()) {
				SyncService.syncServiceBookComplete(book);
			}
			return;
		}
		
		if(Settings.DEBUG_MESSAGES) { Log.i(TAG, book.title() + " Found " + String.valueOf(note_objects.size()) + " notes"); }
		
		ArrayList<Note> notes = mApp.data().database().notesDatabase().notesByBook(book.id());
		
		for(GetAllNoteObject note_object : note_objects) {
            boolean found = false;

            for(Note note : notes) {

                // Note exists
                if(note.id().equalsIgnoreCase(note_object.noteId())) {
                    found = true;

                    // Note was deleted, so remove it
                    if(note_object.isDeleted()) {
                        mApp.data().database().notesDatabase().deleteById(note_object.noteId());
                        break;
                    }

                    // Server Note is newer so update the local note
                    if(!note.isNewer(note_object.dateModified())) {
                        Note updated_note = new Note.Builder()
                                .fromNote(note)
                                .dateModified(note_object.dateModified())
                                .isSynced(true)
                                .content(note_object.text())
                                .build();

                        mApp.data().database().notesDatabase().updateById(updated_note);

                        break;
                        // Local Note is newer so set synced
                    } else {
                        Note updated_note = new Note.Builder()
                                .fromNote(note)
                                .dateModified(note.dateModified())
                                .isSynced(note.isSynced())
                                .content(note.content())
                                .build();

                        mApp.data().database().notesDatabase().updateById(updated_note);

                        break;
                    }
                }
            }

            if(!found) {

                Note new_note = new Note.Builder()
                        .id(note_object.noteId())
                        .bookId(note_object.bookId())
                        .bookVersion(note_object.bookVersion())
                        .chapterId(note_object.chapterId())
                        .pageId(note_object.pageId())
                        .pageNumber(note_object.pageNumber())
                        .dateCreated(note_object.dateCreated())
                        .dateModified(note_object.dateModified())
                        .isSynced(true)
                        .content(note_object.text())
                        .build();

                mApp.data().database().notesDatabase().insert(new_note);
            }

        }
		
		book.setSyncedNotes(true);
		mApp.data().database().booksDatabase().update(book);
		
		if(book.isSyncedNotes() && book.isSyncedBookmarks() && book.isSyncedAnnotations()) {
			SyncService.syncServiceBookComplete(book);
		}
	}

    public void processGetNotesUpdatesResponse(String bookId, GetNotesUpdatesObject objects) {
        if(mApp == null || bookId.isEmpty() || objects == null) {
            return;
        }

        Book book = mApp.data().database().booksDatabase().book(bookId);

        // if the book doesn't exist there is no need to process the notes
        if(book == null) {
            return;
        }

        // Object contains no syncing information so just complete the note sync
        if(!objects.isValid()) {

            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, book.title() + " Found 0 notes, last sync date: " + book.dateSynced()); }

            book.setSyncedNotes(true);
            mApp.data().database().booksDatabase().update(book);

            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Syncing: " + book.isSyncedNotes() + " " +
                    book.isSyncedBookmarks() + " " +
                    book.isSyncedAnnotations()); }

            if(book.isSyncedNotes() && book.isSyncedBookmarks() && book.isSyncedAnnotations()) {
                SyncService.syncServiceBookComplete(book);
            }
            return;
        }

        ArrayList<GetNoteUpdateObject> note_objects = objects.objects();

        if(Settings.DEBUG_MESSAGES) { Log.i(TAG, book.title() + " Found " + String.valueOf(note_objects.size()) + " notes"); }

        ArrayList<Note> notes = mApp.data().database().notesDatabase().notesByBook(book.id());

        for(GetNoteUpdateObject note_object : note_objects) {
            boolean found = false;

            for(Note note : notes) {

                // Note exists
                if(note.id().equalsIgnoreCase(note_object.noteId())) {
                    found = true;

                    // Note was deleted, so remove it
                    if(note_object.isDeleted()) {
                        mApp.data().database().notesDatabase().deleteById(note_object.noteId());
                        break;
                    }

                    // Server Note is newer so update the local note
                    if(!note.isNewer(note_object.dateModified())) {
                        Note updated_note = new Note.Builder()
                                .fromNote(note)
                                .dateModified(note_object.dateModified())
                                .isSynced(true)
                                .content(note_object.text())
                                .build();

                        mApp.data().database().notesDatabase().updateById(updated_note);

                        break;
                        // Local Note is newer so set synced
                    } else {
                        Note updated_note = new Note.Builder()
                                .fromNote(note)
                                .dateModified(note.dateModified())
                                .isSynced(note.isSynced())
                                .content(note.content())
                                .build();

                        mApp.data().database().notesDatabase().updateById(updated_note);

                        break;
                    }
                }
            }

            if(!found) {

                Note new_note = new Note.Builder()
                        .id(note_object.noteId())
                        .bookId(note_object.bookId())
                        .bookVersion(note_object.bookVersion())
                        .chapterId(note_object.chapterId())
                        .pageId(note_object.pageId())
                        .pageNumber(note_object.pageNumber())
                        .dateCreated(note_object.dateCreated())
                        .dateModified(note_object.dateModified())
                        .isSynced(true)
                        .content(note_object.text())
                        .build();

                mApp.data().database().notesDatabase().insert(new_note);
            }

        }

        book.setSyncedNotes(true);
        mApp.data().database().booksDatabase().update(book);

        if(book.isSyncedNotes() && book.isSyncedBookmarks() && book.isSyncedAnnotations()) {
            SyncService.syncServiceBookComplete(book);
        }
    }

    public void processSaveNotesResponse(String bookId, ArrayList<Note> syncedNotes) {
        if(mApp == null || bookId.isEmpty() || syncedNotes == null) {
            return;
        }

        Book book = mApp.data().database().booksDatabase().book(bookId);

        if(book == null) {
            return;
        }

        for(Note note : syncedNotes) {
            Note updated_note = new Note.Builder()
                    .fromNote(note)
                    .content(note.content())
                    .isSynced(true)
                    .build();

            mApp.data().database().notesDatabase().updateById(updated_note);
            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Synced note id: " + note.id() + " , page: " + note.pageNumber()); }
        }
    }
}
