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

package com.metrostarsystems.ebriefing.Data.Framework.Server;

import java.util.UUID;

import android.database.Cursor;

import com.metrostarsystems.ebriefing.Data.Framework.Book.Book.BookStatus;
import com.metrostarsystems.ebriefing.Data.Framework.Database.DatabaseHandle;
import com.metrostarsystems.ebriefing.Services.SyncService.Server.ServerFeature.Builder;

public class ServerInfo {

	private String mId = "";
	private int mRelease = 0;
	private float mVersion = 0.0f;
	
	private ServerInfo(Builder build) {
		mId = build.mId;
		mRelease = build.mRelease;
		mVersion = build.mVersion;
	}
	
	public void setId(String id) {
		mId = id;
	}
	
	public String id() { return mId; }
	public int release() { return mRelease; }
	public float version() { return mVersion; }
	
	public static class Builder {
		private String mId = "";
		private int mRelease = 0;
		private float mVersion = 0.0f;
		
		public Builder() {
			mId = UUID.randomUUID().toString();
		}
		
		public Builder release(int release) { mRelease = release; return this; }
		public Builder version(float version) { mVersion = version; return this; }
		
		public Builder fromCursor(Cursor cursor) {
			
			mId		 			= cursor.getString(cursor.getColumnIndex(DatabaseHandle.SERVER_COLUMN_ID));
			mRelease			= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.SERVER_COLUMN_RELEASE));
			mVersion			= cursor.getFloat(cursor.getColumnIndex(DatabaseHandle.SERVER_COLUMN_VERSION));
			
			return this;
		}
		
		public ServerInfo build() {
			return new ServerInfo(this);
		}
	}
}
