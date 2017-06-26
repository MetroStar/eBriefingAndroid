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

import com.metrostarsystems.ebriefing.Dashboard.Sort.SortDirection;
import com.metrostarsystems.ebriefing.Dashboard.Sort.SortOption;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book.BookStatus;
import com.metrostarsystems.ebriefing.Settings;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BooksDatabase {
	
	private static final String TAG = BooksDatabase.class.getSimpleName();

	private SQLiteDatabase 	mReadDatabase;
	private SQLiteDatabase	mWriteDatabase;
	private DatabaseHandle	mHelper;
	
	private ArrayList<BooksDatabaseChangedListener> mListeners;
	
	public BooksDatabase(DatabaseHandle helper) {
		mHelper = helper;
	}
	
	public BooksDatabase open() throws SQLException {
		mWriteDatabase = mHelper.getWritableDatabase();
		mReadDatabase = mHelper.getReadableDatabase();
		
		return this;
	}
	
	public void close() {
		mHelper.close();
	}
	
	public void addListener(BooksDatabaseChangedListener listener) {
		if(mListeners == null) {
			mListeners = new ArrayList<BooksDatabaseChangedListener>();
		}
		
		mListeners.add(listener);
	}
	
	public void removeListener(BooksDatabaseChangedListener listener) {
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
	
	private void notifyListeners(Book book) {
		if(mListeners != null) {
			for(BooksDatabaseChangedListener listener : mListeners) {
				listener.OnBooksDatabaseChangedListener(book);
			}
		}
	}
	
	private ContentValues generateContent(Book book) {
		ContentValues values = new ContentValues();
		
		values.put(DatabaseHandle.BOOKS_COLUMN_ID, book.id());
		values.put(DatabaseHandle.BOOKS_COLUMN_STATUS, book.status().ordinal());
		values.put(DatabaseHandle.BOOKS_COLUMN_TITLE, book.title());
		values.put(DatabaseHandle.BOOKS_COLUMN_DESCRIPTION, book.description());
		values.put(DatabaseHandle.BOOKS_COLUMN_CHAPTER_COUNT, book.chapterCount());
		values.put(DatabaseHandle.BOOKS_COLUMN_PAGE_COUNT, book.pageCount());
		values.put(DatabaseHandle.BOOKS_COLUMN_SMALL_IMAGE_URL, book.smallImageUrl());
		values.put(DatabaseHandle.BOOKS_COLUMN_LARGE_IMAGE_URL, book.largeImageUrl());
		values.put(DatabaseHandle.BOOKS_COLUMN_IMAGE_VERSION, book.imageVersion());
		values.put(DatabaseHandle.BOOKS_COLUMN_BOOK_VERSION, book.bookVersion());
		values.put(DatabaseHandle.BOOKS_COLUMN_DATE_ADDED, book.dateAdded());
		values.put(DatabaseHandle.BOOKS_COLUMN_DATE_MODIFIED, book.dateModified());
		values.put(DatabaseHandle.BOOKS_COLUMN_NEW, book.isNew() == true ? 1 : 0);
		values.put(DatabaseHandle.BOOKS_COLUMN_FAVORITE, book.isFavorite() == true ? 1 : 0);
		values.put(DatabaseHandle.BOOKS_COLUMN_DOWNLOADED, book.isDownloaded() == true ? 1 : 0);
		values.put(DatabaseHandle.BOOKS_COLUMN_CHAPTERS_DOWNLOADED, book.isChaptersDownloaded() == true ? 1 : 0);
		values.put(DatabaseHandle.BOOKS_COLUMN_PAGES_DOWNLOADED, book.isPagesDownloaded() == true ? 1 : 0);
		values.put(DatabaseHandle.BOOKS_COLUMN_UPDATED, book.isUpdated() == true ? 1 : 0);
		values.put(DatabaseHandle.BOOKS_COLUMN_USER_ADDED, book.userAdded());
		values.put(DatabaseHandle.BOOKS_COLUMN_USER_MODIFIED, book.userModified());
        values.put(DatabaseHandle.BOOKS_COLUMN_DATE_SYNCED, book.dateSynced());
		values.put(DatabaseHandle.BOOKS_COLUMN_SYNCED, book.isSynced() == true ? 1 : 0);
		values.put(DatabaseHandle.BOOKS_COLUMN_SYNCED_NOTES, book.isSyncedNotes() == true ? 1 : 0);
		values.put(DatabaseHandle.BOOKS_COLUMN_SYNCED_BOOKMARKS, book.isSyncedBookmarks() == true ? 1 : 0);
		values.put(DatabaseHandle.BOOKS_COLUMN_SYNCED_ANNOTATIONS, book.isSyncedAnnotations() == true ? 1 : 0);
        values.put(DatabaseHandle.BOOKS_COLUMN_REMOVED, book.isRemoved() == true ? 1 : 0);

		return values;
	}
	
	public boolean insert(Book book) {
        if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "inserting book: " + book.title()); }
		long rows = mWriteDatabase.insert(DatabaseHandle.BOOKS_TABLE, null, generateContent(book));
		
		if(rows > 0) {
			notifyListeners(book);
			
			return true;
		} else {
			return false;
		}
	}
	
	public boolean update(Book book) {
        if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "updating book: " + book.title()); }
		long rows = mWriteDatabase.update(	DatabaseHandle.BOOKS_TABLE, 
									generateContent(book), 
									DatabaseHandle.BOOKS_COLUMN_ID + " = ?", 	// SELECTIONS
									new String[] { String.valueOf(book.id()) } 	// SELECTION ARGS
									);
		
		if(rows > 0) {
			notifyListeners(book);
			
			return true;
		} else {
			return false;
		}
	}
	
	public boolean delete(Book book) {
		long rows = mWriteDatabase.delete(	DatabaseHandle.BOOKS_TABLE,
								DatabaseHandle.BOOKS_COLUMN_ID + " = ?", 	// SELECTIONS
								new String[] { String.valueOf(book.id()) } 	// SELECTION ARGS
								);

		if(rows > 0) {
			notifyListeners(book);
			
			return true;
		} else {
			return false;
		}
	}
	
	public boolean has(String bookId) {
		return countByBook(bookId) > 0;
	}
	
	public int countByBook(String bookId) {
		return (int) DatabaseUtils.longForQuery(mReadDatabase, 
								"select count(*) from " + DatabaseHandle.BOOKS_TABLE +
								" where " + DatabaseHandle.BOOKS_COLUMN_ID + " = ?",
								new String[] { String.valueOf(bookId) }
								);
	}
	
	public int countAvailableBooks() {
		return (int) DatabaseUtils.longForQuery(mReadDatabase, 
								"select count(*) from " + DatabaseHandle.BOOKS_TABLE +
								" where " + DatabaseHandle.BOOKS_COLUMN_STATUS + " = ?",
								new String[] { String.valueOf(BookStatus.STATUS_SERVER.ordinal()) }
								);
	}
	
	public int countMyBooks() {
		return (int) DatabaseUtils.longForQuery(mReadDatabase, 
								"select count(*) from " + DatabaseHandle.BOOKS_TABLE +
								" where " + DatabaseHandle.BOOKS_COLUMN_STATUS + " = ? or " +
								DatabaseHandle.BOOKS_COLUMN_STATUS + " = ? or " + 
								DatabaseHandle.BOOKS_COLUMN_STATUS + " = ? or " +
								DatabaseHandle.BOOKS_COLUMN_STATUS + " = ? or " +
								DatabaseHandle.BOOKS_COLUMN_STATUS + " = ?",
								new String[] { String.valueOf(BookStatus.STATUS_DEVICE.ordinal()),
											 String.valueOf(BookStatus.STATUS_DOWNLOADING_PENDING.ordinal()),
											 String.valueOf(BookStatus.STATUS_DOWNLOADING_ACTIVE.ordinal()),
											 String.valueOf(BookStatus.STATUS_DOWNLOADING_PAUSED.ordinal()),
											 String.valueOf(BookStatus.STATUS_SYNCING.ordinal())}
							  	);
	}
	
	public int countUpdatedBooks() {
		return (int) DatabaseUtils.longForQuery(mReadDatabase, 
								"select count(*) from " + DatabaseHandle.BOOKS_TABLE +
								" where " + DatabaseHandle.BOOKS_COLUMN_UPDATED + " = ?",
								  new String[] { "1" }
								  );
	}
	
	public int countFavoriteBooks() {
		return (int) DatabaseUtils.longForQuery(mReadDatabase, 
								"select count(*) from " + DatabaseHandle.BOOKS_TABLE +
								" where " + DatabaseHandle.BOOKS_COLUMN_FAVORITE + " = ?",
								  new String[] { "1" }
								  );
	}
	
	public int count() {
		return (int) DatabaseUtils.queryNumEntries(mReadDatabase, DatabaseHandle.BOOKS_TABLE);
	}
	
	public Book book(String bookId) {
		Book book = null;
		
		Cursor res = null;
		
		try {
			res = mReadDatabase.rawQuery("select * from " + DatabaseHandle.BOOKS_TABLE +
											" where " + DatabaseHandle.BOOKS_COLUMN_ID + " = ?", 
											new String[] { String.valueOf(bookId) }
											);
			if(res.getCount() > 0) {
				res.moveToFirst();
				book = new Book.Builder().fromCursor(res).build();
			}
		} catch(Exception e) {
            if(Settings.DEBUG_MESSAGES) { Log.e(TAG, "book, Error: book id " + bookId + " " + e.getMessage()); }
		} finally {
			if(res != null) {
				res.close();
			}
		}
		
		return book;
	}

	public ArrayList<Book> getAll() {
		ArrayList<Book> books = new ArrayList<Book>();
		
		Cursor res = null;

		try {
			res =  mReadDatabase.rawQuery( "select * from " + DatabaseHandle.BOOKS_TABLE, null);
			
			if(res.getCount() > 0) {
				res.moveToFirst();
				while(res.isAfterLast() == false) {
					books.add(new Book.Builder().fromCursor(res).build());
					res.moveToNext();
				}
			}
		} catch(Exception e) {
            if(Settings.DEBUG_MESSAGES) { Log.e(TAG, "getAll, " + e.getMessage()); }
		} finally {
			if(res != null) {
				res.close();
			}
		}

		return books;
	}
	
	public ArrayList<Book> getMyBooks() {
		ArrayList<Book> books = new ArrayList<Book>();
		
		Cursor res = null;

		try {
			res =  mReadDatabase.rawQuery( "select * from " + DatabaseHandle.BOOKS_TABLE +
											  " where " + DatabaseHandle.BOOKS_COLUMN_STATUS + " = ? or " +
											  DatabaseHandle.BOOKS_COLUMN_STATUS + " = ? or " + 
											  DatabaseHandle.BOOKS_COLUMN_STATUS + " = ? or " +
											  DatabaseHandle.BOOKS_COLUMN_STATUS + " = ? or " +
											  DatabaseHandle.BOOKS_COLUMN_STATUS + " = ?",
											  new String[] { String.valueOf(BookStatus.STATUS_DEVICE.ordinal()),
															 String.valueOf(BookStatus.STATUS_DOWNLOADING_PENDING.ordinal()),
															 String.valueOf(BookStatus.STATUS_DOWNLOADING_ACTIVE.ordinal()),
															 String.valueOf(BookStatus.STATUS_DOWNLOADING_PAUSED.ordinal()),
															 String.valueOf(BookStatus.STATUS_SYNCING.ordinal())}
											  );
			if(res.getCount() > 0) {
				res.moveToFirst();
				while(res.isAfterLast() == false) {
					books.add(new Book.Builder().fromCursor(res).build());
					res.moveToNext();
				}
			}
		} catch(Exception e) {
            if(Settings.DEBUG_MESSAGES) { Log.e(TAG, "getMyBooks, " + e.getMessage()); }
		} finally {
			if(res != null) {
				res.close();
			}
		}

		return books;
	}
	
	public ArrayList<Book> getMyBooks(SortOption sort, SortDirection direction) {
		if(sort == null || direction == null) {
			return getMyBooks();
		}
		
		ArrayList<Book> books = new ArrayList<Book>();
		
		Cursor res = null;

		try {
			res =  mReadDatabase.rawQuery( "select * from " + DatabaseHandle.BOOKS_TABLE +
											  " where " + DatabaseHandle.BOOKS_COLUMN_STATUS + " = ? or " +
											  DatabaseHandle.BOOKS_COLUMN_STATUS + " = ? or " + 
											  DatabaseHandle.BOOKS_COLUMN_STATUS + " = ? or " +
											  DatabaseHandle.BOOKS_COLUMN_STATUS + " = ? or " +
											  DatabaseHandle.BOOKS_COLUMN_STATUS + " = ?" + 
											  " order by " + sort.column() + " collate nocase " + direction.direction(),
											  new String[] { String.valueOf(BookStatus.STATUS_DEVICE.ordinal()),
															 String.valueOf(BookStatus.STATUS_DOWNLOADING_PENDING.ordinal()),
															 String.valueOf(BookStatus.STATUS_DOWNLOADING_ACTIVE.ordinal()),
															 String.valueOf(BookStatus.STATUS_DOWNLOADING_PAUSED.ordinal()),
															 String.valueOf(BookStatus.STATUS_SYNCING.ordinal())}
											  );
			if(res.getCount() > 0) {
				res.moveToFirst();
				while(res.isAfterLast() == false) {
					books.add(new Book.Builder().fromCursor(res).build());
					res.moveToNext();
				}
			}
		} catch(Exception e) {
            if(Settings.DEBUG_MESSAGES) { Log.e(TAG, "getMyBooks, Error: sort option " + sort + ", sort direction " + direction + " " + e.getMessage()); }
		} finally {
			if(res != null) {
				res.close();
			}
		}

		return books;
	}
	
	public ArrayList<Book> getMyBooks(SortOption sort, SortDirection direction, String contains) {
		if(contains == null || contains.isEmpty()) {
			return getMyBooks(sort, direction);
		}

		ArrayList<Book> books = new ArrayList<Book>();
		
		Cursor res = null;

		try {
			res =  mReadDatabase.rawQuery( "select * from " + DatabaseHandle.BOOKS_TABLE +
											  " where " + DatabaseHandle.BOOKS_COLUMN_TITLE + " like ? and " +
											  DatabaseHandle.BOOKS_COLUMN_STATUS + " = ? or " +
											  DatabaseHandle.BOOKS_COLUMN_STATUS + " = ? or " + 
											  DatabaseHandle.BOOKS_COLUMN_STATUS + " = ? or " +
											  DatabaseHandle.BOOKS_COLUMN_STATUS + " = ? or " +
											  DatabaseHandle.BOOKS_COLUMN_STATUS + " = ?" + 
											  " order by " + sort.column() + " collate nocase " + direction.direction(),
											  new String[] { "%" + contains + "%",
															 String.valueOf(BookStatus.STATUS_DEVICE.ordinal()),
															 String.valueOf(BookStatus.STATUS_DOWNLOADING_PENDING.ordinal()),
															 String.valueOf(BookStatus.STATUS_DOWNLOADING_ACTIVE.ordinal()),
															 String.valueOf(BookStatus.STATUS_DOWNLOADING_PAUSED.ordinal()),
															 String.valueOf(BookStatus.STATUS_SYNCING.ordinal())}
											  );
			if(res.getCount() > 0) {
				res.moveToFirst();
				while(res.isAfterLast() == false) {
					books.add(new Book.Builder().fromCursor(res).build());
					res.moveToNext();
				}
			}
		} catch(Exception e) {
            if(Settings.DEBUG_MESSAGES) { Log.e(TAG, "getMyBooks, Error: sort option " + sort + ", sort direction " + direction + ", contains " + contains + " " + e.getMessage()); }
		} finally {
			if(res != null) {
				res.close();
			}
		}

		return books;
	}
	
	/**
	 * Returns all my books that are not synced
	 * @return all my books that are not synced
	 */
	public ArrayList<Book> getBooksNotSynced() {
		ArrayList<Book> books = new ArrayList<Book>();
		
		Cursor res = null;

		try {
			res =  mReadDatabase.rawQuery( "select * from " + DatabaseHandle.BOOKS_TABLE +
											  " where " + DatabaseHandle.BOOKS_COLUMN_SYNCED + " = ?",
											  new String[] { "0" }
											  );
			if(res.getCount() > 0) {
				res.moveToFirst();
				while(res.isAfterLast() == false) {
					books.add(new Book.Builder().fromCursor(res).build());
					res.moveToNext();
				}
			}
		} catch(Exception e) {
            if(Settings.DEBUG_MESSAGES) { Log.e(TAG, "getMyBooksNotSynced, " + e.getMessage()); }
		} finally {
			if(res != null) {
				res.close();
			}
		}
		
		return books;
	}
	
	public ArrayList<String> getMyBooksTitles() {
		ArrayList<Book> books = getMyBooks();
		ArrayList<String> titles = new ArrayList<String>();
		
		for(Book book : books) {
			titles.add(book.title());
		}
		
		return titles;
	}
	
	public ArrayList<Book> getFavoriteBooks() {
		ArrayList<Book> books = new ArrayList<Book>();
		
		Cursor res = null;

		try {
			res =  mReadDatabase.rawQuery( "select * from " + DatabaseHandle.BOOKS_TABLE +
											  " where " + DatabaseHandle.BOOKS_COLUMN_FAVORITE + " = ?",
											  new String[] { "1" }
											  );
			if(res.getCount() > 0) {
				res.moveToFirst();
				while(res.isAfterLast() == false) {
					books.add(new Book.Builder().fromCursor(res).build());
					res.moveToNext();
				}
			}
		} catch(Exception e) {
            if(Settings.DEBUG_MESSAGES) { Log.e(TAG, "getFavoriteBooks, " + e.getMessage()); }
		} finally {
			if(res != null) {
				res.close();
			}
		}

		return books;
	}
	
	public ArrayList<Book> getAvailableBooks() {
		ArrayList<Book> books = new ArrayList<Book>();

		Cursor res = null;
		
		try {
			res =  mReadDatabase.rawQuery( "select * from " + DatabaseHandle.BOOKS_TABLE +
											  " where " + DatabaseHandle.BOOKS_COLUMN_STATUS + " = ?",
											  new String[] { String.valueOf(BookStatus.STATUS_SERVER.ordinal()) }
											  );
			if(res.getCount() > 0) {
				res.moveToFirst();
				while(res.isAfterLast() == false) {
					books.add(new Book.Builder().fromCursor(res).build());
					res.moveToNext();
				}
			}
		} catch(Exception e) {
            if(Settings.DEBUG_MESSAGES) { Log.e(TAG, "getAvailableBooks, " + e.getMessage()); }
		} finally {
			if(res != null) {
				res.close();
			}
		}

		return books;
	}
	
	public ArrayList<Book> getUpdatedBooks() {
		ArrayList<Book> books = new ArrayList<Book>();

		Cursor res = null;
		
		try {
			res =  mReadDatabase.rawQuery( "select * from " + DatabaseHandle.BOOKS_TABLE +
											  " where " + DatabaseHandle.BOOKS_COLUMN_UPDATED + " = ?",
											  new String[] { "1" }
											  );
			if(res.getCount() > 0) {
				res.moveToFirst();
				while(res.isAfterLast() == false) {
					books.add(new Book.Builder().fromCursor(res).build());
					res.moveToNext();
				}
			}
		} catch(Exception e) {
            if(Settings.DEBUG_MESSAGES) { Log.e(TAG, "getUpdatedBooks, " + e.getMessage()); }
		} finally {
			if(res != null) {
				res.close();
			}
		}

		return books;
	}

    /**
     * Returns all my books that have not completed the download
     * @return all my books that have not completed the download
     */
    public ArrayList<Book> getMyBooksNotDownloaded() {
        ArrayList<Book> books = new ArrayList<Book>();

        Cursor res = null;

        try {
            res =  mReadDatabase.rawQuery( "select * from " + DatabaseHandle.BOOKS_TABLE +
                            " where " + DatabaseHandle.BOOKS_COLUMN_STATUS + " = ?" +
                            " or " + DatabaseHandle.BOOKS_COLUMN_STATUS + " = ?" +
                            " or " + DatabaseHandle.BOOKS_COLUMN_STATUS + " = ?",
                    new String[] {  String.valueOf(BookStatus.STATUS_DOWNLOADING_ACTIVE.ordinal()),
                                    String.valueOf(BookStatus.STATUS_DOWNLOADING_PAUSED.ordinal()),
                                    String.valueOf(BookStatus.STATUS_DOWNLOADING_PENDING.ordinal()) }
            );
            if(res.getCount() > 0) {
                res.moveToFirst();
                while(res.isAfterLast() == false) {
                    books.add(new Book.Builder().fromCursor(res).build());
                    res.moveToNext();
                }
            }
        } catch(Exception e) {
            if(Settings.DEBUG_MESSAGES) { Log.e(TAG, "getMyBooksNotDownloaded, " + e.getMessage()); }
        } finally {
            if(res != null) {
                res.close();
            }
        }

        return books;
    }

	public static interface BooksDatabaseChangedListener {
		public abstract void OnBooksDatabaseChangedListener(Book book);
	}
}
