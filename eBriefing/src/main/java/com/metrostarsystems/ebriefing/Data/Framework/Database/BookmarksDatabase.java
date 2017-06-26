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

import com.metrostarsystems.ebriefing.Data.Framework.Bookmarks.Bookmark;
import com.metrostarsystems.ebriefing.Data.Framework.Database.ChaptersDatabase.ChaptersDatabaseChangedListener;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BookmarksDatabase {
	
	private static final String TAG = BookmarksDatabase.class.getSimpleName();

	private SQLiteDatabase 	mReadDatabase;
	private SQLiteDatabase	mWriteDatabase;
	private DatabaseHandle	mHelper;
	
	private ArrayList<BookmarksDatabaseChangedListener> mListeners;
	
	public BookmarksDatabase(DatabaseHandle helper) {
		mHelper = helper;
	}
	
	public BookmarksDatabase open() throws SQLException {
		mWriteDatabase = mHelper.getWritableDatabase();
		mReadDatabase = mHelper.getReadableDatabase();
		
		return this;
	}
	
	public void close() {
		mHelper.close();
	}
	
	public void addListener(BookmarksDatabaseChangedListener listener) {
		if(mListeners == null) {
			mListeners = new ArrayList<BookmarksDatabaseChangedListener>();
		}
		
		mListeners.add(listener);
	}
	
	public void removeListener(BookmarksDatabaseChangedListener listener) {
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
	
	private void notifyListeners(Bookmark bookmark) {
		if(mListeners != null) {
			for(BookmarksDatabaseChangedListener listener : mListeners) {
				listener.OnBookmarksDatabaseChangedListener(bookmark);
			}
		}
	}
	
	private ContentValues generateContent(Bookmark bookmark) {
		ContentValues values = new ContentValues();
		
		values.put(DatabaseHandle.BOOKMARKS_COLUMN_ID, bookmark.id());
		values.put(DatabaseHandle.BOOKMARKS_COLUMN_BOOK_ID, bookmark.bookId());
		values.put(DatabaseHandle.BOOKMARKS_COLUMN_BOOK_VERSION, bookmark.bookVersion());
		values.put(DatabaseHandle.BOOKMARKS_COLUMN_CHAPTER_ID, bookmark.chapterId());
		values.put(DatabaseHandle.BOOKMARKS_COLUMN_PAGE_ID, bookmark.pageId());
		values.put(DatabaseHandle.BOOKMARKS_COLUMN_PAGE_NUMBER, bookmark.pageNumber());
		values.put(DatabaseHandle.BOOKMARKS_COLUMN_VALUE, bookmark.value());
		values.put(DatabaseHandle.BOOKMARKS_COLUMN_DATE_ADDED, bookmark.dateAdded());
		values.put(DatabaseHandle.BOOKMARKS_COLUMN_DATE_MODIFIED, bookmark.dateModified());
		values.put(DatabaseHandle.BOOKMARKS_COLUMN_REMOVED, bookmark.isRemoved() == true ? 1 : 0);
		values.put(DatabaseHandle.BOOKMARKS_COLUMN_SYNCED, bookmark.isSynced() == true ? 1 : 0);
		values.put(DatabaseHandle.BOOKMARKS_COLUMN_NEW, bookmark.isNew() == true ? 1 : 0);
		
		return values;
	}
	
	public boolean insert(Bookmark bookmark) {
		Log.i(TAG, "inserting bookmark page: " + String.valueOf(bookmark.pageNumber()));
		long rows = mWriteDatabase.insert(DatabaseHandle.BOOKMARKS_TABLE, null, generateContent(bookmark));
		
		if(rows > 0) {
			notifyListeners(bookmark);
			
			return true;
		} else {
			return false;
		}
	}
	
	public boolean updateByNumber(Bookmark bookmark) {
		Log.i(TAG, "updating bookmark page: " + String.valueOf(bookmark.pageNumber()));
		long rows = mWriteDatabase.update(	DatabaseHandle.BOOKMARKS_TABLE, 
											generateContent(bookmark), 
											DatabaseHandle.BOOKMARKS_COLUMN_PAGE_NUMBER + " = ? and " +
											DatabaseHandle.BOOKMARKS_COLUMN_BOOK_ID + " = ?", 	// SELECTIONS
											new String[] { String.valueOf(bookmark.pageNumber()),
														   bookmark.bookId() } 	// SELECTION ARGS
											);
		
		if(rows > 0) {
			notifyListeners(bookmark);
			
			return true;
		} else {
			return false;
		}
	}
	
	public boolean delete(Bookmark bookmark) {
		long rows = mWriteDatabase.delete(	DatabaseHandle.BOOKMARKS_TABLE,
											DatabaseHandle.BOOKMARKS_COLUMN_PAGE_NUMBER + " = ? and " +
											DatabaseHandle.BOOKMARKS_COLUMN_BOOK_ID + " = ?", 	// SELECTIONS
											new String[] { String.valueOf(bookmark.pageNumber()),
														   bookmark.bookId() } 	// SELECTION ARGS
											);
		
		if(rows > 0) {
			notifyListeners(bookmark);
			
			return true;
		} else {
			return false;
		}
	}
	
	public void deleteBookmarks(String bookId) {
		mWriteDatabase.delete(	DatabaseHandle.BOOKMARKS_TABLE,
								DatabaseHandle.BOOKMARKS_COLUMN_BOOK_ID + " = ?", 	// SELECTIONS
								new String[] { bookId } 	// SELECTION ARGS
								);
	}
	
	public boolean has(String bookId, int pageNumber) {
		return countByPage(bookId, pageNumber) > 0;
	}
	
	public int countByBook(String bookId) {
		return (int) DatabaseUtils.longForQuery(mReadDatabase, 
											"select count(*) from " + DatabaseHandle.BOOKMARKS_TABLE +
											" where " + DatabaseHandle.BOOKMARKS_COLUMN_BOOK_ID + " = ?",
											new String[] { bookId }
											);
	}
	
	public int countByPage(String bookId, int pageNumber) {
		return (int) DatabaseUtils.longForQuery(mReadDatabase, 
											"select count(*) from " + DatabaseHandle.BOOKMARKS_TABLE +
											" where (" + DatabaseHandle.BOOKMARKS_COLUMN_PAGE_NUMBER + " = ?" +
											" and " + DatabaseHandle.BOOKMARKS_COLUMN_BOOK_ID + " = ?)",
											new String[] { String.valueOf(pageNumber),
														   bookId}
											);
	}
	
	public int countByChapter(String chapterId) {
		return (int) DatabaseUtils.longForQuery(mReadDatabase, 
											"select count(*) from " + DatabaseHandle.BOOKMARKS_TABLE +
											" where " + DatabaseHandle.BOOKMARKS_COLUMN_CHAPTER_ID + " = ?",
											new String[] { chapterId }
											);
	}
	
	public Bookmark bookmark(String bookmarkId) {
		Bookmark bookmark = null;
		
		Cursor res = null;
		
		try {
			res = mReadDatabase.rawQuery("select * from " + DatabaseHandle.BOOKMARKS_TABLE +
											" where " + DatabaseHandle.BOOKMARKS_COLUMN_ID + " = ?", 
											new String[] { bookmarkId }
											);
			if(res.getCount() > 0) {
				res.moveToFirst();
				bookmark = new Bookmark.Builder().fromCursor(res).build();
			}
		} catch(Exception e) {
			Log.e(TAG, "bookmark, Error: bookmark id " + bookmarkId + " " + e.getMessage());
		} finally {
			if(res != null) {
				res.close();
			}
		}
		
		return bookmark;
	}
	
	public Bookmark bookmark(String bookId, int pageNumber) {
		Bookmark bookmark = null;
		
		Cursor res = null;
		
		try {
			res = mReadDatabase.rawQuery("select * from " + DatabaseHandle.BOOKMARKS_TABLE +
											" where " + DatabaseHandle.BOOKMARKS_COLUMN_PAGE_NUMBER + " = ? and " +
											DatabaseHandle.BOOKMARKS_COLUMN_BOOK_ID + " = ?",
											new String[] { String.valueOf(pageNumber),
														   bookId}
											);
			if(res.getCount() > 0) {
				res.moveToFirst();
				bookmark = new Bookmark.Builder().fromCursor(res).build();
			}
		} catch(Exception e) {
			Log.e(TAG, "bookmark, Error: book id " + bookId + ", page number " + String.valueOf(pageNumber) + " " + e.getMessage());
		} finally {
			if(res != null) {
				res.close();
			}
		}
		
		return bookmark;
	}
	
	public Bookmark bookmarkByPage(String pageId) {
		Bookmark bookmark = null;
		
		Cursor res = null;
		
		try {
			res = mReadDatabase.rawQuery("select * from " + DatabaseHandle.BOOKMARKS_TABLE +
											" where " + DatabaseHandle.BOOKMARKS_COLUMN_PAGE_ID + " = ?", 
											new String[] { pageId }
											);
			if(res.getCount() > 0) {
				res.moveToFirst();
				bookmark = new Bookmark.Builder().fromCursor(res).build();
			}
			
		} catch(Exception e) {
			Log.e(TAG, "bookmarkByPage, Error: page id " + pageId + " " + e.getMessage());
		} finally {
			if(res != null) {
				res.close();
			}
		}
		
		return bookmark;
	}

	public ArrayList<Bookmark> bookmarksByBook(String bookId) {
		ArrayList<Bookmark> bookmarks = new ArrayList<Bookmark>();
		
		Cursor res = null;

		try {
			res =  mReadDatabase.rawQuery("select * from " + DatabaseHandle.BOOKMARKS_TABLE +
											 " where " + DatabaseHandle.BOOKMARKS_COLUMN_BOOK_ID + " = ?" +
											 " order by " + DatabaseHandle.BOOKMARKS_COLUMN_PAGE_NUMBER + " asc",
											 new String[] { bookId }
											 );
			if(res.getCount() > 0) {
				res.moveToFirst();
				while(res.isAfterLast() == false) {
					bookmarks.add(new Bookmark.Builder().fromCursor(res).build());
					res.moveToNext();
				}
			}
		} catch(Exception e) {
			Log.e(TAG, "bookmarksByBook, Error: book id " + bookId + " " + e.getMessage());
		} finally {
			if(res != null) {
				res.close();
			}
		}
		
		return bookmarks;
	}
	
	public ArrayList<Bookmark> bookmarksNotSynced(String bookId) {
		ArrayList<Bookmark> bookmarks = new ArrayList<Bookmark>();
		
		Cursor res = null;

		try {
			res =  mReadDatabase.rawQuery("select * from " + DatabaseHandle.BOOKMARKS_TABLE +
											 " where " + DatabaseHandle.BOOKMARKS_COLUMN_BOOK_ID + " = ?" +
											 " and " + DatabaseHandle.BOOKMARKS_COLUMN_SYNCED + " = ?",
											 new String[] { bookId,
															"0" }
											 );
			if(res.getCount() > 0) {
				res.moveToFirst();
				while(res.isAfterLast() == false) {
					bookmarks.add(new Bookmark.Builder().fromCursor(res).build());
					res.moveToNext();
				}
			}
		} catch(Exception e) {
			Log.e(TAG, "bookmarksNotSynced, Error: book id " + bookId + " " + e.getMessage());
		} finally {
			if(res != null) {
				res.close();
			}
		}

		return bookmarks;
	}
	
	public static interface BookmarksDatabaseChangedListener {
		public abstract void OnBookmarksDatabaseChangedListener(Bookmark bookmark);
	}
}
