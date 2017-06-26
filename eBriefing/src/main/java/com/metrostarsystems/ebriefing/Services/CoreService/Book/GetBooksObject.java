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

package com.metrostarsystems.ebriefing.Services.CoreService.Book;

import org.ksoap2.serialization.SoapObject;

import com.metrostarsystems.ebriefing.Tags;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Services.CoreService.AbstractCoreObject;
import com.metrostarsystems.ebriefing.Services.CoreService.Book.GetBooksObject.ServerBookObject;
import com.metrostarsystems.ebriefing.Services.SyncService.AbstractSyncObject;

public class GetBooksObject extends AbstractCoreObject<ServerBookObject> {

	
	
	public GetBooksObject(boolean valid) {
		super(valid);
	}
	
	private GetBooksObject(Builder build) {
		mObjects = build.objects();
	}	
	
	public static class Builder extends AbstractCoreObject.Builder<ServerBookObject> {
		
		
		public Builder() { }
		
		@Override
		protected void add(ServerConnection connection, SoapObject object) {
			if(connection == null || object == null) {
				return;
			}
			
			mObjects.add(new ServerBookObject.Builder()
												.fromSoap(object)
												.build());
		}
		
		public GetBooksObject build() {
			return new GetBooksObject(this);
		}
	}
	
	public static class ServerBookObject {
		private String 				mId;
		private String 				mTitle;
		private String 				mDescription;
		private int 				mChapterCount;
		private int					mPageCount;
		private String 				mSmallImageUrl;
		private String				mLargeImageUrl;
		private int					mImageVersion;
		private int					mBookVersion;
		private String				mDateAdded;
		private String				mDateModified;
		
		public String id() { return mId; }
		public String title() { return mTitle; }
		public String description() { return mDescription; }
		public int chapterCount() { return mChapterCount; }
		public int pageCount() { return mPageCount; }
		public String smallImageUrl() { return mSmallImageUrl; }
		public String largeImageUrl() { return mLargeImageUrl; }
		public int imageVersion() { return mImageVersion; }
		public int bookVersion() { return mBookVersion; }
		public String dateAdded() { return mDateAdded; }
		public String dateModified() { return mDateModified; }
		
		private ServerBookObject(Builder build) {
			mId				= build.mId;
			mTitle			= build.mTitle;
			mDescription	= build.mDescription;
			mChapterCount	= build.mChapterCount;
			mPageCount		= build.mPageCount;
			mSmallImageUrl	= build.mSmallImageUrl;
			mLargeImageUrl	= build.mLargeImageUrl;
			mImageVersion	= build.mImageVersion;
			mBookVersion	= build.mBookVersion;
			mDateAdded		= build.mDateAdded;
			mDateModified	= build.mDateModified;
		}
		
		public static class Builder {
			private String 				mId;
			private String 				mTitle;
			private String 				mDescription;
			private int 				mChapterCount;
			private int					mPageCount;
			private String 				mSmallImageUrl;
			private String				mLargeImageUrl;
			private int					mImageVersion;
			private int					mBookVersion;
			private String				mDateAdded;
			private String				mDateModified;
		
			public Builder fromSoap(SoapObject object) {
				if(object == null) { 
					return this; 
				}
				
				String id = 
						object.getPrimitiveProperty(Tags.CORE_GET_BOOKS_RESPONSE_ID).toString();
				String title = 
						object.getProperty(Tags.CORE_GET_BOOKS_RESPONSE_TITLE).toString();
				String description = 
						object.getProperty(Tags.CORE_GET_BOOKS_RESPONSE_DESCRIPTION).toString();
				
				if(description.contains("anyType{}")) {
					description = "";
				}
				
				int chapter_count = 0;
				int page_count = 0;
				int image_version = 0;
				int version = 0;
				
				try {
					chapter_count = Integer.valueOf(
							object.getProperty(Tags.CORE_GET_BOOKS_RESPONSE_CHAPTER_COUNT).toString());
					page_count = Integer.valueOf(
							object.getProperty(Tags.CORE_GET_BOOKS_RESPONSE_PAGE_COUNT).toString());
					image_version = Integer.valueOf(
							object.getProperty(Tags.CORE_GET_BOOKS_RESPONSE_IMAGE_VERSION).toString());
					version = Integer.valueOf(
							object.getProperty(Tags.CORE_GET_BOOKS_RESPONSE_VERSION).toString());
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				
				String small_image_url = 
						object.getProperty(Tags.CORE_GET_BOOKS_RESPONSE_SMALL_IMAGE_URL).toString();
				String large_image_url = 
						object.getProperty(Tags.CORE_GET_BOOKS_RESPONSE_LARGE_IMAGE_URL).toString();
				String date_added = 
						object.getProperty(Tags.CORE_GET_BOOKS_RESPONSE_DATE_ADDED).toString();
				String date_modified = 
						object.getProperty(Tags.CORE_GET_BOOKS_RESPONSE_DATE_MODIFIED).toString();
				
				mId		 			= id;
				mTitle				= title;
				mDescription 		= description;
				mChapterCount		= chapter_count;
				mPageCount			= page_count;
				mSmallImageUrl		= small_image_url;
				mLargeImageUrl  	= large_image_url;
				mImageVersion  	 	= image_version;
				mBookVersion 		= version;
				mDateAdded			= date_added;
				mDateModified		= date_modified;
				
				
				return this;
			}
			
			
			public ServerBookObject build() {
				return new ServerBookObject(this);
			}
		}
	}
}
