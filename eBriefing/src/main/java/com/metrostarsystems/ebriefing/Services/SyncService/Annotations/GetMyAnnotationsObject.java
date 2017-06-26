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

package com.metrostarsystems.ebriefing.Services.SyncService.Annotations;

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
import com.metrostarsystems.ebriefing.Services.SyncService.Annotations.GetMyAnnotationsObject.AnnotationObject;

public class GetMyAnnotationsObject extends AbstractSyncObject<AnnotationObject> {
	
    public GetMyAnnotationsObject(boolean valid) {
		super(valid);
	}
    
	
	public GetMyAnnotationsObject(Builder build) {
		mObjects = build.objects();
	}
	
	public static class Builder extends AbstractSyncObject.Builder<AnnotationObject> {
		
		public Builder() { }
		

		@Override
		protected void add(ServerConnection connection, SoapObject object) {
			String book_id = 
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_ANNOTATIONS_RESPONSE_ANNOTATION_BOOK_ID).toString();
			
			String page_id = 
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_ANNOTATIONS_RESPONSE_ANNOTATION_PAGE_ID).toString();
			
			String platform = 
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_ANNOTATIONS_RESPONSE_ANNOTATION_PLATFORM).toString();
			
			String image_data_url = 
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_ANNOTATIONS_RESPONSE_ANNOTATION_IMAGE_URL).toString();
			
			String text_data_url = 
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_ANNOTATIONS_RESPONSE_ANNOTATION_TEXT_URL).toString();
			
			String modified_date = 
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_ANNOTATIONS_RESPONSE_ANNOTATION_MODIFIED_DATE).toString();

			int book_version = 0;
			boolean removed = false;
			
			try {
				book_version = Integer.valueOf(
					object.getProperty(Tags.SYNC_GET_MY_ANNOTATIONS_RESPONSE_ANNOTATION_BOOK_VERSION).toString());

				removed = Boolean.valueOf(
					object.getProperty(Tags.SYNC_GET_MY_ANNOTATIONS_RESPONSE_ANNOTATION_REMOVED).toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			HttpResponse url_response = new GetHttpRequest(connection).execute(text_data_url);
			
			String content = "";
			
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
			
			String chapter_id = connection.app().data().database().pagesDatabase().chapterIdByPage(page_id);
			
			mObjects.add(new AnnotationObject.Builder()
													.bookId(book_id)
													.bookVersion(book_version)
													.chapterId(chapter_id)
													.pageId(page_id)
													.platform(platform)
													.imageDataUrl(image_data_url)
													.textDataUrl(text_data_url)
													.dateModified(modified_date)
													.removed(removed)
													.content(content)
													.pageNumber(page_number)
													.build());
		}
		
		@Override
		public GetMyAnnotationsObject build() {
			return new GetMyAnnotationsObject(this);
		}
	}
	
	public static class AnnotationObject {
		public String 		mBookId = "";
		public int			mBookVersion = 0;
		public String		mChapterId = "";
		public String		mPageId = "";
		public String		mPlatform = "";
		public String		mImageDataUrl = "";
		public String		mTextDataUrl = "";
		public String		mDateModified = "";
		public boolean		mRemoved = false;
		public String		mContent = "";
		public int			mPageNumber = 0;
		
		public AnnotationObject(Builder build) {
			mBookId			= build.mBookId;
			mBookVersion	= build.mBookVersion;
			mChapterId		= build.mChapterId;
			mPageId			= build.mPageId;
			mPlatform		= build.mPlatform;
			mImageDataUrl	= build.mImageDataUrl;
			mTextDataUrl	= build.mTextDataUrl;
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
			public String		mPlatform = "";
			public String		mImageDataUrl = "";
			public String		mTextDataUrl = "";
			public String		mDateModified = "";
			public boolean		mRemoved = false;
			public String		mContent = "";
			public int			mPageNumber = 0;
			
			public Builder() { }
			
			public Builder bookId(String id) { mBookId = id; return this; }
			public Builder bookVersion(int version) { mBookVersion = version; return this; }
			public Builder chapterId(String id) { mChapterId = id; return this; }
			public Builder pageId(String id) { mPageId = id; return this; }
			public Builder platform(String platform) { mPlatform = platform; return this; }
			public Builder imageDataUrl(String url) { mImageDataUrl = url; return this; }
			public Builder textDataUrl(String url) { mTextDataUrl = url; return this; }
			public Builder dateModified(String date) { mDateModified = date; return this; }
			public Builder removed(boolean removed) { mRemoved = removed; return this; }
			public Builder content(String content) { mContent = content; return this; }
			public Builder pageNumber(int number) { mPageNumber = number; return this; }

			public AnnotationObject build() {
				return new AnnotationObject(this);
			}
		}
	}
}
