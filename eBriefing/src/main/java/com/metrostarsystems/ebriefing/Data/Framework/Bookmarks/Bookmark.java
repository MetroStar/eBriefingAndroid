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

package com.metrostarsystems.ebriefing.Data.Framework.Bookmarks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import org.ksoap2.serialization.SoapObject;

import com.metrostarsystems.ebriefing.Tags;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Database.DatabaseHandle;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;

import android.database.Cursor;

public class Bookmark {

	private String 		mId;
	private String		mBookId;
	private int			mBookVersion;
	private String		mChapterId;
	private String 		mPageId;
	private int 		mPageNumber;
	private String 		mValue;
	private String 		mDateAdded;
	private String		mDateModified;
	private boolean		mRemoved;
	private boolean		mSynced;
	private boolean		mNew;
	
	public String id() { return mId; }
	public String bookId() { return mBookId; }
	public int bookVersion() { return mBookVersion; }
	public String chapterId() { return mChapterId; }
	public String pageId() { return mPageId; }
	public int pageNumber() { return mPageNumber; }
	public String value() { return mValue; }
	public String dateAdded() { return mDateAdded; }
	public String dateModified() { return mDateModified; }
	public boolean isRemoved() { return mRemoved; }
	public boolean isSynced() { return mSynced; }
	public boolean isNew() { return mNew; }
	
	public boolean isNewer(String dateModified) {
		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();
		
		SimpleDateFormat format = Book.SERVER_DATE_FORMAT;
		
		try {
			calendar1.setTime(format.parse(dateModified));
			calendar2.setTime(format.parse(dateModified()));
			
			if(calendar2.before(calendar1)) { return false; }
			if(calendar2.after(calendar1)) { return true; }
				
			return true;
		} catch (ParseException e) {
			e.printStackTrace();
		}
			
			
		return false;
	}

	
	private Bookmark(Builder build) {
		mId				= build.mId;
		mBookId			= build.mBookId;
		mBookVersion	= build.mBookVersion;
		mChapterId		= build.mChapterId;
		mPageId			= build.mPageId;
		mPageNumber		= build.mPageNumber;
		mValue			= build.mValue;
		mDateAdded		= build.mDateAdded;
		mDateModified	= build.mDateModified;
		mRemoved		= build.mRemoved;
		mSynced			= build.mSynced;
		mNew			= build.mNew;
	}
	
	
	public static class Builder {
		private String 		mId;
		private String		mBookId;
		private int			mBookVersion;
		private String		mChapterId;
		private String 		mPageId;
		private int 		mPageNumber;
		private String 		mValue;
		private String 		mDateAdded		= Book.dateNow();
		private String		mDateModified	= Book.dateNow();
		private boolean		mRemoved		= false;
		private boolean		mSynced			= false;
		private boolean		mNew			= false;
		
		
		public Builder id() { mId = UUID.randomUUID().toString(); return this; }
		public Builder bookId(String id) { mBookId = id; return this; }
		public Builder bookVersion(int version) { mBookVersion = version; return this; }
		public Builder chapterId(String id) { mChapterId = id; return this; }
		public Builder pageId(String id) { mPageId = id; return this; }
		public Builder pageNumber(int number) { mPageNumber = number; return this; }
		public Builder value(String value) { mValue = value; return this; }
		public Builder dateAdded(String date) {
			if(date.isEmpty()) {
				mDateAdded = Book.dateNow();
			} else {
				mDateAdded = date;
			}

			return this;
		}
		public Builder dateModified(String date) {
			if(date.isEmpty()) {
				mDateModified = Book.dateNow();
			} else {
				mDateModified = date;
			}

			return this;
		}
		public Builder isRemoved(boolean isRemoved) { mRemoved = isRemoved; return this; }
		public Builder isSynced(boolean isSynced) { mSynced = isSynced; return this; }
		public Builder isNew(boolean isNew) { mNew = isNew; return this; }
		
		public Builder fromBookmark(Bookmark bookmark) {
			mId				= bookmark.mId;
			mBookId			= bookmark.mBookId;
			mBookVersion	= bookmark.mBookVersion;
			mChapterId		= bookmark.mChapterId;
			mPageId			= bookmark.mPageId;
			mPageNumber		= bookmark.mPageNumber;
			mDateAdded		= bookmark.mDateAdded;
			mRemoved		= bookmark.mRemoved;
			mSynced			= false;
			mNew 			= false;
			
			return this;
		}
		
		public Builder fromCursor(Cursor cursor) {
			mId				= cursor.getString(cursor.getColumnIndex(DatabaseHandle.BOOKMARKS_COLUMN_ID));
			mBookId			= cursor.getString(cursor.getColumnIndex(DatabaseHandle.BOOKMARKS_COLUMN_BOOK_ID));
			mBookVersion	= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.BOOKMARKS_COLUMN_BOOK_VERSION));
			mChapterId		= cursor.getString(cursor.getColumnIndex(DatabaseHandle.BOOKMARKS_COLUMN_CHAPTER_ID));
			mPageId			= cursor.getString(cursor.getColumnIndex(DatabaseHandle.BOOKMARKS_COLUMN_PAGE_ID));
			mPageNumber		= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.BOOKMARKS_COLUMN_PAGE_NUMBER));
			mValue			= cursor.getString(cursor.getColumnIndex(DatabaseHandle.BOOKMARKS_COLUMN_VALUE));
			mDateAdded		= cursor.getString(cursor.getColumnIndex(DatabaseHandle.BOOKMARKS_COLUMN_DATE_ADDED));
			mDateModified	= cursor.getString(cursor.getColumnIndex(DatabaseHandle.BOOKMARKS_COLUMN_DATE_MODIFIED));
			mRemoved		= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.BOOKMARKS_COLUMN_REMOVED)) == 1 ? true : false;
			mSynced			= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.BOOKMARKS_COLUMN_SYNCED)) == 1 ? true : false;
			mNew			= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.BOOKMARKS_COLUMN_NEW)) == 1 ? true : false;
			
			return this;
		}
		
		public Builder object(ServerConnection connection, SoapObject object) {
			String book_id = 
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_BOOKMARKS_RESPONSE_BOOKMARK_BOOK_ID).toString();
			
			int book_version = 0;
			
			String page_id = 
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_BOOKMARKS_RESPONSE_BOOKMARK_PAGE_ID).toString();
			
			String value = 
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_BOOKMARKS_RESPONSE_BOOKMARK_VALUE).toString(); 
			
			String date_modified = 
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_BOOKMARKS_RESPONSE_BOOKMARK_MODIFIED_DATE).toString();
			
			boolean removed = false;

			try {
				book_version = Integer.valueOf(
						object.getProperty(Tags.SYNC_GET_MY_BOOKMARKS_RESPONSE_BOOKMARK_BOOK_VERSION).toString());
				
				removed = Boolean.valueOf(
						object.getProperty(Tags.SYNC_GET_MY_BOOKMARKS_RESPONSE_BOOKMARK_REMOVED).toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			mId 			= UUID.randomUUID().toString();
			mBookId 		= book_id;
			mBookVersion 	= book_version;
			mPageId 		= page_id;
			mPageNumber 	= connection.app().data().database().pagesDatabase().pageNumber(page_id);
			mChapterId		= connection.app().data().database().pagesDatabase().chapterIdByPage(page_id);
			mValue 			= value;
			mDateAdded		= Book.dateNow();
			mDateModified	= date_modified;
			mRemoved 		= removed;
			mSynced			= true;
			mNew			= false;
			
			return this;
		}

		
		public Bookmark build() {
			return new Bookmark(this);
		}
	}
}
