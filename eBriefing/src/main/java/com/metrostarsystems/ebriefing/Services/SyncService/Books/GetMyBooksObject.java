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

package com.metrostarsystems.ebriefing.Services.SyncService.Books;

import java.util.ArrayList;

import org.ksoap2.serialization.SoapObject;

import com.metrostarsystems.ebriefing.Tags;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Services.SyncService.AbstractSyncObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Books.GetMyBooksObject.BookObject;

public class GetMyBooksObject extends AbstractSyncObject<BookObject> {
	
	public GetMyBooksObject(boolean valid) {
		super(valid);
	}
	
	private GetMyBooksObject(Builder build) {
		mObjects = build.objects();
	}	

	public static class Builder extends AbstractSyncObject.Builder<BookObject> {
		
		
		public Builder() { }
		
		@Override
		protected void add(ServerConnection connection, SoapObject object) {
			if(connection == null || object == null) {
				return;
			}
			
			if(mObjects == null) {
				mObjects = new ArrayList<BookObject>();
			}
			
			String id = object.getPrimitiveProperty(Tags.SYNC_BOOK_REQUEST_BOOK_ID).toString();

			int version = 0;
			boolean favorite = false;
			String date_modified = object.getPrimitiveProperty(Tags.SYNC_BOOK_REQUEST_BOOK_MODIFIED_DATE).toString();
			boolean removed = false;

			try {
				version = Integer.valueOf(object.getProperty(Tags.SYNC_BOOK_REQUEST_BOOK_VERSION).toString());
				favorite = Boolean.valueOf(object.getProperty(Tags.SYNC_BOOK_REQUEST_BOOK_FAVORITE).toString());
				removed = Boolean.valueOf(object.getProperty(Tags.SYNC_BOOK_REQUEST_BOOK_REMOVED).toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			mObjects.add(new BookObject.Builder()
												.id(id)
												.version(version)
												.favorite(favorite)
												.dateModified(date_modified)
												.removed(removed)
												.build());
		}
		
		public Builder setMyBook(String bookId, boolean synced) {
			if(mObjects == null) {
				mObjects = new ArrayList<BookObject>();
			}
			
			mObjects.add(new BookObject.Builder()
												.id(bookId)
												.synced(synced)
												.build());
			
			return this;
		}
		
		public GetMyBooksObject build() {
			return new GetMyBooksObject(this);
		}
	}
	
	
	public static class BookObject {
		public String 		mId = "";
		public int			mVersion = 0;
		public boolean		mFavorite = false;
		public String		mDateModified = "";
		public boolean		mRemoved = false;
		public boolean		mSynced = false;
		
		public BookObject(Builder build) {
			mId				= build.mId;
			mVersion		= build.mVersion;
			mFavorite		= build.mFavorite;
			mDateModified	= build.mDateModified;
			mRemoved		= build.mRemoved;
			mSynced			= build.mSynced;
		}
		
		public static class Builder {
			public String 		mId = "";
			public int			mVersion = 0;
			public boolean		mFavorite = false;
			public String		mDateModified = "";
			public boolean		mRemoved = false;
			public boolean		mSynced = false;
			
			public Builder() { }
			
			public Builder id(String id) { mId = id; return this; }
			public Builder version(int version) { mVersion = version; return this; }
			public Builder favorite(boolean favorite) { mFavorite = favorite; return this; }
			public Builder dateModified(String date) { mDateModified = date; return this; }
			public Builder removed(boolean removed) { mRemoved = removed; return this; }
			public Builder synced(boolean synced) { mSynced = synced; return this; }

			public BookObject build() {
				return new BookObject(this);
			}
		}
	} 
}
