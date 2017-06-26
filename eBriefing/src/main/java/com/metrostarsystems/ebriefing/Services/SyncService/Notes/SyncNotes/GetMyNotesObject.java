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

package com.metrostarsystems.ebriefing.Services.SyncService.Notes.SyncNotes;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.impl.client.BasicResponseHandler;
import org.ksoap2.serialization.SoapObject;

import com.metrostarsystems.ebriefing.Tags;
import com.metrostarsystems.ebriefing.Data.Framework.Page.Page;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Services.Requests.GetHttpRequest;
import com.metrostarsystems.ebriefing.Services.SyncService.AbstractSyncObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.SyncNotes.GetMyNotesObject.NoteObject;

public class GetMyNotesObject extends AbstractSyncObject<NoteObject> {
	
	public GetMyNotesObject(boolean valid) {
		super(valid);
	}
	
	private GetMyNotesObject(Builder build) {
		mObjects = build.objects();
	}
	
	public static class Builder extends AbstractSyncObject.Builder<NoteObject> {
		
		public Builder() { }
		
		@Override
		protected void add(ServerConnection connection, SoapObject object) {
			
			
			String book_id = 
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_NOTES_RESPONSE_NOTE_BOOK_ID).toString();
			
			int book_version = 0;
			boolean removed = false;
			
			try {
				book_version = Integer.valueOf(
						object.getProperty(Tags.SYNC_GET_MY_NOTES_RESPONSE_NOTE_BOOK_VERSION).toString());
				
				removed = Boolean.valueOf(
						object.getProperty(Tags.SYNC_GET_MY_NOTES_RESPONSE_NOTE_REMOVED).toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			String page_id = 
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_NOTES_RESPONSE_NOTE_PAGE_ID).toString();
			
			String value_url = 
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_NOTES_RESPONSE_NOTE_VALUE_URL).toString();
			
			String date_modified = 
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_NOTES_RESPONSE_NOTE_MODIFIED_DATE).toString();

			String content = "";
			
			HttpResponse url_response = new GetHttpRequest(connection).execute(value_url);
			
			if(url_response != null) {
				try {
					content = new BasicResponseHandler().handleResponse(url_response);
				} catch (HttpResponseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			Page page = connection.app().data().database().pagesDatabase().page(page_id);
			
			if(page == null) {
				return;
			}
			
			int page_number = page.pageNumber();
			
			String chapter_id = connection.app().data().database().pagesDatabase().chapterIdByPage(page.id());
			
			mObjects.add(new NoteObject.Builder()
												.bookId(book_id)
												.bookVersion(book_version)
												.chapterId(chapter_id)
												.pageId(page_id)
												.valueUrl(value_url)
												.dateModified(date_modified)
												.removed(removed)
												.content(content)
												.pageNumber(page_number)
												.build());
		}
		
		@Override
		public GetMyNotesObject build() {
			return new GetMyNotesObject(this);
		}
	}
	
	public static class NoteObject {
		public String 		mBookId = "";
		public int			mBookVersion = 0;
		public String		mChapterId = "";
		public String		mPageId = "";
		public String		mValueUrl = "";
		public String		mDateModified = "";
		public boolean		mRemoved = false;
		
		public String		mContent = "";
		
		public int			mPageNumber = 0;
		
		public NoteObject(Builder build) {
			mBookId			= build.mBookId;
			mBookVersion	= build.mBookVersion;
			mChapterId		= build.mChapterId;
			mPageId			= build.mPageId;
			mValueUrl		= build.mDateModified;
			mDateModified	= build.mDateModified;
			mRemoved		= build.mRemoved;
			mContent		= build.mContent;
			mPageNumber		= build.mPageNumber;
		}
		
		public static class Builder {
			public String 		mBookId = "";
			public int			mBookVersion = 0;
			public String		mChapterId = "";
			public String		mPageId = "";
			public String		mValueUrl = "";
			public String		mDateModified = "";
			public boolean		mRemoved = false;
			
			public String		mContent = "";
			
			public int			mPageNumber = 0;
			
			public Builder() { }
			
			public Builder bookId(String id) { mBookId = id; return this; }
			public Builder bookVersion(int version) { mBookVersion = version; return this; }
			public Builder chapterId(String id) { mChapterId = id; return this; }
			public Builder pageId(String id) { mPageId = id; return this; }
			public Builder valueUrl(String url) { mValueUrl = url; return this; }
			public Builder dateModified(String date) { mDateModified = date; return this; }
			public Builder removed(boolean removed) { mRemoved = removed; return this; }
			public Builder content(String content) { mContent = content; return this; }
			public Builder pageNumber(int number) { mPageNumber = number; return this; }

			public NoteObject build() {
				return new NoteObject(this);
			}
		}
	}

}
