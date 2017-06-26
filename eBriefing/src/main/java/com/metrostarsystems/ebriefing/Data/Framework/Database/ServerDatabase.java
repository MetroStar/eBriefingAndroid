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

package com.metrostarsystems.ebriefing.Data.Framework.Database;

import java.util.ArrayList;

import com.metrostarsystems.ebriefing.Dashboard.Sort.SortDirection;
import com.metrostarsystems.ebriefing.Dashboard.Sort.SortOption;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book.BookStatus;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerInfo;
import com.metrostarsystems.ebriefing.Services.SyncService.Server.ServerFeature;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ServerDatabase {
	
	private static final String TAG = ServerDatabase.class.getSimpleName();

	private SQLiteDatabase 	mReadDatabase;
	private SQLiteDatabase	mWriteDatabase;
	private DatabaseHandle	mHelper;
	
	
	
	public ServerDatabase(DatabaseHandle helper) {
		mHelper = helper;
	}
	
	public ServerDatabase open() throws SQLException {
		mWriteDatabase = mHelper.getWritableDatabase();
		mReadDatabase = mHelper.getReadableDatabase();
		
		return this;
	}
	
	public void close() {
		mHelper.close();
	}
	
	
	
	private ContentValues generateServer(ServerInfo info) {
		ContentValues values = new ContentValues();
		
		values.put(DatabaseHandle.SERVER_COLUMN_ID, info.id());
		values.put(DatabaseHandle.SERVER_COLUMN_RELEASE, info.release());
		values.put(DatabaseHandle.SERVER_COLUMN_VERSION, info.version());
		
		
		return values;
	}
	
	public boolean insertServer(ServerInfo info) {
		if(info == null) {
			return false;
		}
		
		long rows = mWriteDatabase.insert(DatabaseHandle.SERVER_TABLE, null, generateServer(info));
		
		if(rows > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public ServerInfo serverInfo() {
		ServerInfo info = null;
		
		Cursor res = null;
		
		try {
			res =  mReadDatabase.rawQuery( "select * from " + DatabaseHandle.SERVER_TABLE, null);
			
			if(res.getCount() > 0) {
				res.moveToFirst();
				info = new ServerInfo.Builder().fromCursor(res).build();
			}
		} catch(Exception e) {
			Log.e(TAG, "serverInfo, Error: " + e.getMessage());
		} finally {
			if(res != null) {
				res.close();
			}
		}

		return info;
	}
	
	public boolean updateServer(ServerInfo info) {
		long rows = mWriteDatabase.update(	DatabaseHandle.SERVER_TABLE, 
									generateServer(info), 
									DatabaseHandle.SERVER_COLUMN_ID + " = ?", 	// SELECTIONS
									new String[] { String.valueOf(info.id()) } 	// SELECTION ARGS
									);
		
		if(rows > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	private ContentValues generateFeature(ServerFeature feature) {
		ContentValues values = new ContentValues();
		
		values.put(DatabaseHandle.SERVER_FEATURES_COLUMN_ID, feature.id());
		values.put(DatabaseHandle.SERVER_FEATURES_COLUMN_NAME, feature.name());
		values.put(DatabaseHandle.SERVER_FEATURES_COLUMN_VERSION, feature.version());
		values.put(DatabaseHandle.SERVER_FEATURES_COLUMN_RELATIVE_URL, feature.url());
		
		return values;
	}
	
	public boolean insertFeature(ServerFeature feature) {
		long rows = mWriteDatabase.insert(DatabaseHandle.SERVER_FEATURES_TABLE, null, generateFeature(feature));
		
		if(rows > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean updateFeature(ServerFeature feature) {
		long rows = mWriteDatabase.update(	DatabaseHandle.SERVER_FEATURES_TABLE, 
									generateFeature(feature), 
									DatabaseHandle.SERVER_FEATURES_COLUMN_NAME + " = ?", 	// SELECTIONS
									new String[] { String.valueOf(feature.name()) } 	// SELECTION ARGS
									);
		
		if(rows > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean hasFeature(String featureName) {
		int count = (int) DatabaseUtils.longForQuery(mReadDatabase, 
								"select count(*) from " + DatabaseHandle.SERVER_FEATURES_TABLE +
								" where " + DatabaseHandle.SERVER_FEATURES_COLUMN_NAME + " = ?",
								new String[] { String.valueOf(featureName) }
								);
		
		return count > 0;
	}
	
	public String featureUrl(String featureName) {
		String url = "";
		
		Cursor res = null;
		
		try {
			res = mReadDatabase.rawQuery("select * from " + DatabaseHandle.SERVER_FEATURES_TABLE +
								" where " + DatabaseHandle.SERVER_FEATURES_COLUMN_NAME + " = ?", 
								new String[] { String.valueOf(featureName) }
								);
			if(res.getCount() > 0) {
				res.moveToFirst();
				url = res.getString(res.getColumnIndex(DatabaseHandle.SERVER_FEATURES_COLUMN_RELATIVE_URL));
			}
			
		} catch(Exception e) {
			Log.e(TAG, "serverInfo, Error: feature name " + featureName + " " + e.getMessage());
		} finally {
			if(res != null) {
				res.close();
			}
		}
		
		return url;
	}
	
	
}
