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

package com.metrostarsystems.ebriefing.Services.CoreService.Chapter;

import java.util.ArrayList;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.metrostarsystems.ebriefing.Tags;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Services.SyncService.AbstractSyncObject;
import com.metrostarsystems.ebriefing.Services.CoreService.Chapter.GetChaptersObject.ChapterObject;

public class GetChaptersObject extends AbstractSyncObject<ChapterObject> {
	
	private static final String TAG = GetChaptersObject.class.getSimpleName();
	
	public GetChaptersObject(boolean valid) {
		super(valid);
	}
	
	private GetChaptersObject(Builder build) {
		mObjects = build.objects();
	}	

	public static class Builder extends AbstractSyncObject.Builder<ChapterObject> {
		
		
		public Builder() { }
		
		@Override
		protected void add(ServerConnection connection, SoapObject object) {
			if(connection == null || object == null) {
				return;
			}
			
			if(mObjects == null) {
				mObjects = new ArrayList<ChapterObject>();
			}
			
			String id = 
					object.getPrimitiveProperty(Tags.CORE_GET_CHAPTERS_RESPONSE_ID).toString();
			
			String title = 
					object.getProperty(Tags.CORE_GET_CHAPTERS_RESPONSE_TITLE).toString();
			
			String description = 
					object.getProperty(Tags.CORE_GET_CHAPTERS_RESPONSE_DESCRIPTION).toString();

			int page_count = 0;
			int image_version = 0;
			
			try {
				page_count = Integer.valueOf(
						object.getProperty(Tags.CORE_GET_CHAPTERS_RESPONSE_PAGE_COUNT).toString());
				
				image_version = Integer.valueOf(
						object.getProperty(Tags.CORE_GET_CHAPTERS_RESPONSE_IMAGE_VERSION).toString());
			} catch (NumberFormatException e) {
				Log.e(TAG, TAG + ": " + id + " has invalid data.");
			}
			
			String small_image_url = 
					object.getProperty(Tags.CORE_GET_CHAPTERS_RESPONSE_SMALL_IMAGE_URL).toString();
			
			String large_image_url = 
					object.getProperty(Tags.CORE_GET_CHAPTERS_RESPONSE_LARGE_IMAGE_URL).toString();
			
			String first_page_id = 
					object.getProperty(Tags.CORE_GET_CHAPTERS_RESPONSE_FIRST_PAGE_ID).toString();
			
			mObjects.add(new ChapterObject.Builder()
												.id(id)
												.title(title)
												.description(description)
												.pageCount(page_count)
												.smallImageUrl(small_image_url)
												.largeImageUrl(large_image_url)
												.imageVersion(image_version)
												.firstPageId(first_page_id)
												.build());
		}
		
		public GetChaptersObject build() {
			return new GetChaptersObject(this);
		}
	}
	
	
	public static class ChapterObject {
		public String 		mId 			= "";
		public String		mTitle 			= "";
		public String		mDescription 	= "";
		public int			mPageCount 		= 0;
		public String		mSmallImageUrl 	= "";
		public String		mLargeImageUrl 	= "";
		public int			mImageVersion 	= 0;
		public String		mFirstPageId 	= "";
		
		public ChapterObject(Builder build) {
			mId 			= build.mId;
			mTitle 			= build.mTitle;
			mDescription 	= build.mDescription;
			mPageCount 		= build.mPageCount;
			mSmallImageUrl 	= build.mSmallImageUrl;
			mLargeImageUrl 	= build.mLargeImageUrl;
			mImageVersion 	= build.mImageVersion;
			mFirstPageId 	= build.mFirstPageId;
		}
		
		public static class Builder {
			public String 		mId 			= "";
			public String		mTitle 			= "";
			public String		mDescription 	= "";
			public int			mPageCount 		= 0;
			public String		mSmallImageUrl 	= "";
			public String		mLargeImageUrl 	= "";
			public int			mImageVersion 	= 0;
			public String		mFirstPageId 	= "";
			
			public Builder() { }
			
			public Builder id(String id) { mId = id; return this; }
			public Builder title(String title) { mTitle = title; return this; }
			public Builder description(String description) { mDescription = description; return this; }
			public Builder pageCount(int count) { mPageCount = count; return this; }
			public Builder smallImageUrl(String url) { mSmallImageUrl = url; return this; }
			public Builder largeImageUrl(String url) { mLargeImageUrl = url; return this; }
			public Builder imageVersion(int version) { mImageVersion = version; return this; }
			public Builder firstPageId(String id) { mFirstPageId = id; return this; }

			public ChapterObject build() {
				return new ChapterObject(this);
			}
		}
	} 
}
