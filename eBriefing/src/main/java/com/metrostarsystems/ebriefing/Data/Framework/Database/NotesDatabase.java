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

package com.metrostarsystems.ebriefing.Data.Framework.Database;

import java.util.ArrayList;

import com.metrostarsystems.ebriefing.Data.Framework.Database.BookmarksDatabase.BookmarksDatabaseChangedListener;
import com.metrostarsystems.ebriefing.Data.Framework.Notes.Note;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NotesDatabase {
	
	private static final String TAG = NotesDatabase.class.getSimpleName();

	private SQLiteDatabase 	mReadDatabase;
	private SQLiteDatabase	mWriteDatabase;
	private DatabaseHandle	mHelper;
	
	private ArrayList<NotesDatabaseChangedListener> mListeners;
	
	public NotesDatabase(DatabaseHandle helper) {
		mHelper = helper;
	}
	
	public NotesDatabase open() throws SQLException {
		mWriteDatabase = mHelper.getWritableDatabase();
		mReadDatabase = mHelper.getReadableDatabase();
		
		return this;
	}
	
	public void close() {
		mHelper.close();
	}
	
	public void addListener(NotesDatabaseChangedListener listener) {
		if(mListeners == null) {
			mListeners = new ArrayList<NotesDatabaseChangedListener>();
		}
		
		mListeners.add(listener);
	}
	
	public void removeListener(NotesDatabaseChangedListener listener) {
		if(mListeners == null) {
			return;
		}
		
		mListeners.remove(listener);
	}
	
	public void removeAllListeners() {
		if(mListeners == null) {
			return;
		}
		
		mListeners.clear();
	}
	
	private void notifyListeners(Note note) {
		if(mListeners != null) {
			for(NotesDatabaseChangedListener listener : mListeners) {
				listener.OnNotesDatabaseChangedListener(note);
			}
		}
	}
	
	private ContentValues generateContent(Note note) {
		ContentValues values = new ContentValues();
		
		values.put(DatabaseHandle.NOTES_COLUMN_ID, note.id());
		values.put(DatabaseHandle.NOTES_COLUMN_BOOK_ID, note.bookId());
		values.put(DatabaseHandle.NOTES_COLUMN_BOOK_VERSION, note.bookVersion());
		values.put(DatabaseHandle.NOTES_COLUMN_CHAPTER_ID, note.chapterId());
		values.put(DatabaseHandle.NOTES_COLUMN_PAGE_ID, note.pageId());
		values.put(DatabaseHandle.NOTES_COLUMN_PAGE_NUMBER, note.pageNumber());
		values.put(DatabaseHandle.NOTES_COLUMN_VALUE_URL, note.valueUrl());
		values.put(DatabaseHandle.NOTES_COLUMN_DATE_CREATED, note.dateCreated());
		values.put(DatabaseHandle.NOTES_COLUMN_DATE_MODIFIED, note.dateModified());
		values.put(DatabaseHandle.NOTES_COLUMN_REMOVED, note.isRemoved() == true ? 1 : 0);
		values.put(DatabaseHandle.NOTES_COLUMN_SYNCED, note.isSynced() == true ? 1 : 0);
		values.put(DatabaseHandle.NOTES_COLUMN_CONTENT, note.content());

		return values;
	}
	
	public boolean insert(Note note) {
		Log.i(TAG, "inserting note page: " + String.valueOf(note.pageNumber()));
		long rows = mWriteDatabase.insert(DatabaseHandle.NOTES_TABLE, null, generateContent(note));
		
		if(rows > 0) {
			notifyListeners(note);
			
			return true;
		} else {
			return false;
		}
	}
	
	public boolean updateById(Note note) {
		long rows = mWriteDatabase.update(	DatabaseHandle.NOTES_TABLE, 
								generateContent(note), 
								DatabaseHandle.NOTES_COLUMN_ID + " = ?", 	// SELECTIONS
								new String[] { String.valueOf(note.id()) } 	// SELECTION ARGS
								);
		
		if(rows > 0) {
			notifyListeners(note);
			
			return true;
		} else {
			return false;
		}
	}
	
	public boolean updateByNumber(Note note) {
		Log.i(TAG, "updating note page: " + String.valueOf(note.pageNumber()));
		long rows = mWriteDatabase.update(	DatabaseHandle.NOTES_TABLE, 
								generateContent(note), 
								DatabaseHandle.NOTES_COLUMN_PAGE_NUMBER + " = ? and " +
								DatabaseHandle.NOTES_COLUMN_BOOK_ID + " = ?", 	// SELECTIONS
								new String[] { String.valueOf(note.pageNumber()),
											   String.valueOf(note.bookId()) } 	// SELECTION ARGS
								);
		
		if(rows > 0) {
			notifyListeners(note);
			
			return true;
		} else {
			return false;
		}
	}
	
	public boolean deleteById(Note note) {
		long rows = mWriteDatabase.delete(	DatabaseHandle.NOTES_TABLE,
										DatabaseHandle.NOTES_COLUMN_ID + " = ?", 	// SELECTIONS
										new String[] { String.valueOf(note.id()) } 	// SELECTION ARGS
										);
		
		if(rows > 0) {
			notifyListeners(note);
			
			return true;
		} else {
			return false;
		}
	}
	
	public boolean deleteById(String noteId) {
		long rows = mWriteDatabase.delete(	DatabaseHandle.NOTES_TABLE,
										DatabaseHandle.NOTES_COLUMN_ID + " = ?", 	// SELECTIONS
										new String[] { String.valueOf(noteId) } 	// SELECTION ARGS
										);
		
		if(rows > 0) {
			notifyListeners(null);
			
			return true;
		} else {
			return false;
		}
	}
	
	public boolean deleteByNumber(Note note) {
		long rows = mWriteDatabase.delete(	DatabaseHandle.NOTES_TABLE,
											DatabaseHandle.NOTES_COLUMN_PAGE_NUMBER + " = ? and " +
											DatabaseHandle.NOTES_COLUMN_BOOK_ID + " = ?", 	// SELECTIONS
											new String[] { String.valueOf(note.pageNumber()),
														   String.valueOf(note.bookId())} 	// SELECTION ARGS
											);
		
		if(rows > 0) {
			notifyListeners(note);
			
			return true;
		} else {
			return false;
		}
	}
	
	public void deleteNotes(String bookId) {
		mWriteDatabase.delete(	DatabaseHandle.NOTES_TABLE,
								DatabaseHandle.NOTES_COLUMN_BOOK_ID + " = ?", 	// SELECTIONS
								new String[] { String.valueOf(bookId) } 	// SELECTION ARGS
								);
	}
	
	public boolean has(String bookId, int pageNumber) {
		return countByPage(bookId, pageNumber) > 0;
	}
	
	public int countByPage(String pageId) {
		return (int) DatabaseUtils.longForQuery(mReadDatabase, 
											"select count(*) from " + DatabaseHandle.NOTES_TABLE +
											" where " + DatabaseHandle.NOTES_COLUMN_PAGE_ID + " = ?" +
											" and " + DatabaseHandle.NOTES_COLUMN_REMOVED + " = ?",
											new String[] { String.valueOf(pageId),
														   "0" }
											);
	}
	
	public int countByChapter(String chapterId) {
		return (int) DatabaseUtils.longForQuery(mReadDatabase, 
											"select count(*) from " + DatabaseHandle.NOTES_TABLE +
											" where " + DatabaseHandle.NOTES_COLUMN_CHAPTER_ID + " = ?",
											new String[] { String.valueOf(chapterId) }
											);
	}
	
	public int countByBook(String bookId) {
		return (int) DatabaseUtils.longForQuery(mReadDatabase, 
											"select count(*) from " + DatabaseHandle.NOTES_TABLE +
											" where " + DatabaseHandle.NOTES_COLUMN_BOOK_ID + " = ?",
											new String[] { String.valueOf(bookId) }
											);
	}
	
	public int countByPage(String bookId, int pageNumber) {
		return (int) DatabaseUtils.longForQuery(mReadDatabase, 
											"select count(*) from " + DatabaseHandle.NOTES_TABLE +
											" where " + DatabaseHandle.NOTES_COLUMN_PAGE_NUMBER + " = ? and " +
											DatabaseHandle.NOTES_COLUMN_BOOK_ID + " = ?",
											new String[] { String.valueOf(pageNumber),
														   String.valueOf(bookId) }
											);
	}
	
	public Note noteById(String noteId) {
		Note note = null;
		
		Cursor res = null; 
		
		try {
			res = mReadDatabase.rawQuery("select * from " + DatabaseHandle.NOTES_TABLE +
											" where " + DatabaseHandle.NOTES_COLUMN_ID + " = ?", 
											new String[] { String.valueOf(noteId) }
											);
			if(res.getCount() > 0) {
				res.moveToFirst();
				note = new Note.Builder().fromCursor(res).build();
			}  
		} catch(Exception e) {
			Log.e(TAG, "noteById, Error: note id " + noteId + " " + e.getMessage());
		} finally {
			if(res != null) {
				res.close();
			}
		}

		return note;
	}
	
	public Note noteByNumber(String bookId, int pageNumber) {
		Note note = null;
		
		Cursor res = null;
		
		try {
			res = mReadDatabase.rawQuery("select * from " + DatabaseHandle.NOTES_TABLE +
											" where " + DatabaseHandle.NOTES_COLUMN_PAGE_NUMBER + " = ? and " +
											DatabaseHandle.NOTES_COLUMN_BOOK_ID + " = ?", 
											new String[] { String.valueOf(pageNumber),
														   String.valueOf(bookId) }
											);
			if(res.getCount() > 0) {
				res.moveToFirst();
				note = new Note.Builder().fromCursor(res).build();
			} 
		} catch(Exception e) {
			Log.e(TAG, "noteByNumber, Error: book id " + bookId + ", page number " + String.valueOf(pageNumber) + " " + e.getMessage());
		} finally {
			if(res != null) {
				res.close();
			}
		}
		
		return note;
	}
	
	public ArrayList<Note> notesByNumber(String bookId, int pageNumber) {
		ArrayList<Note> notes = new ArrayList<Note>();
		
		Cursor res = null;
		
		try {
			res = mReadDatabase.rawQuery("select * from " + DatabaseHandle.NOTES_TABLE +
											" where " + DatabaseHandle.NOTES_COLUMN_PAGE_NUMBER + " = ? and " +
											DatabaseHandle.NOTES_COLUMN_BOOK_ID + " = ?",  
											new String[] { String.valueOf(pageNumber),
														   String.valueOf(bookId) }
											);
			if(res.getCount() > 0) {
				res.moveToFirst();
				while(res.isAfterLast() == false) {
					notes.add(new Note.Builder().fromCursor(res).build());
					res.moveToNext();
				}
			}
		} catch(Exception e) {
			Log.e(TAG, "notesByNumber, Error: book id " + bookId + ", page number " + String.valueOf(pageNumber) + " " + e.getMessage());
		} finally {
			if(res != null) {
				res.close();
			}
		}

		return notes;
	}
	
	public ArrayList<Note> notesByBook(String bookId) {
		ArrayList<Note> notes = new ArrayList<Note>();

		Cursor res = null;
		
		try {
			res =  mReadDatabase.rawQuery("select * from " + DatabaseHandle.NOTES_TABLE +
											 " where " + DatabaseHandle.NOTES_COLUMN_BOOK_ID + " = ? " +
											 "order by " + DatabaseHandle.NOTES_COLUMN_PAGE_NUMBER + " asc",
											 new String[] { String.valueOf(bookId) }
											 );
			if(res.getCount() > 0) {
				res.moveToFirst();
				while(res.isAfterLast() == false) {
					notes.add(new Note.Builder().fromCursor(res).build());
					res.moveToNext();
				}
			}
		} catch(Exception e) {
			Log.e(TAG, "notesByBook, Error: book id " + bookId + " " + e.getMessage());
		} finally {
			if(res != null) {
				res.close();
			}
		}

		return notes;
	}

	public ArrayList<Note> notesByPage(String pageId) {
		ArrayList<Note> notes = new ArrayList<Note>();

		Cursor res = null;
		
		try {
			res =  mReadDatabase.rawQuery("select * from " + DatabaseHandle.NOTES_TABLE +
											 " where " + DatabaseHandle.NOTES_COLUMN_PAGE_ID + " = ?" +
                                             " order by " + DatabaseHandle.NOTES_COLUMN_DATE_MODIFIED + " desc",
											 new String[] { String.valueOf(pageId) }
											 );
			if(res.getCount() > 0) {
				res.moveToFirst();
				while(res.isAfterLast() == false) {
					notes.add(new Note.Builder().fromCursor(res).build());
					res.moveToNext();
				}
			}
		} catch(Exception e) {
			Log.e(TAG, "notesByPage, Error: page id " + pageId + " " + e.getMessage());
		} finally {
			if(res != null) {
				res.close();
			}
		}

		return notes;
	}
	
	public ArrayList<Note> notesNotSynced(String bookId) {
		ArrayList<Note> notes = new ArrayList<Note>();
		
		Cursor res = null;
		
		try {
			res = mReadDatabase.rawQuery("select * from " + DatabaseHandle.NOTES_TABLE +
											" where " + DatabaseHandle.NOTES_COLUMN_BOOK_ID + " = ?" +
											" and " + DatabaseHandle.NOTES_COLUMN_SYNCED + " = ?",  
											new String[] { String.valueOf(bookId),
														   "0" }
											);
			if(res.getCount() > 0) {
				res.moveToFirst();
				while(res.isAfterLast() == false) {
					notes.add(new Note.Builder().fromCursor(res).build());
					res.moveToNext();
				}
			}
		} catch(Exception e) {
			Log.e(TAG, "notesNotSynced, Error: book id " + bookId + " " + e.getMessage());
		} finally {
			if(res != null) {
				res.close();
			}
		}

		return notes;
	}
	
	public static interface NotesDatabaseChangedListener {
		public abstract void OnNotesDatabaseChangedListener(Note note);
	}
}
