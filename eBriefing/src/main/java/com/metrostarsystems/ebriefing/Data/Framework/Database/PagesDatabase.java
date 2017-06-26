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

import com.metrostarsystems.ebriefing.Data.Framework.Page.Page;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PagesDatabase {
	
	private static final String TAG = PagesDatabase.class.getSimpleName();

	private SQLiteDatabase 	mReadDatabase;
	private SQLiteDatabase	mWriteDatabase;
	private DatabaseHandle	mHelper;
	
	public PagesDatabase(DatabaseHandle helper) {
		mHelper = helper;
	}
	
	public PagesDatabase open() throws SQLException {
		mWriteDatabase = mHelper.getWritableDatabase();
		mReadDatabase = mHelper.getReadableDatabase();
		
		return this;
	}
	
	public void close() {
		mHelper.close();
	}
	
	private ContentValues generateContent(Page page) {
		ContentValues values = new ContentValues();
		
		values.put(DatabaseHandle.PAGES_COLUMN_ID, page.id());
		values.put(DatabaseHandle.PAGES_COLUMN_CHAPTER_ID, page.chapterId());
		values.put(DatabaseHandle.PAGES_COLUMN_BOOK_ID, page.bookId());
		values.put(DatabaseHandle.PAGES_COLUMN_URL, page.url());
		values.put(DatabaseHandle.PAGES_COLUMN_PAGE_NUMBER, page.pageNumber());
		values.put(DatabaseHandle.PAGES_COLUMN_MD5, page.md5());
		values.put(DatabaseHandle.PAGES_COLUMN_TYPE, page.type());
		values.put(DatabaseHandle.PAGES_COLUMN_VERSION, page.version());
		return values;
	}
	
	public void insert(Page page) {
		mWriteDatabase.insert(DatabaseHandle.PAGES_TABLE, null, generateContent(page));
	}

	public void deletePage(String pageId) {
		mWriteDatabase.delete(	DatabaseHandle.PAGES_TABLE,
								DatabaseHandle.PAGES_COLUMN_ID + " = ?", 	// SELECTIONS
								new String[] { String.valueOf(pageId) } 	// SELECTION ARGS
								);
	}
	
	public void deletePages(String bookId) {
		mWriteDatabase.delete(	DatabaseHandle.PAGES_TABLE,
								DatabaseHandle.PAGES_COLUMN_BOOK_ID + " = ?", 	// SELECTIONS
								new String[] { String.valueOf(bookId) } 	// SELECTION ARGS
								);
	}
	
	public int countByChapter(String chapterId) {
		return (int) DatabaseUtils.longForQuery(mReadDatabase, 
								"select count(*) from " + DatabaseHandle.PAGES_TABLE +
								" where " + DatabaseHandle.PAGES_COLUMN_CHAPTER_ID + " = ?",
								new String[] { String.valueOf(chapterId) }
								);
	}
	
	public int countByBook(String bookId) {
		return (int) DatabaseUtils.longForQuery(mReadDatabase, 
								"select count(*) from " + DatabaseHandle.PAGES_TABLE +
								" where " + DatabaseHandle.PAGES_COLUMN_BOOK_ID + " = ?",
								new String[] { String.valueOf(bookId) }
								);
	}
	
	public Page page(String pageId) {
		Page page = null;
		
		Cursor res = null;
		
		try {
			res = mReadDatabase.rawQuery("select * from " + DatabaseHandle.PAGES_TABLE +
												" where " + DatabaseHandle.PAGES_COLUMN_ID + " = ?", 
												new String[] { String.valueOf(pageId) }
												);
			
			if(res.getCount() > 0) {
				res.moveToFirst();
				page = new Page.Builder().fromCursor(res).build();
			} 
			
		} catch(Exception e) {
			Log.e(TAG, "page, Error: page id " + pageId + " " + e.getMessage());
		} finally {
			if(res != null) {
				res.close();
			}
		}
		
		return page;
	}
	
	public Page pageByNumber(String bookId, int pageNumber) {
		Page page = null;
		
		Cursor res = null;
		
		try {
			res = mReadDatabase.rawQuery("select * from " + DatabaseHandle.PAGES_TABLE +
											" where " + DatabaseHandle.PAGES_COLUMN_PAGE_NUMBER + " = ? and " +
											DatabaseHandle.PAGES_COLUMN_BOOK_ID + " = ?", 
											new String[] { String.valueOf(pageNumber),
														   String.valueOf(bookId) }
											);
			
			if(res.getCount() > 0) {
				res.moveToFirst();
				page = new Page.Builder().fromCursor(res).build();
			} 
		} catch(Exception e) {
			Log.e(TAG, "pageByNumber, Error: book id " + bookId + ", page number " + String.valueOf(pageNumber) + " " + e.getMessage());
		} finally {
			if(res != null) {
				res.close();
			}
		}
		
		return page;
	}
	
	public int pageNumber(String pageId) {
		int page_number = 0;
		
		Cursor res = null;
		
		try {
			res = mReadDatabase.rawQuery("select * from " + DatabaseHandle.PAGES_TABLE +
					" where " + DatabaseHandle.PAGES_COLUMN_ID + " = ?", 
					new String[] { String.valueOf(pageId) }
					);
			
			if(res.getCount() > 0) {
				res.moveToFirst();
				
				page_number = res.getInt(res.getColumnIndex(DatabaseHandle.PAGES_COLUMN_PAGE_NUMBER));
			}
		} catch(Exception e) {
			Log.e(TAG, "pageNumber, Error: page id " + pageId + " " + e.getMessage());
		} finally {
			if(res != null) {
				res.close();
			}
		}
		
		return page_number;
	}

	public ArrayList<Page> pagesByChapter(String chapterId) {
		ArrayList<Page> pages = new ArrayList<Page>();
		
		Cursor res = null;

		try {
			res =  mReadDatabase.rawQuery("select * from " + DatabaseHandle.PAGES_TABLE +
											 " where " + DatabaseHandle.PAGES_COLUMN_CHAPTER_ID + " = ?" +
											 " order by " + DatabaseHandle.PAGES_COLUMN_PAGE_NUMBER,
											 new String[] { String.valueOf(chapterId) }
											 );
			
			if(res.getCount() > 0) {
				res.moveToFirst();
				while(res.isAfterLast() == false) {
					pages.add(new Page.Builder().fromCursor(res).build());
					res.moveToNext();
				}
			}
		} catch(Exception e) {
			Log.e(TAG, "pagesByChapter, Error: chapter id " + chapterId + " " + e.getMessage());
		} finally {
			if(res != null) {
				res.close();
			}
		}

		return pages;
	}
	
	public ArrayList<Page> pagesByBook(String bookId) {
		ArrayList<Page> pages = new ArrayList<Page>();
		
		Cursor res = null;

		try {
			res =  mReadDatabase.rawQuery("select * from " + DatabaseHandle.PAGES_TABLE +
											 " where " + DatabaseHandle.PAGES_COLUMN_BOOK_ID + " = ?" +
											 " order by " + DatabaseHandle.PAGES_COLUMN_PAGE_NUMBER,
											 new String[] { String.valueOf(bookId) }
											 );
			
			if(res.getCount() > 0) {
				res.moveToFirst();
				while(res.isAfterLast() == false) {
					pages.add(new Page.Builder().fromCursor(res).build());
					res.moveToNext();
				}
			}
		} catch(Exception e) {
			Log.e(TAG, "pagesByBook, Error: book id " + bookId + " " + e.getMessage());
		} finally {
			if(res != null) {
				res.close();
			}
		}

		return pages;
	}
	
	public String chapterIdByPage(String pageId) {
		String id = "";
		
		Cursor res = null;
		
		try {
			res = mReadDatabase.rawQuery("select * from " + DatabaseHandle.PAGES_TABLE +
											" where " + DatabaseHandle.PAGES_COLUMN_ID + " = ?", 
											new String[] { String.valueOf(pageId) }
											);
			
			if(res.getCount() > 0) {
				res.moveToFirst();
				
				id = res.getString(res.getColumnIndex(DatabaseHandle.PAGES_COLUMN_CHAPTER_ID));
			} 
		} catch(Exception e) {
			Log.e(TAG, "chapterIdByPage, Error: page id " + pageId + " " + e.getMessage());
		} finally {
			if(res != null) {
				res.close();
			}
		}
		
		return id;
	}
}
