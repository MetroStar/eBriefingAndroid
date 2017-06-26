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

package com.metrostarsystems.ebriefing.Services.SyncService.Bookmarks;

import org.ksoap2.serialization.SoapObject;

import com.metrostarsystems.ebriefing.Tags;
import com.metrostarsystems.ebriefing.Data.Framework.Page.Page;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Services.SyncService.AbstractSyncObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Bookmarks.GetMyBookmarksObject.BookmarkObject;

public class GetMyBookmarksObject extends AbstractSyncObject<BookmarkObject> {
	
	private GetMyBookmarksObject(Builder build) {
		mObjects = build.objects();
	}
	
	public GetMyBookmarksObject(boolean valid) {
		super(valid);
	}

	public static class Builder extends AbstractSyncObject.Builder<BookmarkObject> {
		
		public Builder() { }
		
		
		@Override
		protected void add(ServerConnection connection, SoapObject object) {
			String book_id = 
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_BOOKMARKS_RESPONSE_BOOKMARK_BOOK_ID).toString();
			
			int book_version = 0;
			
			String page_id = 
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_BOOKMARKS_RESPONSE_BOOKMARK_PAGE_ID).toString();
			
			String value = 
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_BOOKMARKS_RESPONSE_BOOKMARK_VALUE).toString(); 
			
			String modified_date = 
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
			
			Page page = connection.app().data().database().pagesDatabase().page(page_id);
			
			if(page == null) {
				return;
			}
			
			int page_number = page.pageNumber();
			
			String chapter_id = connection.app().data().database().pagesDatabase().chapterIdByPage(page_id);
			
			mObjects.add(new BookmarkObject.Builder()
													.bookId(book_id)
													.bookVersion(book_version)
													.chapterId(chapter_id)
													.pageId(page_id)
													.value(value)
													.dateModified(modified_date)
													.removed(removed)
													.pageNumber(page_number)
													.build());
		}
		
		@Override
		public GetMyBookmarksObject build() {
			return new GetMyBookmarksObject(this);
		}
	}
	
	public static class BookmarkObject {
		public String 		mBookId = "";
		public int			mBookVersion = 0;
		public String		mChapterId = "";
		public String		mPageId = "";
		public String		mValue = "";
		public String		mDateModified = "";
		public boolean		mRemoved = false;
		public int			mPageNumber = 0;
		
		public BookmarkObject(Builder build) {
			mBookId			= build.mBookId;
			mBookVersion	= build.mBookVersion;
			mChapterId		= build.mChapterId;
			mPageId			= build.mPageId;
			mValue			= build.mValue;
			mDateModified	= build.mDateModified;
			mRemoved		= build.mRemoved;
			mPageNumber		= build.mPageNumber;
		}
		
		public static class Builder {
			public String 		mBookId = "";
			public int			mBookVersion = 0;
			public String		mChapterId = "";
			public String		mPageId = "";
			public String		mValue = "";
			public String		mDateModified = "";
			public boolean		mRemoved = false;
			public int			mPageNumber = 0;
			
			public Builder() { }
			
			public Builder bookId(String id) { mBookId = id; return this; }
			public Builder bookVersion(int version) { mBookVersion = version; return this; }
			public Builder chapterId(String id) { mChapterId = id; return this; }
			public Builder pageId(String id) { mPageId = id; return this; }
			public Builder value(String value) { mValue = value; return this; }
			public Builder dateModified(String date) { mDateModified = date; return this; }
			public Builder removed(boolean removed) { mRemoved = removed; return this; }
			public Builder pageNumber(int number) { mPageNumber = number; return this; }

			public BookmarkObject build() {
				return new BookmarkObject(this);
			}
		}
	}
}
