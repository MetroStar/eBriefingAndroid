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

package com.metrostarsystems.ebriefing.Data.Framework.Notes;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.BasicResponseHandler;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.transport.HttpResponseException;

import com.metrostarsystems.ebriefing.Tags;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Bookmarks.Bookmark.Builder;
import com.metrostarsystems.ebriefing.Data.Framework.Database.DatabaseHandle;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Services.Requests.GetHttpRequest;

import android.database.Cursor;
import android.util.Log;

public class Note {
	
	public static final SimpleDateFormat NOTE_VIEW_FORMAT = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
	
	private static final String TAG = Note.class.getSimpleName();

	
	private String 		mId				= "";
	private String		mBookId			= "";
	private int			mBookVersion	= 0;
	private String		mChapterId		= "";
	private String 		mPageId			= "";
	private int 		mPageNumber		= 0;
	private String 		mValueUrl		= "";
	private String 		mDateCreated	= "";
	private String		mDateModified	= "";
	private boolean		mRemoved		= false;
	private boolean		mSynced			= false;
	private String		mContent		= "";
	
	public String id() { return mId; }
	public String bookId() { return mBookId; }
	public int bookVersion() { return mBookVersion; }
	public String chapterId() { return mChapterId; }
	public String pageId() { return mPageId; }
	public int pageNumber() { return mPageNumber; }
	public String valueUrl() { return mValueUrl; }
	public String dateCreated() { return mDateCreated; }
	public String dateModified() { return mDateModified; }
	public boolean isRemoved() { return mRemoved; }
	public boolean isSynced() { return mSynced; }
	public String content() { return mContent; }
	
	public String dateModifiedFormat() { 
		String date = "";
		
		if(mDateModified.isEmpty()) {
			return date;
		}
		
		try {
			date = NOTE_VIEW_FORMAT.format(Book.BOOK_DATE_FORMAT.parse(mDateModified));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return date;
	}

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
	
	private Note(Builder build) {
		mId				= build.mId;
		mBookId			= build.mBookId;
		mBookVersion	= build.mBookVersion;
		mChapterId		= build.mChapterId;
		mPageId			= build.mPageId;
		mPageNumber		= build.mPageNumber;
		mValueUrl		= build.mValueUrl;
		mDateCreated	= build.mDateCreated;
		mDateModified	= build.mDateModified;
		mRemoved		= build.mRemoved;
		mSynced			= build.mSynced;
		mContent		= build.mContent;
	}
	
	
	public static class Builder {
		private String 		mId				= "";
		private String		mBookId			= "";
		private int			mBookVersion	= 0;
		private String		mChapterId		= "";
		private String 		mPageId			= "";
		private int 		mPageNumber		= 0;
		private String 		mValueUrl		= "";
		private String 		mDateCreated	= Book.dateNow();
		private String		mDateModified	= Book.dateNow();
		private boolean		mRemoved		= false;
		private boolean		mSynced			= false;
		private String		mContent		= "";
		
		public Builder id() { mId = UUID.randomUUID().toString(); return this; }
		public Builder id(String id) { mId = id; return this; }
		public Builder bookId(String id) { mBookId = id; return this; }
		public Builder bookVersion(int version) { mBookVersion = version; return this; }
		public Builder chapterId(String id) { mChapterId = id; return this; }
		public Builder pageId(String id) { mPageId = id; return this; }
		public Builder pageNumber(int number) { mPageNumber = number; return this; }
		public Builder valueUrl(String url) { mValueUrl = url; return this; }
		public Builder dateCreated(String date) {
			if(date.isEmpty()) {
				mDateCreated = Book.dateNow();
			} else {
				mDateCreated = date;
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
		public Builder content(String content) { mContent = content; return this; }
		
		public Builder fromNote(Note note) {
			mId				= note.mId;
			mBookId			= note.mBookId;
			mBookVersion	= note.mBookVersion;
			mChapterId		= note.mChapterId;
			mPageId			= note.mPageId;
			mPageNumber		= note.mPageNumber;
			mDateCreated	= note.mDateCreated;
			mRemoved		= note.mRemoved;
			mSynced			= false;
			
			return this;
		}
		
		public Builder fromCursor(Cursor cursor) {
			mId				= cursor.getString(cursor.getColumnIndex(DatabaseHandle.NOTES_COLUMN_ID));
			mBookId			= cursor.getString(cursor.getColumnIndex(DatabaseHandle.NOTES_COLUMN_BOOK_ID));
			mBookVersion	= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.NOTES_COLUMN_BOOK_VERSION));
			mChapterId		= cursor.getString(cursor.getColumnIndex(DatabaseHandle.NOTES_COLUMN_CHAPTER_ID));
			mPageId			= cursor.getString(cursor.getColumnIndex(DatabaseHandle.NOTES_COLUMN_PAGE_ID));
			mPageNumber		= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.NOTES_COLUMN_PAGE_NUMBER));
			mValueUrl		= cursor.getString(cursor.getColumnIndex(DatabaseHandle.NOTES_COLUMN_VALUE_URL));
			mDateCreated	= cursor.getString(cursor.getColumnIndex(DatabaseHandle.NOTES_COLUMN_DATE_CREATED));
			mDateModified	= cursor.getString(cursor.getColumnIndex(DatabaseHandle.NOTES_COLUMN_DATE_MODIFIED));
			mRemoved		= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.NOTES_COLUMN_REMOVED)) == 1 ? true : false;
			mSynced			= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.NOTES_COLUMN_SYNCED)) == 1 ? true : false;
			mContent		= cursor.getString(cursor.getColumnIndex(DatabaseHandle.NOTES_COLUMN_CONTENT));
			
			return this;
		}
		
		public Builder object(ServerConnection connection, SoapObject object) {
			if(object == null) { return this; }
			
			String book_id = 
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_NOTES_RESPONSE_NOTE_BOOK_ID).toString();

			int book_version = 0;
							
			try {
				book_version = Integer.valueOf(
						object.getPrimitiveProperty(Tags.SYNC_GET_MY_NOTES_RESPONSE_NOTE_BOOK_VERSION).toString());
			} catch (NumberFormatException e) {
				Log.e(TAG, "Note: " + book_id + " has invalid data.");
			}

			String page_id = 
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_NOTES_RESPONSE_NOTE_PAGE_ID).toString();

			String value_url = 
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_NOTES_RESPONSE_NOTE_VALUE_URL).toString();

			String date_modified = 
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_NOTES_RESPONSE_NOTE_MODIFIED_DATE).toString();

			boolean removed = Boolean.valueOf(
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_NOTES_RESPONSE_NOTE_REMOVED).toString());

			HttpResponse url_response = new GetHttpRequest(connection).execute(value_url);

			String value = "";

			if(url_response != null) {
				try {
					value = new BasicResponseHandler().handleResponse(url_response);
				} catch (HttpResponseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}


			mId 			= UUID.randomUUID().toString();
			mBookId 		= book_id;
			mBookVersion 	= book_version;
			mPageId 		= page_id;
			mPageNumber 	= connection.app().data().database().pagesDatabase().pageNumber(page_id);
			mChapterId		= connection.app().data().database().pagesDatabase().chapterIdByPage(page_id);
			mValueUrl		= value_url;
			mDateCreated	= Book.dateNow();
			mDateModified 	= date_modified;
			mRemoved 		= removed;
			mSynced			= true;
			mContent 		= value;
			
			return this;
		}
		
		public Note build() {
			return new Note(this);
		}
	}
}
