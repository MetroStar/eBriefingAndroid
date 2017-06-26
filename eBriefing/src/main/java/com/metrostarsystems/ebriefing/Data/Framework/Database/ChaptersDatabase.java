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

import com.metrostarsystems.ebriefing.Data.Framework.Chapter.Chapter;
import com.metrostarsystems.ebriefing.Data.Framework.Database.BooksDatabase.BooksDatabaseChangedListener;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ChaptersDatabase {
	
	private static final String TAG = ChaptersDatabase.class.getSimpleName();

	private SQLiteDatabase 	mReadDatabase;
	private SQLiteDatabase	mWriteDatabase;
	private DatabaseHandle	mHelper;
	
	private ArrayList<ChaptersDatabaseChangedListener> mListeners;
	
	public ChaptersDatabase(DatabaseHandle helper) {
		mHelper = helper;
	}
	
	public ChaptersDatabase open() throws SQLException {
		mWriteDatabase = mHelper.getWritableDatabase();
		mReadDatabase = mHelper.getReadableDatabase();
		
		return this;
	}
	
	public void close() {
		mHelper.close();
	}
	
	public void addListener(ChaptersDatabaseChangedListener listener) {
		if(mListeners == null) {
			mListeners = new ArrayList<ChaptersDatabaseChangedListener>();
		}
		
		mListeners.add(listener);
	}
	
	public void removeListener(ChaptersDatabaseChangedListener listener) {
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
	
	private void notifyListeners(Chapter chapter) {
		if(mListeners != null) {
			for(ChaptersDatabaseChangedListener listener : mListeners) {
				listener.OnChaptersDatabaseChangedListener(chapter);
			}
		}
	}
	
	private ContentValues generateContent(Chapter chapter) {
		ContentValues values = new ContentValues();
		
		values.put(DatabaseHandle.CHAPTERS_COLUMN_ID, chapter.id());
		values.put(DatabaseHandle.CHAPTERS_COLUMN_BOOK_ID, chapter.bookId());
		values.put(DatabaseHandle.CHAPTERS_COLUMN_TITLE, chapter.title());
		values.put(DatabaseHandle.CHAPTERS_COLUMN_DESCRIPTION, chapter.description());
		values.put(DatabaseHandle.CHAPTERS_COLUMN_PAGE_COUNT, chapter.pageCount());
		values.put(DatabaseHandle.CHAPTERS_COLUMN_SMALL_IMAGE_URL, chapter.smallImageUrl());
		values.put(DatabaseHandle.CHAPTERS_COLUMN_LARGE_IMAGE_URL, chapter.largeImageUrl());
		values.put(DatabaseHandle.CHAPTERS_COLUMN_IMAGE_VERSION, chapter.imageVersion());
		values.put(DatabaseHandle.CHAPTERS_COLUMN_FIRST_PAGE_ID, chapter.firstPageId());
		
		return values;
	}
	
	public boolean insert(Chapter chapter) {
		long rows = mWriteDatabase.insert(DatabaseHandle.CHAPTERS_TABLE, null, generateContent(chapter));
		
		if(rows > 0) {
			notifyListeners(chapter);
			
			return true;
		} else {
			return false;
		}
	}

	public void deleteChapter(String chapterId) {
		mWriteDatabase.delete(	DatabaseHandle.CHAPTERS_TABLE,
								DatabaseHandle.CHAPTERS_COLUMN_ID + " = ?", 	// SELECTIONS
								new String[] { String.valueOf(chapterId) } 	// SELECTION ARGS
								);
	}
	
	public void deleteChapters(String bookId) {
		mWriteDatabase.delete(	DatabaseHandle.CHAPTERS_TABLE,
								DatabaseHandle.CHAPTERS_COLUMN_BOOK_ID + " = ?", 	// SELECTIONS
								new String[] { String.valueOf(bookId) } 	// SELECTION ARGS
								);
	}
	
	public int countByBook(String bookId) {
		return (int) DatabaseUtils.longForQuery(mReadDatabase, 
								"select count(*) from " + DatabaseHandle.CHAPTERS_TABLE +
								" where " + DatabaseHandle.CHAPTERS_COLUMN_BOOK_ID + " = ?",
								new String[] { String.valueOf(bookId) }
								);
	}
	
	public Chapter chapter(String chapterId) {
		Chapter chapter = null;
		
		Cursor res = null;
		
		try {
			res = mReadDatabase.rawQuery("select * from " + DatabaseHandle.CHAPTERS_TABLE +
											" where " + DatabaseHandle.CHAPTERS_COLUMN_ID + " = ?", 
											new String[] { String.valueOf(chapterId) }
											);
			if(res.getCount() > 0) {
				res.moveToFirst();
				chapter = new Chapter.Builder().fromCursor(res).build();
			}
		} catch(Exception e) {
			Log.e(TAG, "chapter, Error: chapter id " + chapterId + " " + e.getMessage());
		} finally {
			if(res != null) {
				res.close();
			}
		}
		
		return chapter;
	}
	
	public String chapterId(String bookId, int pageNumber) {
		int page_count = 0;
	
		ArrayList<Chapter> chapters = chaptersByBook(bookId);
		
		for(int index = 0; index < chapters.size(); index++) {
			Chapter chapter = chapters.get(index);
			
			page_count += chapter.pageCount();
			
			if(pageNumber <= page_count) {
				return chapters.get(index).id();
			}
		}
		
		return "";
	}
	
	public int chapterNumber(String bookId, int pageNumber) {
		int page_count = 0;
	
		ArrayList<Chapter> chapters = chaptersByBook(bookId);
		
		for(int index = 0; index < chapters.size(); index++) {
			Chapter chapter = chapters.get(index);
			
			page_count += chapter.pageCount();
			
			if(pageNumber <= page_count) {
				return index + 1;
			}
		}
		
		return 0;
	}

	public ArrayList<Chapter> chaptersByBook(String bookId) {
		ArrayList<Chapter> chapters = new ArrayList<Chapter>();

		Cursor res = null;
		
		try {
			res =  mReadDatabase.rawQuery("select * from " + DatabaseHandle.CHAPTERS_TABLE +
											 " where " + DatabaseHandle.CHAPTERS_COLUMN_BOOK_ID + " = ?",
											 new String[] { String.valueOf(bookId) }
											 );
			if(res.getCount() > 0) {
				res.moveToFirst();
				while(res.isAfterLast() == false) {
					chapters.add(new Chapter.Builder().fromCursor(res).build());
					res.moveToNext();
				}
			}
		} catch(Exception e) {
			Log.e(TAG, "chaptersByBook, Error: book id " + bookId + " " + e.getMessage());
		} finally {
			if(res != null) {
				res.close();
			}
		}

		return chapters;
	}
	
	public String toString(String bookId) {
		ArrayList<Chapter> chapters = chaptersByBook(bookId);
		
		StringBuilder sb = new StringBuilder();
		
		for(Chapter chapter : chapters) {
			sb.append(chapter.title() + "\n");
		}
		
		return sb.toString();
	}
	
	public static interface ChaptersDatabaseChangedListener {
		public abstract void OnChaptersDatabaseChangedListener(Chapter chapter);
	}
}
