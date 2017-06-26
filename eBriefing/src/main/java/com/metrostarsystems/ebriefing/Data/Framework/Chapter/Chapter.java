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

package com.metrostarsystems.ebriefing.Data.Framework.Chapter;

import org.ksoap2.serialization.SoapObject;

import android.database.Cursor;
import android.util.Log;

import com.metrostarsystems.ebriefing.Utilities;
import com.metrostarsystems.ebriefing.Data.Framework.Database.DatabaseHandle;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;

public class Chapter {
	
	private static final String TAG = Chapter.class.getSimpleName();

	private String 		mId;
	private String		mBookId;
	private String 		mTitle;
	private String 		mDescription;
	private int 		mPageCount;
	private String 		mSmallImageUrl;
	private String 		mLargeImageUrl;
	private int 		mImageVersion;
	private String 		mFirstPageId;
	
	public String id() { return mId; }
	public String bookId() { return mBookId; }
	public String title() { return mTitle; }
	public String description() { return mDescription; }
	public int pageCount() { return mPageCount; }
	
	public String directory() { return mBookId + "/"; }
	public String smallImageUrl() { return mSmallImageUrl; }
	public String smallImageFilename() { return mId + "small.png"; }
	public String smallImageFilePath() { return directory() + smallImageFilename(); }
	public String largeImageUrl() { return mLargeImageUrl; }
	public String largeImageFilename() { return mId + "large.png"; }
	public String largeImageFilePath() { return directory() + largeImageFilename(); }
	public int imageVersion() { return mImageVersion; }
	public String firstPageId() { return mFirstPageId; }
	
	
	private Chapter(Builder build) {
		mId					= build.mId;
		mBookId				= build.mBookId;
		mTitle				= build.mTitle;
		mDescription		= build.mDescription;
		mPageCount			= build.mPageCount;
		mSmallImageUrl		= build.mSmallImageUrl;
		mLargeImageUrl		= build.mLargeImageUrl;
		mImageVersion		= build.mImageVersion;
		mFirstPageId		= build.mFirstPageId;
	}
	
	
	public static class Builder {
		private String 		mId;
		private String 		mBookId;
		private String 		mTitle;
		private String 		mDescription;
		private int 		mPageCount;
		private String 		mSmallImageUrl;
		private String 		mLargeImageUrl;
		private int 		mImageVersion;
		private String 		mFirstPageId;
		
		public Builder bookId(String id) { mBookId = id; return this; }
		
		public Builder fromCursor(Cursor cursor) {
			mId		 		= cursor.getString(cursor.getColumnIndex(DatabaseHandle.CHAPTERS_COLUMN_ID));
			mBookId			= cursor.getString(cursor.getColumnIndex(DatabaseHandle.CHAPTERS_COLUMN_BOOK_ID));
			mTitle			= cursor.getString(cursor.getColumnIndex(DatabaseHandle.CHAPTERS_COLUMN_TITLE));
			mDescription 	= cursor.getString(cursor.getColumnIndex(DatabaseHandle.CHAPTERS_COLUMN_DESCRIPTION));
			mPageCount		= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.CHAPTERS_COLUMN_PAGE_COUNT));
			mSmallImageUrl	= cursor.getString(cursor.getColumnIndex(DatabaseHandle.CHAPTERS_COLUMN_SMALL_IMAGE_URL));
			mLargeImageUrl  = cursor.getString(cursor.getColumnIndex(DatabaseHandle.CHAPTERS_COLUMN_LARGE_IMAGE_URL));
			mImageVersion   = cursor.getInt(cursor.getColumnIndex(DatabaseHandle.CHAPTERS_COLUMN_IMAGE_VERSION));
			mFirstPageId 	= cursor.getString(cursor.getColumnIndex(DatabaseHandle.CHAPTERS_COLUMN_FIRST_PAGE_ID));
			
			return this;
		}
		
		public Builder fromSoap(ServerConnection connection, SoapObject object) {
			if(object == null) { return this; }
			
			String id = object.getPrimitiveProperty("ID").toString();
			String title = object.getProperty("Title").toString();
			String description = object.getProperty("Description").toString();
			String first_page_id = object.getProperty("FirstPageID").toString();
			
			int page_count = 0;
			int image_version = 0;
			
			try {
				page_count = Integer.valueOf(object.getProperty("PageCount").toString());
				image_version = Integer.valueOf(object.getProperty("ImageVersion").toString());
			} catch (NumberFormatException e) {
				Log.e(TAG, TAG + ": " + id + " has invalid data.");
			}
			
			String small_image_url = object.getProperty("SmallImageURL").toString();
			String large_image_url = object.getProperty("LargeImageURL").toString();
			
			mId 			= id;
			mTitle 			= title;
			mDescription 	= description;
			mPageCount 		= page_count;
			mSmallImageUrl 	= small_image_url;
			mLargeImageUrl 	= large_image_url;
			mImageVersion 	= image_version;
			mFirstPageId 	= first_page_id;
			
			return this;
		}
		
		public Chapter build() {
			return new Chapter(this);
		}
	}
}
