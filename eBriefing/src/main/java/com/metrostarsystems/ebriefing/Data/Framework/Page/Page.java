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

package com.metrostarsystems.ebriefing.Data.Framework.Page;

import org.ksoap2.serialization.SoapObject;

import android.database.Cursor;
import android.util.Log;

import com.metrostarsystems.ebriefing.Utilities;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Chapter.Chapter.Builder;
import com.metrostarsystems.ebriefing.Data.Framework.Database.DatabaseHandle;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;

public class Page {
	
	private static final String TAG = Page.class.getSimpleName();

	private String 		mId;
	private String		mChapterId;
	private String		mBookId;
	private String 		mUrl;
	private int 		mPageNumber;
	private String		mMD5;
	private String 		mType;
	private int 		mVersion;
	
	public String id() { return mId; }
	public String chapterId() { return mChapterId; }
	public String bookId() { return mBookId; }
	public String url() { return mUrl; }
	public String directory() { return mBookId + "/"; }
	public String filename() { return mId + "." + mType; }
	public String filePath() { return directory() + filename(); }
	public int pageNumber() { return mPageNumber; }
	public String md5() { return mMD5; }
	public String type() { return mType; }
	public int version() { return mVersion; }
	
	
	private Page(Builder build) {
		mId					= build.mId;
		mChapterId			= build.mChapterId;
		mBookId				= build.mBookId;
		mUrl				= build.mUrl;
		mPageNumber			= build.mPageNumber;
		mMD5				= build.mMD5;
		mType				= build.mType;
		mVersion			= build.mVersion;
	}
	
	
	public static class Builder {
		private String 		mId;
		private String		mChapterId;
		private String		mBookId;
		private String 		mUrl;
		private int 		mPageNumber;
		private String		mMD5;
		private String 		mType;
		private int 		mVersion;
		
		
		public Builder fromCursor(Cursor cursor) {
			mId		 		= cursor.getString(cursor.getColumnIndex(DatabaseHandle.PAGES_COLUMN_ID));
			mChapterId		= cursor.getString(cursor.getColumnIndex(DatabaseHandle.PAGES_COLUMN_CHAPTER_ID));
			mBookId			= cursor.getString(cursor.getColumnIndex(DatabaseHandle.PAGES_COLUMN_BOOK_ID));
			mUrl			= cursor.getString(cursor.getColumnIndex(DatabaseHandle.PAGES_COLUMN_URL));
			mPageNumber 	= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.PAGES_COLUMN_PAGE_NUMBER));
			mMD5			= cursor.getString(cursor.getColumnIndex(DatabaseHandle.PAGES_COLUMN_MD5));
			mType			= cursor.getString(cursor.getColumnIndex(DatabaseHandle.PAGES_COLUMN_TYPE));
			mVersion		= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.PAGES_COLUMN_VERSION));
			
			return this;
		}
		
		public Builder fromSoap(ServerConnection connection, SoapObject object, Book book) {
			if(connection == null || object == null || book == null) { 
				return this; 
			}

			String id = object.getPrimitiveProperty("ID").toString();
			String url = object.getProperty("URL").toString();
			String md5 = object.getProperty("MD5").toString();
			String type = object.getProperty("Type").toString();
			
			int page_number = 0;
			int version = 0;
			
			try {
				page_number = Integer.valueOf(object.getProperty("PageNumber").toString());
				version = Integer.valueOf(object.getProperty("Version").toString());
			} catch (NumberFormatException e) {
				Log.e(TAG, "Page: " + id + " has invalid data.");
			}
			
			
			mId				= id;
			mChapterId		= connection.app().data().database().chaptersDatabase().chapterId(book.id(), page_number);
			mBookId			= book.id();
			mUrl 			= url;
			mPageNumber 	= page_number;
			mMD5 			= md5;
			mType 			= type;
			mVersion		= version;
			
			return this;
		}

		
		public Page build() {
			return new Page(this);
		}
	}
}
